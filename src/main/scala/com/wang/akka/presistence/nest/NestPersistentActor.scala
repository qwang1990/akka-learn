package com.wang.akka.presistence.nest

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.actor.Actor.Receive
import akka.persistence.PersistentActor

/**
  * Created by wangqi on 16/12/9.
  */
class NestPersistentActor extends PersistentActor {

  override def persistenceId: String = "heheheh"

  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = {
    case c: String =>
      sender() ! c

      persistAsync(s"$c-1-outer") { outer1 =>
        Thread.sleep(10000)
        sender() ! outer1
        persist(s"$c-1-inner") { inner1 =>
          sender() ! inner1
        }
      }

      persistAsync(s"$c-2-outer") { outer2 =>
        sender() ! outer2
        persist(s"$c-2-inner") { inner2 =>
          sender() ! inner2
        }
      }
  }
}

class Sender extends Actor with ActorLogging{

  val child = context.actorOf(Props[NestPersistentActor],"test")

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    child ! "wang"
    child ! "qi"
  }

  override def receive: Receive = {
    case a => log.info("receive:{}",a)
  }
}

object Main extends App {
  val system = ActorSystem("nest")
  val sender = system.actorOf(Props[Sender],"sender")
  val sender2 = system.actorOf(Props[Sender],"sender2")
}