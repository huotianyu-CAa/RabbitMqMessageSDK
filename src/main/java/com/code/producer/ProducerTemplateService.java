package com.code.producer;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.code.entity.MessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class ProducerTemplateService implements ProducerTemplate, RabbitTemplate.ConfirmCallback {

    private RabbitTemplate rabbitTemplate;

    public ProducerTemplateService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(this);
    }


    @Override
    public void send(String queueName, String exchangeName, Object eventContent, CorrelationData correlationData) throws Exception {
        rabbitTemplate.convertAndSend(exchangeName, queueName, eventContent, correlationData);
        log.info("mqMessage:{}",eventContent);
    }

    @Override
    public void sendMessageEntity(String queueName, String exchangeName, MessageEntity entity, CorrelationData correlationData) throws Exception {
        if (StrUtil.isBlank(entity.getTemplateId())) {
            throw new Exception("告警消息模版id不能为空");
        }
        if (StrUtil.isBlank(entity.getTitle())) {
            throw new Exception("告警消息标题不能为空");
        }
        if (StrUtil.isBlank(entity.getTimestamp())) {
            throw new Exception("告警消息时间不能为空");
        }
        String jsonString = JSONObject.toJSONString(entity);
        rabbitTemplate.convertAndSend(exchangeName, queueName, jsonString, correlationData);
        log.info("mqMessage:{}",jsonString);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if (b) {
            log.info("告警消息发送到 exchange 成功，id: {}", correlationData.getId());
        } else {
            log.error("告警消息发送到 exchange 失败，原因: {}", s);
        }
    }
}
