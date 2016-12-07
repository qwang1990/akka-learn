package com.wang.akka

/**
  * Created by wangqi on 16/12/5.
  */
case class Write(memo:String)

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}
class ActorWithProtocol extends Actor with Stash with ActorLogging {
  def receive = {
    case "open" =>
      unstashAll()
      context.become({
        case Write(a) => log.info("写入了{}",a)
        case "close" =>
          unstashAll()
          context.unbecome()
        case msg => stash()
      }, discardOld = false) // stack on top instead of replacing
    case msg => stash(); log.info("收到信息{}",msg)
  }

}

object StashMain extends App {
  val system = ActorSystem("ActorWithProtocol")
  val stash = system.actorOf(Props[ActorWithProtocol], name = "actorWithProtocol")

  val a = Write("1")

  //It is illegal to stash the same message twice????
  stash ! a
  stash ! a
  stash ! Write("2")
  stash ! Write("3")

  stash ! "open"
  stash ! "close"

}
