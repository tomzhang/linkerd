package io.buoyant.k8s

import com.twitter.finagle.{Filter, Http, Stack}
import com.twitter.finagle.http.{Request, Response}
import io.buoyant.k8s
import scala.io.Source

/**
 * Meant to be implemented by configuration classes that need to
 * produce a k8s API client.
 */
trait ClientConfig {
  import ClientConfig._

  def host: Option[String]
  def portNum: Option[Int]

  protected def getHost = host.getOrElse(DefaultHost)

  protected def getPort = portNum.getOrElse(DefaultPort)

  protected def dst = s"/$$/inet/$getHost/$getPort"

  protected def mkClient(
    params: Stack.Params = Stack.Params.empty
  ) = {
    val setHost = new SetHostFilter(getHost, getPort)
    Http.client.withParams(Http.client.params ++ params)
      .withStreaming(true)
      .filtered(setHost)
  }
}

object ClientConfig {
  val DefaultHost = "localhost"
  val DefaultNamespace = "default"
  val DefaultPort = 8001
}
