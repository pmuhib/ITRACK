package com.client.itrack.fragments;


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
import android.widget.Toast;

import com.client.itrack.activities.NewsDetail;
import com.client.itrack.R;
import com.client.itrack.adapters.NewsAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.ClickListener;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.EventModel;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DividerItemDecoration;
import com.client.itrack.utility.RecyclerTouchListener;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewslistFragments extends Fragment {

    RecyclerView recyclerView;
    ArrayList<EventModel> alleventlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.eventlist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupTopBar();

        alleventlist= new ArrayList<>();
        recyclerView = (RecyclerView)getView().findViewById(R.id.eventItemList);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       // recyclerView.setAdapter(drawerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent= new Intent(getActivity(), NewsDetail.class);
                intent.putExtra("newsid",alleventlist.get(position).event_id);
                intent.putExtra("position",position);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        loadEventData();
    }



    private void setupTopBar() {

        Toolbar newsUpdateToolBar  = ((Toolbar) getActivity().findViewById(R.id.toolbar));
        ImageView btn_navigation = (ImageView)newsUpdateToolBar.findViewById(R.id.btn_navigation);
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });

    }

    private void loadEventData() {

        alleventlist.clear();
        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL+"newslist";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("news_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            EventModel emodel = new EventModel();
                            emodel.event_id = jobject.getString("news_id");
                            emodel.mname = jobject.getString("name");
                            emodel.mdate = jobject.getString("date");
                            emodel.imagename=jobject.getString("image");
                            emodel.des=jobject.getString("des");
                            alleventlist.add(emodel);

                        }

                        NewsAdapter adapter = new NewsAdapter(alleventlist,getActivity());

                        recyclerView.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        // recyclerView.setAdapter(drawerAdapter);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(dividerItemDecoration);

                        adapter.notifyDataSetChanged();

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
