package com.wang.akka

import akka.typed.ActorRef

sealed trait Command
final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent])
  extends Command

sealed trait SessionEvent
final case class SessionGranted(handle: ActorRef[PostMessage]) extends SessionEvent
final case class SessionDenied(reason: String) extends SessionEvent
final case class MessagePosted(screenName: String, message: String) extends SessionEvent

final case class PostMessage(message: String)
