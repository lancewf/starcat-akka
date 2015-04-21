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

class CircularQueueTest {
  @Test
  def testOK() = assertTrue(true)
  
  
  @Test
  def testIsEmpty(){
    val q = new CircularQueue[Int]
    
    assertTrue(q.isEmpty())
  }
  
  @Test
  def testIsEmptyNot(){
    val q = new CircularQueue[Int]
    
    q.push(1)
    
    assertFalse(q.isEmpty())
  }
  
  @Test
  def testSize(){
    val q = new CircularQueue[Int]
    
    q.push(1)
    
    assertEquals(1, q.size())
  }

}