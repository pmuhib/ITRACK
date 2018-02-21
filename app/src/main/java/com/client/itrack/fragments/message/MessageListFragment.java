package com.client.itrack.fragments.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.client.itrack.activities.CategoryContainer;
import com.client.itrack.activities.HomeContainer;
import com.client.itrack.R;
import com.client.itrack.adapters.MessageListAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.ClickListener;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.MessageModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DividerItemDecoration;
import com.client.itrack.utility.RecyclerTouchListener;
import com.client.itrack.utility.SharedPreferenceStore;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sony on 30-05-2016.
 */
public class MessageListFragment extends Fragment {

    View view  ;
    RecyclerView recyclerMessageList ;
    ArrayList<MessageModel> listMessages ;
    String empId ,empType;
    AppGlobal appGlobal = AppGlobal.getInstance();
    private String empCompId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.message_list_fragment,container,false);
        setUpTopBar();
        setUpBottomBar();
        return view ;
    }

    private void setUpTopBar() {

        Toolbar toolbarMessages = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ImageView btn_navigation = (ImageView) toolbarMessages.findViewById(R.id.btn_navigation);
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void setUpBottomBar() {

        RelativeLayout bottomBarMessages =   (RelativeLayout) view.findViewById(R.id.bottombar) ;
        ImageView  composeMessage = (ImageView) bottomBarMessages.findViewById(R.id.composeMessage) ;
        composeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent intent = new Intent(getActivity(), CategoryContainer.class);
                    intent.putExtra("catpos", 51 );
                    intent.putExtra("senderId",empCompId) ; // Logged in User
                    intent.putExtra("senderEmpId",empId) ;
                    intent.putExtra("senderType", empType) ; // Logged-in user Type
                    intent.putExtra("receiverId","0");
                    intent.putExtra("receiverEmpId","0");
                    intent.putExtra("receiverType",setDefaultReceiverType());

                    intent.putExtra("msgSubject","");
                    startActivity(intent);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        });



        ImageView  ivGoToHome = (ImageView) bottomBarMessages.findViewById(R.id.ivGoToHome);
        ivGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeContainer.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

    private String setDefaultReceiverType() {
        String receiver = "" ;
        switch(empType)
        {
            case "employee" :
                receiver =  Constants.CLIENT_EMP_TYPE;
                break ;

            case "client" :
                receiver =  Constants.ADMIN_EMP_TYPE;
                break ;
        }
        return receiver ;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CategoryContainer container = (CategoryContainer) getActivity() ;
        Toolbar toolBarMessage  = ((Toolbar) container.findViewById(R.id.toolbar)) ;

        ImageView btnNavigation  = (ImageView) (toolBarMessage != null ? toolBarMessage.findViewById(R.id.btn_navigation) : null);
        if(btnNavigation!=null) {
            btnNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }

        recyclerMessageList = (RecyclerView) view.findViewById(R.id.recyclerMessageList);
        listMessages = new ArrayList<>();
        MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity(),listMessages) ;
        recyclerMessageList.setAdapter(messageListAdapter);
        recyclerMessageList.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerMessageList, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                RelativeLayout msgDetailContainer= (RelativeLayout) view.findViewById(R.id.msgDetailContainer);
                RelativeLayout msgSummaryContainer= (RelativeLayout) view.findViewById(R.id.msgSummaryContainer);
                msgSummaryContainer.setAlpha(0.6f);
                msgSummaryContainer.setBackgroundResource(R.drawable.msg_rw_bg);
                msgDetailContainer.setVisibility(View.VISIBLE);

                ImageView imgDeleteMsgBtn = (ImageView)msgDetailContainer.findViewById(R.id.imgDeleteMsgBtn);
                imgDeleteMsgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteMessageFromList(listMessages.get(position).messageId,position);

                    }
                });

                ImageView imgReplyMsgBtn= (ImageView) msgDetailContainer.findViewById(R.id.imgReplyMsgBtn);
                imgReplyMsgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try
                        {
                            Intent intent = new Intent(getActivity(), CategoryContainer.class);
                            intent.putExtra("catpos", 51 );
                            intent.putExtra("senderId",empCompId) ;
                            intent.putExtra("senderEmpId",empId) ;
                            intent.putExtra("senderType", empType) ;
                            intent.putExtra("receiverId",listMessages.get(position).senderId);
                            intent.putExtra("receiverType",listMessages.get(position).senderType);
                            intent.putExtra("receiverEmpId",listMessages.get(position).senderEmployeeId);
                            intent.putExtra("msgSubject","RE: "+listMessages.get(position).msgSubject);
                            startActivity(intent);
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                });

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        empId = SharedPreferenceStore.getValue(getActivity(),"Userid","0");
        empType= SharedPreferenceStore.getValue(getActivity(),"Type","employee");
        empCompId = appGlobal.userCompId ;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Utils.isNetworkConnected(getActivity(),false))
            loadMessagesByUserId(empId,empType);
        else
            Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
    }

    private void deleteMessageFromList(String messageId, final int position) {
        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL+"msgdelete";
        HashMap<String ,String> data =  new HashMap<>();

        data.put("msg_id",messageId);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {

                        listMessages.remove(position);
                        MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity(),listMessages) ;
                        recyclerMessageList.setAdapter(messageListAdapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerMessageList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        // recyclerView.setAdapter(drawerAdapter);
                        recyclerMessageList.setItemAnimator(new DefaultItemAnimator());
                        recyclerMessageList.addItemDecoration(dividerItemDecoration);
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }

    private void loadMessagesByUserId(String empId,String empType) {

        listMessages.clear();
        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL+"msglistbyid";
        HashMap<String ,String> data =  new HashMap<>();

        data.put("login_id",empId); //Login Id
        data.put("rec_type",empType);  // login Type
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("msg_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            MessageModel messageModel = new MessageModel();
                            messageModel.messageId = jobject.getString("id") ;
                            messageModel.senderId =  jobject.getString("sender") ;  // Sender Id
                            messageModel.senderEmployeeId = jobject.getString("sender_employee_id") ;
                            messageModel.receiverEmployeeId = jobject.getString("receiver_employee_id") ;
                            String senderType =  jobject.getString("sender_type") ;
                            messageModel.senderType= senderType ;
                            String rec_type =jobject.getString("rec_type") ;
                            messageModel.recType = rec_type ;
                            switch(senderType)
                            {
                                case Constants.ADMIN_TYPE :
                                    messageModel.senderFName= "Admin" ;
                                    messageModel.senderLName= "" ;
                                    break ;

                                case Constants.CLIENT_EMP_TYPE:
                                    messageModel.senderFName= jobject.getString("f_name") ;
                                    messageModel.senderLName= jobject.getString("l_name") ;
                                    break ;

                                case Constants.ADMIN_EMP_TYPE :
                                    messageModel.senderFName= jobject.getString("f_name") ;
                                    messageModel.senderLName= jobject.getString("l_name") ;
                                    break ;
                            }
                            switch(rec_type)
                            {
                                case Constants.ADMIN_EMP_TYPE :
                                    messageModel.senderImg =jobject.getString("emp_img") ;
                                    break ;

                                case Constants.CLIENT_EMP_TYPE :
                                    messageModel.senderImg =jobject.getString("image") ;
                                    break ;
                            }


                            messageModel.recId =jobject.getString("receiver") ;
                            messageModel.msgCreateDate = jobject.getString("create_date") ;
                            messageModel.msgCreateTime =jobject.getString("time") ;
                            messageModel.msgContent =jobject.getString("message") ;
                            messageModel.msgSubject =jobject.getString("subject") ;

//                            switch(jobject.getString("readunread"))
//                            {
//                                case "0" :
//                                    messageModel.isRead = true ;
//                                    break ;
//
//                                case "1" :
//                                    messageModel.isRead = false ;
//                                    break;
//                            }
                            listMessages.add(messageModel);
                        }

                        MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity(),listMessages) ;
                        recyclerMessageList.setAdapter(messageListAdapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerMessageList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        // recyclerView.setAdapter(drawerAdapter);
                        recyclerMessageList.setItemAnimator(new DefaultItemAnimator());
                        recyclerMessageList.addItemDecoration(dividerItemDecoration);

                    } else {
                        MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity(),listMessages) ;
                        recyclerMessageList.setAdapter(messageListAdapter);
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }
}
