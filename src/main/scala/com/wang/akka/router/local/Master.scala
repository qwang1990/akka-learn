package com.wang.akka.router.local

/**
  * Created by wangqi on 16/12/7.
  */
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.routing.Broadcast
import akka.routing.FromConfig

trait PointValueType[T] {
  val value: T
}

case class Temp(value:Int) extends PointValueType[Int]

case class Work(a:PointValueType[_])

class Master extends Actor {
//  var router: Router = {
//    val routees = Vector.fill(5) {
//      val r = context.actorOf(Props[Worker])
//      context watch r
//      ActorRefRoutee(r)
//    }
//    Router(RoundRobinRoutingLogic(), routees)
//  }
//
//  def receive = {
//    case w: Work =>
//      router.route(w, sender())
//    case Terminated(a) =>
//      router = router.removeRoutee(a)
//      val r = context.actorOf(Props[Worker])
//      context watch r
//      router = router.addRoutee(r)
//  }


  val router: ActorRef =
    context.actorOf(FromConfig.props(Props[Worker]), "router1")

  def receive = {
    case w: Work =>
      router ! Broadcast(w)
//    case Terminated(a) =>
//      router = router.removeRoutee(a)
//      val r = context.actorOf(Props[Worker])
//      context watch r
//      router = router.addRoutee(r)
  }

}

class Worker extends Actor {
  override def receive: Receive = {
    case Work(a) => println(this+"我收到A了"+a.value); Thread.sleep(10000);  sender ! Work(a)
  }
}

class Sender extends Actor {
  val master = context.actorOf(Props[Master], name = "master")
  override def receive: Receive = {
    case a:Work => println("Sender收到了a");  master ! a
  }
}

object Main extends App{
  val system = ActorSystem("Wroker")

  val sender = system.actorOf(Props[Sender], name = "sender")

  sender ! Work(Temp(1))



//  sender ! Work(2)
//  sender ! Work(3)
//  sender ! Work(4)

}