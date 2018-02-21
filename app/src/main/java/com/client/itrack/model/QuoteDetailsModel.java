package com.client.itrack.model;

import java.io.Serializable;

/**
 * Created by sony on 20-06-2016.
 */
public class QuoteDetailsModel implements Serializable {

    /****** Services Details *******/
    public String quoteId ="" ;
    public String client_id ="" ;
    public String clientEmpId = "";
    public String servicesOfInterests ="" ;
    public String loading_id  = "" ;
    public String origin="" ;
    public String loadingCity ="" ;
    public String loadingState ="" ;
    public String loadingCountry="";
    public String destination ="";
    public String destinationCity ="" ;
    public String destinationState ="" ;
    public String destinationCountry="" ;
    public String commodity="" ;
    public String pieces="" ;
    public String weight ="" ;
    public String others="" ;
    public String addServices ="";
    public boolean isOtherOrigin =  false  ;
    /****** Contact Details *******/
    public String account="" ;
    public String empName ="" ;
    public String clientCompName ="" ;
    public String countryCodePhone ="" ;
    public String clientEmpPhone ="" ;
    public String clientEmpFax="" ;
    public String clientEmpEmail="" ;
    public String clientEmpAddres ="";
    public String cityCode ="" ;
    public String stateCode ="" ;
    public String countryCode ="" ;
    public String ziocode="" ;
    public String industry ="" ;
    public String contactMethod ="";
    public String contactMessage="" ;
    public String createdDate=""  ;
    public String quoteStatus ="" ;
    public String parposal_status ="" ;
    public String currency_code="" ;
    public String price ="";
    public String response_message="";

}
