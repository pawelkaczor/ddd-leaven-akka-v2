package ecommerce.tests.e2e

case class EndpointConfig(host: String = "localhost", path: String = "", port: Int = 80) {

  def withSubPath(subPath: String) = copy(path = s"$path/$subPath")

  def /(subPath: String) = withSubPath(subPath)

  def toUrl = s"http://$host:$port/$path"
}
