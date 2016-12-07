package com.wang.akka

import akka.actor.{Actor, ActorLogging, ReceiveTimeout}

import scala.concurrent.duration._

/**
  * Created by wangqi on 16/12/5.
  */
class TimeOutActor extends Actor with ActorLogging{
  // To set an initial delay
  //context.setReceiveTimeout(1000 milliseconds)
  def receive = {
    case "Hello" =>
      log.info("收到请求{}",sender)
      // To set in a response to a message
      context.setReceiveTimeout(100 milliseconds)
    case ReceiveTimeout =>
      // To turn it off
      context.setReceiveTimeout(Duration.Undefined)
      throw new RuntimeException("Receive timed out")
  }
}
