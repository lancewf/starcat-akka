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
import org.finfrock.starcat.core.Component

class RegularPulseTest {
  @Test
  def testOK() = assertTrue(true)
  
  
  @Test
  def testStartAndStop(){
    val system = ActorSystem("Test")

    var isSlipnetCalled = false
    val slipnet = system.actorOf(Props(new Actor() with ActorLogging {
      def receive = {
        case Component.ExecuteCodelet(codelet) => {
          log.info("ExecuteCodelet " + codelet.name)
          isSlipnetCalled = true
        }
        case a => log.info("Slipent " + a)
      }
    }))
    
    val codeletActor = system.actorOf(Props(new Actor() with ActorLogging {
      def receive = {
        case a => log.info("codelet " + a)
      }
    }), "codelet")
    
    val pulse = system.actorOf(SlipnetBehaviorRegularPulse.getProps(slipnet), 
        "SlipnetBehaviorRegularPulse")
    
    pulse ! RegularPulseActor.Start

    system.actorOf(Props(new Actor() {
      implicit val ex = this.context.dispatcher
      def receive = {
        case "push" => {
          pulse ! RegularPulseActor.Push(new Codelet("codelet", codeletActor))
        }
        case "Stop" => {
          cancellablePush.cancel()
          pulse ! RegularPulseActor.Stop
          system.scheduler.scheduleOnce(
            FiniteDuration(1, TimeUnit.SECONDS),
            self, "shutdown")
        }
        case "shutdown" =>{
          assertTrue(isSlipnetCalled)
          system.shutdown()
        }
        case a => println(a)
      }

      val cancellablePush = system.scheduler.schedule(FiniteDuration(1, TimeUnit.SECONDS), 
          FiniteDuration(1, TimeUnit.SECONDS),
        self, "push")
      system.scheduler.scheduleOnce(
        FiniteDuration(5, TimeUnit.SECONDS),
        self, "Stop")
    }))
    
    system.awaitTermination()
  }

}