package ecommerce

import pl.newicom.dddd.office.RemoteOfficeId

package object invoicing {

  implicit object InvoicingOfficeId extends RemoteOfficeId("Invoice", "Invoicing")

}
