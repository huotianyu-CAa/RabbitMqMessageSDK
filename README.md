# 1 本地skd打包方式
## 1.1 设置
File ——》 Project Structure ——》 Project Settings ——》 Artifacts ——》 右栏左上角+ ——》JAR ——》 From Modules with dependencies——》OK
直接确定无需指定主类
不用更改 点击apply

## 1.2 构建
Build ——》 Build Artifacts

Build（第一次构建）
Rebuild（重新构建，会先自定清理上次构建jar包）
Clean（清理构建好的jar包）

jar生成在out文件夹下

## 1.3 maven 引入本地sdk包
```
mvn install:install-file -Dmaven.repo.local=/Users/huotianyu/developTool/maven-repository -DgroupId=com.code -DartifactId=RabbitMqMessageSDK -Dversion=1.0.0 -Dpackaging=jar -Dfile=/Users/huotianyu/code/hqq-project/RabbitMqMessageSDK/out/artifacts/RabbitMqMessageSDK_jar/RabbitMqMessageSDK.jar
```

命令参数说明：

- Dmaven.repo.local   指定的maven仓库的地址

- DgroupId            指maven创库存放jar包的路径, 也是pom文件中groupId：标签中的值；

- DartifactId         指是pom文件中artifactId标签中的值

- Dversion            指版本号；
- Dfile               指当前jar所存放的位置

# 2 skd使用说明
## 2.1 在pom中引入依赖

```java
<dependency>
      <groupId>com.code</groupId>
      <artifactId>RabbitMqMessageSDK</artifactId>
      <version>1.0.0</version>
</dependency>
```

## 2.2 rabbitmq连接池以及配置初始化

```java

    private DefaultEventController controller;
    private ProducerTemplate producerTemplate;

    @Bean
    public void init() throws Exception {
        MqConfig config = new MqConfig("serverHost",
                "username",
                "password", "virtualHost");
        controller = DefaultEventController.getInstance(config);
        producerTemplate = controller.getEopEventTemplate();
    }

    /**
     * 用于测试rabbitmq消息发送与回调
     * @return
     * @throws Exception
     */
    @GetMapping("/TestMessageAck1")
    public String TestMessageAck1() throws Exception {
        List<MessageContentItemEntity> list = new ArrayList<>();
        MessageContentItemEntity item = new MessageContentItemEntity(1, "123", "23");
        list.add(item);
        MessageEntity po = new MessageEntity();
        po.setTemplateId("1");
        po.setTitle("test11");
        po.setTimestamp(LocalDateTime.now().toString());
        po.setItemList(list);
        String jsonString = JSONObject.toJSONString(po);
        MessageEntity po1 = JSONObject.parseObject(jsonString, MessageEntity.class);

        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationId = new CorrelationData(messageId);
        // 统一发送方法，不限制message类型
        producerTemplate.send("key", "amq.direct", po1.toString(), correlationId);
        // 统一发送方法，限制message类型为MessageEntity
        producerTemplate.sendMessageEntity("key", "amq.direct", po, correlationId);
        return "ok";
    }
```
## 2.3 实体类说明
### 2.3.1 MessageEntity
该实体类为告警消息实体类，向实体类中填充相应字段

- **如果项目中已有rabbitmq实例，可以不使用SDK的统一消息发送方法，但注意将MessageEntity对象转换为JSONObject字符串之后发送到消息队列**
```java
public class MessageEntity {
    // 必填 模版id
    String templateId;
    // 必填 标题
    String title;
    String level;
    String ip;
    //必填 时间
    String timestamp;
    String reason;
    String traceId;
    Boolean isImportant;
    List<String> notifiedArray;
    List<MessageContentItemEntity> itemList;
}
```
### 2.3.2 MessageContentItemEntity
该实体类用于告警消息中自定义字段，消费者处理时会循环获取，并将key作为字段名称，value作为字段值
```java
public class MessageContentItemEntity implements Serializable {
    Integer idx;
    String key;
    String value;
}
```





