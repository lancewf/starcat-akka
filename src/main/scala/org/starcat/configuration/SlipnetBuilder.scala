package org.starcat.configuration

import org.starcat.slipnet.Slipnet
import java.util.UUID
import org.starcat.codelets.BehaviorCodelet
import org.starcat.slipnet.Link
import org.starcat.slipnet.SlipnetNode

class SlipnetBuilder {
  // -------------------------------------------------------------------------
  //  Public Members
  // -------------------------------------------------------------------------

  private val slipnet = new Slipnet()
  private val codeletBuilder = new CodeletBuilder(slipnet)

  // -------------------------------------------------------------------------
  // Protected Members
  // -------------------------------------------------------------------------

  /**
   * Create a codelet
   * 
   * @param codeletBeingConstructed
   *            the codelet being constructed
   * @param sourceNode
   *            the slipnet node associated to the codelet
   * @param urgency
   *            The urgency group to assign to the codelet in the coderack.
   *            the higher the urgency the more likely that it gets chosen in
   *            the coderack
   *            
   * UUID.randomUUID().toString()
   */
  protected def createCodelet(codeletBeingConstructed:BehaviorCodelet,
      sourceNode:String, urgency:Double, numberToEmit:Int) {
    codeletBuilder.setColeletBeingConstructed(codeletBeingConstructed)
    codeletBuilder.setSourceNode(sourceNode)
  }

  /**
   * Create a codelet
   * 
   * @param codeletBeingConstructed
   *            the codelet being constructed
   * @param name
   *            the name of the codelet
   * @param sourceNode
   *            the Slipnet node associated to the codelet
   * @param urgency
   *            The urgency group to assign to the codelet in the Coderack.
   *            the higher the urgency the more likely that it gets chosen in
   *            the Coderack
   * @param succAmount
   *            the amount to add to the Slipnet node if the codelet succeeds
   * @param failureAmount
   *            the amount to add to the Slipnet node if the codelet fails
   * @param numberToEmit
   *            The number of codelets clones to emit to the Coderack when
   *            activated
   * @param successActivatorName
   *            during a successful activation the slipnet node to add the
   *            success amount to
   * @param failureActivatorName
   *            during a failure activation the slipnet node to subtract the
   *            failure amount to
   */
  protected def createCodelet(codeletBeingConstructed:BehaviorCodelet,
      sourceNode:String, urgency:Double, succAmount:Int ,
      failureAmount:Int, numberToEmit:Int, successActivatorName:String,
      failureActivatorName:String) {
    codeletBuilder.setColeletBeingConstructed(codeletBeingConstructed)
    codeletBuilder.setSourceNode(sourceNode)
    codeletBuilder.addSuccessActivator(successActivatorName,
        succAmount);
    codeletBuilder.addFailureActivator(failureActivatorName,
        failureAmount);
  }

  /**
   * Create a codelet
   * 
   * @param codeletBeingConstructed
   *            the codelet being constructed
   * @param sourceNode
   *            the Slipnet node associated to the codelet
   * @param urgency
   *            The urgency group to assign to the codelet in the Coderack.
   *            the higher the urgency the more likely that it gets chosen in
   *            the Coderack
   * @param succAmount
   *            the amount to add to the Slipnet node if the codelet succeeds
   * @param failureAmount
   *            the amount to add to the Slipnet node if the codelet fails
   * @param numberToEmit
   *            The number of codelets clones to emit to the Coderack when
   *            activated
   * @param successActivatorName
   *            during a successful activation the slipnet node to add the
   *            success amount to
   */
  protected def createCodelet(codeletBeingConstructed:BehaviorCodelet,
      sourceNode:String, urgency:Double, succAmount:Int,
      numberToEmit:Int, successActivatorName:String) {
    codeletBuilder.setColeletBeingConstructed(codeletBeingConstructed)
    codeletBuilder.setSourceNode(sourceNode)
    codeletBuilder.addSuccessActivator(successActivatorName,
        succAmount)
  }

  /**
   * Create a codelet
   * 
   * @param codeletBeingConstructed
   *            the codelet being constructed
   * @param name
   *            the name of the codelet
   * @param sourceNode
   *            the slipnet node associated to the codelet
   * @param urgency
   *            The urgency group to assign to the codelet in the coderack.
   *            the higher the urgency the more likely that it gets chosen in
   *            the coderack
   * @param succAmount
   *            the amount to add to the slipnet node if the codelet succeeds
   * @param failureAmount
   *            the amount to add to the slipnet node if the codelet fails
   * @param numberToEmit
   *            The number of codelets clones to emit to the coderack when
   *            activated
   * @param successActivatorName
   *            during a successful activation the slipnet node to add the
   *            success amount to
   * @param failureActivatorName
   *            during a failure activation the slipnet node to subtract the
   *            failure amount to
   */
  protected def createCodelet(codeletBeingConstructed:BehaviorCodelet,
      name:String, sourceNode:String, urgency:Double, succAmount:Int ,
      failureAmount:Int, numberToEmit:Int,
      failureActivatorNames:List[String]) {
    codeletBuilder.setColeletBeingConstructed(codeletBeingConstructed)
    codeletBuilder.setSourceNode(sourceNode)

    for (failureActivatorName <- failureActivatorNames) {
      codeletBuilder.addFailureActivator(failureActivatorName,
          failureAmount);
    }
  }

  /**
   * Create a one way link
   * 
   * @param linkBegingBuilt
   *            The link being created
   * @param fromSlipnetNode
   *            the source of the spread of activation
   * @param toSlipnetNode
   *            the receiver of the spread of activation
   * @param intrinsicLength
   *            The percentage of activation from the source (from) to
   *            receiver (to)
   * @param labelNode
   */
  protected def createLink(fromSlipnetNodeName:String,
      toSlipnetNodeName:String, intrinsicLength:Int) {
    
    val name = "Link - " + fromSlipnetNodeName + " -> " + toSlipnetNodeName;
    val toSlipnetNode = slipnet.getSlipnetNode(toSlipnetNodeName)
    val fromSlipnetNode = slipnet.getSlipnetNode(fromSlipnetNodeName)
    val linkBegingBuilt = new Link(name, intrinsicLength, fromSlipnetNode, toSlipnetNode)
    
    fromSlipnetNode.addLateralLink(linkBegingBuilt)
  }

  /**
   * Create a slipnet node
   * 
   * @param name
   *            the name of the slipnet node
   * @param memoryLevel
   *            The higher the value the longer
   *            memory is stored in the node.
   * @param initalActivation
   *            the initial activation value
   * @param activationThreashold
   *            This is the level that the slipnet has to be before it places 
   *            an assigned codelet into the coderack
   */
  protected def createSlipnetNode(name:String, memoryLevel:Int,
      initalActivation:Int, activationThreashold:Int) {
    
    val newNode = new SlipnetNode(name, memoryLevel, 
        initalActivation, activationThreashold, 0);

    slipnet.addSlipnetNode(newNode);
  }
}