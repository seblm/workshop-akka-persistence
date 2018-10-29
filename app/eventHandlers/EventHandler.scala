package eventHandlers

import event.{BoardEvent, Event, TicketEvent}
import states.Board.{Archived, Running}
import states.{Board, Ticket}

trait EventHandler[State, E <: Event] {
  def handle(e: E)(s: Option[State]): Option[State]
}

object BoardEventHandler extends EventHandler[states.Board, event.BoardEvent] {
  override def handle(e: BoardEvent)(state: Option[Board]): Option[Board] = {
    e match {
      case BoardEvent.Created(_, title, description) => Some(Board(title, description, Running))
      case BoardEvent.StatusChanged(Archived) => state.map(s => Board(s.title, s.description, Archived))
      case _ => state
    }
  }
}

object TicketEventHandler extends EventHandler[states.Ticket, event.TicketEvent] {

  override def handle(e: TicketEvent)(state: Option[states.Ticket]): Option[Ticket] = {
    e match {
      case TicketEvent.Created(_, boardId, title, description, status) => Some(Ticket(status, boardId, title, description))
      case TicketEvent.StatusChanged(boardId, status) => state.map(s => Ticket(status, boardId, s.title, s.description))
      case _ => state
    }
  }
}