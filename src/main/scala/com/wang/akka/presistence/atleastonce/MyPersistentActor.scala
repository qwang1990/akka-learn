package com.wang.akka.presistence.atleastonce

/**
  * Created by wangqi on 16/12/9.
  */
import akka.actor.{Actor, ActorLogging, ActorSelection}
import akka.persistence.{AtLeastOnceDelivery, PersistentActor}

case class Msg(deliveryId: Long, s: String)
case class Confirm(deliveryId: Long)

sealed trait Evt
case class MsgSent(s: String) extends Evt
case class MsgConfirmed(deliveryId: Long) extends Evt

//这个很关键，解决了我的疑惑
//During recovery, calls to deliver will not send out messages,
// those will be sent later if no matching confirmDelivery will have been performed.
class MyPersistentActor(destination: ActorSelection)
  extends PersistentActor with AtLeastOnceDelivery {

  override def persistenceId: String = "persistence-id"

  override def receiveCommand: Receive = {
    case s: String           => persist(MsgSent(s))(updateState)
    case Confirm(deliveryId) => persist(MsgConfirmed(deliveryId))(updateState)
  }

  override def receiveRecover: Receive = {
    case evt: Evt => updateState(evt)
  }

  def updateState(evt: Evt): Unit = evt match {
    case MsgSent(s) =>
      deliver(destination)(deliveryId => Msg(deliveryId, s))

    case MsgConfirmed(deliveryId) => confirmDelivery(deliveryId)
  }
}

class MyDestination extends Actor with ActorLogging {
  def receive = {
    case Msg(deliveryId, s) =>
      log.info("receive:{}",s)
      sender() ! Confirm(deliveryId)
  }
}
