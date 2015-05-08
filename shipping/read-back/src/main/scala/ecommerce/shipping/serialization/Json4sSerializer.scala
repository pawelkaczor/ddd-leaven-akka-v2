package ecommerce.shipping.serialization

import java.nio.ByteBuffer
import java.nio.charset.Charset

import akka.actor.{ActorRef, ExtendedActorSystem}
import akka.serialization.Serialization
import ecommerce.shipping.shippingOffice
import org.json4s.JsonAST.JString
import org.json4s._
import org.json4s.ext.{JodaTimeSerializers, UUIDSerializer}
import org.json4s.native.Serialization.{read, write}
import pl.newicom.dddd.delivery.protocol.Processed
import pl.newicom.dddd.delivery.protocol.alod.{Processed => AlodProcessed}
import pl.newicom.dddd.messaging.MetaData

class Json4sSerializer(sys: ExtendedActorSystem) extends akka.serialization.Serializer {

  val UTF8 = Charset.forName("UTF-8")
  val Identifier: Int = ByteBuffer.wrap("shipping-json4s".getBytes(UTF8)).getInt

  def identifier = Identifier

  implicit val formats: Formats = shippingOffice.serializationHints ++ DefaultFormats ++
    JodaTimeSerializers.all + UUIDSerializer + ActorRefSerializer
      FullTypeHints(List(
        classOf[MetaData],
        Class.forName("akka.persistence.PersistentImpl"),
        classOf[Processed],
        classOf[AlodProcessed]
      ))

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

  object ActorRefSerializer extends CustomSerializer[ActorRef](format => (
    {
      case JString(s) => sys.provider.resolveActorRef(s)
    },
    {
      case x: ActorRef => JString(Serialization.serializedActorPath(x))
    }
    ))


}