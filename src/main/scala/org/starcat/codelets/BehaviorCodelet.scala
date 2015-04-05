package org.starcat.codelets

import org.starcat.slipnet.SlipnetNode
import org.starcat.slipnet.Slipnet
import org.starcat.coderack.Coderack
import org.starcat.workspace.Workspace

trait BehaviorCodelet extends Codelet {
  
  // -----------------------------------------------------------------------------
  // Private Data
  // ------------------------------------------------------------------------------

  private var successActivationRecipients = List[SlipnetNodeActivationRecipient]()

  private var failureActivationRecipients = List[SlipnetNodeActivationRecipient]()
  
  private var workspaceSuccess = false
  
  // -----------------------------------------------------------------------------
  // Public Members
  // -----------------------------------------------------------------------------

  val name:String
  val numberToEmit:Int
  
  def setWorkspaceSuccess(workspaceSuccess:Boolean){
    this.workspaceSuccess = workspaceSuccess
  }
  
  def setSuccessActivationRecipients(succRec: List[SlipnetNodeActivationRecipient]) {
    successActivationRecipients = succRec
  }

  def getSuccessActivationRecipients():List[SlipnetNodeActivationRecipient] = successActivationRecipients

  def addSuccessActivationRecipient(node:SlipnetNode, amountToAdd:Int) {
    successActivationRecipients ::= new SlipnetNodeActivationRecipient(
        node, amountToAdd)
  }

  def addSuccessActivationRecipient(node:SlipnetNodeActivationRecipient ) {
    successActivationRecipients ::= node
  }

  def setFailureActivationRecipients(
      failRec:List[SlipnetNodeActivationRecipient]) {
    failureActivationRecipients = failRec;
  }

  def getFailureActivationRecipients():List[SlipnetNodeActivationRecipient] = failureActivationRecipients

  def addFailureActivationRecipient(node:SlipnetNode, amountToAdd:Int) {
    failureActivationRecipients ::= new SlipnetNodeActivationRecipient(
        node, amountToAdd)
  }

  def addFailureActivationRecipient(
      node:SlipnetNodeActivationRecipient) {
    failureActivationRecipients ::= node
  }

  // -----------------------------------------------------------------------------
  // Overridden Codelet Members
  // -----------------------------------------------------------------------------

  /*
   * Codelets are duplicated many times during execution. The clone method
   * facilitates that duplication process.
   */
  override def clone():BehaviorCodelet = {
    val cod = super.clone().asInstanceOf[BehaviorCodelet];
    cod.setSuccessActivationRecipients(this
        .getSuccessActivationRecipients());
    cod.setFailureActivationRecipients(this
        .getFailureActivationRecipients());

    return cod;
  }

  def execute(coderack:Coderack) {
    coderack.push(this, urgency);
  }

  def execute(slipnet:Slipnet) {
    if (workspaceSuccess) {
      for (successfullRecipient <- successActivationRecipients) {
        slipnet.addActivation(
            successfullRecipient.activationRecipient,
            successfullRecipient.amountToAdd);
      }
    } else {
      for (failureRecipient <- failureActivationRecipients) {
        slipnet.addActivation(
            failureRecipient.activationRecipient,
            failureRecipient.amountToAdd)
      }
    }
  }

  def preExecute(coderack:Coderack) {
    coderack.registerCodelet(this)
  }

  def preExecute(slipnet:Slipnet) {
    slipnet.registerCodelet(this)
  }

  def preExecute(workspace:Workspace) {
    workspace.registerCodelet(this)
  }

  def postExecute(coderack:Coderack) {
    coderack.unregisterCodelet(this)
  }

  def postExecute(slipnet:Slipnet ) {
    slipnet.unregisterCodelet(this)
  }

  def postExecute(workspace:Workspace) {
    workspace.fireCodeletEvent(this)
    workspace.unregisterCodelet(this)
  }
}