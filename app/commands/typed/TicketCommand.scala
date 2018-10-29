package commands.typed

import akka.actor.typed.ActorRef
import cats.data.Validated
import event.TicketEvent
import states.Ticket
import states.Ticket.{Done, InProgress, Todo}

sealed trait TicketCommand extends Command[TicketEvent, states.Ticket]

object TicketCommand {

  case class UpdateStatusTodo(replyTo: ActorRef[Either[Command.Error, TicketEvent]]) extends TicketCommand {
    override def validate(id: String, persisted: Option[Ticket]): Validated[Command.Error, TicketEvent] =
      validateEvent(persisted, Todo)
  }

  case class UpdateStatusInProgress(replyTo: ActorRef[Either[Command.Error, TicketEvent]]) extends TicketCommand {
    override def validate(id: String, persisted: Option[Ticket]): Validated[Command.Error, TicketEvent] =
      validateEvent(persisted, InProgress)
  }

  case class UpdateStatusDone(replyTo: ActorRef[Either[Command.Error, TicketEvent]]) extends TicketCommand {
    override def validate(id: String, persisted: Option[Ticket]): Validated[Command.Error, TicketEvent] =
      validateEvent(persisted, Done)
  }

  private def validateEvent(persisted: Option[Ticket], newStatus: Ticket.Status) =
    (for {
      ticket <- persisted
    } yield {
      if (ticket.status == newStatus)
        Validated.invalid("wrong status")
      else
        Validated.valid(TicketEvent.StatusChanged(ticket.boardId, newStatus))
    }).getOrElse(Validated.invalid("no ticket"))


  case class Create(boardId: String, title: String, description: String, replyTo: ActorRef[Either[Command.Error, TicketEvent]]) extends TicketCommand {
    override def validate(id: String, persisted: Option[Ticket]): Validated[Command.Error, TicketEvent] =
      if (persisted.nonEmpty)
        Validated.invalid("ticket already exists")
      else if (title.isEmpty || description.isEmpty)
        Validated.invalid("title or description can't be empty")
      else
        Validated.valid(TicketEvent.Created(id, boardId, title, description))
  }

}