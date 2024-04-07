package com.code.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author huotianyu
 * date 2024.4.1
 * @deprecated 告警消息自定义属性
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageContentItemEntity implements Serializable {

    /**
     * 索引标识
     */
    Integer idx;

    /**
     * 字段名称
     */
    String key;

    /**
     * 字段值
     */
    String value;

}
