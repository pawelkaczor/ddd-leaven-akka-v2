package ecommerce.tests.e2e

import pl.newicom.dddd.office.OfficeInfo

case class EndpointConfig(host: String = "localhost", officeInfo: OfficeInfo[_], path: String = "", port: Int = 80) {

  def withSubPath(subPath: String) = copy(path = s"$path/$subPath")

  def /(subPath: String) = withSubPath(subPath)

  def serializationHints = officeInfo.serializationHints

  def toUrl = s"http://$host:$port/$path"
}
