import com.yiziton.DataModelImportApplication;
import com.yiziton.dataimport.waybill.bean.AfterSaleInfo;
import com.yiziton.dataimport.waybill.dao.AfterSaleInfoMapper;
import com.yiziton.dataimport.waybill.dao.MilestoneInfoMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2018/11/03 12:04
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={DataModelImportApplication.class})
@ActiveProfiles("dev")
public class DaoTest {

    @Autowired
    private AfterSaleInfoMapper afterSaleInfoMapper;

    @Autowired
    MilestoneInfoMapper milestoneInfoMapper;

    @Test
    public void test1(){
        afterSaleInfoMapper.insert(new AfterSaleInfo());
        AfterSaleInfo afterSaleInfo = afterSaleInfoMapper.selectByPrimaryKey("1");
        Assert.assertEquals("1", afterSaleInfo.getId());
    }

    @Test
    public void test2(){
        //MilestoneInfo milestoneInfo = milestoneInfoMapper.selectByPrimaryKey("WcxGsmCR4LFTqH3o");
        //Timestamp operateTime = milestoneInfo.getOperateTime();
        Timestamp timestamp2 = new Timestamp(1542710432773L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = Timestamp.valueOf("2018-11-20 18:40:33");
        System.out.println("mytime:"+ timestamp2);
        //long time = operateTime.getTime();
        //System.out.println(operateTime);
        //System.out.println(time);
        // 2018-11-20 20:30:58.347

    }
}
