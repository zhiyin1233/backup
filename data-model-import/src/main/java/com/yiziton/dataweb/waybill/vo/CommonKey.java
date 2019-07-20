package com.yiziton.dataweb.waybill.vo;

import java.util.Objects;

/**
 * @Description: 获取下拉查询列表传参 ： key
 * @Author: kdh
 * @Date: 2018/12/12
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public enum CommonKey {

    SALESMAN(0, "业务员"),
    CONSIGNOR(1, "发货人"),
    RECEVICE(2, "收货人"),
    SECOND_CUSTOMER(3, "二级商家"),
    OPEN_BILL_NODE(4, "开单网点"),
    MASTER(5, "安装师傅"),
    MASTER_NODE(6, "末端网点"),//原 安装师傅所属网点
    CARRIER(7, "承运商");

    private Integer code;
    private String message;

    CommonKey(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据code获取Enum对象
     *
     * @param code Integer
     * @return WaybilStatus
     */
    public static CommonKey getEnumByCode(Integer code) {
        if (Objects.nonNull(code)) {
            for (CommonKey value : CommonKey.values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * 根据String(Enum名)获取Enum对象(转换成枚举对象)
     *
     * @param enumName String
     * @return WaybilStatus
     */
    public static CommonKey getEnumByEnumNameString(String enumName) {
        for (CommonKey value : CommonKey.values()) {
            if (value.name().equals(enumName)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据String(Massage名)获取Enum对象
     *
     * @param massageName String
     * @return WaybilStatus
     */
    public static CommonKey getEnumByMassageString(String massageName) {
        for (CommonKey value : CommonKey.values()) {
            if (value.message.equals(massageName)) {
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return this.message;
    }
}
