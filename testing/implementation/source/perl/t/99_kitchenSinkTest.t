#!/usr/bin/perl
use strict;
use warnings;
use 5.010;
use Test::More; # How we'll run tests

#################################
# IMPORTS
#################################
#
# Before we begin testing, get the number of CPUs we have to work with
#
my $cpu;
BEGIN
{
   use Linux::Cpuinfo;
   $cpu = Linux::Cpuinfo->new();
}
use Proc::Queue qw(run_back running_now waitpids), 
                size => $cpu->num_cpus();

use ConstraintChecker qw(check);
use RandClassGenerator;
use Cwd qw(getcwd abs_path);
use Data::Dumper;
use FindBin qw($Bin);

#################################
# SET DIRECTORY TO THIS ONE
#
# (Where this file is actually located)
#################################
my ($oldDir) = getcwd();
chdir ($Bin);

#################################

my ($cpath, $test_classDir, $dest) = @ARGV;
$ClassMaker::CPATHS = [$cpath];
$ClassMaker::DEST   = $dest;

#################################
# GLOBALS
#################################

# 
# By the end of testing, will contain all RCG's which failed at some point
# during testing. Their "getError" method will return a message explaining
# why they failed
#
my @failures;

###################################################################
#                             TESTING                             #
###################################################################
# EXPLANATION:
#
# Testing will be done in 4 steps:
#
#  1) All classes will be generated according to unique, random data
#  2) All generated classes will be compiled w/ Javac
#  3) All compiled classes will be run and dump their output
#  4) All ouptut will verified.
#
# This is done so that I can fork children to accomplish each task in parallel, 
# thereby speeding up testing to something less than 6 days.
#################################

diag (); # Give us a clean line to start on

# STEP 1: Gen files
my $rcgs = &genJavaClasses
(
   c_limit => 100,
   i_limit => 1000,
   l_limit => 1000,
);

# STEP 2: Compile all the generated files at once
my $allClassBuilder = ClassMaker->new
(
   files => [map { @{$_->getCm()->getFiles()} } @{$rcgs}],
   mainClass => "tooManyToCount",
);
note("Building gen'd test files");
$allClassBuilder->makeClassFiles("99_kitchenSink.compile_errors");

# STEP 3: Run each gen'd class file
&runClasses($rcgs);

# STEP 4: Verify output
&verifyOutput($rcgs);

#
# If any RCG's failed, the use should be notified. 
#
for my $rcg (@failures)
{
   fail ($rcg->getName().": ".$rcg->getError());
}

done_testing();

chdir ($oldDir);

###################################################################
#                           FUNCTIONS                             #
###################################################################

#
# Generates all the java files. We'll fork off children to do the work in 
# parallel. Since this is simply the generation step, the work will be nearly
# instantaneous ('cause Perl's fast). But, in keeping with the other portion of
# this test which uses children, I figured spawning processes couldn't hurt us.
#
# $_[0] = Hash of values to limit how many RCG's are created:
#
#   c_limit => # of courses
#   i_limit => # of instructors
#   l_limit => # of locations
#
# Returns a reference to a list of RCGs
#
# genJavaClasses ==>
sub genJavaClasses
{
   my (%args) = @_;
   my (@r);

   for (my $cs = 1; $cs < $args{c_limit}; $cs += 10)
   {
      for (my $is = 1; $is < $args{i_limit}; $is += 100)
      {
         for (my $ls = 1; $ls < $args{l_limit}; $ls += 100)
         {
            my $name  = "test_c${cs}_i${is}_l${ls}";
            my $jFile = "$test_classDir/\u$name.java";

            my $rcg = RandClassGenerator->new(
            {
               name => "test_c${cs}_i${is}_l${ls}",
               jfile => $jFile,
               cs   => $cs,
               is   => $is,
               ls   => $ls,
            });
            #
            # Fork children to do each build simlutaneously
            #
            run_back { $rcg->genClass(42) };
            push (@r, $rcg);
         }
      }
   }

   #
   # Knock-off any/all straggling children
   #
   waitpid (-1, 0) while (running_now());

   #
   # Return all the RCG's we used which were successful. Those which wren't will
   # be added to the "failures" array
   #
   &prune (\@r);
   return \@r;
}#<==

#
# Run each gen'd class as a separate child. 
#
# $_[0] = Ref to hash w/ old PID's for values and still-need-to-be-used RCG's 
#         for values. This function will delete all the old mappings in this
#         hash and replace them with new PID-RCG pairs. (Thus, there will still
#         be the same RCG values in the hash, but different, fresh PID's for 
#         them as their keys
#
# runClasses ==> 
sub runClasses
{
   my ($rcgs) = @_;

   for (my $i = 0; $i < @{$rcgs}; $i ++)
   {
      my $rcg = $rcgs->[$i];
      run_back { $rcg->runClass() };
      note ("Running ".$rcg->getName());
   }
   #
   # Wait for any/all straggling children
   #
   waitpid(-1, 0) while (running_now());
   &prune($rcgs);
}#<==

#
# Verifies the output of every class, making sure generated schedules comply 
# to scheduling constraints. 
#
# $_[0] = Hash w/ PID-RCG key-value pairs
# $_[1] = Hash of failed RCG's, w/ the RCG's as its values and the corresponding
#         PID of the failed process as their keys
#
# verifyOutput ==>
#
sub verifyOutput
{
   my ($rcgs) = @_;

   for (my $i = 0; $i < scalar @{$rcgs}; $i ++)
   {
      my $rcg = $rcgs->[$i];
      ok(check($rcg->getDumpFile()), $rcg->getName()." passed"); 
   }
}#<==

#
# Given a list of RCGs, removes any which have something returned from their 
# "getError" method. Those removed are also added to the global "failures" array
#
# $_[0] = Array ref of RCGs
#
# prune ==>
sub prune
{
   my ($rcgs) = @_;

   for (my $i = 0; $i < $#{$rcgs}; $i ++)
   {
      if ($rcgs->[$i]->getError())
      {
         push (@failures, $rcgs->[$i]);
         delete $rcgs->[$i];
      }
   }
}#<==
