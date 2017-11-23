package ecommerce

import pl.newicom.dddd.aggregate.AggregateId
import pl.newicom.dddd.office.RemoteOfficeId

package object invoicing {

  type InvoiceId = AggregateId

  implicit object InvoicingOfficeId extends RemoteOfficeId[invoicing.Command]("Invoice", "Invoicing", classOf[invoicing.Command])

}
