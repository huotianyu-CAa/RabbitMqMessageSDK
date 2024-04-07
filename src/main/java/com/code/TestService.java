package com.code;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSONObject;
import com.code.entity.MessageContentItemEntity;
import com.code.entity.MessageEntity;
import com.code.global.DefaultEventController;
import com.code.global.MqConfig;
import com.code.producer.ProducerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class TestService {
    private DefaultEventController controller;
    private ProducerTemplate producerTemplate;
    @Bean
    public void init() throws Exception {
       MqConfig config=new MqConfig("192.168.110.118","test","123456","localtest");
       controller=DefaultEventController.getInstance(config);
       log.info("test");
       producerTemplate =controller.getEopEventTemplate();
    }

    /**
     * 用于测试rabbitmq消息发送与回调
     * @return
     * @throws Exception
     */
    @GetMapping("/TestMessageAck")
    public String TestMessageAck() throws Exception {
        List<MessageContentItemEntity> list = new ArrayList<>();
        MessageContentItemEntity item = new MessageContentItemEntity(1, "123", "23");
        list.add(item);
        MessageEntity po = new MessageEntity();
        po.setTemplateId("1");
        po.setTitle("test");
        po.setTimestamp(LocalDate.now().toString());
        po.setItemList(list);
        String jsonString = JSONObject.toJSONString(po);
        MessageEntity po1 = JSONObject.parseObject(jsonString, MessageEntity.class);

//        CorrelationData correlationId = new CorrelationData(String.valueOf(1112));
        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationId = new CorrelationData(messageId);
//        producerTemplate.send("testtest","alert",po1.toString(),correlationId);
        producerTemplate.send("key", "amq.direct", po1.toString(), correlationId);
        producerTemplate.sendMessageEntity("key", "amq.direct", po, correlationId);
        return "ok";
    }

}
