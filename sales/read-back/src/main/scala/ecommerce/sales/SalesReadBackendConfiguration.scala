package ecommerce.sales

import pl.newicom.dddd.view.sql.SqlViewStoreConfiguration
import pl.newicom.eventstore.EventStoreProvider

trait SalesReadBackendConfiguration extends SqlViewStoreConfiguration with EventStoreProvider
