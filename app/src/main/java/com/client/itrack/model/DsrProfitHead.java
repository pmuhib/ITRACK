package com.client.itrack.model;

import java.util.ArrayList;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class DsrProfitHead {

    private String Refno;
    private String NetProfit;

    ArrayList<DsrProfitDetail> dsrprofitdetail=new ArrayList<>();

   /* public DsrProfitHead(String refno, String netProfit) {
        Refno = refno;
        NetProfit = netProfit;
    }
*/
    public ArrayList<DsrProfitDetail> getDsrprofitdetail() {
        return dsrprofitdetail;
    }

    public void setDsrprofitdetail(ArrayList<DsrProfitDetail> dsrprofitdetail) {
        this.dsrprofitdetail = dsrprofitdetail;
    }

    public String getRefno() {
        return Refno;
    }

    public void setRefno(String refno) {
        Refno = refno;
    }

    public String getNetProfit() {
        return NetProfit;
    }

    public void setNetProfit(String netProfit) {
        NetProfit = netProfit;
    }

}
