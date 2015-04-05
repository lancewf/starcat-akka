package org.starcat.slipnet

import org.starcat.core.Component
import scala.collection.mutable.HashMap
import org.starcat.codelets.Codelet
import org.starcat.codelets.BehaviorCodelet
import org.starcat.codelets.CodeletEvent

object Slipnet {
  private val DEF_OBJ_NAME = "Item"
  private val DEF_STRUCT_NAME = "Relation"
  private val DEF_IDENT_NAME = "Identity"
  private val DEF_CON = 0
  private val DEF_ACT = 0
  private val DEF_THRESH = 0
  private val DEF_NUM_POST = 0

}

class Slipnet extends Component {
  // --------------------------------------------------------------------------
  // Protected Data
  // --------------------------------------------------------------------------

  // An array of key-value pairs; keys are conceptnames
  // and values are associated nodes
  protected var slipnodeStore = Map[String, SlipnetNode]();

  // createIdentityNode
  addSlipnetNode(new SlipnetNode(Slipnet.DEF_IDENT_NAME, Slipnet.DEF_CON, Slipnet.DEF_ACT,
    Slipnet.DEF_THRESH, Slipnet.DEF_NUM_POST))

  // createRootObjectNode
  addSlipnetNode(new SlipnetNode(Slipnet.DEF_OBJ_NAME, Slipnet.DEF_CON, Slipnet.DEF_ACT, Slipnet.DEF_THRESH,
    Slipnet.DEF_NUM_POST))

  // createRootStructureNode
  addSlipnetNode(new SlipnetNode(Slipnet.DEF_STRUCT_NAME, Slipnet.DEF_CON, Slipnet.DEF_ACT, Slipnet.DEF_THRESH,
    Slipnet.DEF_NUM_POST))

  // --------------------------------------------------------------------------
  // Component Members
  // --------------------------------------------------------------------------

  def executeCodelet(codelet: Codelet) {
    preExecuteCodelet(codelet);
    codelet.execute(this);
    postExecuteCodelet(codelet);
  }

  protected def postExecuteCodelet(codelet: Codelet) {
    codelet.postExecute(this)
  }

  protected def preExecuteCodelet(codelet: Codelet) {
    codelet.preExecute(this)
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
      node.spreadActivation()
    }

    for (node <- getSlipnetNodes()) {
      node.update()
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
  def addSlipnetNode(node: SlipnetNode) {
    slipnodeStore += (node.name -> node)
    node.setSlipnet(this);
    // This is important. It adds an identity link to itself. It
    // must be called after setSlipnet() as it gets the identity
    // label node from the slipnet. It cannot be called in the
    // constructor because of preliminary nodes created that need to
    // get around some issues. Basically remember this happens here
    // when a node is added to the Slipnet.
    node.linkToSelf();
  }

  def getSlipnetNode(key: String): SlipnetNode = slipnodeStore(key)

  def getIdentityNode(): SlipnetNode = getSlipnetNode(Slipnet.DEF_IDENT_NAME)

  def getRootObjectNode(): SlipnetNode = getSlipnetNode(Slipnet.DEF_OBJ_NAME)

  def getRootRelationNode(): SlipnetNode = getSlipnetNode(Slipnet.DEF_STRUCT_NAME)

  /**
   * It is recommended that you do NOT call this method right now. It destroys
   * the Slipnet's construction of certain inherent nodes to the Slipnet when
   * you pass in new nodes. Unless slipnetNodes has been created by another
   * Slipnet, this method isn't safe at all.
   *
   */
  def setSlipnetNodeStore(slipnetNodes: Map[String, SlipnetNode]) {
    slipnodeStore = Map[String, SlipnetNode]()

    for (node <- slipnetNodes.values) {
      addSlipnetNode(node)
    }
  }

  def addActivation(node: SlipnetNode, amount: Int) {
    node.addActivationToBuffer(amount);
  }

  def getSlipnetNodes(): List[SlipnetNode] = slipnodeStore.values.toList

  def postCodelet(codelet: Codelet) {
    fireCodeletEvent(codelet)
  }

  def handleBehaviorCodeletSuccessEvent(event: CodeletEvent) {
    event.codelet match {
      case codelet: BehaviorCodelet => {
        for (recipient <- codelet.getSuccessActivationRecipients()) {
          addActivation(recipient.activationRecipient,
            recipient.amountToAdd)
        }
      }
    }
  }

  def handleBehaviorCodeletFailureEvent(event: CodeletEvent) {
    event.codelet match {
      case codelet: BehaviorCodelet => {
        for (recipient <- codelet.getFailureActivationRecipients()) {
          addActivation(recipient.activationRecipient,
            recipient.amountToAdd);
        }
      }
    }
  }
}