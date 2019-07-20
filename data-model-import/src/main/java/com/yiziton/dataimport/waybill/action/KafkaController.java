package com.yiziton.dataimport.waybill.action;

import com.yiziton.commons.vo.enums.TopicType;
import com.yiziton.dataimport.config.KafkaManager;
import com.yiziton.dataweb.core.annotation.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: KafkaController</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2019/07/03 13:55
 */
@Action("kafkaController")
@Slf4j
public class KafkaController {

    @Autowired
    private KafkaManager kafkaManager;

    //@RequestMapping(value = "/api/kafka/set", method = RequestMethod.GET)
    public String setKafka(String flag){
        if (flag.equals("true")){
            kafkaManager.start(TopicType.NODE_TOPIC_ID);
            return "开启成功!";
        } else if (flag.equals("false")){
            kafkaManager.stop(TopicType.NODE_TOPIC_ID);
            return "关闭成功!";
        } else {
            return "参数错误!";
        }
    }
}
