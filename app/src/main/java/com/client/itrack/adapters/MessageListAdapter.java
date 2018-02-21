package com.client.itrack.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.MessageModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sony on 30-05-2016.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

    AppGlobal appGlobal = AppGlobal.getInstance();
    private final Context context;
    private final List<MessageModel> listMessages;

    public MessageListAdapter( Context context,List<MessageModel> listMessages) {
        this.context  = context  ;
        this.listMessages =  listMessages ;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_list_item_layout,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        MessageModel messageModel = listMessages.get(position) ;
        String senderId =  messageModel.senderId ;
        String senderType =  messageModel.senderType ;
        String senderName =  messageModel.senderFName+" "+messageModel.senderLName ;
        holder.tvSenderName.setText(senderName.trim()) ;
        holder.tvMsgDetail.setText(messageModel.msgContent);
        holder.msgSubject.setText(messageModel.msgSubject);
        holder.msgDate.setText(messageModel.msgCreateDate);
        holder.msgTime.setText(messageModel.msgCreateTime);

        Picasso.with(context).load(Constants.IMAGE_BASE_URL+messageModel.senderImg).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(holder.imageSender);
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView tvSenderName,msgDate,msgTime,msgSubject,tvMsgDetail;
        ImageView imageSender,imgDeleteMsgBtn;

        public MessageViewHolder(View itemView) {
            super(itemView);
            final RelativeLayout msgDetailContainer = (RelativeLayout)itemView.findViewById(R.id.msgDetailContainer);
            final RelativeLayout msgSummaryContainer = (RelativeLayout)itemView.findViewById(R.id.msgSummaryContainer);
            tvSenderName = (TextView) itemView.findViewById(R.id.tvSenderName) ;
            msgDate =  (TextView) itemView.findViewById(R.id.msgDate);
            msgTime = (TextView) itemView.findViewById(R.id.msgTime);
            msgSubject = (TextView)  itemView.findViewById(R.id.msgSubject);
            imageSender = (ImageView) itemView.findViewById(R.id.imageSender);

            imgDeleteMsgBtn = (ImageView)msgDetailContainer.findViewById(R.id.imgDeleteMsgBtn);
            ImageView imgCloseMsgBtn =  (ImageView) msgDetailContainer.findViewById(R.id.imgCloseMsgBtn);
            imgCloseMsgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgDetailContainer.setVisibility(View.GONE);
                    msgSummaryContainer.setBackgroundResource(0);
                    msgSummaryContainer.setAlpha(1.0f);
                }
            });
            tvMsgDetail =  (TextView) msgDetailContainer.findViewById(R.id.tvMsgDetail);
        }
    }

}

