package com.yiziton.dataweb.waybill.vo;

import java.util.List;

/**
 * @author wanglianbo
 * @date 2018-12-13 14:03
 */
public class WaybillNodeVO {

    private String currentOperation;

    private List<String> operationList;

    public String getCurrentOperation() {
        return currentOperation;
    }

    public void setCurrentOperation(String currentOperation) {
        this.currentOperation = currentOperation;
    }

    public List<String> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<String> operationList) {
        this.operationList = operationList;
    }
}
