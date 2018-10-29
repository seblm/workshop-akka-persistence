package serialization

import akka.persistence.pg.JsonString
import event.{BoardEvent, TicketEvent}
import play.api.libs.json.{JsObject, Json}

class EventJsonEncoder extends akka.persistence.pg.event.JsonEncoder {

  override def toJson: PartialFunction[Any, JsonString] = {
    case e: BoardEvent =>
      import event.BoardEvent._
      JsonString(Json.toJson(e).toString())
  }

  override def fromJson: PartialFunction[(JsonString, Class[_]), AnyRef] = {
    case (jsonEvent, classOf[BoardEvent]) =>
      import event.BoardEvent._
      Json.fromJson(JsObj ect())
  }

}
