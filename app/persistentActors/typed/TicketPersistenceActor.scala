package persistentActors.typed

import akka.actor.ActorSystem
import akka.actor.typed.Behavior
import akka.persistence.typed.scaladsl.{Effect, PersistentBehaviors}
import commands.typed.TicketCommand
import event.TicketEvent
import eventHandlers.TicketEventHandler
import play.api.db.Database

object TicketPersistenceActor {

  def behavior(persistenceId: String)(implicit database : Database, actorSystem : ActorSystem): Behavior[TicketCommand] = PersistentBehaviors
    .receive[TicketCommand, TicketEvent, Option[states.Ticket]](
    persistenceId, None,
    (ticket, command) => command.validate(persistenceId, ticket)
      .fold(error => Effect.none.thenRun(_ => command.replyTo ! Left(error)),
        event => Effect.persist(event).thenRun(_ => command.replyTo ! Right(event)))
    , //ici on valide les commande et retourne le résultat à l'envoyeur
    (ticket, event) => TicketEventHandler.handle(event)(ticket) // ici on utilise l'event handler pour modifier l'état courant
  )
}
//ici on sauvegarde le snapshot