package com.yiziton.dataimport.demo.dao;


import com.yiziton.commons.vo.demo.DemoBean;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoMapper {
    int insert(DemoBean bean);
}