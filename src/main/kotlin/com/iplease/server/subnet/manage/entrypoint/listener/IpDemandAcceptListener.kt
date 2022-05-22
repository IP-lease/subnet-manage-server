package com.iplease.server.subnet.manage.entrypoint.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.iplease.lib.messa.error.data.global.WrongPayloadError
import com.iplease.lib.messa.error.data.ip.demand.status.IpDemandStatusAcceptError
import com.iplease.lib.messa.error.data.global.UnknownError
import com.iplease.lib.messa.error.type.GlobalErrorTypeV1
import com.iplease.lib.messa.error.type.IpDemandErrorTypeV1
import com.iplease.lib.messa.event.data.ip.demand.status.IpDemandStatusAcceptEvent
import com.iplease.lib.messa.event.type.IpDemandEventTypeV1
import com.iplease.server.subnet.manage.service.IpValidService
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class IpDemandAcceptListener(
    val ipValidService: IpValidService,
    val rabbitTemplate: RabbitTemplate
) {
    companion object { const val QUEUE_NAME = "server.subnet.manage" }
    val LOGGER = LoggerFactory.getLogger(this::class.java)
    val objectMapper = ObjectMapper().registerKotlinModule()

    @RabbitListener(queues = [QUEUE_NAME])
    fun listen(@Payload payload: String, message: Message) {
        if(!message.messageProperties.receivedRoutingKey.equals(IpDemandEventTypeV1.IP_DEMAND_STATUS_ACCEPT.routingKey)) return
        LOGGER.info("event listened!")
        LOGGER.info("- queue : $QUEUE_NAME")
        LOGGER.info("- event : ${IpDemandEventTypeV1.IP_DEMAND_STATUS_ACCEPT.routingKey}")

        process(payload).subscribe()
    }

    private fun process(payload: String) =
        payload.toMono()
            .map { objectMapper.readValue(payload, IpDemandEventTypeV1.IP_DEMAND_STATUS_ACCEPT.eventPayloadType.java) as IpDemandStatusAcceptEvent }
            .map { ipValidService.checkIp(it.demandedIp) }
            .doFirst{ LOGGER.trace("handling event...")}
            .doOnSuccess{ LOGGER.trace("handling complete!") }
            .doOnError{
                LOGGER.warn("error occurred during handle event!")
                LOGGER.warn("- error : ${it.javaClass.simpleName}")
                LOGGER.warn("- cause : ${it.message}")
                onError(payload)
            }.onErrorResume { Mono.empty() }

    private fun onError(payload: String) {
        payload.toMono()
            .readEvent()
            .publishError()
            .doOnError{ sendUnknownError() }
            .onErrorResume { Mono.empty() }
            .subscribe()
    }

    private fun Mono<String>.readEvent(): Mono<IpDemandStatusAcceptEvent> {
        return map { objectMapper.readValue(it, IpDemandEventTypeV1.IP_DEMAND_STATUS_ACCEPT.eventPayloadType.java) }
            .map { it as IpDemandStatusAcceptEvent }
            .doOnError { sendWrongPayloadError() }
            .onErrorResume { Mono.empty() }
    }

    private fun Mono<IpDemandStatusAcceptEvent>.publishError(): Mono<Unit> =
        map { IpDemandStatusAcceptError(it.demandUuid, it.issuerUuid, it.managerUuid, it.demandedIp) }
            .map { objectMapper.writeValueAsString(it) }
            .map{ rabbitTemplate.convertAndSend(IpDemandErrorTypeV1.IP_DEMAND_STATUS_ACCEPT.routingKey, it) }

    private fun sendWrongPayloadError() = WrongPayloadError("IpDemandAcceptEvent 구독중, 잘못된 Payload로 인한 오류가 발생하였습니다!")
            .let { objectMapper.writeValueAsString(it) }
            .let { rabbitTemplate.convertAndSend(GlobalErrorTypeV1.WRONG_PAYLOAD.routingKey, it) }
    private fun sendUnknownError() = UnknownError("IpDemandAcceptEvent 구독중, 알 수 없는 오류가 발생하였습니다!")
            .let { objectMapper.writeValueAsString(it) }
            .let { rabbitTemplate.convertAndSend(GlobalErrorTypeV1.UNKNOWN_ERROR.routingKey, it) }

}