package ecommerce

import org.json4s.FullTypeHints
import org.json4s.ext.EnumSerializer
import pl.newicom.dddd.office.OfficeInfo
import pl.newicom.dddd.serialization.JsonSerializationHints

package object shipping {

  implicit val shippingOffice: OfficeInfo[ShippingOffice] = OfficeInfo("Shipping", new JsonSerializationHints {
      val typeHints = ShippingCommands + ShippingEvents
      val serializers = List(new EnumSerializer(ShippingStatus))

      object ShippingCommands extends FullTypeHints(
        List(
          classOf[CreateShipment]
        ))

      object ShippingEvents extends FullTypeHints(
        List(
          classOf[ShipmentCreated]
        ))
  })
}
