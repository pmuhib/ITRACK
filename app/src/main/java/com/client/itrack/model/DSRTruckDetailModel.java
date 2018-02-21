package com.client.itrack.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sony on 19-05-2016.
 */
public class DSRTruckDetailModel implements Serializable {
    public String dsr_truck_id ;
    public String dsr_id  ;
    public String  driver_name ;

    public String driver_phone,driver_phone1,driver_phone2 ;
    public  String trailor_type;
    public String vehicle_number ;
    public String off_loading_date ;
    public  String detention = "0" ;
    public  String detention_rate= "0" ;
    public  String detention_client = "0" ;// [20-02-2017]
    public  String detention_rate_client = "0" ;// [20-02-2017]
    public String del_receive_date ;
    public String truck_status ;
    public  String detention_cost ; //  detention_cost  = detention*detention_rate
    public String truck_current_location ;
    public String  code_phone_no,code_phone_no1,code_phone_no2 ;
    public String currency_code ;
    public String dest_currency_code ;
    public String remark;
    public String delete_status ;
    public String create_date ;
    public  String detention_location;
    public boolean  isSubmitted=false;
    public TruckFinancialDetailModel truckFinancialDetailModel ;
    public List<AdditionCostModel> listAdditionalCost;
}
