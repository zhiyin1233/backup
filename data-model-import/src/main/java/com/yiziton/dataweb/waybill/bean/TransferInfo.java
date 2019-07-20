package com.yiziton.dataweb.waybill.bean;

/**
 * 承接信息
 *
 * @author wanglianbo
 * @Timestamp 2018-12-05 18:38
 */
public class TransferInfo {

    private String waybillId;

    // TODO: 2018-12-05 缺少干线运输信息

    private String carrier;

    private String outDeliverRelayId;

    private String carrierDepartureLine;

    private String carrierDepartureTime;

    private String limitArrivalTime;

    private String actualArrivalTime;

    private String transferBillId;

    private String carrierReceiver;

    private String carrierReceiverPhone;

    private String carrierAddress;

    private String pickUpMethod;

    //网点信息
    private String masterNode;

    private String nodeCode;

    private String nodeType;

    private String nodeDutyName;

    private String masterName;

    private String masterPhone;

    private String limitDistributeTime;

    private String actualDistributeTime;

    private String limitAppointmentTime;

    private String actualAppoinmentTime;

    private String limitDoorTime;

    private String actualDoorTime;

    private String limitSignTime;

    private String actualSignTime;

    private Integer masterType;

    // TODO: 2018-12-05  缺少直营中转信息


    public Integer getMasterType() {
        return masterType;
    }

    public void setMasterType(Integer masterType) {
        this.masterType = masterType;
    }

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getOutDeliverRelayId() {
        return outDeliverRelayId;
    }

    public void setOutDeliverRelayId(String outDeliverRelayId) {
        this.outDeliverRelayId = outDeliverRelayId;
    }

    public String getCarrierDepartureLine() {
        return carrierDepartureLine;
    }

    public void setCarrierDepartureLine(String carrierDepartureLine) {
        this.carrierDepartureLine = carrierDepartureLine;
    }

    public String getCarrierDepartureTime() {
        return carrierDepartureTime;
    }

    public void setCarrierDepartureTime(String carrierDepartureTime) {
        this.carrierDepartureTime = carrierDepartureTime;
    }

    public String getLimitArrivalTime() {
        return limitArrivalTime;
    }

    public void setLimitArrivalTime(String limitArrivalTime) {
        this.limitArrivalTime = limitArrivalTime;
    }

    public String getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(String actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public String getTransferBillId() {
        return transferBillId;
    }

    public void setTransferBillId(String transferBillId) {
        this.transferBillId = transferBillId;
    }

    public String getCarrierReceiver() {
        return carrierReceiver;
    }

    public void setCarrierReceiver(String carrierReceiver) {
        this.carrierReceiver = carrierReceiver;
    }

    public String getCarrierReceiverPhone() {
        return carrierReceiverPhone;
    }

    public void setCarrierReceiverPhone(String carrierReceiverPhone) {
        this.carrierReceiverPhone = carrierReceiverPhone;
    }

    public String getCarrierAddress() {
        return carrierAddress;
    }

    public void setCarrierAddress(String carrierAddress) {
        this.carrierAddress = carrierAddress;
    }

    public String getPickUpMethod() {
        return pickUpMethod;
    }

    public void setPickUpMethod(String pickUpMethod) {
        this.pickUpMethod = pickUpMethod;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getMasterPhone() {
        return masterPhone;
    }

    public void setMasterPhone(String masterPhone) {
        this.masterPhone = masterPhone;
    }

    public void setLimitDistributeTime(String limitDistributeTime) {
        this.limitDistributeTime = limitDistributeTime;
    }

    public void setActualDistributeTime(String actualDistributeTime) {
        this.actualDistributeTime = actualDistributeTime;
    }

    public String getLimitAppointmentTime() {
        return limitAppointmentTime;
    }

    public void setLimitAppointmentTime(String limitAppointmentTime) {
        this.limitAppointmentTime = limitAppointmentTime;
    }

    public String getActualAppoinmentTime() {
        return actualAppoinmentTime;
    }

    public void setActualAppoinmentTime(String actualAppoinmentTime) {
        this.actualAppoinmentTime = actualAppoinmentTime;
    }

    public String getLimitDoorTime() {
        return limitDoorTime;
    }

    public void setLimitDoorTime(String limitDoorTime) {
        this.limitDoorTime = limitDoorTime;
    }

    public String getActualDoorTime() {
        return actualDoorTime;
    }

    public void setActualDoorTime(String actualDoorTime) {
        this.actualDoorTime = actualDoorTime;
    }

    public String getLimitSignTime() {
        return limitSignTime;
    }

    public void setLimitSignTime(String limitSignTime) {
        this.limitSignTime = limitSignTime;
    }

    public String getActualSignTime() {
        return actualSignTime;
    }

    public void setActualSignTime(String actualSignTime) {
        this.actualSignTime = actualSignTime;
    }

    public String getMasterNode() {
        return masterNode;
    }

    public void setMasterNode(String masterNode) {
        this.masterNode = masterNode;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeDutyName() {
        return nodeDutyName;
    }

    public void setNodeDutyName(String nodeDutyName) {
        this.nodeDutyName = nodeDutyName;
    }

    public String getLimitDistributeTime() {
        return limitDistributeTime;
    }

    public String getActualDistributeTime() {
        return actualDistributeTime;
    }
}
