package commands.typed

import akka.actor.typed.ActorRef
import cats.data.Validated
import event.BoardEvent
import states.Board


sealed trait BoardCommand extends Command[BoardEvent, Board]

object BoardCommand {

  //only here for sharding
  case class Exit(replyTo: ActorRef[Either[Command.Error, BoardEvent]]) extends BoardCommand {
    override def validate(id: String, persisted: Option[Board]): Validated[Command.Error, BoardEvent] = Validated.valid(BoardEvent.Exit)
  }

  case class Create(title: String, description: String, replyTo: ActorRef[Either[Command.Error, BoardEvent]]) extends BoardCommand {
    override def validate(id: String, persisted: Option[Board]): Validated[Command.Error, BoardEvent] =
      if (persisted.nonEmpty)
        Validated.invalid("board already exists")
      else if (title.isEmpty || description.isEmpty)
        Validated.invalid("description or title must not be empty")
      else
        Validated.valid(BoardEvent.Created(id, title, description))
  }

  case class Archive(replyTo: ActorRef[Either[String, BoardEvent]]) extends BoardCommand

}