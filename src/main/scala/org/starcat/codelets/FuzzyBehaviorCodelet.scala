package org.starcat.codelets

import org.starcat.util.FuzzySet
import org.starcat.slipnet.Slipnet

abstract class FuzzyBehaviorCodelet(
    successOneValueX:Double, 
    successMinimumZeroValueX:Double, 
    successMaximumZeroValueX:Double, 
    failureOneValueX:Double, 
    failureMinimumZeroValueX:Double, 
    failureMaximumZeroValueX:Double) extends BehaviorCodelet()
{
   // --------------------------------------------------------------------------
   // Private Data
   // --------------------------------------------------------------------------
      
   private val successFuzzySet = new FuzzySet(successMinimumZeroValueX, 
       successMaximumZeroValueX, successOneValueX)
   private val failureFuzzySet = new FuzzySet(failureMinimumZeroValueX, 
       failureMaximumZeroValueX,failureOneValueX)
   private var crispValue:Double = 0.0;
   
   // --------------------------------------------------------------------------
   // Public Members
   // --------------------------------------------------------------------------
   
   def setCrispValue(crispValue:Double){
      this.crispValue = crispValue;
   }
   
   // --------------------------------------------------------------------------
   // Overridden Codelet Members
   // --------------------------------------------------------------------------
      
   override def execute(slipnet:Slipnet)
   {
      val successMemberValue = successFuzzySet.getMemberValue(crispValue)
      val failureMemberValue = failureFuzzySet.getMemberValue(crispValue)
      for (successfullRecipient <- getSuccessActivationRecipients()){
         val amountToAdd = (successMemberValue.toDouble * 
             successfullRecipient.amountToAdd.toDouble).toInt

         slipnet.addActivation(successfullRecipient.activationRecipient,
            amountToAdd);
      }
      
      for (failureRecipient <- getFailureActivationRecipients()){
         val amountToAdd = (failureMemberValue.toDouble * 
             failureRecipient.amountToAdd.toDouble).toInt

         slipnet.addActivation(failureRecipient.activationRecipient,
            amountToAdd);
      }
   }
}