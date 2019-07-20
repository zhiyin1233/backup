package com.yiziton.dataweb.waybill.vo;

/**
 * @author wanglianbo
 * @date 2018-12-05 21:07
 */
public class TransferInfoVO {

    private String waybillId;

    //---------- 干线运输信息----------

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

    //---------- 直营网点信息----------

    private String directDeptName;

    private String directDeptCode;

    private String directDeptType;

    private String directDeptMaster;

    private String directLimitAppointmentTime;

    private String directActualAppointmentTime;

    private String directLimitDistributeTime;

    private String directActualDistributeTime;


    //---------- 安装师傅信息----------


    private String masterName;

    private String masterPhone;

    private String limitAppointmentTime;

    private String actualAppoinmentTime;

    private String limitDoorTime;

    private String actualDoorTime;

    private String limitSignTime;

    private String actualSignTime;

    //---------- 直营中转信息 ----------

    private String transferCarrier;

    private String transferOutDeliverRelayId;

    private String transferCarrierDepartureLine;

    private String transferCarrierDepartureTime;

    private String transferLimitArrivalTime;

    private String transferActualArrivalTime;

    private String transferTransferBillId;

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

    public String getDirectDeptName() {
        return directDeptName;
    }

    public void setDirectDeptName(String directDeptName) {
        this.directDeptName = directDeptName;
    }

    public String getDirectDeptCode() {
        return directDeptCode;
    }

    public void setDirectDeptCode(String directDeptCode) {
        this.directDeptCode = directDeptCode;
    }

    public String getDirectDeptType() {
        return directDeptType;
    }

    public void setDirectDeptType(String directDeptType) {
        this.directDeptType = directDeptType;
    }

    public String getDirectDeptMaster() {
        return directDeptMaster;
    }

    public void setDirectDeptMaster(String directDeptMaster) {
        this.directDeptMaster = directDeptMaster;
    }

    public String getDirectLimitAppointmentTime() {
        return directLimitAppointmentTime;
    }

    public void setDirectLimitAppointmentTime(String directLimitAppointmentTime) {
        this.directLimitAppointmentTime = directLimitAppointmentTime;
    }

    public String getDirectActualAppointmentTime() {
        return directActualAppointmentTime;
    }

    public void setDirectActualAppointmentTime(String directActualAppointmentTime) {
        this.directActualAppointmentTime = directActualAppointmentTime;
    }

    public String getDirectLimitDistributeTime() {
        return directLimitDistributeTime;
    }

    public void setDirectLimitDistributeTime(String directLimitDistributeTime) {
        this.directLimitDistributeTime = directLimitDistributeTime;
    }

    public String getDirectActualDistributeTime() {
        return directActualDistributeTime;
    }

    public void setDirectActualDistributeTime(String directActualDistributeTime) {
        this.directActualDistributeTime = directActualDistributeTime;
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

    public String getTransferCarrier() {
        return transferCarrier;
    }

    public void setTransferCarrier(String transferCarrier) {
        this.transferCarrier = transferCarrier;
    }

    public String getTransferOutDeliverRelayId() {
        return transferOutDeliverRelayId;
    }

    public void setTransferOutDeliverRelayId(String transferOutDeliverRelayId) {
        this.transferOutDeliverRelayId = transferOutDeliverRelayId;
    }

    public String getTransferCarrierDepartureLine() {
        return transferCarrierDepartureLine;
    }

    public void setTransferCarrierDepartureLine(String transferCarrierDepartureLine) {
        this.transferCarrierDepartureLine = transferCarrierDepartureLine;
    }

    public String getTransferCarrierDepartureTime() {
        return transferCarrierDepartureTime;
    }

    public void setTransferCarrierDepartureTime(String transferCarrierDepartureTime) {
        this.transferCarrierDepartureTime = transferCarrierDepartureTime;
    }

    public String getTransferLimitArrivalTime() {
        return transferLimitArrivalTime;
    }

    public void setTransferLimitArrivalTime(String transferLimitArrivalTime) {
        this.transferLimitArrivalTime = transferLimitArrivalTime;
    }

    public String getTransferActualArrivalTime() {
        return transferActualArrivalTime;
    }

    public void setTransferActualArrivalTime(String transferActualArrivalTime) {
        this.transferActualArrivalTime = transferActualArrivalTime;
    }

    public String getTransferTransferBillId() {
        return transferTransferBillId;
    }

    public void setTransferTransferBillId(String transferTransferBillId) {
        this.transferTransferBillId = transferTransferBillId;
    }
}
