package com.client.itrack.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.utility.SocialMedia;

public class SocialMediaActivity extends AppCompatActivity {

    ImageView fbLink ,twitterLink,linkedinLink ,instagramLink ,emailUsLink ,whatsappLink ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        fbLink = (ImageView) findViewById(R.id.fbLink);
        fbLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String url  = view.getContentDescription().toString() ;
                showWebView(SocialMedia.FB_URL,SocialMedia.FB);
            }
        });

        twitterLink = (ImageView) findViewById(R.id.twitterLink);
        twitterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String url  = view.getContentDescription().toString() ;
                showWebView(SocialMedia.TWITTER_URL,SocialMedia.TWITTER);
            }
        });
        linkedinLink = (ImageView) findViewById(R.id.linkedinLink);
        linkedinLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String url  = view.getContentDescription().toString() ;
                showWebView(SocialMedia.LINKEDIN_URL,SocialMedia.LINKEDIN);
            }
        });
        instagramLink = (ImageView) findViewById(R.id.instagramLink);
        instagramLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String url  = view.getContentDescription().toString() ;
                showWebView(SocialMedia.INSTAGRAM_URL,SocialMedia.INSTAGRAM);
            }
        });

        whatsappLink = (ImageView) findViewById(R.id.whatsappLink);
        whatsappLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String url  = view.getContentDescription().toString() ;
               // showWebView(SocialMedia.WHATSAPP_URL,SocialMedia.WHATSAPP);
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
//                sendIntent.setPackage("com.whatsapp");
//                sendIntent.setType("text/plain");
//                startActivity(sendIntent);

//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SocialMedia.WHATSAPP_URL));
//                startActivity(browserIntent);


//                Uri mUri = Uri.parse("smsto:8130670372");
//                Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
//                mIntent.setPackage("com.whatsapp");
//                mIntent.putExtra("sms_body", "The text goes here");
//                mIntent.putExtra("chat",true);
//                startActivity(mIntent);

                openWhatsApp();


            }
        });

        emailUsLink = (ImageView) findViewById(R.id.emailUsLink);
        emailUsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] to  ={SocialMedia.EMAIL_US_URL};
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_EMAIL, to);
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(intent, "Send mail"));
                }
            }
        });
    }

    private void openWhatsApp() {
        String smsNumber = "965 9788 8445";
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");//phone number without "+" prefix
            startActivity(sendIntent);
        } else {
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            Toast.makeText(this, "WhatsApp not Installed",
                    Toast.LENGTH_SHORT).show();
            startActivity(goToMarket);
        }
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }





    private void showWebView(String url,String title) {
        Intent intent = new Intent(getApplicationContext(),BrowserActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Toolbar socialMediaToolbar = (Toolbar) findViewById(R.id.socialMediaToolbar);
        final ImageView client_detail_edit  = (ImageView) socialMediaToolbar.findViewById(R.id.client_detail_edit);
        client_detail_edit.setVisibility(View.GONE);
        final ImageView client_detail_more_option  = (ImageView) socialMediaToolbar.findViewById(R.id.client_detail_more_option);
        client_detail_more_option.setVisibility(View.GONE);

        final ImageView btn_navigation  = (ImageView) socialMediaToolbar.findViewById(R.id.btn_navigation);
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextView txt_heading  = (TextView) socialMediaToolbar.findViewById(R.id.txt_heading);
        txt_heading.setText(getResources().getText(R.string.social_media));
    }

//    private void requestGetSocialMedia() {
//
//        String url = Constants.BASE_URL+"get_social_link";
//        Utils.showLoadingPopup(SocialMediaActivity.this);
//        HttpPostRequest.doPost(SocialMediaActivity.this, url, new HttpRequestCallback() {
//            @Override
//            public void response(String errorMessage, String responseData) {
//                Utils.hideLoadingPopup();
//
//                try {
//                    JSONObject jObj = new JSONObject(responseData);
//
//                    JSONArray socialLinksJArr  = jObj.getJSONArray("social_links");
//
//                    for (int indexSocialLinkObj = 0; indexSocialLinkObj < socialLinksJArr.length(); indexSocialLinkObj++) {
//                        JSONObject socialLinkObj =  socialLinksJArr.getJSONObject(indexSocialLinkObj);
//                        String title  = socialLinkObj.getString("title");
//                        switch(title)
//                        {
//                            case  "facebook" :
//                                fbLink.setContentDescription(socialLinkObj.getString("url"));
//                                break ;
//
//                            case "Linkedin" :
//                                linkedinLink.setContentDescription(socialLinkObj.getString("url"));
//                                break ;
//
//                            case "Twitter" :
//                                twitterLink.setContentDescription(socialLinkObj.getString("url"));
//                                break ;
//
//                            case "Email" :
//                                emailUsLink.setContentDescription(socialLinkObj.getString("url"));
//                                break ;
//                        }
//
//                    }
//
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                Utils.hideLoadingPopup();
//            }
//        });
//
//    }
}
