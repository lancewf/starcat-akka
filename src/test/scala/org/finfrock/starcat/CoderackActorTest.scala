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
import org.finfrock.starcat.coderack.CoderackActor

class CoderackActorTest extends TestKit(ActorSystem("CoderackActorTest")) with DefaultTimeout with ImplicitSender {

  @Test
  def testOK() = assertTrue(true)

 
  @Test
  def testPushOne(){
    val coderack = system.actorOf(CoderackActor.getProps(), "Coderack")
    val codelet = new Codelet("codelet", self)
    coderack ! CoderackActor.push(codelet, 10)
    coderack ! CoderackActor.GetSize
    
    expectMsg(1)
  }
  
  @Test
  def testPushAndPop(){
    val coderack = system.actorOf(CoderackActor.getProps(), "Coderack")
    val codelet = new Codelet("codelet", self)
    coderack ! CoderackActor.push(codelet, 10)

    coderack ! CoderackActor.Pop
    
    coderack ! CoderackActor.GetSize
    
    expectMsg(0)
  }
  
  @Test
  def testPushTwo(){
    val coderack = system.actorOf(CoderackActor.getProps(), "Coderack")
    val codelet = new Codelet("codelet", self)
    coderack ! CoderackActor.push(codelet, 10)
    coderack ! CoderackActor.push(codelet, 5)
    
    coderack ! CoderackActor.GetSize
    
    expectMsg(2)
  }
  
  @Test
  def testListener(){
    val coderack = system.actorOf(CoderackActor.getProps(), "Coderack")
    val codelet = new Codelet("codelet", self)
    coderack ! Component.AddCodeletEventListener(self)
    coderack ! CoderackActor.push(codelet, 10)
    
    coderack ! CoderackActor.Pop
    
    expectMsg(Component.HandleCodeletEvent(new CodeletEvent(coderack, codelet)))
  }
}

