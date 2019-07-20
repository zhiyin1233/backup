package com.yiziton.dataweb.waybill.action;


import com.yiziton.commons.utils.bean.Beans;
import com.yiziton.commons.vo.enums.ServiceType;
import com.yiziton.dataweb.core.action.contentType.excel.ExcelFile;
import com.yiziton.dataweb.core.annotation.Action;
import com.yiziton.dataweb.waybill.bean.*;
import com.yiziton.dataweb.waybill.dataobject.WaybillbaseInfoDO;
import com.yiziton.dataweb.waybill.job.DemoJob;
import com.yiziton.dataweb.waybill.service.WayBillService;
import com.yiziton.dataweb.waybill.utils.GridRequest;
import com.yiziton.dataweb.waybill.utils.excel.ExportExcelUtils;
import com.yiziton.dataweb.waybill.vo.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/11/27
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Action("waybill")
public class WayBillAction {

    @Autowired
    WayBillService wayBillService;
    @Autowired
    ExportExcelUtils exportExcelUtils;

    @Autowired
    DemoJob demoJob;


    public void testSend(String param) throws Exception{
        demoJob.execute(param);
    }

    /**
     * 报表文件导出
     *
     * @param conditionVo
     */
    public ExcelFile exportWayBill(WayBillConditionVO conditionVo) throws Exception {
        Workbook workbook = wayBillService.exportWayBill(conditionVo);
        ExcelFile file = new ExcelFile();
        file.setFileName("WayBillexport");
        file.setWorkbook(workbook);
        return file;
    }


    public Page<WayBillInfo> findWayBillByCode(GridRequest gridRequest, WayBillConditionVO conditionVo) {
        return wayBillService.findWayBillByCode(gridRequest, conditionVo);
    }

    /**
     * 根据条件分页查询运单
     *
     * @param gridRequest
     * @param wayBillConditionVo
     * @return
     */
    public Page<WaybillInfoVO> findWaybillList(GridRequest gridRequest, WayBillConditionVO wayBillConditionVo) {
        return wayBillService.findWaybillList(gridRequest, wayBillConditionVo);
    }

    /**
     * 查询运单明细信息
     * @param wayBillConditionVO
     * @return
     */
    public WaybillDetailVO findWaybillDetailInfo(WayBillConditionVO wayBillConditionVO) {
        return wayBillService.findWaybillDetailInfo(wayBillConditionVO);
    }

    /**
     * 查询运单基本信息
     */
    public WaybillbaseInfoVO findWaybillBaseInfo(String waybillId) {
        return wayBillService.findWaybillBaseInfo(waybillId);
    }

    /**
     * 根据运单号查询货物信息
     *
     * @param wayBillConditionVO
     * @return
     */
    public List<GoodsInfo> findGoodsInfo(WayBillConditionVO wayBillConditionVO) {
        return wayBillService.findGoodsInfo(wayBillConditionVO);
    }

    public List<WaybillRelationInfo> findWaybillRelationInfo(WaybillRelationInfo waybillRelationInfo) {
        return wayBillService.findWaybillRelationInfo(waybillRelationInfo);
    }

    /**
     * 承接信息
     *
     * @param wayBillConditionVO
     * @return
     */
    public TransferInfo findWaybillTransferInfo(WayBillConditionVO wayBillConditionVO) {
        return wayBillService.findWaybillTransferInfo(wayBillConditionVO);
    }

    /**
     * 查询异常信息
     *
     * @param wayBillConditionVO
     * @return
     */
    public ExceptionInfoVO findAbnormalInfo(WayBillConditionVO wayBillConditionVO) {
        return wayBillService.findAbnormalInfo(wayBillConditionVO);
    }

    /**
     * 查询售后信息
     *
     * @param wayBillConditionVO
     * @return
     */
    public List<AfterSaleTaskInfo> findAfterSaleInfo(WayBillConditionVO wayBillConditionVO) {
        return wayBillService.findAfterSaleInfo(wayBillConditionVO);
    }

    /**
     * 查询费用信息
     *
     * @param wayBillConditionVO
     * @return
     */
    public List<BillDetailInfoVO> findFeeInfo(WayBillConditionVO wayBillConditionVO) {
        return wayBillService.findFeeInfo(wayBillConditionVO);
    }

    /**
     * 查看里程碑轨迹信息
     *
     * @param wayBillConditionVO
     * @return
     */
    public List<List<MilestoneInfo>> findMilestoneInfo(WayBillConditionVO wayBillConditionVO) {
        return wayBillService.findMilestoneInfo(wayBillConditionVO);
    }

    public ClaimInfoVO findClaimInfo(WayBillConditionVO wayBillConditionVO) {
        return wayBillService.findClaimInfo(wayBillConditionVO);
    }

    /**
     * 操作内容查看的英文转中文接口
     * @return
     */
    public Map<String,String> getField(){
        Map<String,String> field = new HashMap<>();
        field.put("reservoirArea","库区");
        field.put("reservoirAreaType","库区类型");
        field.put("abnreservoirAreaType","异常库存分类");
        field.put("remark","备注");
        field.put("outDeliverRelayId","外发交接单号");
        field.put("carrier","承运商名称");
        field.put("outDeliverMans","外发员");
        field.put("plateNumber","车牌号");
        field.put("driverName","司机姓名");
        field.put("carrierCode","承运商ID");
        field.put("pickUpMethod","提货方式");
        field.put("carrierReceiver","承运商收货人");
        field.put("carrierReceiverPhone","承运商收货人手机");
        field.put("areaId","地址编号");
        field.put("carrierAddress","承运商地址");
        field.put("carrierCompanyName","承运商公司全称");
        field.put("startSentArea","承运商始发地");
        field.put("carrierDestination","承运商目的地");
        field.put("carrierSettlementType","承运商结算政策");
        field.put("shortDeliverRelayId","短途交接单号");
        field.put("shortDeliverNode","短途交接目的网点");
        field.put("arrivalArea","提货目的地");
        field.put("arrivalAddress","提货详细地址");
        field.put("pickUpGoodsPhone","提货电话");
        field.put("pickUpGoodsPassword","提货密码");
        field.put("logisticsBillId","物流单号");
        field.put("logisticsCompany","物流公司名称");
        field.put("carrier","承运商名称");
        field.put("cancelReason","取消原因");
        field.put("taskBillId","任务单号");
        field.put("masterName","师傅名称");
        field.put("masterNode","归属网点");
        field.put("nodeCode","网点账号");
        field.put("nodeType","网点类型");
        field.put("nodeDutyName","网点负责人");
        field.put("masterPhone","师傅账号");
        field.put("masterType","师傅类型");
        field.put("masterCode","服务商ID");
        field.put("doorTime","上门时间");
        field.put("doorPeriod","上门时间段");
        field.put("signType","签收类型");
        field.put("signName","签收人");
        field.put("signPicture","签收图片链接");
        field.put("trackType","跟踪类型");
        field.put("trackContext","跟踪内容");
        field.put("receivablesName","收款人");
        field.put("receivablesCode","收款账号");
        field.put("reimbursement","报销人");
        field.put("dutyDepartment","责任部门");
        field.put("payType","支付类型");
        field.put("compensateReason","补偿原因");
        field.put("payPicture","支付图片");
        field.put("checkMethod","核销方式");
        field.put("checkStatus","核销状态");
        field.put("checkBillId","核销单号");
        field.put("handleClaimType","处理理赔类型");
        field.put("fullOffer","足额报价");
        field.put("handleFee","处理金额");
        field.put("handleView","处理意见");
        field.put("accommodationBillMonth","通融账单月份");
        field.put("accommodationFee","通融费用");
        field.put("accommodationReason","通融原因");
        field.put("compensationBillMonth","赔偿账单月份");
        field.put("compensationBillId","赔偿单号");
        field.put("compensationStatus","赔偿状态");
        field.put("closeFee","结案金额");
        field.put("closeView","结案意见");
        field.put("taskType","任务类型");
        field.put("taskStatus","任务状态");
        field.put("applyClaimType","申请理赔类型");
        field.put("claimReason","理赔原因");
        field.put("claimFee","理赔金额");
        field.put("claimGoods","理赔品名");
        field.put("confirmPicture","确认图片");
        field.put("outDeliverCost","外发成本");
        field.put("disposeRemark","处理备注");
        field.put("addType","追赔类型");
        field.put("addReason","追赔原因");
        field.put("addFee","追赔金额");
        field.put("addGoods","追赔品名");
        field.put("addObject","追赔对象");
        field.put("addDuty","追赔责任人名称");
        field.put("addDutyCode","追赔责任人编号");
        field.put("goodsCode","商品编号");
        field.put("goods","品名");
        field.put("picture","图片");
        field.put("returnFee","返货金额");
        field.put("logisticsPhone","物流电话");
        field.put("transferBillId","中转单号");
        field.put("carrierBillId","承运商内部单号");
        field.put("route","线路");
        field.put("transferArrivalAddress","中转到达地址");
        field.put("transferLogisticsPhone","中转物流电话");
        field.put("customerBillId","客户单号");
        field.put("orderBillId","订单号");
        field.put("departureTime","发车时间");
        field.put("departureAddress","发往地址");
        field.put("transferDepartureTime","中转发车时间");
        field.put("transferArrivalTime","中转到达时间");
        field.put("arrivedAddress","到达地址");
        field.put("exceptionCode","异常编号");
        field.put("exceptionType","异常类型");
        field.put("exceptionStatus","异常状态");
        return field;
    }

    public WaybillNodeVO findWaybillNodeList(WayBillConditionVO wayBillConditionVO) {
        return wayBillService.findWaybillNodeList(wayBillConditionVO);
    }


}
