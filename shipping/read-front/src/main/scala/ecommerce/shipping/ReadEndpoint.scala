package ecommerce.shipping

import org.json4s.Formats
import pl.newicom.dddd.http.Endpoint
import pl.newicom.dddd.view.sql.SqlViewStore

abstract class ReadEndpoint(implicit formats: Formats) extends Endpoint[SqlViewStore]