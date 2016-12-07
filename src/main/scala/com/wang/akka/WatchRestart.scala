package com.wang.akka

/**
  * Created by wangqi on 16/12/5.
  */
import akka.actor.{Actor, ActorLogging, Props, Terminated}

class WatchRestart extends Actor with ActorLogging {
//  val restartChild = context.actorOf(Props.empty, "restartChild")
//  context.watch(restartChild) // <-- this is the only call needed for registration
//  var lastSender = context.system.deadLetters

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    log.info("我要启动了。。。。。。")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    context.children foreach { child ⇒
      context.unwatch(child)
      context.stop(child)
    }
    log.error("我要重启了！！")
    postStop()
  }

  def receive = {
    case "restart" =>
      throw new Exception("我错了")
    case a => log.info("{}",a)
  }

}
