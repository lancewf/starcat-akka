package org.finfrock.starcat.slipnet

import akka.actor.Actor
import akka.actor.ActorLogging
import scala.util.Random
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.BehaviorCodeletActor
import org.finfrock.starcat.codelets.CodeletActor
import BehaviorCodeletActor._
import akka.pattern.{ ask, pipe }
import scala.concurrent.duration._
import akka.util.Timeout
import java.util.UUID
import org.finfrock.starcat.codelets.Codelet
import akka.actor.Props

object SlipnetNodeActor {
  case class AddActivationToBuffer(amount: Int)
  object GetActivation
  object GetActivationThreshold
  object LinkToSelf
  object GetCategoryNodes
  object GetInstanceNodes
  object GetPropertyNodes
  object GetLateralNodes
  object GetIdentityLink
  object GetCategoryLinks
  object GetIncomingLinks
  object GetInstanceLinks
  object GetMemoryRetention
  object GetCodeletList
  object GetLateralLinks
  object GetNumActivationIncreaseAttempts
  object GetNumTimesFullyActivated
  object GetNumUpdatesToClampAct
  object GetOutgoingLinks
  object GetPropertyLinks
  object SpreadActivation
  object Update
  case class AddCodelet(codelet: Codelet)
  case class SetActivation(activation: Int)
  case class AddIncomingLink(link: Link)
  case class AddLateralLink(link: Link)
  case class AddPropertyLink(link: PropertyLink)
  case class ClampActivation(numUpdates: Int)

  def getProps(name: String, memoryRetention: Int, initialActivation: Int,
               activationThreshold: Int, slipnet:ActorRef): Props = {
    Props(classOf[SlipnetNodeActor], name, memoryRetention,
      initialActivation, activationThreshold, slipnet)
  }
}

/**
 * activationThreshold - this the threshold when activation is reached codeletes are posted
 *     range: 1 - 100
 * initialActivation - the initial activation level range: 0 - 100
 */
class SlipnetNodeActor(name: String, memoryRetention: Int, initialActivation: Int,
     activationThreshold: Int, slipnet:ActorRef) extends Actor with ActorLogging {

  private val MAXACTIVATION = 100
  private val MAX_LINK_LENGTH = 100
  private val MINACTIVATION = 0
  
  implicit val timeout = Timeout(5 seconds)
  implicit val ex = context.dispatcher
  
  /**
   * The algorithm for the probability of posting codelets is basically the
   * amount over the threshold the SlipnetNode is divided by the maximum
   * activation less the activation threshold.
   */
  private val activationProbability: Double = 70.0
  private val rng = new Random()

  private var numTimesCalcFullAct = 0
  private var numTimesGoFullAct = 0
  private var activation = initialActivation
  private var activationBuffer = 0


  // linkage management
  private var incomingLinks = List[Link]()
  private var outgoingLinks = List[Link]()
  private var categoryLinks = List[CategoryLink]()
  private var instanceLinks = List[InstanceLink]()
  private var lateralLinks = List[Link]()
  private var propertyLinks = List[PropertyLink]()
  private var codeletList = List[Codelet]()

  // instrumentation
  private var numActivationIncreaseAttempts = 0

  /*
   * When the SlipnetNode goes to full activation, this is the number of
   * Codelet objects posted.
   */
  private var numTimesFullyActivated = 0

  /*
   * Sets the number of slipnet updates that occur before activation can
   * change. Default is 0.
   */
  private var numUpdatesToClampAct = 0

  def receive = {
    case SlipnetNodeActor.AddActivationToBuffer(amount) => {
      numActivationIncreaseAttempts += 1
      activationBuffer = activationBuffer + amount;
    }
    case SlipnetNodeActor.GetActivation                    => sender ! activation
    case SlipnetNodeActor.SetActivation(setActivation)     => activation = setActivation
    case SlipnetNodeActor.GetActivationThreshold           => sender ! activationThreshold
    case SlipnetNodeActor.LinkToSelf                       => linkToSelf()
    case SlipnetNodeActor.AddLateralLink(link)             => addLateralLink(link)
    case SlipnetNodeActor.AddIncomingLink(link)            => addIncomingLink(link)
    case SlipnetNodeActor.AddPropertyLink(link)            => addPropertyLink(link)
    case SlipnetNodeActor.ClampActivation(numUpdates)      => clampActivation(numUpdates)
    case SlipnetNodeActor.AddCodelet(codelet)              => codeletList ::= codelet
    case SlipnetNodeActor.GetCategoryNodes                 => sender ! getCategoryNodes()
    case SlipnetNodeActor.GetInstanceNodes                 => sender ! getInstanceNodes()
    case SlipnetNodeActor.GetPropertyNodes                 => sender ! getPropertyNodes()
    case SlipnetNodeActor.GetLateralNodes                  => sender ! getLateralNodes()
    case SlipnetNodeActor.GetIdentityLink                  => sender ! getIdentityLink()
    case SlipnetNodeActor.GetCategoryLinks                 => sender ! categoryLinks
    case SlipnetNodeActor.GetCodeletList                   => sender ! codeletList
    case SlipnetNodeActor.GetMemoryRetention               => sender ! memoryRetention
    case SlipnetNodeActor.GetIncomingLinks                 => sender ! incomingLinks
    case SlipnetNodeActor.GetInstanceLinks                 => sender ! instanceLinks
    case SlipnetNodeActor.GetLateralLinks                  => sender ! lateralLinks
    case SlipnetNodeActor.GetNumActivationIncreaseAttempts => sender ! numActivationIncreaseAttempts
    case SlipnetNodeActor.GetNumTimesFullyActivated        => sender ! numTimesFullyActivated
    case SlipnetNodeActor.GetNumUpdatesToClampAct          => sender ! numUpdatesToClampAct
    case SlipnetNodeActor.GetOutgoingLinks                 => sender ! outgoingLinks
    case SlipnetNodeActor.GetPropertyLinks                 => sender ! propertyLinks
    case SlipnetNodeActor.SpreadActivation                 => spreadActivation()
    case SlipnetNodeActor.Update                           => update()
  }

  protected def spreadActivation() {
    if (activation != 0) {
      for ((link, neighborNode) <- getNeighbors()) {
        // if at max link length then no activation can be spread, because to far apart.
        if (link.intrinsicLength != MAX_LINK_LENGTH) {  
          val increaseAmmount = math.round(activation.toDouble * 
              (0.01 * link.getDegreeOfAssociation())).toInt
          
          neighborNode ! SlipnetNodeActor.AddActivationToBuffer(increaseAmmount)
        }
      }
    }
  }

  private def getNeighbors(): List[(Link, ActorRef)] = {
    var neighbors = Set[ActorRef]()
    var neighborsAndLink = List[(Link, ActorRef)]()
    for {
      link <- outgoingLinks
      if (!neighbors.contains(link.toSlipnetNode))
    } {
      neighbors += link.toSlipnetNode
      neighborsAndLink ::= (link, link.toSlipnetNode)
    }
    return neighborsAndLink
  }

  /*
   * Algorithm: if node is not at full activation if activation + buffer is at
   * least max activation then go to full activation else add buffer to
   * current activation if node is above threshold probabilistically go to
   * full activation if at full activation then probabilistically post
   * codelets decay()
   */
  private def update() {
    if (numUpdatesToClampAct != 0) {
      numUpdatesToClampAct -= 1
    } else {
      if (activation != MAXACTIVATION) {
        if (activation + activationBuffer >= MAXACTIVATION) {
          activation = MAXACTIVATION
          numTimesFullyActivated += 1
        } else {
          activation += activationBuffer
        }
      }
      activationBuffer = 0

      // if over threshold, probability of discontinuously
      // becoming fully activated
      if (activation >= activationThreshold) {
        if (goToFullActivation()) {
          activation = MAXACTIVATION
          numTimesFullyActivated += 1
        }
        // if over threshold, probability of posting codelets
        postCodelets()
      }
      decay()
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
    if (activation != MINACTIVATION) {
      val amount = math.ceil(0.01 * (MAXACTIVATION - memoryRetention).toDouble * activation.toDouble).toInt

      if (activation - amount <= MINACTIVATION) {
        activation = MINACTIVATION;
      } else {
        activation -= amount;
      }
    }
  }

  private def clampActivation(numUpdates: Int) {
    numUpdatesToClampAct = numUpdates;
  }

  def getIdentityLink(): Link = {
    for (lateralLink <- lateralLinks) {
      if (lateralLink.toSlipnetNode == lateralLink.fromSlipnetNode
        && lateralLink.toSlipnetNode == self) {
        return lateralLink
      }
    }
    return null
  }

  private def getLateralNodes(): Set[ActorRef] = {
    (for (lateralLink <- lateralLinks) yield {
      lateralLink.toSlipnetNode
    }).toSet
  }

  private def getPropertyNodes(): Set[ActorRef] = {
    (for (propertyLink <- propertyLinks) yield {
      propertyLink.toSlipnetNode
    }).toSet
  }

  private def getInstanceNodes(): Set[ActorRef] = {
    (for (instanceLink <- instanceLinks) yield {
      instanceLink.toSlipnetNode
    }).toSet
  }

  private def getCategoryNodes(): Set[ActorRef] = {
    (for (categortyLink <- categoryLinks) yield {
      categortyLink.toSlipnetNode
    }).toSet
  }

  private def addPropertyLink(link: PropertyLink) {
    propertyLinks ::= link
    outgoingLinks ::= link
    link.toSlipnetNode ! SlipnetNodeActor.AddIncomingLink(link)
  }

  private def addLateralLink(link: Link) {
    lateralLinks ::= link
    outgoingLinks ::= link
    link.toSlipnetNode ! SlipnetNodeActor.AddIncomingLink(link)
  }

  private def addIncomingLink(link: Link) {
    incomingLinks ::= link
  }

  private def linkToSelf() {
    val lLink = Link.createIdentityLink(self, UUID.randomUUID().toString())
    addLateralLink(lLink)
  }

  private def goToFullActivation(): Boolean = {
    val rand = rng.nextDouble() * 100

    if (rand < activationProbability) {
      numTimesGoFullAct += 1
      return true
    } else{
      return false
    }
  }

  /**
   * Post codelets to the coderack with the given probability <b> Up to the
   * given number of codelets to post, probabilistically post the codelets to
   * the coderack based on probability
   *
   * @param probability
   *            the probability to post each new codelet
   */
  private def postCodelets() {
    for {
      codelet <- codeletList
      count <- 0 until codelet.numberToEmit
      rand = rng.nextDouble() * 100
      if (rand < activationProbability)
    } {
      postCloneOfCodeletToSlipnet(codelet)
    }
  }
  
  private def postCloneOfCodeletToSlipnet(codelet:Codelet){
    val postCodeletFuture = for{clonedCodeletActor <- ask(codelet.codeletActor, CodeletActor.GetClonedCodelet).mapTo[ActorRef]} yield{
      val clonedCodelet = new Codelet(name = clonedCodeletActor.path.name, codeletActor = clonedCodeletActor,
        urgency = codelet.urgency, numberToEmit = codelet.numberToEmit,
        codeletType = codelet.codeletType, timeToDie = codelet.timeToDie)
      
      SlipnetActor.PostCodelet(clonedCodelet)
    }
    postCodeletFuture.pipeTo(slipnet)
  }
}