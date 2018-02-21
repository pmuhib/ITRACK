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
import com.client.itrack.model.DrawerModel;

import java.util.ArrayList;



public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.UniLookHolder> {
    private final ArrayList<DrawerModel> allData;
    private final Context context;

    public DrawerAdapter(ArrayList<DrawerModel> allData, Context context) {
        this.allData = allData;
        this.context = context;
    }

    @Override
    public UniLookHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.menu_item_layout, viewGroup, false);
        UniLookHolder holder = new UniLookHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final UniLookHolder holder, final int position) {
        final DrawerModel model1 = allData.get(position);
      //  Toast.makeText(context, "" + getItemCount() + allData.size(), Toast.LENGTH_SHORT).show();

        updateModel(holder, model1, position);

    }


    private void updateModel(UniLookHolder holder, DrawerModel model1, int i) {
       // TypedArray typedArray = model1.imageUrl;
        holder.itemTitle.setText(model1.itemTitle);
 //       holder.itemPrice.setText(model1.cardScore);
      //  holder.itemIcon.setImageResource(typedArray.getResourceId(i, -1));
        holder.itemIcon.setImageResource(model1.drawerCardIcon);
    }

    @Override
    public int getItemCount() {
        return allData.size();
    }

    class UniLookHolder extends RecyclerView.ViewHolder {
        View rootView;
        private TextView itemTitle;
        private ImageView itemPrice;
        private ImageView itemIcon;


        public UniLookHolder(View view) {
            super(view);
            rootView = view;
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemPrice = (ImageView) view.findViewById(R.id.drawer_righticon);
            itemIcon = (ImageView) view.findViewById(R.id.item_icon);

        }
    }
}
