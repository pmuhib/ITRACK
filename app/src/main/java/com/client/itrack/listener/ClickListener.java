package com.client.itrack.listener;

import android.view.View;

/**
 * Created by NITISH on 4/17/2016.
 */
public interface ClickListener {

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
