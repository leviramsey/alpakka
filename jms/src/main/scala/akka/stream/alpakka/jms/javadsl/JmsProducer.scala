/*
 * Copyright (C) 2016-2018 Lightbend Inc. <http://www.lightbend.com>
 */

package akka.stream.alpakka.jms.javadsl

import java.util.concurrent.CompletionStage

import akka.stream.alpakka.jms.{scaladsl, JmsMessage, JmsProducerSettings}
import akka.stream.alpakka.jms.JmsProducerMessage._
import akka.stream.javadsl.Source
import akka.stream.scaladsl.{Flow, Keep}
import akka.{Done, NotUsed}

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters

object JmsProducer {

  /**
   * Java API: Creates an [[JmsProducer]] for [[JmsMessage]]s
   */
  def flow[R <: JmsMessage](
      settings: JmsProducerSettings
  ): akka.stream.javadsl.Flow[R, R, JmsProducerStatus] =
    akka.stream.alpakka.jms.scaladsl.JmsProducer.flow(settings).mapMaterializedValue(toProducerStatus).asJava

  /**
   * Java API: Creates an [[JmsProducer]] for [[Envelope]]s
   */
  def flexiFlow[R <: JmsMessage, PassThrough](
      settings: JmsProducerSettings
  ): akka.stream.javadsl.Flow[Envelope[R, PassThrough], Envelope[R, PassThrough], JmsProducerStatus] =
    akka.stream.alpakka.jms.scaladsl.JmsProducer
      .flexiFlow[R, PassThrough](settings)
      .mapMaterializedValue(toProducerStatus)
      .asJava

  /**
   * Java API: Creates an [[JmsProducer]] for [[JmsMessage]]s
   */
  def create[R <: JmsMessage](
      settings: JmsProducerSettings
  ): akka.stream.javadsl.Sink[R, CompletionStage[Done]] =
    akka.stream.alpakka.jms.scaladsl.JmsProducer
      .apply(settings)
      .mapMaterializedValue(FutureConverters.toJava)
      .asJava

  /**
   * Java API: Creates an [[JmsProducer]] for strings
   */
  def textSink(settings: JmsProducerSettings): akka.stream.javadsl.Sink[String, CompletionStage[Done]] =
    akka.stream.alpakka.jms.scaladsl.JmsProducer
      .textSink(settings)
      .mapMaterializedValue(FutureConverters.toJava)
      .asJava

  /**
   * Java API: Creates an [[JmsProducer]] for bytes
   */
  def bytesSink(settings: JmsProducerSettings): akka.stream.javadsl.Sink[Array[Byte], CompletionStage[Done]] =
    akka.stream.alpakka.jms.scaladsl.JmsProducer
      .bytesSink(settings)
      .mapMaterializedValue(FutureConverters.toJava)
      .asJava

  /**
   * Java API: Creates an [[JmsProducer]] for maps with primitive datatypes as value
   */
  def mapSink(
      settings: JmsProducerSettings
  ): akka.stream.javadsl.Sink[java.util.Map[String, Any], CompletionStage[Done]] = {

    val scalaSink =
      akka.stream.alpakka.jms.scaladsl.JmsProducer
        .mapSink(settings)
        .mapMaterializedValue(FutureConverters.toJava)
    val javaToScalaConversion =
      Flow.fromFunction((javaMap: java.util.Map[String, Any]) => javaMap.asScala.toMap)
    javaToScalaConversion.toMat(scalaSink)(Keep.right).asJava
  }

  /**
   * Java API: Creates an [[JmsProducer]] for serializable objects
   */
  def objectSink(
      settings: JmsProducerSettings
  ): akka.stream.javadsl.Sink[java.io.Serializable, CompletionStage[Done]] =
    akka.stream.alpakka.jms.scaladsl.JmsProducer
      .objectSink(settings)
      .mapMaterializedValue(FutureConverters.toJava)
      .asJava

  private def toProducerStatus(scalaStatus: scaladsl.JmsProducerStatus) = new JmsProducerStatus {

    override def connectorState: Source[JmsConnectorState, NotUsed] =
      scalaStatus.connectorState.map(_.asJava).asJava
  }
}
