package org.starcat.slipnet

import scala.util.Random
import scala.collection.mutable.ArrayBuffer
import org.starcat.codelets.Codelet
import scala.collection.mutable
import org.starcat.codelets.BehaviorCodelet

object SlipnetNode {
  private val MAXACTIVATION = 100;
  private val MINACTIVATION = 0;
}

class SlipnetNode(val name:String, initialConceptualDepth:Int, initialActivation:Int,
      initialActivationThreshold:Int, numCodeletsToPost:Int) extends Cloneable {
  private val rng = new Random()

  private var activationBuffer = 0;
  private var numTimesCalcFullAct = 0
  private var numTimesGoFullAct = 0
  private var conceptualDepth = initialConceptualDepth
  private var activationThreshold = initialActivationThreshold 

  // linkage management
  private var incomingLinks = List[Link]()
  private var outgoingLinks = List[Link]()

  private var categoryLinks = List[CategoryLink]();
  private var instanceLinks = List[InstanceLink]();
  private var lateralLinks = List[Link]();
  private var propertyLinks = List[PropertyLink]();
  private var slipLinks = List[SlipLink]();

  private var codeletList = List[Codelet]();

  // instrumentation
  private var numActivationIncreaseAttempts = 0;

  /*
   * When the SlipnetNode goes to full activation, this is the number of
   * Codelet objects posted.
   */
  private var numTimesFullyActivated = 0;
  
  private var activation = initialActivation

  /*
   * Sets the number of slipnet updates that occur before activation can
   * change. Default is 0.
   */
  private var numUpdatesToClampAct = 0;

  private var slipnet:Slipnet = null
  
  
  def linkToSelf() {
    val lLink = Link.createIdentityLink(this)
    addLateralLink(lLink)
  }

  def activate() {
    activation = SlipnetNode.MAXACTIVATION
  }

  def addActivationToBuffer(amount:Int) {
    numActivationIncreaseAttempts += 1
    activationBuffer = activationBuffer + amount;
  }

  def addCodelet(codelet:Codelet) {
    codeletList ::= codelet
  }

  def addCategoryLink(link:CategoryLink) {
    categoryLinks ::= link
    outgoingLinks ::= link
    link.toNode.addIncomingLink(link)
  }

  def addInstanceLink(link:InstanceLink) {
    instanceLinks ::= link
    outgoingLinks ::= link
    link.toNode.addIncomingLink(link)
  }

  def addLateralLink(link:Link) {
    lateralLinks ::= link
    outgoingLinks ::= link
    link.toNode.addIncomingLink(link)
  }

  def addPropertyLink(link:PropertyLink) {
    propertyLinks ::= link
    outgoingLinks ::= link
    link.toNode.addIncomingLink(link)
  }

  def addSlipLink(link:SlipLink) {
    slipLinks ::= link
    outgoingLinks ::= link
    link.toNode.addIncomingLink(link)
  }

  def addToActivationBuffer(activationBufferIncrement:Int) {
    this.activationBuffer += activationBufferIncrement;
  }

  def clampActivation(numUpdates:Int) {
    numUpdatesToClampAct = numUpdates;
  }

  /*
   * shallow clone implementation
   */
  override def clone():SlipnetNode = {
    
    val node = try {
      super.clone().asInstanceOf[SlipnetNode]
    } catch {
      case cnse:CloneNotSupportedException =>{
        throw new InternalError(cnse.getMessage())
      }
    }
    return node;
  }

  def getActivation():Int = activation

  def setActivation(act:Int) {
    activation = act
  }

  def getActivationThreshold():Int = activationThreshold

  def getCategoryNodes():Set[SlipnetNode] = {
    
    val cat = new mutable.HashSet[SlipnetNode]()

    for (categortyLink <- categoryLinks) {
      cat.add(categortyLink.toNode)
    }

    return cat.toSet;
  }

  def getInstanceNodes():Set[SlipnetNode] =  {
    val instances = new mutable.HashSet[SlipnetNode]()
    for (instanceLink <- instanceLinks) {
      instances.add(instanceLink.toNode)
    }

    instances.toSet
  }

  def getPropertyNodes():Set[SlipnetNode] =  {
    val props = new mutable.HashSet[SlipnetNode]()

    for(propertyLink <- propertyLinks) {

      props.add(propertyLink.toNode);
    }

    props.toSet
  }

  def getLateralNodes():Set[SlipnetNode] =  {
    val lats = new mutable.HashSet[SlipnetNode]()

    for (lateralLink <- lateralLinks) {
      lats.add(lateralLink.toNode);
    }
    lats.toSet
  }

  def getIdentityLink():Link = {
    for (lateralLink <- lateralLinks) {
      if (lateralLink.toNode == lateralLink.fromNode
          && lateralLink.toNode == this) {
        return lateralLink
      }
    }
    return null
  }

  def getCategoryLinks():List[CategoryLink] = categoryLinks

  def getCodeletList():List[Codelet] = codeletList

  def getConceptualDepth():Int = conceptualDepth

  def getIncomingLinks():List[Link] = incomingLinks

  def getInstanceLinks():List[InstanceLink] = instanceLinks

  def getLabelNode(toNode:SlipnetNode):SlipnetNode = {
    var link = getSlipLinkTo(toNode)
    if (link == null) {
      return null;
    }
    return link.labelNode;
  }

  def getLateralLinks():List[Link] = lateralLinks

  def getLinkTo(toNode:SlipnetNode):Link = {
    if (isLinkedTo(toNode)) {
      for (link <- getOutgoingLinks()) {
        if (link.toNode == toNode) {
          return link;
        }
      }
    }
    return null;
  }

  def getnumActivationIncreaseAttempts():Int = {
    return numActivationIncreaseAttempts;
  }

  def getNumActivationIncreaseAttempts():Int = {
    return numActivationIncreaseAttempts;
  }

  def getNumTimesFullyActivated():Int = {
    return numTimesFullyActivated;
  }

  def getNumUpdatesToClampAct():Int = {
    return numUpdatesToClampAct;
  }

  def getOutgoingLinks():List[Link] = outgoingLinks

  def getPropertyLinks():List[PropertyLink] = propertyLinks

  def getSlipLinks():List[SlipLink] = slipLinks

  def getSlipLinkTo(toNode:SlipnetNode):SlipLink = {
    if (isLinkedTo(toNode)) {
      for (link <- slipLinks) {
        if (link.isInstanceOf[SlipLink]) {
          return link.asInstanceOf[SlipLink]
        }
      }
    }
    return null;
  }

  def getSlipnet() = slipnet

  protected def goToFullActivation(probability:Double):Boolean = {
    numTimesCalcFullAct += 1

    val rand = rng.nextDouble()

    if (rand < probability) {
      numTimesGoFullAct += 1
      return true
    }
    return false
  }

  def isActive():Boolean = (activation == SlipnetNode.MAXACTIVATION)

  protected def isCategoryLinkedTo(anotherNode:SlipnetNode):Boolean = {
    isLinkedTo(categoryLinks, anotherNode)
  }

  def isInstanceOf(anotherNode:SlipnetNode):Boolean = {
    isLinkedTo(categoryLinks, anotherNode)
  }

  protected def isInstanceLinkedTo(anotherNode: SlipnetNode):Boolean = {
    isLinkedTo(instanceLinks, anotherNode);
  }

  def isCategoryOf(anotherNode:SlipnetNode):Boolean =  {
    return isLinkedTo(instanceLinks, anotherNode)
  }

  def isLinkedTo(toNode:SlipnetNode):Boolean =  {
    return isLinkedTo(outgoingLinks, toNode);
  }

  def isRelatedTo(toNode:SlipnetNode):Boolean = {
    if (this == toNode) {
      return true
    }
    return isLinkedTo(toNode)
  }

  def isSlipLinkedTo(toNode:SlipnetNode):Boolean = {
    return isLinkedTo(slipLinks, toNode);
  }

  /*
   * a little funky, this takes the other node and looks to see if it has a
   * property link to this node, since property links are not really
   * bidirectional
   */
  def isPropertyOf(anotherNode:SlipnetNode):Boolean =  {
    return isLinkedTo(anotherNode.getPropertyLinks(), this);
  }

  def setActivationThreshold(newActivationThreshold:Int) {
    this.activationThreshold = newActivationThreshold
  }

  def setCodeletList(codeletList:List[Codelet]) {
    this.codeletList = codeletList;
  }

  /**
   * the higher the value the longer memory is stored in the node.
   * 
   * @param conceptualDepth
   */
  def setConceptualDepth(conceptualDepth:Int) {
    this.conceptualDepth = conceptualDepth;
  }

  def setNumTimesFullyActivated(numTimesFullyActivated:Int) {
    this.numTimesFullyActivated = numTimesFullyActivated;
  }

  def setNumUpdatesToClampAct(numUpdatesToClampAct:Int) {
    this.numUpdatesToClampAct = numUpdatesToClampAct;
  }

  def setSlipnet(net:Slipnet) {
    slipnet = net;
  }

  def spreadActivation() {
    if (activation == 0) {
      return;
    }

    for (neighborNode <- getNeighbors()) {
        val link = getLinkTo(neighborNode)
        if (link != null && link.intrinsicLength != SlipnetNode.MAXACTIVATION) {
          val increaseAmmountD = activation.toDouble * (0.01 * link.getDegreeOfAssociation())

          val increaseAmmount = math.round(increaseAmmountD).toInt
          neighborNode.addActivationToBuffer(increaseAmmount)
        }
    }
  }

  /*
   * Algorithm: if node is not at full activation if activation + buffer is at
   * least max activation then go to full activation else add buffer to
   * current activation if node is above threshold probabilistically go to
   * full activation if at full activation then probabilistically post
   * codelets decay()
   */
  def update() {
    if (numUpdatesToClampAct != 0) {
      numUpdatesToClampAct -= 1
      return;
    }
    if (activation != SlipnetNode.MAXACTIVATION) {
      if (activation + activationBuffer >= SlipnetNode.MAXACTIVATION) {
        activation = SlipnetNode.MAXACTIVATION;
        numTimesFullyActivated+=1;
      } else {
        activation += activationBuffer
      }
    }
    activationBuffer = 0

    // if over threshold, probability of discontinuously
    // becoming fully activated
    if (activation >= activationThreshold) {
      if (goToFullActivation(getActivationProbability())) {
        activation = SlipnetNode.MAXACTIVATION;
        numTimesFullyActivated+=1
      }
      // if over threshold, probability of posting codelets
      postCodelets(getActivationProbability());
    }
    decay();
  }

  def getNumTimesCalcFullAct():Int = {
    return numTimesCalcFullAct;
  }

  def getNumTimesGoFullAct():Int = {
    return numTimesGoFullAct;
  }

  // -----------------------------------------------------------------------------
  // Private Methods
  // -----------------------------------------------------------------------------

  private def addIncomingLink(link:Link) {
    incomingLinks ::= link
  }

  /**
   * Post codelets to the coderack with the given probability <b> Up to the
   * given number of codelets to post, probabilistically post the codelets to
   * the coderack based on probability
   * 
   * @param probability
   *            the probability to post each new codelet
   */
  private def postCodelets(probability:Double) {
    for (codelet <- codeletList) {
      val behaviorCodelet = codelet.asInstanceOf[BehaviorCodelet]
      val numCodeletsToPost = behaviorCodelet.numberToEmit
      for (count <- 0 until numCodeletsToPost) {
        val rand = rng.nextDouble();
        if (rand < probability) {
          slipnet.postCodelet(behaviorCodelet.clone())
        }
      }
    }
  }

  /*
   * Algorithm: Activation buffer goes to 0 if (node has activation above min)
   * then reduce activation by amount equal to 1% of difference between max
   * activation and conceptual depth times the current activation if (the
   * difference between the current activation and the amount to be reduced
   * puts the node at minimum activation or below) then set the node's
   * activation to minimum else reduce activation by calculated amount
   */
  private def decay() {
    if (activation != SlipnetNode.MINACTIVATION) {
      val amount = math.ceil(0.01 * (SlipnetNode.MAXACTIVATION - conceptualDepth) * activation).toInt

      if (activation - amount <= SlipnetNode.MINACTIVATION) {
        activation = SlipnetNode.MINACTIVATION;
      } else {
        activation -= amount;
      }
    }
  }

  private def isLinkedTo(linkList:List[Link],
      anotherNode:SlipnetNode) :Boolean = {
    if (linkList == null) {
      return false;
    }

    for {link <- linkList} {
      if (link.isToNode(anotherNode)) {
        return true;
      }
    }
    return false;
  }

  /**
   * The algorithm for the probability of posting codelets is basically the
   * amount over the threshold the SlipnetNode is divided by the maximum
   * activation less the activation threshold.
   */
  private def getActivationProbability():Double = {
    return (activation - activationThreshold).toDouble / (SlipnetNode.MAXACTIVATION - activationThreshold).toDouble
  }

  private def getNeighbors():Set[SlipnetNode] = {
    val nodeLinks = getOutgoingLinks()
    // SlipnetNode[] neighbors = new SlipnetNode[nodeLinks.size()];
    val neighbors = new mutable.HashSet[SlipnetNode]
    for (link <- nodeLinks) {
        neighbors.add(link.toNode)
    }
    return neighbors.toSet;
  }
}