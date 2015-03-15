package ecommerce

import ecommerce.invoicing.Invoice.{CreateInvoice, InvoiceCreated, PaymentReceived, ReceivePayment}
import ecommerce.sales.Money
import org.json4s.FullTypeHints
import pl.newicom.dddd.messaging.event.AggregateSnapshotId
import pl.newicom.dddd.office.OfficeInfo
import pl.newicom.dddd.serialization.JsonSerializationHints

package object invoicing {

  implicit val invoicingOffice: OfficeInfo[Invoice] = OfficeInfo("Invoicing", new JsonSerializationHints {

    override val typeHints = InvoicingCommands + InvoicingEvents + InvoicingValueObjects
    override val serializers = List()

    object InvoicingCommands extends FullTypeHints(
      List(
        classOf[CreateInvoice],
        classOf[ReceivePayment]
      ))

    object InvoicingEvents extends FullTypeHints(
      List(
        classOf[InvoiceCreated],
        classOf[PaymentReceived]
      ))

    object InvoicingValueObjects extends FullTypeHints(
      List(
        classOf[AggregateSnapshotId],
        classOf[Money]
      )
    )

  })
}
