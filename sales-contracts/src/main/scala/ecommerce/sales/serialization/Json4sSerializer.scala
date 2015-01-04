package ecommerce.sales.serialization

import java.nio.ByteBuffer
import java.nio.charset.Charset

import ecommerce.sales.{SalesValueObjects, ReservationEvents}
import org.json4s._
import org.json4s.ext.{JodaTimeSerializers, UUIDSerializer}
import org.json4s.native.Serialization.{read, write}
import pl.newicom.dddd.messaging.MetaData

class Json4sSerializer extends akka.serialization.Serializer {

  val UTF8 = Charset.forName("UTF-8")
  val Identifier: Int = ByteBuffer.wrap("json4s".getBytes(UTF8)).getInt

  def identifier = Identifier

  implicit val formats: Formats = DefaultFormats ++
    JodaTimeSerializers.all + UUIDSerializer +
    ReservationEvents + SalesValueObjects + new ShortTypeHints(List(classOf[MetaData]))

  def includeManifest = true

  override def fromBinary(bytes: Array[Byte], manifestOpt: Option[Class[_]]) = {
    implicit val manifest = manifestOpt match {
      case Some(x) => Manifest.classType[AnyRef](x)
      case None    => Manifest.AnyRef
    }
    read(new String(bytes, UTF8))
  }

  override def toBinary(o: AnyRef) = {
    write(o).getBytes(UTF8)
  }

}