package ecommerce.tests.e2e

import io.restassured.RestAssured._
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.HttpClientConfig
import io.restassured.config.HttpClientConfig.HttpClientFactory
import io.restassured.filter.log.LogDetail
import io.restassured.module.scala.RestAssuredSupport.AddThenToResponse
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.SystemDefaultHttpClient
import org.apache.http.params.HttpConnectionParams
import org.json4s.Formats
import org.json4s.native.Serialization.write
import org.scalatest.{Matchers, WordSpecLike}
import pl.newicom.dddd.aggregate.Command

trait TestDriver extends WordSpecLike with Matchers {

  val clientConfig: HttpClientConfig = config.getHttpClientConfig
    .httpClientFactory(new HttpClientFactory() {
      override def createHttpClient: HttpClient = {
        val rv         = new SystemDefaultHttpClient
        val httpParams = rv.getParams
        HttpConnectionParams.setConnectionTimeout(httpParams, 2 * 1000) //Wait 5s for a connection
        HttpConnectionParams.setSoTimeout(httpParams, 60 * 1000)        // Default session is 60s
        rv
      }
    })
    .reuseHttpClientInstance()

  def using[R](endpoint: EndpointConfig)(testBody: RequestSpecBuilder => R): R = {
    testBody(
      new RequestSpecBuilder()
        .setConfig(config.httpClient(clientConfig))
        .setBaseUri(endpoint.toUrl)
        .setContentType("application/json")
        .log(LogDetail.ALL)
    )
  }

  def POST(implicit builder: RequestSpecBuilder): POSTOps =
    new POSTOps(builder.build())

  def GET(implicit builder: RequestSpecBuilder): GETOps =
    new GETOps(builder.build())

  class POSTOps(reqSpec: RequestSpecification) {

    def command(c: Command)(implicit formats: Formats): ValidatableResponse =
      given(reqSpec)
        .body(write(c))
        .header("Command-Type", c.getClass.getName)
      .post()
      .Then()
        .log().all()
        .statusCode(200)
  }

  class GETOps(reqSpec: RequestSpecification) {

    def /(subPath: String): ValidatableResponse =
      given(reqSpec)
        .get(subPath)
        .Then()
        .log().all()
        .statusCode(200)
  }

}
