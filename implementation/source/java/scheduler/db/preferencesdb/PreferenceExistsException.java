package scheduler.db.preferencesdb;

/**
 * Thrown when adding a preference which already exists. Originally written by
 * Alex as a nested class, it has been moved to this file because I HATE NESTED
 * CLASSES, and they're impossible to locate when you need to import them. 
 *
 * @author Eric Liebowitz
 * @version 02sep10
 */
public class PreferenceExistsException extends Exception 
{
   /**
    * Constructor calls the exception constructor.
    * 
    */
   public PreferenceExistsException() 
   {
      super();
   }
}
