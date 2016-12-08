package com.wang.akka.fsm

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.Actor.Receive

/**
  * Created by wangqi on 16/12/7.
  */
class BuncherReceive extends Actor{
  override def receive: Receive = {
    case Batch(a) => println(a)
  }
}

object Main extends App {
  val system = ActorSystem("main")
  val buncherReceive = system.actorOf(Props[BuncherReceive],"buncherReceive")
  val buncher = system.actorOf(Props[Buncher],"buncher")

  buncher ! SetTarget(buncherReceive)
  buncher ! Queue(42)
  buncher ! Queue(43)
  buncher ! Queue(44)
  buncher ! Flush
  buncher ! Queue(45)
}
