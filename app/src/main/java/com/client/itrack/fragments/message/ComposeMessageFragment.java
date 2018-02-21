package com.client.itrack.fragments.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.adapters.CompanySelectorAdapter;
import com.client.itrack.adapters.EmployeeAdapter;
import com.client.itrack.adapters.EmployeeSpinnerAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.ClientModel;
import com.client.itrack.model.EmployeeModel;
import com.client.itrack.utility.Constants;

import com.client.itrack.utility.DividerItemDecoration;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sony on 07-06-2016.
 */
public class ComposeMessageFragment extends Fragment {

    View view  ;
    String senderId,senderEmpId,receiverEmpId , receiverId ,senderType ,receiverType;
    ArrayList<ClientModel> listClients = new ArrayList<>() ;
    ArrayList<EmployeeModel> listClientCompEmployee= new ArrayList<>() ;
    Spinner companySpinner,spinnerCompEmployee ;
    EditText etMessageBody ,etMessageSubject ,etAdminName;
    TextView tvSendMessage ,tvCancelMessageSend,lblClients ,lblEmployee;
    private Spinner spinnerAdminEmployee;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.compose_message_fragment,container,false);

        setUpToolbar();
        setupGUI();
        bindDataToGUI();
        setupGUISettings();

        return view ;
    }


    private void sendMessageToClient(String senderId,String senderEmpId, String receiverId,String receiverEmpId, String msgSubject, String msgBody, String senderType, String receiverType) {

        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL + "msgto";
        HashMap<String ,String> data =  new HashMap<>();

        data.put("sender_id",senderId);     // Logged in Id ---> Sender Id
        data.put("reciver_id",receiverId);          // Receiver Client Id
        data.put("sender_employee_id",senderEmpId); // Sender Employee Id Logged in Emp Id
        data.put("receiver_employee_id",receiverEmpId);
        data.put("sender_type",senderType);   // loggedIn User Type // Sender Type
        data.put("reciver_type",receiverType);
        data.put("subject",msgSubject);
        data.put("message",msgBody);

        HttpPostRequest.doPost(getActivity(), url,Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    String msg  =  jobj.getString("msg");
                    if (status) {
                        Toast.makeText(getActivity(),msg, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(),msg, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void bindDataToGUI() {
        Bundle bundle = getArguments() ;
        String msgSubject="";
        if(bundle!=null)
        {
            senderId =  bundle.getString("senderId");
            senderEmpId =  bundle.getString("senderEmpId");
            senderType=  bundle.getString("senderType");
            receiverId = bundle.getString("receiverId");
            receiverEmpId =  bundle.getString("receiverEmpId");
            receiverType = bundle.getString("receiverType");
            msgSubject = bundle.getString("msgSubject");
        }
        etMessageSubject.setText(msgSubject);
    }

    private void setupGUISettings() {
        switch(receiverType)
        {
            case Constants.ADMIN_TYPE :
                // Admin GUI Setting
                etAdminName.setVisibility(View.VISIBLE);
                lblEmployee.setText("Employee");
                etAdminName.setText("Admin");
                lblClients.setVisibility(View.GONE);
                companySpinner.setVisibility(View.GONE);
                spinnerCompEmployee.setVisibility(View.GONE);
                spinnerAdminEmployee.setVisibility(View.GONE);
                break ;

            case Constants.CLIENT_EMP_TYPE :
                // Client Employee GUI Setting
                lblEmployee.setText("Employee");
                etAdminName.setVisibility(View.GONE);
                lblClients.setVisibility(View.VISIBLE);
                companySpinner.setVisibility(View.VISIBLE);
                spinnerCompEmployee.setVisibility(View.VISIBLE);
                spinnerAdminEmployee.setVisibility(View.GONE);
                if(Utils.isNetworkConnected(getActivity(),false))
                    loadClientList();

                break ;

            case Constants.ADMIN_EMP_TYPE:
                lblEmployee.setText("GL. Employee");
                etAdminName.setVisibility(View.GONE);
                lblClients.setVisibility(View.GONE);
                companySpinner.setVisibility(View.GONE);
                spinnerCompEmployee.setVisibility(View.GONE);
                spinnerAdminEmployee.setVisibility(View.VISIBLE);
                // Admin Employee GUI Setting
                if(Utils.isNetworkConnected(getActivity(),false))
                    loadAdminEmployeeList();
                break ;
        }
    }

    private void loadAdminEmployeeList() {
        String url = Constants.BASE_URL + "globalemployeelist";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                try {
                    int selectedAdminEmp = -1 ;
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    listClientCompEmployee.clear();
                    if (status) {
                        EmployeeModel employeeModel  ;
                        employeeModel = new EmployeeModel() ;
                        employeeModel.companyId =   "0" ;
                        employeeModel.companyName = "Please Select Employee";
                        listClientCompEmployee.add(employeeModel);
                        JSONArray jsonArray = jobj.getJSONArray("globalemployee_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);

                            employeeModel = new EmployeeModel();
                            String compEmpId  = jobject.getString("user_id");
                            if(Integer.parseInt(compEmpId)==Integer.parseInt(receiverEmpId))
                            {
                                selectedAdminEmp = i+1;
                            }
                            employeeModel.companyId =  compEmpId ; // Admin Comp Id
                            employeeModel.companyName = jobject.getString("f_name")+" "+jobject.getString("l_name");
                            employeeModel.email = jobject.getString("email");
                            employeeModel.companyLogoName=jobject.getString("image");
                            employeeModel.phoneNumber=jobject.getString("phone_no");
                            employeeModel.type=jobject.getString("type");
                            listClientCompEmployee.add(employeeModel);
                        }

                        EmployeeSpinnerAdapter adapter = new EmployeeSpinnerAdapter(getActivity(),listClientCompEmployee);
                        spinnerAdminEmployee.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        if(selectedAdminEmp!=-1)
                            spinnerAdminEmployee.setSelection(selectedAdminEmp);
                        else
                            spinnerAdminEmployee.setSelection(0);

                    } else {
                        EmployeeSpinnerAdapter adapter = new EmployeeSpinnerAdapter(getActivity(),listClientCompEmployee);
                        spinnerAdminEmployee.setAdapter(adapter);
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

    private void setupGUI() {
        lblClients =  (TextView)view.findViewById(R.id.lblClients) ;
        lblEmployee =  (TextView)view.findViewById(R.id.lblEmployee) ;
        companySpinner= (Spinner)view.findViewById(R.id.company_spinner) ;
        spinnerAdminEmployee= (Spinner)view.findViewById(R.id.spinnerAdminEmployee) ;
        spinnerCompEmployee= (Spinner)view.findViewById(R.id.spinnerCompEmployee) ;
        spinnerCompEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    receiverType  =  listClientCompEmployee.get(position).type ;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        etAdminName = (EditText)view.findViewById(R.id.etAdminName) ;
        etMessageBody = (EditText)view.findViewById(R.id.etMessageBody);
        etMessageSubject = (EditText)view.findViewById(R.id.etMessageSubject) ;
        tvSendMessage = (TextView)view.findViewById(R.id.tvSendMessage);
        tvCancelMessageSend = (TextView)view.findViewById(R.id.tvCancelMessageSend);
        tvCancelMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                    loadEmployeeByClientId(listClients.get(position).companyId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tvSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgBody =  etMessageBody.getText().toString().trim() ;
                String msgSubject =  etMessageSubject.getText().toString().trim() ;
                switch(receiverType)
                {
                    case "admin":
                        break ;

                    case "client" :
                        int indexComp =  companySpinner.getSelectedItemPosition();
                        int indexCompEmp =  spinnerCompEmployee.getSelectedItemPosition();
                        if(indexComp==0)
                        {
                            Toast.makeText(getActivity(), "Please select client.", Toast.LENGTH_SHORT).show();
                            return  ;
                        }
                        if(indexCompEmp==0)
                        {
                            Toast.makeText(getActivity(), "Please select Employee.", Toast.LENGTH_SHORT).show();
                            return  ;
                        }

                        receiverId = listClients.size()>0?listClients.get(indexComp).companyId:"";
                        receiverEmpId = listClientCompEmployee.size()>0? listClientCompEmployee.get(indexCompEmp).companyId:"" ;
                        break ;

                    case "employee" :
                        receiverId = "1" ; // Admin Comp Id
                        int indexCEmp = spinnerAdminEmployee.getSelectedItemPosition();
                        if(indexCEmp==0)
                        {
                            Toast.makeText(getActivity(), "Please select Employee.", Toast.LENGTH_SHORT).show();
                            return  ;
                        }
                        receiverEmpId = listClientCompEmployee.size()>0?listClientCompEmployee.get(indexCEmp).companyId:"";
                        break ;
                }


                if(receiverId.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please add At least one client!", Toast.LENGTH_SHORT).show();
                    return  ;
                }

                if(receiverEmpId.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please add At least one Employee!", Toast.LENGTH_SHORT).show();
                    return  ;
                }

                if(Utils.isNetworkConnected(getActivity(),false))
                    sendMessageToClient(senderId,senderEmpId,receiverId,receiverEmpId,msgSubject,msgBody,senderType,receiverType);
                else
                    Toast.makeText(getActivity(), "No Internet connectivity!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void setUpToolbar() {

        Toolbar toolbarComposeMSG = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ImageView btn_navigation =  (ImageView) toolbarComposeMSG.findViewById(R.id.btn_navigation);
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


    }


    private void loadEmployeeByClientId(String client_id) {

        String url = Constants.BASE_URL+"employeelistbyclientid";
        HashMap<String,String> data= new HashMap<>();
        data.put("client_id",client_id);
        HttpPostRequest.doPost(getActivity(), url,Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                //  Utils.hideLoadingPopup();

                try {
                    int selectedClientEmp = 0 ;
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    listClientCompEmployee.clear();
                    if (status) {
                        EmployeeModel employeeModel  ;
                        employeeModel = new EmployeeModel() ;
                        employeeModel.companyId =   "0" ;
                        employeeModel.companyName = "Please Select Employee";
                        listClientCompEmployee.add(employeeModel);
                        JSONArray jsonArray = jobj.getJSONArray("employee_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);

                            employeeModel = new EmployeeModel();
                            String compEmpId  = jobject.getString("user_id");
                            if(Integer.parseInt(compEmpId)==Integer.parseInt(receiverEmpId))
                            {
                                selectedClientEmp = i+1;
                            }
                            employeeModel.companyId =  compEmpId+"" ;
                            employeeModel.companyName = jobject.getString("f_name")+" "+jobject.getString("l_name");
                            employeeModel.email = jobject.getString("email");
                            employeeModel.companyLogoName=jobject.getString("emp_img");
                            employeeModel.phoneNumber=jobject.getString("phone_no");
                            employeeModel.type=jobject.getString("type");
                            listClientCompEmployee.add(employeeModel);
                        }

                        EmployeeSpinnerAdapter adapter = new EmployeeSpinnerAdapter(getActivity(),listClientCompEmployee);
                        spinnerCompEmployee.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        if(selectedClientEmp!=0)
                            spinnerCompEmployee.setSelection(selectedClientEmp);
                        else
                            spinnerCompEmployee.setSelection(0);

                    } else {
                        EmployeeSpinnerAdapter adapter = new EmployeeSpinnerAdapter(getActivity(),listClientCompEmployee);
                        spinnerCompEmployee.setAdapter(adapter);
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

    private void loadClientList() {
        //Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL + "clientlist";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                // Utils.hideLoadingPopup();
                listClients.clear();
                try {
                    int selectedClient = 0 ;
                    ClientModel clientModel ;
                    clientModel = new ClientModel();
                    clientModel.companyId = "0";
                    clientModel.companyName = "Please Select Company";
                    listClients.add(clientModel);
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {

                        JSONArray jsonArray = jobj.getJSONArray("client_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            clientModel = new ClientModel();
                            String compId  = jobject.getString("comp_id");
                            if(Integer.parseInt(compId)==Integer.parseInt(receiverId))
                            {
                                selectedClient=i+1;
                            }
                            clientModel.companyId =compId;
                            clientModel.companyName = jobject.getString("company_name");
                            clientModel.companyCode = jobject.getString("comp_code");
                            clientModel.email = jobject.getString("company_email");
                            clientModel.companyLogoName = jobject.getString("logo_thumb");
                            clientModel.phoneNumber = jobject.getString("company_phone");
                            clientModel.type =  jobject.getString("type");
                            listClients.add(clientModel);
                        }

                        CompanySelectorAdapter adapter = new CompanySelectorAdapter(getActivity(), listClients);
                        companySpinner.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        if(selectedClient!=0) {
                            companySpinner.setSelection(selectedClient);
                            companySpinner.setEnabled(false);
                            companySpinner.setFocusable(false);
                        }
                        else {
                            companySpinner.setSelection(0);
                            companySpinner.setEnabled(true);
                            companySpinner.setFocusable(true);
                        }

                    } else {
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
