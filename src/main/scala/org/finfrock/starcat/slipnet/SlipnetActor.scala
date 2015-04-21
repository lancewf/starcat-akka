package org.finfrock.starcat.slipnet

import org.finfrock.starcat.core.Component
import scala.collection.mutable.HashMap
import org.finfrock.starcat.codelets.CodeletEvent
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.ActorLogging
import org.finfrock.starcat.codelets.CodeletActor
import akka.actor.Props
import org.finfrock.starcat.codelets.Codelet

object SlipnetActor {
  case class PostCodelet(codelet:Codelet)
  case class AddSlipnetNode(slipnetNode: ActorRef)
  
  def getProps():Props = Props(classOf[SlipnetActor])
}

class SlipnetActor extends Component{
  private val DEF_OBJ_NAME = "Item"
  private val DEF_STRUCT_NAME = "Relation"
  private val DEF_IDENT_NAME = "Identity"
  private val DEF_CON = 0
  private val DEF_ACT = 0
  private val DEF_THRESH = 0

  // --------------------------------------------------------------------------
  // Protected Data
  // --------------------------------------------------------------------------

  // An array of key-value pairs; keys are conceptnames
  // and values are associated nodes
  //Map[String, SlipnetNode]
  private var slipnodeStore = Map[String, ActorRef]()
  
  // createIdentityNode
  addSlipnetNode(context.system.actorOf(
      SlipnetNodeActor.getProps(DEF_IDENT_NAME, DEF_CON, DEF_ACT, DEF_THRESH, self), DEF_IDENT_NAME))

  // createRootObjectNode
  addSlipnetNode(context.system.actorOf(
      SlipnetNodeActor.getProps(DEF_OBJ_NAME, DEF_CON, DEF_ACT, DEF_THRESH, self), DEF_OBJ_NAME))

  // createRootStructureNode
  addSlipnetNode(context.system.actorOf(
      SlipnetNodeActor.getProps(DEF_STRUCT_NAME, DEF_CON, DEF_ACT, DEF_THRESH, self), DEF_STRUCT_NAME))

  override def receive = super.receive orElse {
    case SlipnetActor.PostCodelet(codelet) => postCodelet(codelet)
    case SlipnetActor.AddSlipnetNode(slipnetNode) => addSlipnetNode(slipnetNode)
  }
  // --------------------------------------------------------------------------
  // Component Members
  // --------------------------------------------------------------------------

  def executeCodelet(codelet: Codelet) {
    preExecuteCodelet(codelet);
    codelet.executeSlipnet(self)
    postExecuteCodelet(codelet);
  }

  protected def postExecuteCodelet(codelet: Codelet) {
    codelet.postExecuteSlipnet(self)
  }

  protected def preExecuteCodelet(codelet: Codelet) {
    codelet.preExecuteSlipnet(self)
  }

  /**
   * Updates are periodically triggered by the SlipnetTide. During each
   * update, all event data from the workspace is reviewed, and that data
   * determines which nodes receive increased activation. Next, all nodes get
   * to spread activation to their neighbors, and (if over the activation
   * threshold) possibly become fully active and possibly produce codelets.
   * Lastly, node activations decay.
   */
  def update() {
    for (node <- getSlipnetNodes()) {
      node ! SlipnetNodeActor.SpreadActivation
    }

    for (node <- getSlipnetNodes()) {
      node ! SlipnetNodeActor.Update
    }
  }

  // --------------------------------------------------------------------------
  // Local Public Members
  // --------------------------------------------------------------------------

  /**
   * Bi-directional association from Slipnet to node and vice versa. Relieves
   * user of having to remember to set it both ways. Identity lateral link is
   * also built here.
   */
  protected def addSlipnetNode(slipnetNode: ActorRef) {
    slipnodeStore += (slipnetNode.path.name -> slipnetNode)
    // This is important. It adds an identity link to itself. It
    // must be called after setSlipnet() as it gets the identity
    // label node from the slipnet. It cannot be called in the
    // constructor because of preliminary nodes created that need to
    // get around some issues. Basically remember this happens here
    // when a node is added to the Slipnet.
    slipnetNode ! SlipnetNodeActor.LinkToSelf
  }

  def getSlipnetNode(key: String): ActorRef = slipnodeStore(key)

  def getIdentityNode() = getSlipnetNode(DEF_IDENT_NAME)

  def getRootObjectNode() = getSlipnetNode(DEF_OBJ_NAME)

  def getRootRelationNode() = getSlipnetNode(DEF_STRUCT_NAME)

  /**
   * It is recommended that you do NOT call this method right now. It destroys
   * the Slipnet's construction of certain inherent nodes to the Slipnet when
   * you pass in new nodes. Unless slipnetNodes has been created by another
   * Slipnet, this method isn't safe at all.
   *
   */
  def setSlipnetNodeStore(slipnetNodes: Map[String, ActorRef]) {
    slipnodeStore = Map[String, ActorRef]()

    for (node <- slipnetNodes.values) {
      addSlipnetNode(node)
    }
  }

  protected def addActivation(node: ActorRef, amount: Int) {
    node ! SlipnetNodeActor.AddActivationToBuffer(amount);
  }

  protected def getSlipnetNodes(): List[ActorRef] = slipnodeStore.values.toList

  protected def postCodelet(codelet: Codelet) {
    fireCodeletEvent(codelet)
  }
}