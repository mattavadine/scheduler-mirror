#!/usr/bin/perl
use strict;
use warnings;
use 5.010;
use threads;
use Test::More;       # How we'll run tests

#################################
# CONSTANTS
#################################
our $C_LIMIT = 100;
our $C_DIV   = 10;
our $I_LIMIT = 1000;
our $I_DIV   = 100;
our $L_LIMIT = 1000;
our $L_DIV   = 100;

#################################
# IMPORTS
#################################
#
# Before we begin testing, get the number of CPUs we have to work with
#
my $CPUS;
BEGIN
{
   use Linux::Cpuinfo;
   $CPUS = Linux::Cpuinfo->new()->num_cpus();
}

#################################
# IMPORTS
#################################
use ConstraintChecker qw(check);
use Cwd qw(getcwd abs_path);
use Data::Dumper;
use FindBin qw($Bin);
use Thread::Pool;

#################################
# SET DIRECTORY TO THIS ONE
#
# (Where this file is actually located)
#################################
my ($oldDir) = getcwd();
chdir ($Bin);

#################################
# GLOBALS
#################################
#
# Receive our cmd line args
#
my ($test_class_dir) = @ARGV;

###################################################################
#                             TESTING                             #
###################################################################

diag (); # Give us a clean line to start on

#
# Run the test class with different numerical arguments
#
&verifyOutput(&runClass
(
   c_count => [grep { ($_ % $C_DIV) == 0 } 1..$C_LIMIT],
   i_count => [grep { ($_ % $I_DIV) == 0 } 1..$I_LIMIT],
   l_count => [grep { ($_ % $L_DIV) == 0 } 1..$L_LIMIT],
));

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
# runClasses ==>
sub runClass
{
   my (%args) = @_;
   my (@r);

   my $pool = Thread::Pool->new(
   {
      do => sub
      {
         my ($cs, $is, $ls, $name) = @_;
         my $file  = abs_path("$name");

         diag ("Running '$name'");
         system ("java -cp $test_class_dir Test_RandData ".
            "$file $cs $is $ls 1> $file.out 2> $file.err");
      },
      workers => $CPUS,
      minjobs => $CPUS * 2, # Always have jobs available for workers
   });


   for my $cs (@{$args{c_count}})
   {
      for my $is (@{$args{i_count}})
      {
         for my $ls (@{$args{l_count}})
         {
            my $name = "test_c${cs}_i${is}_l${ls}";
            $pool->job($cs, $is, $ls, $name);
            push (@r, $name);
         }
      }
   }

   $pool->shutdown();

   return \@r;
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
   my ($files) = @_;

   my $pool = Thread::Pool->new(
   {
      optimize => "cpu",
      do => sub
      {
         diag ("Checking '$_[0]'");
         ok(check($_[0]), "$_[0] passed");
      },
      workers => $CPUS,
      minjobs => $CPUS * 2, # Always have jobs for our workers
   });

   for my $file (@{$files})
   {
      if (-e $file)
      {
         $pool->job($file);
      }
      else
      {
         fail ("$file failed");
      }
   }

   $pool->shutdown();
}#<==
