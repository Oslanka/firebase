package com.exutech.chacha.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.exutech.chacha.MainActivity;
import com.exutech.chacha.R;
import com.exutech.chacha.bean.FriendlyMessage;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by EXUTECH007 on 2016/6/2.
 */
public class FirebaseRecyclerAdapter  extends RecyclerView.Adapter {

    private List<FriendlyMessage> list;
    private MainActivity context;


    public FirebaseRecyclerAdapter(List<FriendlyMessage> list,MainActivity context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MainActivity.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MainActivity.MessageViewHolder holder = (MainActivity.MessageViewHolder) viewHolder;
        FriendlyMessage friendlyMessage = list.get(position);
        context.bindHoder(holder,friendlyMessage);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }
}
