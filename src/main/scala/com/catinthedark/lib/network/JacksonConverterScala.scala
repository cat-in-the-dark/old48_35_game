package com.catinthedark.lib.network

import java.io.IOException

import com.catinthedark.lib.network.IMessageBus.Wrapper
import com.catinthedark.lib.network.messages.{DisconnectedMessage, GameStartedMessage}
import com.fasterxml.jackson.databind.ObjectMapper

import scala.collection.mutable

class JacksonConverterScala(private val objectMapper: ObjectMapper) extends NetworkTransport.Converter {
  private val converters = new mutable.HashMap[String, Map[String, Any] => Any]()
  
  registerConverter[GameStartedMessage](classOf[GameStartedMessage], data => {
    val message = new GameStartedMessage()
    message.setRole(data("role").asInstanceOf[String])
    message.setClientID(data("clientID").asInstanceOf[String])
    message
  })

  registerConverter[DisconnectedMessage](classOf[DisconnectedMessage], data => {
    val message = new DisconnectedMessage()
    message.setClientID(data.get("clientID").asInstanceOf[String])
    message
  })
  
  override def toJson(data: Any): String = {
    val wrapper = ScalaWrapper(data = data, className = data.getClass.getCanonicalName, sender = null)
    try {
      objectMapper.writeValueAsString(wrapper)
    } catch {
      case e: Exception =>
        throw new NetworkTransport.ConverterException(s"Can't convert to json $data : ${e.getMessage}", e)
    }
  }

  override def fromJson(json: String): Wrapper = {
    try {
      val wrapper = objectMapper.readValue(json, classOf[ScalaWrapper])
      val converter = converters.getOrElse(wrapper.className, throw new NetworkTransport.ConverterException(s"There is no ${wrapper.className} converter"))
      val data = converter.apply(wrapper.getData.asInstanceOf[Map[String, Any]])

      wrapper.copy(data = data)
    } catch {
      case e: IOException =>
        throw new NetworkTransport.ConverterException(s"Can't parse $json : ${e.getMessage}", e)
    }
  }
  
  def registerConverter[T](clazz: Class[T], converter: Map[String, Any] => T): JacksonConverterScala = {
    converters.put(clazz.getCanonicalName, converter)
    this
  }
  
  def registeredConverters = converters.keySet
}

case class ScalaWrapper(data: Any, className: String, sender: String = null) extends Wrapper {
  override def getData: AnyRef = data.asInstanceOf[AnyRef]

  override def getSender: String = sender 
}