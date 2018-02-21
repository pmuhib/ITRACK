package com.client.itrack.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.EventModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by NITISH on 4/15/2016.
 */
/*
public class EventAdapter  extends RecyclerView.A{
}
*/


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    AppGlobal appGlobal = AppGlobal.getInstance();
    private List<EventModel> moviesList;
    Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView newsNumber, mdate, desc;
        public ImageView circleicon;

        public MyViewHolder(View view) {
            super(view);
            newsNumber = (TextView) view.findViewById(R.id.newsNumber);
            mdate = (TextView) view.findViewById(R.id.mdate);
            desc = (TextView) view.findViewById(R.id.description);
            circleicon = (ImageView) view.findViewById(R.id.imageicon);
        }
    }


    public EventAdapter(List<EventModel> moviesList,Activity ctx) {
        this.moviesList = moviesList;
        this.ctx=ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eventitemlayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EventModel movie = moviesList.get(position);
      //  String newsPos = (position)<9 ?"0"+(position+1) : ""+(position+1) ;
        holder.newsNumber.setText(movie.mname.trim());
        holder.mdate.setText(movie.mdate);
        holder.desc.setText(Html.fromHtml(movie.des));
        Picasso.with(ctx).load(Constants.EVENT_THUMB_IMG_BASE_URL+movie.imagename).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(holder.circleicon);
        //   Picasso.with(ctx).load()
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
