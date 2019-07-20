
import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.serialize.BeanJsonSerializer;
import com.yiziton.commons.utils.OID;
import com.yiziton.commons.vo.enums.OperationProcessMethod;
import com.yiziton.commons.vo.enums.TopicType;
import com.yiziton.commons.vo.waybill.ConsignorInfoVO;
import com.yiziton.commons.vo.waybill.GoodsInfoVO;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.commons.vo.waybill.MessageBody;
import com.yiziton.commons.vo.waybill.ReceviceInfoVO;
import com.yiziton.commons.vo.waybill.WaybillInfoVO;
import com.yiziton.dataimport.commons.ErrorUtil;
import com.yiziton.dataimport.waybill.bean.MilestoneInfo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/10/31
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class ConsumerTest {

    @Test
    public void testUrl(){
        int i = 0;
        int k = 1/0;
        try{
            int j = 1/0;
        }catch (Throwable e){
            System.out.println();
        }
    }

    @Test
    public void producerTest () throws Exception{
        try {
            Properties props = new Properties();
            props.put("bootstrap.servers", "119.204.66.63:9092");
            //请求符合最完美标准，最慢最耐用
            props.put("acks", "all");
            //请求失败重试机制，失败进行重试的次数
            //props.put("retries", 0);
            //发送记录缓冲区
            //props.put("batch.size", 1000);
            //props.put("linger.ms", 1);
            //定义缓冲总量存储器，用于保存尚未传输到服务器的记录
            //props.put("buffer.memory", 33554432);
            //启用幂等性 幂等生产者将卡夫卡的交付语义从至少一次加强到一次交付。特别是生产者重试将不再引入重复。如果设置，则 retries配置将默认为Integer.MAX_VALUE，acks配置将默认为all
            props.put("enable.idempotence", true);

            //序列化方式
            props.put("key.serializer", StringSerializer.class.getName());
            props.put("value.serializer", BeanJsonSerializer.class.getName());
            Timestamp ts = Timestamp.valueOf("2018-11-14 14:42:27");
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
            waybillInfoVo.setCheckCode("CC" + oid);
            waybillInfoVo.setCheckBillId("CB" + oid);
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
//            goodsInfoVo.setGoodsType("子母床");
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
//            goodsInfoVO2.setGoodsType("子母床");
            goodsInfoList.add(goodsInfoVO2);

            messageBody.setGoodsInfoVOList(goodsInfoList);

            message.setMessageBody(messageBody);
            Producer<String, Object> producer = new KafkaProducer<>(props);
            //异步发送
            producer.send(new ProducerRecord<String, Object>("mytest5", "mykey", message)).get();
            producer.flush();
        }catch (Throwable throwable){
            System.out.println(ErrorUtil.parseError(throwable));
        }
    }

    @Test
    public void testDeserialize(){
        String data = "{\"apiName\":\"oldTaskToDataController.transferData\",\"dealMethod\":\"OLD_DATA_MESSAGE\",\"messageFrom\":\"IPS\",\"oldMessageBody\":{\"goodsInfoVOList\":[{\"deliveryFee\":0.0,\"goodsName\":\"转椅\",\"installFee\":20.0,\"installItems\":1,\"packing\":\"CARTON\",\"packingItems\":0,\"salePrice\":0.0,\"volume\":0.0,\"weight\":0.0},{\"deliveryFee\":0.0,\"goodsName\":\"子母床(抽床+床垫）\",\"installFee\":140.0,\"installItems\":1,\"packing\":\"CARTON\",\"packingItems\":14,\"salePrice\":120.0,\"volume\":1.3,\"weight\":0.0},{\"deliveryFee\":120.0,\"goodsName\":\"书桌（带书架）\",\"installFee\":80.0,\"installItems\":1,\"packing\":\"CARTON\",\"packingItems\":0,\"salePrice\":0.0,\"volume\":0.0,\"weight\":0.0}],\"milestoneInfoVOList\":[{\"operateTime\":1491225089000,\"operation\":\"HEADQUARTERS_APPOINTMENT\",\"operationContext\":{\"doorPeriod\":\"下午\",\"taskBillId\":\"1zt0667511\",\"doorTime\":\"2017-04-03 21:11:29\"},\"operationOganization\":\"一智通供应链管理有限公司\",\"operationSys\":\"IPS\",\"operationType\":\"FORWARD\",\"operator\":\"系统\"}],\"waybillInfoVO\":{\"arrivalNode\":\"一智通售后调度组\",\"billRelationInfoVO\":{\"customerBillId\":\"1zt0667511\",\"orderBillId\":\"\"},\"channelSource\":\"ORDINARY\",\"checkBillId\":\"\",\"checkMethod\":\"CHECK_NO\",\"checkType\":\"TMALL\",\"consignorInfoVO\":{\"code\":\"KH2017023099\",\"customerType\":\"SEND\",\"mobile\":\"13873238976\",\"name\":\"菲艺轩家具\"},\"destination\":\"安徽省, 合肥市, 庐阳区\",\"masterInfoVO\":{\"actualAppoinmentTime\":1491225089000,\"actualSignTime\":1491387255307,\"masterCode\":\"13866148744\",\"masterName\":\"徐礼飞\",\"masterNode\":\"安徽众包\",\"masterPhone\":\"13866148744\",\"masterType\":\"MASTER\",\"nodeCode\":\"20000000001\",\"nodeDutyName\":\"安徽众包\",\"nodeType\":\"众包网点\",\"trunkEndTime\":1491198262840},\"openBillNode\":\"一智通乐从营业部\",\"openBillOperator\":\"梁翠梅\",\"openBillTime\":1490964720000,\"originatingNode\":\"一智通乐从营业部\",\"paymentType\":\"CASH\",\"productType\":\"ORDINARY\",\"receviceInfoVO\":{\"address\":\"安徽省合肥市庐阳区海棠街道海亮红玺台g1栋402室\",\"area\":\"庐阳区\",\"areaId\":\"226582xxxxxxxxxx\",\"city\":\"合肥市\",\"elevator\":0,\"floor\":0,\"latitude\":\"31.86901100\",\"longitude\":\"117.28377500\",\"mobile\":\"15955191993\",\"name\":\"一智通转温耀庭\",\"province\":\"安徽省\"},\"saleTotalPrice\":120.0,\"salesman\":\"\",\"serviceType\":\"DISTRIBUTION_INSTALLATION\",\"settlementType\":\"PAY_CASH\",\"signTime\":1491387255307,\"statementValue\":3000.0,\"totalInstallItems\":3,\"totalPackingItems\":14,\"totalVolume\":1.3,\"totalWeight\":0.0,\"waybillStatus\":\"FINISH\",\"waybillType\":\"NORMAL\"}},\"operationClass\":\"C\",\"sentTime\":1552377328254,\"waybillId\":\"1zt0667511\"}&spilt:com.yiziton.commons.vo.waybill.OldDataMessage";
        Object object = BeanJsonDeserializer.deserialize(data);
        System.out.println(object);
    }

    @Test
    public void consumerTest01 () throws Exception{
        Properties props = new Properties();
        props.put("bootstrap.servers", "129.204.66.63:9092");
        props.put("group.id", TopicType.NODE_TOPIC_GROUP);
        props.put("enable.auto.commit", false);
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", BeanJsonDeserializer.class.getName());
        KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(TopicType.NODE_TOPIC));
        while (true) {
            ConsumerRecords<String, Object> records = consumer.poll(100);
            for (ConsumerRecord<String, Object> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        }
    }
    @Test
    public void testTime() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,-30);
        System.out.println(sdf.format(date));
        System.out.println(sdf.format(calendar.getTime()));
    }

    public void saveFile(String data, String folder) throws IOException{
        FileOutputStream os = null;
        try{
            File file = null;
            String path = null;
            if(StringUtils.isEmpty(path)){
                file = new File(folder+".txt");
            }else{
                File filePath = new File(path);
                if(!filePath.exists()){
                    filePath.mkdirs();
                }
                file = new File(filePath,folder+".txt");
            }

            if(!file.exists()){
                file.createNewFile();
            }
            os = new FileOutputStream(file,true);
            os.write(data.getBytes());
            os.write("\r\n".getBytes());
        }catch (IOException e){
            throw e;
        }finally {
            try{
                os.close();
            }catch (IOException e){
                throw e;
            }
        }
    }
    @Test
    public void testSaveFile() throws Exception{
        saveFile("aaabbbcccdd",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        saveFile("eeefffsss",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    @Test
    public void saveFile() throws IOException {
        FileOutputStream os = null;
        String path = "logs/datafile/";
        String data = "aaaa";
        String folder = "2017";
        try{
            File file = null;
            if(StringUtils.isEmpty(path)){
                file = new File(folder+".txt");
            }else{
                File filePath = new File(path);
                if(!filePath.exists()){
                    filePath.mkdirs();
                }
                file = new File(filePath,folder+".txt");
            }

            if(!file.exists()){
                file.createNewFile();
            }
            os = new FileOutputStream(file,true);
            os.write(data.getBytes());
            os.write("\r\n".getBytes());
        }catch (IOException e){
            throw e;
        }finally {
            try{
                os.close();
            }catch (IOException e){
                throw e;
            }
        }
    }
}
