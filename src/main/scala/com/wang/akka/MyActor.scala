package com.wang.akka

import akka.actor.{Actor, ActorLogging}

/**
  * Created by wangqi on 16/12/4.
  */
object MyActor {
  case class Greeting(from: String)
  case object Goodbye
}
class MyActor extends Actor with ActorLogging {
  import MyActor._
  def receive = {
    case Greeting(greeter) => log.info(s"I was greeted by $greeter.")
    case Goodbye           => log.info("Someone said goodbye to me.")
  }
}
