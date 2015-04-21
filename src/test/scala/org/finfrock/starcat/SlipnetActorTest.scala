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
import org.finfrock.starcat.core.Component
import org.finfrock.starcat.codelets.Codelet
import org.finfrock.starcat.codelets.CodeletEvent

class SlipnetActorTest extends TestKit(ActorSystem("TestKitUsageSpec")) with DefaultTimeout with ImplicitSender {

  @Test
  def testOK() = assertTrue(true)

 
  @Test
  def testUpdate(){
    val slipnet = system.actorOf(SlipnetActor.getProps(), "Slipnet")
    slipnet ! SlipnetActor.AddSlipnetNode(self)
    slipnet ! Component.Update
    
    expectMsg(SlipnetNodeActor.LinkToSelf)
    expectMsg(SlipnetNodeActor.SpreadActivation)
    expectMsg(SlipnetNodeActor.Update)
  }
  
  @Test
  def testPostCodelet(){
    val slipnet = system.actorOf(SlipnetActor.getProps(), "Slipnet")
    val codelet = new Codelet("codelet", self)
    slipnet ! Component.AddCodeletEventListener(self)
    slipnet ! SlipnetActor.PostCodelet(codelet)
    
    expectMsg(Component.HandleCodeletEvent(new CodeletEvent(slipnet, codelet)))
  }
}

