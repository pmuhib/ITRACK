package com.client.itrack.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.R;

import com.client.itrack.model.QuoteModel;
import com.client.itrack.utility.AppGlobal;

import java.util.List;


public class QuoteListAdapter extends RecyclerView.Adapter<QuoteListAdapter.MyViewHolder> {

    private List<QuoteModel> quoteList;
    Context ctx;
    AppGlobal appGlobal = AppGlobal.getInstance();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvQuoteClientName, tvQuoteCreatedDate, tvQuoteCode ;

        public MyViewHolder(View view) {
            super(view);
            tvQuoteClientName = (TextView) view.findViewById(R.id.tvQuoteClientName);
            tvQuoteCreatedDate = (TextView) view.findViewById(R.id.tvQuoteCreatedDate);
            tvQuoteCode = (TextView) view.findViewById(R.id.tvQuoteCode);

        }
    }


    public QuoteListAdapter(Activity ctx,List<QuoteModel> cList) {
        this.quoteList = cList;
        this.ctx=ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quote_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QuoteModel quoteModel = quoteList.get(position);
        holder.tvQuoteClientName.setText(quoteModel.clientCompName.trim());
        holder.tvQuoteCreatedDate.setText(quoteModel.quoteCreatedDate);
        holder.tvQuoteCode.setText("Quote Number : "+quoteModel.quoteCode);
    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }
}
