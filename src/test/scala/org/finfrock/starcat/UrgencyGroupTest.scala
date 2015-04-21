package org.finfrock.starcat

import org.junit._
import Assert._
import akka.actor.ActorSystem
import org.finfrock.starcat.core.SlipnetBehaviorRegularPulse
import akka.actor.Actor
import akka.actor.Props
import org.finfrock.starcat.core.RegularPulseActor
import scala.concurrent.duration.FiniteDuration
import akka.actor.ActorLogging
import org.finfrock.starcat.codelets.Codelet
import java.util.concurrent.TimeUnit
import org.finfrock.starcat.util.CircularQueue
import org.finfrock.starcat.coderack.UrgencyGroup
import akka.testkit.{TestKit, TestActorRef}
import org.finfrock.starcat.slipnet.SlipnetUpdateCodelet

class UrgencyGroupTest extends TestKit(ActorSystem("testSystem")) {
  @Test
  def testOK() = assertTrue(true)
  
  
  @Test
  def testSize0(){
    val urgencyGroup = new UrgencyGroup(3)
    
    assertEquals(0, urgencyGroup.size())
  }
  
  @Test
  def testSize1(){
    val urgencyGroup = new UrgencyGroup(3)
    
    val actorRef = TestActorRef[SlipnetUpdateCodelet]
    val codelet = new Codelet("codelet", actorRef)
    urgencyGroup.add(codelet)
    
    assertEquals(1, urgencyGroup.size())
  }
  
  @Test
  def testRemoveSameObject(){
    val urgencyGroup = new UrgencyGroup(3)
    
    val actorRef = TestActorRef[SlipnetUpdateCodelet]
    val codelet = new Codelet("codelet", actorRef)
    urgencyGroup.add(codelet)
    
    assertEquals(1, urgencyGroup.size())
    
    urgencyGroup.remove(codelet)
    
    assertEquals(0, urgencyGroup.size())
  }
  
  @Test
  def testRemoveDifferentObjectSameEverything(){
    val urgencyGroup = new UrgencyGroup(3)
    
    val actorRef = TestActorRef[SlipnetUpdateCodelet]
    val codelet = new Codelet("codelet", actorRef)
    urgencyGroup.add(codelet)
    
    assertEquals(1, urgencyGroup.size())
    
 
    val codelet2 = new Codelet("codelet", actorRef, timeToDie = codelet.timeToDie)
    urgencyGroup.remove(codelet2)
    
    assertEquals(0, urgencyGroup.size())
  }
  
  @Test
  def testRemoveDifferentObjectDifferentName(){
    val urgencyGroup = new UrgencyGroup(3)
    
    val actorRef = TestActorRef[SlipnetUpdateCodelet]
    val codelet = new Codelet("codelet", actorRef)
    urgencyGroup.add(codelet)
    
    assertEquals(1, urgencyGroup.size())
    
 
    val codelet2 = new Codelet("codelet2", actorRef)
    urgencyGroup.remove(codelet2)
    
    assertEquals(1, urgencyGroup.size())
  }

}