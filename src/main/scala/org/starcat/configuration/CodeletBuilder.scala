package org.starcat.configuration

import org.starcat.slipnet.Slipnet
import org.starcat.codelets.BehaviorCodelet

/*
 * Configuration class for configuring codelets. NOTE: when 
 * configuring codelets, the <type> tag must be the first
 * tag inside the <codelet> tag.  This is so the proper 
 * codelet class can be created then populated with data.
 */
class CodeletBuilder(slipnet:Slipnet) {
// -------------------------------------------------------------------------
  // Private Data
  // -------------------------------------------------------------------------

  private var codeletBeingConstructed:BehaviorCodelet = null
  
  // -------------------------------------------------------------------------
  // Public Members
  // -------------------------------------------------------------------------

  def setColeletBeingConstructed(codeletBeingConstructed:BehaviorCodelet) {
    this.codeletBeingConstructed = codeletBeingConstructed;
  }

  /**
   * sets the source node for the codelet being constructed as well as adding
   * the in-construction codelet to the node
   */
  def setSourceNode(node:String) {
    val slipnetNode = slipnet.getSlipnetNode(node);
    slipnetNode.addCodelet(codeletBeingConstructed);
  }

  /**
   * During a successful activation of the codelet the slipnet node passed
   * in's activation is add to by the codelets failure amount
   * 
   * @param sucessRecipientNodeName
   *            the name of the failure node to add as a failure recipient
   */
  def addSuccessActivator(sucessRecipientNodeName:String,
      amountToAdd:Int) {
    val slipnetNode = slipnet.getSlipnetNode(sucessRecipientNodeName)
    codeletBeingConstructed.addSuccessActivationRecipient(slipnetNode,
        amountToAdd);
  }

  /**
   * During a failed activation the slipnet node passed in activation is
   * subtract by the codelets failure amount
   * 
   * @param failureNodeName
   *            the name of the failure node to add as a failure recipient
   */
  def addFailureActivator(failureNodeName:String, amountToAdd:Int) {
    val slipnetNode = slipnet.getSlipnetNode(failureNodeName)
    codeletBeingConstructed.addFailureActivationRecipient(slipnetNode,
        amountToAdd);
  }
}