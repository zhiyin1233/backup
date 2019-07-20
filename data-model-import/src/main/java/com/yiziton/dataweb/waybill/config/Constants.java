package com.yiziton.dataweb.waybill.config;

public class Constants {
    /**
     * 基础包名
     */
    public static final String PLATFORM_BASE_PACKAGE = "yiziton.basePackage";
    /**
     * 默认端口之mongodb
     */
    public static final int DEFAULT_PORT_MONGO = 27017;
    /**
     * 默认端口之mongodb
     */
    public static final int DEFAULT_PORT_REDIS = 6379;

    /**
     * 默认端口之rabbitmq
     */
    public static final int DEFAULT_PORT_AMQP = 5672;

    /**
     * 默认端口之stomp
     */
    public static final int DEFAULT_PORT_STOMP = 61613;

    /**
     * 动作地址映射
     */
    public static final String ACTION_URL_MAPPING = "/action/**";
    /**
     * 图视图地址映射
     */
    public static final String GRAPH_URL_MAPPING = "/graph";
}
