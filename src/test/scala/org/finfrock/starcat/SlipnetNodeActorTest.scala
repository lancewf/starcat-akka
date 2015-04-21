package org.finfrock.starcat

import org.junit._
import Assert._
import akka.testkit.TestActorRef
import org.finfrock.starcat.slipnet.SlipnetNodeActor
import akka.actor.ActorSystem
import akka.testkit.{ TestActors, DefaultTimeout, ImplicitSender, TestKit }
import akka.actor.Actor
import org.finfrock.starcat.slipnet.SlipnetActor
import org.finfrock.starcat.codelets.Codelet
import org.finfrock.starcat.codelets.CodeletActor
import akka.actor.Props
import org.finfrock.starcat.slipnet.Link

class SlipnetNodeActorTest extends TestKit(ActorSystem("TestKitUsageSpec")) with DefaultTimeout with ImplicitSender {

  val slipnet = system.actorOf(SlipnetActor.getProps(), "Slipnet")

  @Test
  def testOK() = assertTrue(true)

  @Test
  def testSetAndGetActivation() {
    val slipnetNode = system.actorOf(SlipnetNodeActor.getProps(
      "SlipnetNode", memoryRetention = 100,
      initialActivation = 100, activationThreshold = 100, slipnet = slipnet))

    slipnetNode ! SlipnetNodeActor.GetActivation

    expectMsg(100)

    slipnetNode ! SlipnetNodeActor.SetActivation(80)

    slipnetNode ! SlipnetNodeActor.GetActivation

    expectMsg(80)
  }

  @Test
  def testActivationThreshold() {
    val slipnetNode = system.actorOf(SlipnetNodeActor.getProps(
      "SlipnetNode", memoryRetention = 100,
      initialActivation = 100, activationThreshold = 90, slipnet = slipnet))

    slipnetNode ! SlipnetNodeActor.GetActivationThreshold

    expectMsg(90)
  }

  @Test
  def testUpdateDecay() {
    val slipnetNode = system.actorOf(SlipnetNodeActor.getProps(
      "SlipnetNode", memoryRetention = 80,
      initialActivation = 100, activationThreshold = 100, slipnet = slipnet), "SlipnetNode")
    val codeletActor = system.actorOf(Props(new Actor {
      def receive = {
        case _ =>
      }
    }))
    val codelet = new Codelet("codelet", codeletActor, numberToEmit = 1)

    slipnetNode ! SlipnetNodeActor.AddCodelet(codelet)

    slipnetNode ! SlipnetNodeActor.Update

    slipnetNode ! SlipnetNodeActor.GetActivation

    expectMsg(80)
  }

  @Test
  def testCreateLink() {
    val slipnetNodeFrom = system.actorOf(SlipnetNodeActor.getProps(
      "SlipnetNodeFrom", memoryRetention = 100,
      initialActivation = 50, activationThreshold = 100, slipnet = slipnet))
      
    val name = "Link - SlipnetNodeFrom  -> SlipnetNodeFromTo "
    val link = new Link(name, 50, slipnetNodeFrom, self)
    
    slipnetNodeFrom ! SlipnetNodeActor.AddLateralLink(link)
    
    expectMsg(SlipnetNodeActor.AddIncomingLink(link))
  }
  
  @Test
  def testSpreadActivation() {
    val slipnetNodeFrom = system.actorOf(SlipnetNodeActor.getProps(
      "SlipnetNodeFrom", memoryRetention = 100,
      initialActivation = 50, activationThreshold = 100, slipnet = slipnet))
      
    val slipnetNodeTo = system.actorOf(SlipnetNodeActor.getProps(
      "SlipnetNodeTo", memoryRetention = 100,
      initialActivation = 50, activationThreshold = 100, slipnet = slipnet))
      
    val name = "Link - SlipnetNodeFrom  -> SlipnetNodeFromTo "
    val link = new Link(name, 50, slipnetNodeFrom, self)
    
    slipnetNodeFrom ! SlipnetNodeActor.AddLateralLink(link)
    
    slipnetNodeFrom ! SlipnetNodeActor.SpreadActivation
    
    expectMsg(SlipnetNodeActor.AddIncomingLink(link))
    expectMsg(SlipnetNodeActor.AddActivationToBuffer(25))
  }
  
  @Test
  def testSpreadActivationNoSpreadLongLink() {
    val slipnetNodeFrom = system.actorOf(SlipnetNodeActor.getProps(
      "SlipnetNodeFrom", memoryRetention = 100,
      initialActivation = 50, activationThreshold = 100, slipnet = slipnet))
      
    val slipnetNodeTo = system.actorOf(SlipnetNodeActor.getProps(
      "SlipnetNodeTo", memoryRetention = 100,
      initialActivation = 50, activationThreshold = 100, slipnet = slipnet))
      
    val name = "Link - SlipnetNodeFrom  -> SlipnetNodeFromTo "
    val link = new Link(name, 100, slipnetNodeFrom, self)
    
    slipnetNodeFrom ! SlipnetNodeActor.AddLateralLink(link)
    
    slipnetNodeFrom ! SlipnetNodeActor.SpreadActivation
    
    expectMsg(SlipnetNodeActor.AddIncomingLink(link))
  }
}

