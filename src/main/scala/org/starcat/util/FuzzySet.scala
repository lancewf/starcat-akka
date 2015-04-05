package org.starcat.util

   /**
    * Create a fuzzy set with minZeroValueX1 at min maximumZeroValueX1 at max
    * and oneValueX1 at one on the graph below
    * 
    * M 1|
    * e  |      _-|-_
    * M  |    _-  |  -_
    * B  |  _-    |    -_
    * E 0|________________
    * R  |min    one   max  : value
    * 
    */
class FuzzySet(minimumZeroValueX:Double, maximumZeroValueX:Double,
         oneValueX:Double) {
   /**
   * This method finds the membership value from the 
   * 
   * M 1|
   * e  |      _-|-_
   * M  |    _-  |  -_
   * B  |  _-    |    -_
   * E 0|________________
   * R  |min    one   max  : value
   * 
   * @param crispValue The X value
   * @return the membership value a double between 0 and 1. 
   */
   def getMemberValue(crispValue:Double):Double = {
      if (crispValue == oneValueX)
      {
         return 1;
      }
      else if (crispValue > oneValueX && crispValue <= maximumZeroValueX)
      {
         return maximumMemberValue(crispValue);
      }
      
      else if (crispValue < oneValueX && crispValue >= minimumZeroValueX)
      {
         return minimumMemberValue(crispValue);
      }
      else
      { //crispValue > maximumZeroValueX || crispValue < minimumZeroValueX
         return 0;
      }
   }

   def SingletonValue():Double = {
      return oneValueX;
   }
   
  // -------------------------------------------------------------------------
  // Private Members
  // -------------------------------------------------------------------------

   /**
    * This method finds the membership value on the Left side of the one Value
    * 
    * M 1|
    * e  |      _-|-_
    * M  |    _-  |  -_
    * B  |  _-    |    -_
    * E 0|________________
    * R  |min    one   max  : value
    * 
    * @param crispValue The X value
    * @return the membership value
    */
   private def minimumMemberValue(crispValue:Double):Double ={
      if (minimumZeroValueX == Double.MinValue)
      {
         return 1;
      }
            // rise   /                run
      val slope = (0 - 1) / (minimumZeroValueX - oneValueX);
      
      
      val y_intercept = (-1) * slope * minimumZeroValueX;

      return slope * crispValue + y_intercept;
   }

   /**
    * This method finds the membership value on the right side of the one Value
    * 
    * M 1|
    * e  |      _-|-_
    * M  |    _-  |  -_
    * B  |  _-    |    -_
    * E 0|________________
    * R  |min    one   max  : value
    * @param crispValue The X value
    * @return the membership value
    */
   private def maximumMemberValue(crispValue:Double):Double = {
      if (maximumZeroValueX == Double.MaxValue)
      {
         return 1;
      }

      val slope = 1 / (oneValueX - maximumZeroValueX);
      val y_intercept = (-1) * slope * maximumZeroValueX;

      return slope * crispValue + y_intercept;
   }
}