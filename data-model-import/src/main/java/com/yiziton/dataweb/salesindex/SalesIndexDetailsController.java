package com.yiziton.dataweb.salesindex;

import com.yiziton.dataweb.core.annotation.Action;
import com.yiziton.dataweb.salesindex.pojo.*;
import com.yiziton.dataweb.waybill.dao.ConsignorOpentimeMapper;
import com.yiziton.dataweb.waybill.dao.OrganizeMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 钻取明细controller
 *
 * @author HuangHuai on 2019-07-17 09:53
 */
@Action("salesIndexDetails")
public class SalesIndexDetailsController {


    @Autowired
    ConsignorOpentimeMapper consignorOpentimeMapper;

    @Autowired
    OrganizeMapper  organizeMapper;
    @Autowired
    OrganizeService organizeService;

    @Autowired
    SalesIndexService salesIndexService;

//    -----------------------------------钻取收入项明细接口start---------------------------------------------------

    /**
     * 收入项分布钻取接口：揽货费、运输费、安装费、保价费、基础送货费等明细查询 <br>
     * 查询条件是时间以及部门
     */
    public Object queryByIncome(SalesIndexQueryVO vo) {
        List<SalesIndexDO> list = getDataByPage(vo, salesIndexService.queryByDateAndDept(vo));
        List<IncomeAnalysisVO> collect = list.stream().map(e -> BeanUtil.copy(e, IncomeAnalysisVO.class)).collect(Collectors.toList());
        return collect;
    }

    private List<SalesIndexDO> getDataByPage(Page page, List<SalesIndexDO> list) {
        int start = (page.getPage() - 1) * 10;
        start = start < 0 ? 0 : start;
        int pageSize = page.getPageSize() < 1 ? 30 : page.getPageSize();

        int size = list.size();
        if (size > 0 && start < size) {
            int toIndex = start + pageSize;
            list = list.subList(start, toIndex >= size ? size : toIndex);
        } else {
            list = new ArrayList<>(0);
        }
        return list;
    }


    @Data
    public static class IncomeAnalysisVO extends SalesIndexDimension {
        public String waybill_id;
        public String customer_name;
        public String second_name;
        public String salesman;
    }
//    -----------------------------------钻取收入项明细接口end---------------------------------------------------


    //    -----------------------------------销售指标-运单查询start---------------------------------------------------

    /**
     * 1）查询条件：组织架构、运单号、客户名称/二级商家、开单时间、签收时间、服务类型（多选）、始发网点（多选）
     * 2）默认表头：运单号、客户名称、客户等级、服务类型、运单总金额、开单时间、签收时间、业务员
     * 3）表头可加维度：一级部门、二级部门、三级部门、二级商家、始发网点
     * 4）默认表头及其顺序：运单号、客户名称、二级商家、客户等级、服务类型、始发网点、运单总金额、
     * 开单时间、签收时间、一级部门、二级部门、三级部门、业务员
     *
     * @param vo
     * @return
     */
    public Object waybillQuery(SalesIndexQueryVO vo) {
        List<SalesIndexDO> list = salesIndexService.waybillQuery(vo);
        list = getDataByPage(vo, list);
        List<WaybillDetails> collect = list.stream().map(e -> BeanUtil.copy(e, WaybillDetails.class)).collect(Collectors.toList());
        return collect;
    }

    //默认表头及其顺序：运单号、客户名称、二级商家、客户等级、服务类型、始发网点、运单总金额、
    //开单时间、签收时间、一级部门、二级部门、三级部门、业务员
    @Data
    public static class WaybillDetails {
        public String waybill_id;
        public String customer_name;
        public String second_name;
        public String client_grade;
        public String service_type;
        public String originating_node;
        public Double billing_income;
        public Date   open_bill_time;
        public Date   sign_time;
        public String sales_departments;
        public String salesman;
    }


//    -----------------------------------销售指标-运单查询end---------------------------------------------------


//    -----------------------------------销售指标-客户查询start---------------------------------------------------

    /**
     * 销售指标-运单查询
     *
     * @param vo
     * @return
     */
    public Object customerQuery(SalesIndexQueryVO vo) {
        List<SalesIndexDO> list = salesIndexService.customerQuery(vo);
        list = getDataByPage(vo, list);
        List<CustomerDetails> collect = list.stream().map(e -> BeanUtil.copy(e, CustomerDetails.class)).collect(Collectors.toList());
        return collect;
    }

    /**
     * 1）查询条件：组织架构、客户名称、客户等级、是否新客户、新客户分级、结算方式、账期、价格类型（分为标准价格客户和非标准价格客户）
     * 2）默认表头：客户名称、毛利率、渗透率、业务员、是否新客户、客户等级、结算方式、价格类型（分为标准价格客户和非标准价格客户）
     * 3）表头可加维度：一级部门、二级部门、三级部门、二级商家、新客户分级、客户物流费、毛利等级、账期
     * 4）默认表头及其顺序：客户名称、二级商家、毛利率、（毛利等级）、渗透率、客户物流费、一级部门、二级部门、三级部门、
     * 业务员、是否新客户、新客户分级、客户等级、结算方式、账期、价格类型（分为标准价格客户和非标准价格客户）
     */
    @Data
    public static class CustomerDetails {
        public String customer_name;
        public String second_name;
        //毛利率 不要
        //渗透率 不要
        public Double client_fee;
        public String sales_departments;
        public String salesman;
        public int    inNewCustomer;
        public String client_grade;
        public String settlement_type;
        public String payment_period;
        public String client_type;
    }

//    -----------------------------------销售指标-客户查询end---------------------------------------------------


}
