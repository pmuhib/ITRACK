package com.client.itrack.model;

/**
 * Created by sony on 19-09-2016.
 */
public class DFTPaymentTypeModel {

    private String payTypeId ;
    private String payType ;

    public DFTPaymentTypeModel(String payType, String payTypeId) {
        this.payType = payType;
        this.payTypeId = payTypeId;
    }

    public String getPayType() {
        return payType;
    }

    public String getPayTypeId() {
        return payTypeId;
    }


}
