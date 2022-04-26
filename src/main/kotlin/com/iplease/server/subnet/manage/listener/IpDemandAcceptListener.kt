package com.iplease.server.subnet.manage.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.subnet.manage.data.dto.IpDemandAcceptDto
import com.iplease.server.subnet.manage.data.dto.IpDemandAcceptFailureDto
import com.iplease.server.subnet.manage.service.IpValidService
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class IpDemandAcceptListener(
    val ipValidService: IpValidService,
    val rabbitTemplate: RabbitTemplate
) {
    companion object {
        const val QUEUE_NAME = "server.subnet.manage"
        const val EVENT_NAME = "v1.event.ip.demand.status.accept"
    }
    val LOGGER = LoggerFactory.getLogger(IpDemandAcceptListener::class.java)

    @RabbitListener(queues = [QUEUE_NAME])
    fun listen(@Payload payload: String, message: Message) {
        if(message.messageProperties.receivedRoutingKey.equals(EVENT_NAME)) {
            LOGGER.info("event listened!")
            LOGGER.info("- queue : $QUEUE_NAME")
            LOGGER.info("- event : $EVENT_NAME")
            try {
                LOGGER.info("handling event...")
                handle(payload)
                LOGGER.info("handling complete!")
            } catch (e: Throwable) {
                LOGGER.info("error occurred during handle event!")
                LOGGER.info("- error : ${e.javaClass.simpleName}")
                LOGGER.info("- cause : ${e.message}")
                onError(payload)
            }
        }
    }

    private fun onError(payload: String) {
        ObjectMapper().registerModule(KotlinModule())
            .readValue(payload, IpDemandAcceptDto::class.java)
            .run { IpDemandAcceptFailureDto(demandUuid, issuerUuid, managerUuid, demandedIp) }
            .let (ObjectMapper()::writeValueAsString)
            .let{ rabbitTemplate.convertAndSend("v1.error.ip.demand.status.accept", it) }
    }

    private fun handle(payload: String) {
        ObjectMapper().registerModule(KotlinModule())
            .readValue(payload, IpDemandAcceptDto::class.java)
            .demandedIp
            .let(ipValidService::checkIp)
    }
}