package com.client.itrack.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sony on 12-05-2016.
 */
public class TruckFinancialDetailModel implements  Serializable {
    public String our_price_customer ;
    public String our_cost ;
    public String advance ;
    public String balance ;
    public String border_charges ;
    public String balance_paid ;
    public String border_charge_include="Yes";
    public ArrayList<TruckDocDetailModel> documents = new ArrayList<>() ;

}
