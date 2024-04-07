package com.code;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSONObject;
import com.code.global.DefaultEventController;
import com.code.global.MqConfig;
import com.code.entity.MessageContentItemEntity;
import com.code.entity.MessageEntity;
import com.code.producer.ProducerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RabbitTest {

    private DefaultEventController controller;
    private ProducerTemplate producerTemplate;

    @Before
    public void init() throws Exception {
//        MqConfig config=new MqConfig("192.168.110.118","test","123456","localtest");
//        controller=DefaultEventController.getInstance(config);
//        log.info("test");
//        producerTemplate =controller.getEopEventTemplate();

        MqConfig config = new MqConfig("amqp-cn-lbj3er9d7005.cn-zhangjiakou.amqp-1.net.mq.amqp.aliyuncs.com",
                "MjphbXFwLWNuLWxiajNlcjlkNzAwNTpMVEFJNEdKTFZpbUJRb3ltV3VaWHFCN2c=",
                "NzY4MkEwM0EyNTEzQTE5RDAyRTA5QjI1MUMxMTA4MTM1RDdDQzQzODoxNjk5Nzc4MDcwNzY1", "dev");
        controller = DefaultEventController.getInstance(config);
        producerTemplate = controller.getEopEventTemplate();


    }

    @Test
    public void test() throws Exception {
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

        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationId = new CorrelationData(messageId);
//        producerTemplate.send("testtest","alert",po1.toString(),correlationId);
//        producerTemplate.send("key", "amq.direct", po1.toString(), correlationId);
        producerTemplate.sendMessageEntity("key", "amq.direct", po, correlationId);
    }
}
