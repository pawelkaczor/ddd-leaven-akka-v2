package ecommerce.monitoring

import akka.kernel.Bootable
import kamon.Kamon

class MonitoringRunner extends Bootable {

  override def startup(): Unit = {
    Kamon.start()
  }

  override def shutdown(): Unit = {
    Kamon.shutdown()
  }
}
