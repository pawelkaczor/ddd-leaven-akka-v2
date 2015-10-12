package ecommerce

import pl.newicom.dddd.office.OfficeInfo

package object invoicing {

  implicit val invoicingOffice: OfficeInfo[InvoicingOffice] = OfficeInfo("Invoice")
}
