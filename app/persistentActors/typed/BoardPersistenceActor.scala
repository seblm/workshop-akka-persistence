package persistentActors.typed

import akka.actor.ActorSystem
import akka.actor.typed.Behavior
import akka.persistence.typed.scaladsl.{Effect, PersistentBehaviors}
import commands.typed.BoardCommand
import event.BoardEvent
import eventHandlers.BoardEventHandler
import play.api.db.Database

object BoardPersistenceActor {

  def behavior(persistenceId: String)(implicit database: Database, actorSystem: ActorSystem): Behavior[BoardCommand] = PersistentBehaviors
    .receive[BoardCommand, BoardEvent, Option[states.Board]](
    persistenceId, None,
    (board, command) => command.validate(persistenceId, board)
      .fold(error => Effect.none.thenRun(_ => command.replyTo ! Left(error)),
        event => Effect.persist(event).thenRun(_ => command.replyTo ! Right(event)))
    , //ici on valide les commande et retourne le résultat à l'envoyeur
    (board, event) => BoardEventHandler.handle(event)(board) // ici on utilise l'event handler pour modifier l'état courant
  )
}

//ici on sauvegarde le snapshot