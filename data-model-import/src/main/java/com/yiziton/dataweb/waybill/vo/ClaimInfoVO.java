package com.yiziton.dataweb.waybill.vo;

import com.yiziton.dataweb.waybill.bean.ChaseClaimInfo;
import com.yiziton.dataweb.waybill.bean.ClaimInfo;

import java.util.List;

/**
 * @author wanglianbo
 * @date 2018-12-09 15:07
 */
public class ClaimInfoVO {

    private String waybillId;

    private List<ClaimInfo> claimInfoList;

    private List<ChaseClaimInfo> chaseClaimInfoList;

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public List<ClaimInfo> getClaimInfoList() {
        return claimInfoList;
    }

    public void setClaimInfoList(List<ClaimInfo> claimInfoList) {
        this.claimInfoList = claimInfoList;
    }

    public List<ChaseClaimInfo> getChaseClaimInfoList() {
        return chaseClaimInfoList;
    }

    public void setChaseClaimInfoList(List<ChaseClaimInfo> chaseClaimInfoList) {
        this.chaseClaimInfoList = chaseClaimInfoList;
    }
}
