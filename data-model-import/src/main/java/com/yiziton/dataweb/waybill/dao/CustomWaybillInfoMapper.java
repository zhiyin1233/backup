package com.yiziton.dataweb.waybill.dao;

import com.github.pagehelper.Page;
import com.yiziton.dataweb.waybill.bean.*;
import com.yiziton.dataweb.waybill.dataobject.WaybillbaseInfoDO;
import com.yiziton.dataweb.waybill.vo.WayBillConditionVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * @author wanglianbo
 * @date 2018-12-04 18:53
 */
public interface CustomWaybillInfoMapper {

    /**
     * 根据条件分页查询运单信息(包含货物信息)
     *
     * @param wayBillConditionVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<WayBillInfo> selectWaybillInfoListByCondition(@Param("wayBillConditionVo") WayBillConditionVO wayBillConditionVo,
                                                       @Param("pageNumKey") int pageNum,
                                                       @Param("pageSizeKey") int pageSize);

    /**
     * 查询操作明细的值
     * @param waybillId
     * @param operation
     * @param field
     * @return
     */
    String selectWaybillOperationDetailVal(@Param("waybillId") String waybillId, @Param("operation") Integer operation, @Param("field") String field);

    /**
     * 查询运单费用总额
     * @param waybillId
     * @param billingObject
     * @param billingType
     * @return
     */
    BigDecimal selectWaybillBillingSum(@Param("waybillId") String waybillId, @Param("billingObject") Integer billingObject, @Param("billingType") Integer billingType);





    List<GoodsInfo> selectGoodsInfo(WayBillConditionVO wayBillConditionVO);

    List<WaybillRelationInfo> selectWaybillRelationInfo(WaybillRelationInfo waybillRelationInfo);

    /**
     * 根据运单相关联的异常信息
     *
     * @param wayBillConditionVo
     * @return
     */
    List<ExceptionInfo> selectExceptionInfoList(WayBillConditionVO wayBillConditionVo);

    /**
     * 查询运单的轨迹信息
     *
     * @param wayBillConditionVo
     * @return
     */
    List<MilestoneInfo> selectMilestoneInfoList(WayBillConditionVO wayBillConditionVo);

    /**
     * 查询运单的轨迹节点的操作明细信息
     * 已经合并到selectMilestoneInfoList中查询
     *
     * @param wayBillConditionVo
     * @return
     */
    List<OperationDetailInfo> selectOperationDetailList(WayBillConditionVO wayBillConditionVo);

    /**
     * 查询运单的费用流水明细
     *
     * @param wayBillConditionVO
     * @return
     */
    List<BillDetailInfo> selectBillDetailList(WayBillConditionVO wayBillConditionVO);

    /**
     * 查询运单的售后任务信息
     *
     * @param wayBillConditionVO
     * @return
     */
    List<AfterSaleTaskInfo> selectAfterSaleTaskList(WayBillConditionVO wayBillConditionVO);

    /**
     * 查询运单的承接信息
     *
     * @param wayBillConditionVO
     * @return
     */
    TransferInfo selectTransferInfo(WayBillConditionVO wayBillConditionVO);

    /**
     * 查询运单的节点信息
     *
     * @param waybillNodeInfo
     * @return
     */
    List<WaybillNodeInfo> selectWaybillNodeInfo(WaybillNodeInfo waybillNodeInfo);


    List<HashMap<String, Object>> selectClaimInfo(String waybillId);

    HashMap<String, Object> selectClaimProcessInfo(@Param("waybillId") String waybillId, @Param("relatedBillId") String relatedBillId);

    HashMap<String, Object> selectClaimSettlementInfo(@Param("waybillId") String waybillId, @Param("relatedBillId") String relatedBillId);

    List<HashMap<String, Object>> selectChaseClaimInfo(String waybillId);

    HashMap<String, Object> selectChaseClaimProcessInfo(@Param("waybillId") String waybillId, @Param("relatedBillId") String relatedBillId);

    HashMap<String, Object> selectChaseClaimSettlementInfo(@Param("waybillId") String waybillId, @Param("relatedBillId") String relatedBillId);

    /**
     * 查询所有正向节点
     * @param waybillId
     * @return
     */
    List<MilestoneInfo> selectWaybillMilestone(@Param("waybillId") String waybillId);

    /**
     * 查询所有操作节点
     * @param waybillId
     * @return
     */
    List<MilestoneInfo> selectAllWaybillMilestone(@Param("waybillId") String waybillId);

    /**
     * 查询增值服务收款状态
     * @param waybillId
     * @return
     */
    String selectWaybillIncrementStatus(@Param("waybillId") String waybillId);

    /**
     * 查询运单基本信息
     * @param waybillId
     * @return
     */
    WaybillbaseInfoDO findWaybillBaseInfo(@Param("waybillId") String waybillId);

    List<HashMap<String, String>> selectApplyClaimType(@Param("waybillId") String waybillId);

    List<HashMap<String, String>> selectHandleClaimType(@Param("waybillId") String waybillId);
}
