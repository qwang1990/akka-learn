package com.wang.akka.faulttolerance

/**
  * Created by wangqi on 16/12/6.
  */
import akka.actor.{ActorRef, ActorSystem, Props, Terminated}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import akka.testkit.{EventFilter, ImplicitSender, TestActors, TestKit}

class FaultHandlingDocSpec(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem(
    "FaultHandlingDocSpec",
    ConfigFactory.parseString("""
      akka {
        loggers = ["akka.testkit.TestEventListener"]
        loglevel = "WARNING"
      }
      """)))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A supervisor" must "apply the chosen strategy for its child" in {
    // code here
    val supervisor = system.actorOf(Props[Supervisor], "supervisor")

    supervisor ! Props[Child]
    val child = expectMsgType[ActorRef] // retrieve answer from TestKit’s testActor

    child ! 42 // set state to 42
    child ! "get"
    expectMsg(42)

    child ! new ArithmeticException // crash it
    child ! "get"
    expectMsg(42)

    watch(child) // have testActor watch “child”
    child ! new IllegalArgumentException // break it
    expectMsgPF() { case Terminated(`child`) => () }



    supervisor ! Props[Child] // create new child
    val child2 = expectMsgType[ActorRef]
    watch(child2)
    child2 ! "get" // verify it is alive
    expectMsg(0)

    child2 ! new Exception("CRASH") // escalate failure
    expectMsgPF() {
      case t @ Terminated(`child2`) if t.existenceConfirmed => ()
    }



  }
}
