package ecommerce.invoicing

import ecommerce.sales.Money
import org.json4s.{Formats, FullTypeHints}
import pl.newicom.dddd.messaging.event.AggregateSnapshotId
import pl.newicom.dddd.serialization.{JsonExtraSerHints, JsonSerializationHintsProvider}

class InvoicingSerializationHintsProvider extends JsonSerializationHintsProvider {

  val typeHints = InvoicingCommands + InvoicingEvents + InvoicingValueObjects

  object InvoicingCommands extends FullTypeHints(
    List(
      classOf[CreateInvoice],
      classOf[ReceivePayment]
    ))

  object InvoicingEvents extends FullTypeHints(
    List(
      classOf[InvoiceCreated],
      classOf[OrderBilled],
      classOf[PaymentExpired],
      classOf[OrderBillingFailed]
    ))

  object InvoicingValueObjects extends FullTypeHints(
    List(
      classOf[AggregateSnapshotId],
      classOf[Money]
    )
  )

  override def hints(default: Formats) = JsonExtraSerHints(typeHints, List())
}
