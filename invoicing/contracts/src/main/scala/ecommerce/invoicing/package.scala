package ecommerce

import pl.newicom.dddd.office.RemoteOfficeId

package object invoicing {

  implicit object InvoicingOfficeId extends RemoteOfficeId[invoicing.Command]("Invoice", "Invoicing", classOf[invoicing.Command])

}
