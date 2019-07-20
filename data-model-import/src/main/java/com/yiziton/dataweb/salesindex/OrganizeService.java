package com.yiziton.dataweb.salesindex;

import com.yiziton.dataweb.salesindex.pojo.OrganizeDO;
import com.yiziton.dataweb.waybill.dao.OrganizeMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 钻取明细controller
 *
 * @author HuangHuai on 2019-07-17 09:53
 */
@Service
public class OrganizeService implements InitializingBean {

    List<OrganizeDO>        all       = new ArrayList<>();
    Map<String, OrganizeDO> codeToAll = new HashMap<>();
    @Autowired
    OrganizeMapper organizeMapper;

    public List<OrganizeDO> queryAll(boolean flush) {
        if (flush || all.isEmpty()) {
            cacheAll(flush);
        }
        return all;
    }

    private void cacheAll(boolean forceFlush) {
        Integer i = 1;
        synchronized (all) {
            if (forceFlush) {
                all.clear();
            }

            if (all.isEmpty()) {
                //过滤null 和status!=1的
                all = organizeMapper.queryAll().stream().filter(e -> CommonUtil.isStringNotEmpty(e.code) && i.equals(e.status)).collect(Collectors.toList());
                for (OrganizeDO organizeDO : all) {
                    codeToAll.put(organizeDO.code, organizeDO);
                }
            }
        }
    }

    /**
     * 吧组织架构全表缓存到map
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        queryAll(true);
    }

    /**
     * 获取所有子孙
     *
     * @param code        组织架构中的自身code
     * @param includeSelf 是否包含自己
     * @return
     */
    public List<OrganizeDO> getAllDescendantCodes(String code, boolean includeSelf) {
        if (CommonUtil.isStringEmpty(code)) {
            return new ArrayList<>();
        }
        List<OrganizeDO> list = new ArrayList<>();
        getAllDescendantCodes(code, list);
        if (includeSelf) {
            OrganizeDO d = codeToAll.get(code);
            if (d != null) list.add(d);
        }
        return list;
    }

    /**
     * 递归遍历获取所有子孙对象，并放到list中
     */
    private void getAllDescendantCodes(String code, List<OrganizeDO> list) {
        List<OrganizeDO> allChildrens = getAllChildrens(code);
        if (!allChildrens.isEmpty()) {
            list.addAll(allChildrens);
            for (OrganizeDO allChildren : allChildrens) {
                getAllDescendantCodes(allChildren.code, list);
            }
        }
    }

    /**
     * 获取所有子，不包含自己，不包含孙
     */
    public List<OrganizeDO> getAllChildrens(String parent) {
        List<OrganizeDO> all = this.all.stream().filter(e -> parent.equals(e.parent_code) && !e.parent_code.equals(e.code))
                .collect(Collectors.toList());
        return all;
    }
}
