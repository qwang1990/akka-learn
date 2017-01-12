package com.wang.akka.presistence.fsm

import akka.persistence.fsm.PersistentFSM.FSMState

/**
  * Created by wangqi on 16/12/10.
  */
//用户接受的指令
sealed trait Command
case class AddItem(item: Item) extends Command
case object Buy extends Command
case object Leave extends Command
case object GetCurrentCart extends Command

//用户状态
sealed trait UserState extends FSMState
case object LookingAround extends UserState {
  override def identifier: String = "Looking Around"
}
case object Shopping extends UserState {
  override def identifier: String = "Shopping"
}
case object Inactive extends UserState {
  override def identifier: String = "Inactive"
}
case object Paid extends UserState {
  override def identifier: String = "Paid"
}

//Customer's actions are "recorded" as a sequence of "domain events" which are persisted.
// Those events are replayed on an actor's start in order to restore the latest customer's state
sealed trait DomainEvent
case class ItemAdded(item: Item) extends DomainEvent
case object OrderExecuted extends DomainEvent
case object OrderDiscarded extends DomainEvent


//用户状态
case class Item(id: String, name: String, price: Float)

sealed trait ShoppingCart {
  def addItem(item: Item): ShoppingCart
  def empty(): ShoppingCart
}
case object EmptyShoppingCart extends ShoppingCart {
  def addItem(item: Item) = NonEmptyShoppingCart(item :: Nil)
  def empty() = this
}
case class NonEmptyShoppingCart(items: Seq[Item]) extends ShoppingCart {
  def addItem(item: Item) = NonEmptyShoppingCart(items :+ item)
  def empty() = EmptyShoppingCart
}

