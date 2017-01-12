package com.wang.akka.presistence.demo1

/**
  * Created by wangqi on 16/12/8.
  */
import akka.actor._
import akka.persistence._

case class Cmd(data: String)
case class Evt(data: String)

case class ExampleState(events: List[String] = Nil) {
  def updated(evt: Evt): ExampleState = copy(evt.data :: events)
  def size: Int = events.length
  override def toString: String = events.reverse.toString
}

class ExamplePersistentActor(id:String) extends PersistentActor with ActorLogging{
  override def persistenceId = id

  var state = ExampleState()

  def updateState(event: Evt): Unit =
    state = state.updated(event)

  def numEvents =
    state.size

  val receiveRecover: Receive = {
    case evt: Evt                                 => updateState(evt); log.info("我在恢复：{}",evt)
    case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot; log.info("我在恢复：{}",snapshot)
    case RecoveryCompleted => log.info("我已经恢复完了")
    case SnapshotOffer(metadata, offeredSnapshot) => log.info("这是什么？？{}",offeredSnapshot)
  }

  val receiveCommand: Receive = {
    case Cmd(data) =>
      persist(Evt(s"${data}-${numEvents}"))(updateState)
      persist(Evt(s"${data}-${numEvents + 1}")) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case "snap"  => saveSnapshot(state)
    case "print" => println(state)
    case SaveSnapshotSuccess(metadata)         => log.info("snapshot success:{}",metadata)
    case SaveSnapshotFailure(metadata, reason) => log.info("snapshot fail:{},reason:{}",metadata,reason)

  }

}


object Main extends App{
  val system = ActorSystem("presistence-1")
  val pa = system.actorOf(Props(classOf[ExamplePersistentActor],"1"),"test")
  val pa2 = system.actorOf(Props(classOf[ExamplePersistentActor],"2"),"test2")
  pa ! Cmd("hehe")
  pa ! Cmd("heihei")
  pa ! "print"

  pa2 ! Cmd("p2 hehe")
  pa2 ! Cmd("p2 heihei")
  pa2 ! "print"

  pa ! "snap"
  //pa2 ! "snap"
}
