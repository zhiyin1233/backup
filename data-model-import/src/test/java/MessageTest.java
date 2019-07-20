import com.xxl.job.core.biz.model.ReturnT;
import com.yiziton.commons.EventProducer;
import com.yiziton.commons.config.ProducerConfig;
import com.yiziton.commons.utils.OID;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.Operation;
import com.yiziton.commons.vo.enums.TopicType;
import com.yiziton.commons.vo.waybill.*;
import com.yiziton.DataModelImportApplication;
import com.yiziton.dataimport.commons.FileUtils;
import com.yiziton.dataimport.waybill.action.HistoryDataImportAction;
import com.yiziton.dataimport.waybill.action.OrderAction;
import com.yiziton.dataimport.waybill.action.SysExceptionDealAction;
import com.yiziton.dataimport.waybill.bean.FeeInfo;
import com.yiziton.dataimport.waybill.dao.ext.FeeInfoExtMapper;
import com.yiziton.dataimport.waybill.vo.SysExceptionWayBillVO;
import com.yiziton.dataimport.waybill.vo.SysWayBillVO;
import com.yiziton.dataweb.waybill.job.DemoJob;
import com.yiziton.dataweb.waybill.job.EngineeringReportExportJob;
import com.yiziton.dataweb.waybill.job.ReportExportJob;
import com.yiziton.dataweb.waybill.vo.ImportConditionVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/10/31
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@RunWith(MyRunner.class)
@SpringBootTest(classes={DataModelImportApplication.class})
@ActiveProfiles("prd")
/*@RunWith(SpringRunner.class)
@ComponentScan("com.yiziton")
@SpringBootTest(classes = {DataModelImportApplication.class,FileUtils.class,EventProducer.class,ProducerConfig.class})
@ActiveProfiles("dev")*/
public class MessageTest {

    @Autowired
    FileUtils fileUtils;

    @Autowired
    EventProducer eventProducer;
    @Autowired
    ProducerConfig producerConfig;

    @Autowired
    DemoJob demoJob;

    @Autowired
    ReportExportJob reportExportJob;

    @Autowired
    EngineeringReportExportJob engineeringReportExportJob;

    @Test
    public void testReportExportJob() throws Exception{
        reportExportJob.execute(null);
    }

    @Test
    public void testEngineeringReportExportJob() throws Exception{
        engineeringReportExportJob.execute(null);
    }

    @Test
    public void producerTest () throws Exception{
        MessageRedisCacheInfo cacheInfo = new MessageRedisCacheInfo();
        cacheInfo.setSerializeNo(2L);
        Message message = new Message();
        message.setMessageFrom("IPS");
        message.setOperationClass("U");
        message.setWaybillId("1zts18112004631");
        message.setSentTime(new Timestamp(System.currentTimeMillis()));
        message.setApiName("abnormalController.abnormalHandle");
        message.setRelatedBillId(null);
        message.setOperation("EXCEPTION_HANDLING");
        message.setOperationType("AFTER_SALE");
        message.setOperateTime(new Timestamp(System.currentTimeMillis()));
        message.setOperator("浙南一区众包");

        MessageBody messageBody = new MessageBody();

        MilestoneInfoVO milestoneInfoVO = new MilestoneInfoVO();
        Map<String, Object> operationContext = new HashMap<>();
        operationContext.put("taskType","服务商返货");
        operationContext.put("taskBillId", "1zts18112004610-fh1");
        operationContext.put("taskStatus", "返回客户");
        operationContext.put("disposeRemark", "已补录");
        milestoneInfoVO.setOperationContext(operationContext);
        ExceptionInfoVO exceptionInfoVO = new ExceptionInfoVO();
        exceptionInfoVO.setExceptionCode("YC2019012010");
        exceptionInfoVO.setExceptionStatus("已处理");
        exceptionInfoVO.setDispose("浙南一区众包");
        exceptionInfoVO.setDisposeNode("浙南一区众包");
        exceptionInfoVO.setDisposeResult("浙南一区众包");
        exceptionInfoVO.setActualDisposeTime(new Timestamp(System.currentTimeMillis()));
        messageBody.setMilestoneInfoVO(milestoneInfoVO);
        messageBody.setExceptionInfoVO(exceptionInfoVO);

        message.setMessageBody(messageBody);
        cacheInfo.setMessage(message);
        eventProducer.fire(2L,"mykey",message,"nodeTopicTest2");
        Thread.sleep(2000);

        MessageRedisCacheInfo cacheInfo2 = new MessageRedisCacheInfo();
        cacheInfo2.setSerializeNo(1L);
        Message message2 = new Message();
        message2.setMessageFrom("SCM");
        message2.setOperationClass("C");
        message2.setWaybillId("1zts18112004622");
        message2.setSentTime(new Timestamp(System.currentTimeMillis()));
        message2.setApiName("saveServiceProviderOrCarrierFlow");
        message2.setRelatedBillId(null);
        message2.setOperateTime(new Timestamp(System.currentTimeMillis()));
        message2.setOperator("浙南一区众包");

        MessageBody messageBody2 = new MessageBody();
        BillingInfoVO billingInfoVO = new BillingInfoVO();
        billingInfoVO.setBillingObject("MASTER");
        billingInfoVO.setRelatedBillId("YC2019012010");
        billingInfoVO.setBillingType("ABNORMAL");
        billingInfoVO.setAccounting("PAY");
        billingInfoVO.setBillingName("陈作敏");
        billingInfoVO.setBillingPhone("15967075633");
        billingInfoVO.setTotalPrice(150.0);
        billingInfoVO.setBillingTime(new Timestamp(System.currentTimeMillis()));
        Map<String, Object> billingInfo = new HashMap<>();
        billingInfo.put("abnStorageFee",150.0);

        billingInfoVO.setFeeInfoVO(billingInfo);

        messageBody2.setWaybillId("1zts18112004622");

        messageBody2.setBillingInfoVO(billingInfoVO);
        message2.setMessageBody(messageBody2);
        cacheInfo2.setMessage(message2);
        eventProducer.fire(1L,"mykey",message2,TopicType.NODE_TOPIC);

    }

    /*@Test
    public void testFinance () throws Exception{
        MessageBilling message = new MessageBilling();
        message.setWaybillId("financeTopic");
        eventProducer.fire(message.getWaybillId(),message,"financeTopic");
    }*/

    @Test
    public void testNode () throws Exception{
        Message message = new Message();
        message.setWaybillId("nodeTopic");
        eventProducer.fire(message.getWaybillId(),message,"nodeTopic");
    }

    @Test
    public void waybillOpen () throws Exception{

        Timestamp ts = Timestamp.valueOf("2018-11-17 00:00:00");
        String oid = OID.get().toString();
        Message message = new Message();
        message.setMessageFrom("SCM");
        message.setOperationClass("C");

        message.setWaybillId("1ztWcQ9SHvz4p0TQK1j");
        message.setSentTime(ts);
        message.setApiName("开单接口");
        message.setRelatedBillId(null);
        message.setOperation("WAYBILL_OPEN");
        message.setOperationType("正常节点");
        message.setOperateTime(ts);
        message.setOperator("王五");
        message.setOperationSys("SCM");
        message.setOperationOganization("一智通总部");

        MessageBody messageBody = new MessageBody();
        WaybillInfoVO waybillInfoVo = new WaybillInfoVO();

        ConsignorInfoVO consignorInfo = new ConsignorInfoVO();
        consignorInfo.setCode("consignor2" + oid);
        consignorInfo.setName("李四");
        consignorInfo.setCustomerType("vip");

        consignorInfo.setMobile("13000000000");
        messageBody.setConsignorInfoVO(consignorInfo);

        ReceviceInfoVO receviceInfo = new ReceviceInfoVO();
        receviceInfo.setName("赵六");
        receviceInfo.setMobile("13500000000");
        receviceInfo.setAddress("广东省，广州市，天河区，陶庄街道");
        receviceInfo.setElevator(1);
        receviceInfo.setFloor(1);
        messageBody.setReceviceInfoVO(receviceInfo);

        waybillInfoVo.setProductType("优达");
        waybillInfoVo.setServiceType("配送到家并安装");
        waybillInfoVo.setWaybillStatus("WAITING_WAREHOUSING");
        waybillInfoVo.setCheckType("天猫精准核销");
        waybillInfoVo.setCheckCode("CC"+oid);
        waybillInfoVo.setCheckBillId("CB"+oid);
        waybillInfoVo.setCheckStatus("未核销");
        waybillInfoVo.setChannelSource(null);
        waybillInfoVo.setPaymentType("余额");
        waybillInfoVo.setSettlementType("现付");
        waybillInfoVo.setThirdBillingId(null);
        waybillInfoVo.setDestination("广东省，广州市，天河区，陶庄街道");
        waybillInfoVo.setTotalVolume(1.01);
        waybillInfoVo.setTotalInstallItems(1);
        waybillInfoVo.setTotalPackingItems(1);
        waybillInfoVo.setTotalWeight(1.02);
        waybillInfoVo.setStatementValue(1.04);
        waybillInfoVo.setOpenBillNode("乐从营业部");
        waybillInfoVo.setOpenBillOperator("刘芬");
        waybillInfoVo.setArrivalNode("售后调度组");
        waybillInfoVo.setOpenBillTime(ts);
        waybillInfoVo.setSignTime(ts);
        waybillInfoVo.setSaleTotalPrice(1.03);
        waybillInfoVo.setMarbleBlocks(1);
        waybillInfoVo.setSalesman("何金");
        waybillInfoVo.setSalesmanPhone("13800000000");
        waybillInfoVo.setWaybillType("TEST");

        messageBody.setWaybillInfoVO(waybillInfoVo);

        List<GoodsInfoVO> goodsInfoList = new ArrayList<GoodsInfoVO>();
        GoodsInfoVO goodsInfoVo = new GoodsInfoVO();
        goodsInfoVo.setGoodsName("子母床");
        goodsInfoVo.setGoodsCode("sp001");
        goodsInfoVo.setCustomerGoodsName("子母床");
        goodsInfoVo.setCustomerGoodsCode("子母床");
        goodsInfoVo.setInstallItems(9);
        goodsInfoVo.setPackingItems(2);
        goodsInfoVo.setVolume(8.01);
        goodsInfoVo.setWeight(6.01);
        goodsInfoVo.setInstallFee(50.01);
        goodsInfoVo.setDeliveryFee(40.01);
        goodsInfoVo.setSalePrice(5000.01);
//        goodsInfoVo.setGoodsType("子母床");
        goodsInfoList.add(goodsInfoVo);

        GoodsInfoVO goodsInfoVO2 = new GoodsInfoVO();
        goodsInfoVO2.setGoodsName("子母床");
        goodsInfoVO2.setGoodsCode("sp001");
        goodsInfoVO2.setCustomerGoodsName("子母床");
        goodsInfoVO2.setCustomerGoodsCode("子母床");
        goodsInfoVO2.setInstallItems(9);
        goodsInfoVO2.setPackingItems(2);
        goodsInfoVO2.setVolume(8.11);
        goodsInfoVO2.setWeight(6.11);
        goodsInfoVO2.setInstallFee(50.11);
        goodsInfoVO2.setDeliveryFee(40.11);
        goodsInfoVO2.setSalePrice(3000.11);
//        goodsInfoVO2.setGoodsType("子母床");
        goodsInfoList.add(goodsInfoVO2);

        messageBody.setGoodsInfoVOList(goodsInfoList);

        message.setMessageBody(messageBody);
        eventProducer.fire(message.getWaybillId(),message,"mytest5");
    }

    @Autowired
    private OrderAction orderAction;

    @Autowired
    private SysExceptionDealAction sysExceptionDealAction;

    @Autowired
    HistoryDataImportAction historyDataImportAction;

    @Test
    public void execute() throws Exception {
        //SysExceptionWayBillVO sysExceptionWayBillVO = orderAction.getAllExceptionWaybillNo(null);

        List<String>  wayBillNos = new ArrayList<>();
        wayBillNos.add("1zts19043007048");
        wayBillNos.add("1zts19043005577");
        wayBillNos.add("1zts19043003801");
        wayBillNos.add("1zts19042200216");
        wayBillNos.add("1zts19043006843");


        if(wayBillNos != null && wayBillNos.size() > 0){

            SysWayBillVO sysWayBillVO = new SysWayBillVO();
            sysWayBillVO.setWayBillNos(wayBillNos);
            sysExceptionDealAction.deleteWayBillInfoCache(sysWayBillVO);
            ImportConditionVO importConditionVO = new ImportConditionVO();
            importConditionVO.setWaybillIds(wayBillNos);
            String result = historyDataImportAction.dataImport(importConditionVO);

        }
    }


    @Autowired
    FeeInfoExtMapper feeInfoMapper;
    @Test
    public void testMybatis(){
        FeeInfo fee = feeInfoMapper.selectByPrimaryKey("1ztWcSAr2_z4p0YcD4z");

        List<String> keySet = new ArrayList<String>();
        keySet.add("1ztWcSAr2_z4p0YcD4z");
        //List list = feeInfoMapper.selectByParams("1ztWcSAr2_z4p0YcD4z","1ztWcSAr2_z4p0YcD4z","1ztWcSAr2_z4p0YcD4z",keySet);
       /* for(FeeInfo o : list){
            o.setStatus(0);
            feeInfoMapper.updateByPrimaryKeySelective(o);
        }*/
        System.out.print("");
    }

}
