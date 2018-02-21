package com.client.itrack.utility;

/**
 * Created by NeoPraveeb on 26/02/16.
 */
public interface Constants {

    String WEB_BASE_URL  = "http://webdevelopmentreviews.net/global/" ;
    String BASE_URL= WEB_BASE_URL+"Webservice/";

    String IMAGE_BASE_URL= WEB_BASE_URL+"assets/uploads/thumbs/company/";
    String IMAGE_BASE_URL_ADMIN =  WEB_BASE_URL+"assets/uploads/generaluser/";
    String NEWS_IMG_BASE_URL = WEB_BASE_URL+"assets/uploads/news/" ;
    String NEWS_THUMBS_IMG_BASE_URL = WEB_BASE_URL+"assets/uploads/thumbs/news/" ;
    //String EVENT_IMG_BASE_URL = WEB_BASE_URL+"assets/uploads/event/" ;
    String EVENT_THUMB_IMG_BASE_URL = WEB_BASE_URL+"assets/uploads/thumbs/event/" ;


    String DFT_DOC_BASE_URL = WEB_BASE_URL+"assets/uploads/attachment/";

    String LOG_TAG = "FANDOOO";
    String LOG_TAG2 = "FANDOOOOOO";
    long HTTP_CONNECTION_TIMEOUT = 30000;

    int REQUEST_CAMERA = 10000 ;
    int REQUEST_GALLERY = 10001 ;

    String ADMIN_TYPE = "admin" ;
    String CLIENT_EMP_TYPE = "client" ;
    String ADMIN_EMP_TYPE = "employee" ;

    String PROJECT_ID =  "256057505821";
}
