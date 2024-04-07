package com.code.entity;


import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author huotianyu
 * date 2024.4.1
 * @deprecated 告警消息统一发送格式
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity implements Serializable {

    /**
     * 模版id
     */
    String templateId;

    /**
     * 标题
     */
    String title;

    /**
     * 报警级别
     */
    String level;

    /**
     * 所在机器ip
     */
    String ip;

    /**
     * 报警时间
     */
    String timestamp;

    /**
     * 报警原因
     */
    String reason;

    /**
     * traceId
     */
    String traceId;

    /**
     * 用于微信的@操作
     * 是否@人
     */
    Boolean isImportant;

    /**
     * 用于微信的@操作
     * 需要被@的人
     */
    List<String> notifiedArray;

    /**
     * 自定义报警属性
     */
    List<MessageContentItemEntity> itemList;

}
