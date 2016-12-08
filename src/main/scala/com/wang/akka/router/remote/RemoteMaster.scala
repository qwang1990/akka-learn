package com.wang.akka.router.remote

import akka.actor.{Address, AddressFromURIString, Props}
import akka.remote.routing.RemoteRouterConfig
import akka.routing.RoundRobinPool

/**
  * Created by wangqi on 16/12/7.
  */
case class Work(a:Int)


//class RemoteMaster {
//  val addresses = Seq(
//    Address("akka.tcp", "remotesys", "otherhost", 1234),
//    AddressFromURIString("akka.tcp://othersys@anotherhost:1234"))
//  val routerRemote = system.actorOf(
//    RemoteRouterConfig(RoundRobinPool(5), addresses).props(Props[Echo]))
//}


