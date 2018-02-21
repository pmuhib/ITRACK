package com.client.itrack.model;

import java.util.ArrayList;

/**
 * Created by sony on 12-05-2016.
 */
public class DSRCargoDetailModel {

    private static DSRCargoDetailModel dsrCargoDetailModel  =  null ;

    public int numberOfTrucks ;
    public ArrayList<DSRTruckDetailModel> listDSRTruckDetails;

    /*public static DSRCargoDetailModel getInstance()
    {
        if(dsrCargoDetailModel !=null)
        {
            dsrCargoDetailModel = new DSRCargoDetailModel();
        }
        return dsrCargoDetailModel ;
    }


    public void clear()
    {
        dsrCargoDetailModel = null ;
    }*/

}