package com.wang.akka

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{Actor, ActorKilledException, ActorLogging, ActorRef, ActorSystem, Kill, OneForOneStrategy, Props, Terminated}

import scala.concurrent.duration._
/**
  * Created by wangqi on 16/12/4.
  */
class FirstActor extends Actor with ActorLogging {
  import context._
  val myActor = actorOf(Props[MyActor], name = "myactor")
  val watchActor = actorOf(Props[WatchActor],name="watchActor")
  val watchRestart = actorOf(Props[WatchRestart],name="watchRestart")
 // val timeOutActor = actorOf(Props[TimeOutActor],name = "timeOutActor")

  val temp = context.watch(watchRestart)

  override def preStart() {
    val tem = context.actorSelection("/user/firstActor/myactor")
    log.info("{}",tem)
    tem ! MyActor.Greeting("这里也能发??")
    log.warning("注意,我要开始运行了")
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ActorKilledException      => log.error("呵呵呵呵{}",sender()); Resume
      case _: Exception                 => log.error("你也走这里？"); Restart
    }

  def receive = {
    case "stop" => stop(self)
    case "kill" => watchActor ! "kill"
    case "finished" => log.info("watch的子actor已经完结");
    case "restart" =>temp ! "restart"
    case Terminated(`watchRestart`) =>
      log.warning("虽然restart，我还是能收到信息")
    case "wang" => watchRestart ! "wang"
 //   case "timeOut" =>  timeOutActor ! "Hello"
    case "testKill" => myActor ! Kill
    case x => myActor ! x
  }
}

object Main extends App{

  val system: ActorSystem = ActorSystem("Hello")
  val a: ActorRef = system.actorOf(Props[FirstActor], "firstActor")

//  a ! MyActor.Greeting("heheheh")
//  a ! MyActor.Goodbye
//  a ! "kill"
//  a ! "restart"
//  a ! "wang"
//  a ! "timeOut"
    a ! "testKill"
}
