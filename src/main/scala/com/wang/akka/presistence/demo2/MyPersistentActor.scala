package com.wang.akka.presistence.demo2

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

/**
  * Created by wangqi on 16/12/9.
  */
class MyPersistentActor extends PersistentActor {

  override def persistenceId = "my-stable-persistence-id"

  override def receiveRecover: Receive = {
    case _ => // handle recovery here
  }

  override def receiveCommand: Receive = {
    case c: String => {
      sender() ! c
      persistAsync(s"evt-$c-1") { e => sender() ! e }
      persistAsync(s"evt-$c-2") { e => sender() ! e }
    }
  }
}

class Sender extends Actor with ActorLogging{
  val child = context.actorOf(Props[MyPersistentActor],"hehe")

  override def receive: Receive = {
    case "hello"=>child ! "hello"
    case "hey"  =>child ! "hey"
    case a=> log.info("receive:{}",a)
  }
}

object Main extends App {
  val system = ActorSystem("pre2")
  val sender = system.actorOf(Props[Sender],"sender")
  sender ! "hello"
  sender ! "hey"

}

