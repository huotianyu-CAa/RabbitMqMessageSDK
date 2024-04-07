package com.code.global;


import com.code.producer.ProducerTemplateService;
import com.code.producer.ProducerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.StringUtils;

/**
 * 和rabbitmq通信的控制器，主要负责：
 * <p>1、和rabbitmq建立连接</p>
 * <p>2、声明exChange和queue以及它们的绑定关系</p>
 * <p>3、启动消息监听容器，并将不同消息的处理者绑定到对应的exchange和queue上</p>
 * <p>4、持有消息发送模版以及所有exchange、queue和绑定关系的本地缓存</p>
 *
 * @author huotianyu
 * date 2024.4.2
 */
@Slf4j
public class DefaultEventController implements EventController {

    private CachingConnectionFactory connectionFactory;

    private MqConfig config;

    private RabbitTemplate template;

    private ProducerTemplate producerTemplate;

    private static DefaultEventController defaultEventController;

    /**
     * 单例
     *
     * @param config
     * @return
     */
    public synchronized static DefaultEventController getInstance(MqConfig config) {
        if (defaultEventController == null) {
            defaultEventController = new DefaultEventController(config);
        }
        return defaultEventController;
    }

    /**
     * 构造方法，初始化Connection和RabbitTemplate
     *
     * @param config
     */
    private DefaultEventController(MqConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Config can not be null.");
        }
        this.config = config;
        initConnectionFactory();
        template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true);
        template.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                log.info("ReturnCallback 消息：{},回应码：{},回应信息：{},交换机：{},路由键：", message, i, s, s1, s2);
            }
        });

        producerTemplate = new ProducerTemplateService(template);
    }

    /**
     * 初始化rabbitmq连接
     */
    private void initConnectionFactory() {
        connectionFactory = new CachingConnectionFactory();
        if (config.getServerHost().contains(",")) {
            connectionFactory.setAddresses(config.getServerHost());
        } else {
            connectionFactory.setHost(config.getServerHost());
            connectionFactory.setPort(config.getPort());
        }
        connectionFactory.setChannelCacheSize(config.getEventMsgProcessNum());

        connectionFactory.setUsername(config.getUsername());
        connectionFactory.setPassword(config.getPassword());
        if (!StringUtils.isEmpty(config.getVirtualHost())) {
            connectionFactory.setVirtualHost(config.getVirtualHost());
        }
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
    }


    public ProducerTemplate getEopEventTemplate() {
        return producerTemplate;
    }

}
