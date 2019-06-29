package create;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.CreateTableOptions;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CreateWaybillMainTable {

    static String masteraddr = "192.168.100.73,192.168.100.74,192.168.100.75";
    // 创建kudu的数据库链接
    static KuduClient client = new KuduClient.KuduClientBuilder(masteraddr).defaultSocketReadTimeoutMs(6000).build();


    private static ColumnSchema newColumn(String name, Type type, boolean iskey) {
        ColumnSchema.ColumnSchemaBuilder column = new ColumnSchema.ColumnSchemaBuilder(name, type);
        column.key(iskey);
        column.nullable(true);
        return column.build();
    }

    private static ColumnSchema newColumn(String name, Type type, boolean iskey, boolean nullable) {
        ColumnSchema.ColumnSchemaBuilder column = new ColumnSchema.ColumnSchemaBuilder(name, type);
        column.key(iskey);
        column.nullable(nullable);
        return column.build();
    }

    public static void main(String[] args) throws KuduException {
//        dropTable("waybillMain");
//        createTable();
        //createNewColStat(WaybillMainDO.class);
        System.out.println("is_send_w_a_s_pre_event".matches(".*(_[a-z]_).*"));
    }

    private static void dropTable(String tableName) {
        try {
            client.deleteTable(tableName);
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    public static void createNewColStat(Class clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {

            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String s = "columns.add(newColumn(\"%s\", %s, true,true));";
            String underlineName = StringUtil.toUnderlineName(field.getName());
            if (underlineName.matches(".*(_[a-z]_).*")) {
                throw new RuntimeException("无法转换成下划线，因为有连续的大写字母 " + field.getName() + "-->" + underlineName);
            }
            System.out.println(String.format(s, underlineName, getKuduType(field)));

        }

    }

    private static String getKuduType(Field field) {
        Class<?> type = field.getType();
        if (type == String.class) {
            return "Type.STRING";
        } else if (type == Integer.class) {
            return "Type.INT32";
        } else if (type == Long.class  ) {
            return "Type.INT64";
        } else if (type == Double.class||type == BigDecimal.class) {
            return "Type.DOUBLE";
        } else if (type == Date.class || type == Timestamp.class) {
            return "Type.UNIXTIME_MICROS";

        } else if (type == Boolean.class) {
            return "Type.BOOL";
        }
        return "Type.STRING";
    }

    private static void createTable() throws KuduException {

        List<ColumnSchema> columns = new LinkedList<>();

        columns.add(newColumn("waybill_id", Type.STRING, true, false));
        columns.add(newColumn("_id", Type.STRING, true, false));
        columns.add(newColumn("order_no", Type.STRING, true, true));
        columns.add(newColumn("is_add", Type.BOOL, true, true));
        columns.add(newColumn("is_draft", Type.BOOL, true, true));
        columns.add(newColumn("is_send_wms_pre_event", Type.BOOL, true, true));
        columns.add(newColumn("is_binding_order", Type.BOOL, true, true));
        columns.add(newColumn("is_patch", Type.BOOL, true, true));
        columns.add(newColumn("company", Type.STRING, true, true));
        columns.add(newColumn("open_waybill_depart", Type.STRING, true, true));
        columns.add(newColumn("is_shipper_mapper", Type.BOOL, true, true));
        columns.add(newColumn("is_shipper_mapper_binding", Type.BOOL, true, true));
        columns.add(newColumn("shipper_mapper_id", Type.STRING, true, true));
        columns.add(newColumn("shipper", Type.STRING, true, true));
        columns.add(newColumn("receiver", Type.STRING, true, true));
        columns.add(newColumn("service", Type.STRING, true, true));
        columns.add(newColumn("wooden_frame", Type.STRING, true, true));
        columns.add(newColumn("goods", Type.STRING, true, true));
        columns.add(newColumn("parent_bar_infor", Type.STRING, true, true));
        columns.add(newColumn("bar_infors", Type.STRING, true, true));
        columns.add(newColumn("product", Type.STRING, true, true));
        columns.add(newColumn("old_product", Type.STRING, true, true));
        columns.add(newColumn("service_fee", Type.STRING, true, true));
        columns.add(newColumn("super_area", Type.STRING, true, true));
        columns.add(newColumn("original_super_area", Type.STRING, true, true));
        columns.add(newColumn("original_exceed_area_fee", Type.STRING, true, true));
        columns.add(newColumn("relation_order_no", Type.STRING, true, true));
        columns.add(newColumn("ika_sal_no", Type.STRING, true, true));
        columns.add(newColumn("out_send", Type.BOOL, true, true));
        columns.add(newColumn("original_return_type", Type.STRING, true, true));
        columns.add(newColumn("original_return_type_name", Type.STRING, true, true));
        columns.add(newColumn("calculate_fee_type", Type.STRING, true, true));
        columns.add(newColumn("pay_type", Type.STRING, true, true));
        columns.add(newColumn("pay_type_name", Type.STRING, true, true));
        columns.add(newColumn("pay_method", Type.STRING, true, true));
        columns.add(newColumn("pay_method_name", Type.STRING, true, true));
        columns.add(newColumn("remark", Type.STRING, true, true));
        columns.add(newColumn("first_node", Type.BOOL, true, true));
        columns.add(newColumn("billing_mode", Type.STRING, true, true));
        columns.add(newColumn("pre_open_time", Type.STRING, true, true));
        columns.add(newColumn("billing_time", Type.STRING, true, true));
        columns.add(newColumn("prewarning_time", Type.STRING, true, true));
        columns.add(newColumn("warning_time", Type.STRING, true, true));
        columns.add(newColumn("time_out_time", Type.STRING, true, true));
        columns.add(newColumn("sign_time", Type.STRING, true, true));
        columns.add(newColumn("arrival_time", Type.STRING, true, true));
        columns.add(newColumn("last_follow_time", Type.STRING, true, true));
        columns.add(newColumn("waybill_flag", Type.STRING, true, true));
        columns.add(newColumn("waybill_flag_name", Type.STRING, true, true));
        columns.add(newColumn("waybill_stop", Type.BOOL, true, true));
        columns.add(newColumn("cus_waybill", Type.BOOL, true, true));
        columns.add(newColumn("last_status", Type.STRING, true, true));
        columns.add(newColumn("last_status_name", Type.STRING, true, true));
        columns.add(newColumn("relay_waybill_status", Type.STRING, true, true));
        columns.add(newColumn("relay_waybill_status_name", Type.STRING, true, true));
        columns.add(newColumn("extremity_waybill_status", Type.STRING, true, true));
        columns.add(newColumn("extremity_waybill_status_name", Type.STRING, true, true));
        columns.add(newColumn("abnormal_state", Type.STRING, true, true));
        columns.add(newColumn("abnormal_state_name", Type.STRING, true, true));
        columns.add(newColumn("payment_status", Type.STRING, true, true));
        columns.add(newColumn("payment_status_name", Type.STRING, true, true));
        columns.add(newColumn("accept_status", Type.STRING, true, true));
        columns.add(newColumn("accept_status_name", Type.STRING, true, true));
        columns.add(newColumn("wms_status", Type.STRING, true, true));
        columns.add(newColumn("wms_status_name", Type.STRING, true, true));
        columns.add(newColumn("ow_arrival", Type.BOOL, true, true));
        columns.add(newColumn("source_system", Type.STRING, true, true));
        columns.add(newColumn("total_fee", Type.STRING, true, true));
        columns.add(newColumn("arrive_pay_cost", Type.STRING, true, true));
        columns.add(newColumn("collection_cost", Type.STRING, true, true));
        columns.add(newColumn("new_is_wms_data", Type.BOOL, true, true));
        columns.add(newColumn("after_sales_type", Type.STRING, true, true));
        columns.add(newColumn("business_type", Type.STRING, true, true));
        columns.add(newColumn("business_type_name", Type.STRING, true, true));
        columns.add(newColumn("business_look", Type.BOOL, true, true));
        columns.add(newColumn("picking_time", Type.STRING, true, true));
        columns.add(newColumn("create_time", Type.STRING, true, true));
        columns.add(newColumn("last_update_time", Type.STRING, true, true));
        columns.add(newColumn("version", Type.STRING, true, true));
        columns.add(newColumn("creator", Type.STRING, true, true));
        columns.add(newColumn("creator_name", Type.STRING, true, true));
        columns.add(newColumn("modifier", Type.STRING, true, true));
        columns.add(newColumn("modifier_name", Type.STRING, true, true));
        columns.add(newColumn("is_sign", Type.BOOL, true, true));


//        columns.add(newColumn("waybillId", Type.STRING, true, false));
//        columns.add(newColumn("_id", Type.STRING, false, false));
//        columns.add(newColumn("orderNo", Type.STRING, false));
//        columns.add(newColumn("isAdd", Type.BOOL, false));
//        columns.add(newColumn("isDraft", Type.BOOL, false));
//        columns.add(newColumn("isSendWMSPreEvent", Type.BOOL, false));
//        columns.add(newColumn("isBindingOrder", Type.BOOL, false));
//        columns.add(newColumn("isPatch", Type.BOOL, false));
//        columns.add(newColumn("company", Type.STRING, false));
//        columns.add(newColumn("openWaybillDepart", Type.STRING, false));
//        columns.add(newColumn("isShipperMapper", Type.BOOL, false));
//        columns.add(newColumn("isShipperMapperBinding", Type.BOOL, false));
//        columns.add(newColumn("shipperMapperId", Type.STRING, false));
//        columns.add(newColumn("shipper", Type.STRING, false));
//        columns.add(newColumn("receiver", Type.STRING, false));
//        columns.add(newColumn("service", Type.STRING, false));
//        columns.add(newColumn("woodenFrame", Type.STRING, false));
//        columns.add(newColumn("goods", Type.STRING, false));
//        columns.add(newColumn("parentBarInfor", Type.STRING, false));
//        columns.add(newColumn("barInfors", Type.STRING, false));
//        columns.add(newColumn("product", Type.STRING, false));
//        columns.add(newColumn("oldProduct", Type.STRING, false));
//        columns.add(newColumn("serviceFee", Type.STRING, false));
//        columns.add(newColumn("superArea", Type.STRING, false));
//        columns.add(newColumn("originalSuperArea", Type.STRING, false));
//        columns.add(newColumn("originalExceedAreaFee", Type.STRING, false));
//        columns.add(newColumn("relationOrderNo", Type.STRING, false));
//        columns.add(newColumn("ikaSalNo", Type.STRING, false));
//        columns.add(newColumn("outSend", Type.BOOL, false));
//        columns.add(newColumn("originalReturnType", Type.STRING, false));
//        columns.add(newColumn("originalReturnTypeName", Type.STRING, false));
//        columns.add(newColumn("calculateFeeType", Type.STRING, false));
//        columns.add(newColumn("payType", Type.STRING, false));
//        columns.add(newColumn("payTypeName", Type.STRING, false));
//        columns.add(newColumn("payMethod", Type.STRING, false));
//        columns.add(newColumn("payMethodName", Type.STRING, false));
//        columns.add(newColumn("remark", Type.STRING, false));
//        columns.add(newColumn("firstNode", Type.BOOL, false));
//        columns.add(newColumn("billingMode", Type.STRING, false));
//        columns.add(newColumn("preOpenTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("billingTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("prewarningTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("warningTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("timeOutTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("signTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("arrivalTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("lastFollowTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("waybillFlag", Type.STRING, false));
//        columns.add(newColumn("waybillFlagName", Type.STRING, false));
//        columns.add(newColumn("waybillStop", Type.BOOL, false));
//        columns.add(newColumn("cusWaybill", Type.BOOL, false));
//        columns.add(newColumn("lastStatus", Type.STRING, false));
//        columns.add(newColumn("lastStatusName", Type.STRING, false));
//        columns.add(newColumn("relayWaybillStatus", Type.STRING, false));
//        columns.add(newColumn("relayWaybillStatusName", Type.STRING, false));
//        columns.add(newColumn("extremityWaybillStatus", Type.STRING, false));
//        columns.add(newColumn("extremityWaybillStatusName", Type.STRING, false));
//        columns.add(newColumn("abnormalState", Type.STRING, false));
//        columns.add(newColumn("abnormalStateName", Type.STRING, false));
//        columns.add(newColumn("paymentStatus", Type.STRING, false));
//        columns.add(newColumn("paymentStatusName", Type.STRING, false));
//        columns.add(newColumn("acceptStatus", Type.STRING, false));
//        columns.add(newColumn("acceptStatusName", Type.STRING, false));
//        columns.add(newColumn("wmsStatus", Type.STRING, false));
//        columns.add(newColumn("wmsStatusName", Type.STRING, false));
//        columns.add(newColumn("owArrival", Type.BOOL, false));
//        columns.add(newColumn("sourceSystem", Type.STRING, false));
//        columns.add(newColumn("totalFee", Type.STRING, false));
//        columns.add(newColumn("arrivePayCost", Type.STRING, false));
//        columns.add(newColumn("collectionCost", Type.STRING, false));
//        columns.add(newColumn("newIsWmsData", Type.BOOL, false));
//        columns.add(newColumn("afterSalesType", Type.STRING, false));
//        columns.add(newColumn("businessType", Type.STRING, false));
//        columns.add(newColumn("businessTypeName", Type.STRING, false));
//        columns.add(newColumn("businessLook", Type.BOOL, false));
//        columns.add(newColumn("pickingTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("createTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("lastUpdateTime", Type.UNIXTIME_MICROS, false));
//        columns.add(newColumn("version", Type.INT64, false));
//        columns.add(newColumn("creator", Type.STRING, false));
//        columns.add(newColumn("creatorName", Type.STRING, false));
//        columns.add(newColumn("modifier", Type.STRING, false));
//        columns.add(newColumn("modifierName", Type.STRING, false));
//        columns.add(newColumn("isSign", Type.BOOL, false));


        Schema schema = new Schema(columns);


        //创建表时提供的所有选项
        CreateTableOptions options = new CreateTableOptions();
        // 设置表的replica备份和分区规则
        List<String> parcols = new LinkedList<String>();
        parcols.add("waybillId");

        //设置表的备份数
        options.setNumReplicas(1);
        //设置range分区
        options.setRangePartitionColumns(parcols);
        //设置hash分区和数量
        options.addHashPartitions(parcols, 3);
        try {
            client.createTable("waybill_main", schema, options);
        } catch (KuduException e) {
            e.printStackTrace();
        }
        client.close();
    }
}