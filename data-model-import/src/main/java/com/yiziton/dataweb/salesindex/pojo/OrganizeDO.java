package com.yiziton.dataweb.salesindex.pojo;

import java.util.Date;

/**
 * organize 
 * @author huanghuai on 2019-07-17 11:13:33.
 */
public class OrganizeDO {
    /**
     * 主键
     */
    public String id;

    /**
     * 部门code
     */
    public String code;

    /**
     * 部门名称
     */
    public String name;

    /**
     * 隶属部门code
     */
    public String parent_code;

    /**
     * 排序
     */
    public Integer sort;

    /**
     * 层级
     */
    public Integer level;

    /**
     * 是否外部
     */
    public Boolean external;

    /**
     * 外部部门类型，1(customer/客户)、3(carrier/承运商)、2(service/服务商)
     */
    public Integer external_type;

    /**
     * 外部部门类型，CUSTOMER(客户)、CARRIER(承运商)、MASTER(service/服务商)
     */
    public String external_type_name;

    /**
     * 地址区域code
     */
    public String area_code;

    /**
     * 详细地址
     */
    public String address_detailed;

    /**
     * 完整地址
     */
    public String address_complete;

    /**
     * 租户编码
     */
    public String lessee_code;

    /**
     * 状态，2(del/删除)、1(normal/正常)
     */
    public Integer status;

    /**
     * 新建时间，与SCM 的保持一致
     */
    public Date create_time;

    /**
     * 更新时间
     */
    public Date update_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_code() {
        return parent_code;
    }

    public void setParent_code(String parent_code) {
        this.parent_code = parent_code;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public Integer getExternal_type() {
        return external_type;
    }

    public void setExternal_type(Integer external_type) {
        this.external_type = external_type;
    }

    public String getExternal_type_name() {
        return external_type_name;
    }

    public void setExternal_type_name(String external_type_name) {
        this.external_type_name = external_type_name;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getAddress_detailed() {
        return address_detailed;
    }

    public void setAddress_detailed(String address_detailed) {
        this.address_detailed = address_detailed;
    }

    public String getAddress_complete() {
        return address_complete;
    }

    public void setAddress_complete(String address_complete) {
        this.address_complete = address_complete;
    }

    public String getLessee_code() {
        return lessee_code;
    }

    public void setLessee_code(String lessee_code) {
        this.lessee_code = lessee_code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}