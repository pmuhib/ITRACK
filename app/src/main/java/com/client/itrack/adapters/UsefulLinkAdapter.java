package com.client.itrack.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.client.itrack.R;
import com.client.itrack.activities.BrowserActivity;
import com.client.itrack.model.LinkModel;
import com.client.itrack.model.UsefulLinkModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Atul on 4/15/2016.
 */
/*
public class EventAdapter  extends RecyclerView.A{
}
*/


public class UsefulLinkAdapter extends RecyclerView.Adapter<UsefulLinkAdapter.MyViewHolder> {

    private List<UsefulLinkModel> listUsefulLinks;
    Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleUsefulLink;
        public LinearLayout urlsContainer ;
        public MyViewHolder(View view) {
            super(view);
            titleUsefulLink = (TextView) view.findViewById(R.id.titleUsefulLink);
            urlsContainer = (LinearLayout) view.findViewById(R.id.urlsContainer);
        }
    }


    public UsefulLinkAdapter(List<UsefulLinkModel> listUsefulLinks, Activity ctx) {
        this.listUsefulLinks = listUsefulLinks;
        this.ctx = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.uesful_link_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UsefulLinkModel   usefulLinkModel = listUsefulLinks.get(position);
        holder.titleUsefulLink.setText(usefulLinkModel.category);
        holder.urlsContainer.removeAllViews();
        ArrayList<LinkModel> urls = usefulLinkModel.urls ;

        for (int indexUrl = 0; indexUrl < urls.size(); indexUrl++) {
            LinkModel linkModel =  urls.get(indexUrl);
            final String title = linkModel.title;
            final String url = linkModel.url;
            TextView textView = new TextView(ctx);
            textView.setPadding(0,0,0,30);
            textView.setText(title);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(R.style.GLLblNormalStyle);
            }else{
                textView.setTextAppearance(ctx,R.style.GLLblNormalStyle);
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx,BrowserActivity.class);
                    intent.putExtra("url",url);
                    intent.putExtra("title",title);
                    ctx.startActivity(intent);
                }
            });
            holder.urlsContainer.addView(textView);
        }


    }

    @Override
    public int getItemCount() {
        return listUsefulLinks.size();
    }
}
