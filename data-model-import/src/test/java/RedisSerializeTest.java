import com.alibaba.fastjson.JSON;
import com.yiziton.DataModelImportApplication;
import com.yiziton.commons.EventProducer;
import com.yiziton.commons.utils.ExecutorUtil;

import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.CacheMsgTypeEnum;
import com.yiziton.commons.vo.enums.NodeType;
import com.yiziton.commons.vo.enums.Operation;
import com.yiziton.commons.vo.enums.OperationProcessMethod;
import com.yiziton.commons.vo.enums.OperationType;
import com.yiziton.commons.vo.enums.TopicType;
import com.yiziton.commons.vo.waybill.ExceptionInfoVO;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.commons.vo.waybill.MessageBody;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.bean.MilestoneInfo;
import com.yiziton.dataimport.waybill.dao.ext.MilestoneInfoExtMapper;
import com.yiziton.dataimport.waybill.job.CacheClearJob;
import com.yiziton.dataimport.waybill.job.CacheExpireJob;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RunWith(MyRunner.class)
@SpringBootTest(classes={DataModelImportApplication.class})
@ActiveProfiles("dev")
@Slf4j
public class RedisSerializeTest {

    @Autowired
    ExecutorUtil executorUtil;

    @Autowired
    SysExceptionMapper sysExceptionMapper;

    @Autowired
    MilestoneInfoExtMapper milestoneInfoExtMapper;

    @Autowired
    CacheExpireJob cacheExpireJob;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private OrderProcessor orderProcessor;

    @Autowired
    private EventProducer eventProducer;

    private static String order_no = "2656565";

    @Test
    public void testExecutor() throws Exception{
        String wayBillNo = "22131311448,111";
        String[] wayBillNos = null;
        Long serialNo = null;
        if(wayBillNo.contains(",")){
            wayBillNos = wayBillNo.split(",");
            serialNo = orderProcessor.getIncrNo(wayBillNos[0]);
        }else{
            serialNo = orderProcessor.getIncrNo(wayBillNo);
        }
        log.info("serialNo:"+serialNo+",getIncrNo:"+wayBillNo);
        if(wayBillNo.contains(",")){
            wayBillNos = wayBillNo.split(",");
             orderProcessor.getIncrNo(wayBillNos[0]);
        }else{
             orderProcessor.getIncrNo(wayBillNo);
        }
        //Long sendNo = 5L;
        executorUtil.dealMessage(()->{
            try {
                Message message = new Message();
                message.setMessageFrom("SCM");
                message.setOperationClass("C");
                message.setWaybillId(wayBillNo);
                message.setSentTime(new Timestamp(System.currentTimeMillis()));
                message.setApiName("createBilling");
                message.setRelatedBillId(null);
                message.setOperationType(OperationType.ABNORMAL.name());
                message.setOperation(Operation.ARRIVAL_DESTINATION.name());
                message.setOperateTime(new Timestamp(System.currentTimeMillis()));
                message.setOperator("王五");
                MessageBody messageBody = new MessageBody();
                ExceptionInfoVO exceptionInfoVO = new ExceptionInfoVO();
                exceptionInfoVO.setDisposeRemark("aaa");
                messageBody.setExceptionInfoVO(exceptionInfoVO);
                message.setMessageBody(messageBody);
                eventProducer.fire(wayBillNo,message,TopicType.NODE_TOPIC);
            }catch (Exception e){
                //dataInvoke.dealException(wayBillNo,Operation.ARRIVAL_DESTINATION.name(),e);
            }
        });
        Thread.sleep(5000);
    }

    @Test
    public void testSql(){
        SysExceptionInfo sysExceptionInfo = sysExceptionMapper.selectByNoForGetAll("1zts18111204351");
        System.out.println(sysExceptionInfo);
    }

    @Test
    public void testZset(){
        boolean bool = redisTemplate.opsForZSet().add("1111223", "aaa", 1);
        redisTemplate.opsForZSet().add("1111223", "bbb", 3);
        redisTemplate.opsForZSet().add("1111223", "ccc", 2);

        redisTemplate.expire("1111223", 30, TimeUnit.DAYS);
    }

    @Test
    public void testZsetMsg() throws Exception{
        long [] sids = new long[]{1,0,6,7,8,9};
//        long [] sids = new long[]{5,2,3,1,6,4,8,7,9};
        for(int i = 0; i < 9; i++) {
            //long no = orderProcessor.getIncrNo("122223334");
            MessageRedisCacheInfo info = new MessageRedisCacheInfo();
            info.setType(CacheMsgTypeEnum.BILLING_MSG);
            info.setSerializeNo(sids[i]);
            Billing billing = new Billing();
            billing.setId(10086 + i);
            billing.setName("hello billing.--" + i);
            //info.setMessage(billing);
            List<MessageRedisCacheInfo> msgs = orderProcessor.getAllShouldHandleMsg(order_no, info);
            msgs.forEach(msg -> {
                System.out.println("deal:" + JSON.toJSON(msg));
            });
            printObj();
            Set<String> ids = orderProcessor.getUnhandleMsgOrderNo();
            System.out.println("unhandle orderNo:" + JSON.toJSON(ids));
            System.out.println("--------------------------------------");
        }

    }

    @Test
    public void testSame() throws Exception{
        MilestoneInfo milestoneInfo = new MilestoneInfo();
        milestoneInfo.setWaybillId("1zts18121709322");
        milestoneInfo.setSentTime(new Timestamp(1545012525000L));
        milestoneInfo.setOperation(Operation.getEnumByEnumNameString("WAYBILL_OPEN").getCode());
        if (milestoneInfoExtMapper.selectCountForSame(milestoneInfo) > 0) {
            System.out.println("exception in");
        }
    }

    @Test
    public void testRedis() throws Exception{
        cacheExpireJob.execute(null);
    }

    private void printObj() {
       Set<MessageRedisCacheInfo> msgs = orderProcessor.getOrderNoMsgs(order_no);
       msgs.forEach(msg -> {
           System.out.println(JSON.toJSON(msg));
       });
        System.out.println("---------------------------");
    }

    @Data
    class Billing {
        private Integer id;
        private String name;
    }

    @Data
    class OldData {
        private String color;
        private String desc;
    }
    @Data
    class WayBill {
        private String hande;
        private String ziton;
    }
    @Test
    public void saveFile() throws IOException {
        FileOutputStream os = null;
        String path = "logs/datafile/";
        String data = "aaaab";
        String folder = "2018";
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
            log.error("saveFile Exception",e);
            throw e;
        }finally {
            try{
                os.close();
            }catch (IOException e){
                log.error("saveFile Exception",e);
                throw e;
            }
        }
    }
}
