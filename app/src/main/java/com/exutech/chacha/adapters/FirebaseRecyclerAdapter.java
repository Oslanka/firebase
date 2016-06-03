package com.exutech.chacha.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.exutech.chacha.MainActivity;
import com.exutech.chacha.R;
import com.exutech.chacha.bean.FriendlyMessage;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by EXUTECH007 on 2016/6/2.
 */
public class FirebaseRecyclerAdapter extends RecyclerView.Adapter {

    private List<FriendlyMessage> list;
    private MainActivity context;
    private FirebaseUser mFirebaseUser;
    private final int TYPEME = 0;
    private final int TYPEHE = 1;


    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getUid().equals(mFirebaseUser.getUid() + "")) {
            return TYPEME;
        } else {
            return TYPEHE;
        }

    }

    public FirebaseRecyclerAdapter(List<FriendlyMessage> list, MainActivity context, FirebaseUser mFirebaseUser) {
        this.list = list;
        this.context = context;
        this.mFirebaseUser = mFirebaseUser;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType==TYPEHE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null);
        }else if (viewType==TYPEME){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_me, null);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MessageViewHolder holder = (MessageViewHolder) viewHolder;
        FriendlyMessage friendlyMessage = list.get(position);
        bindHoder(holder, friendlyMessage,position);
    }

    @Override
    public int getItemCount() {

        return list == null ? 0 : list.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(com.exutech.chacha.R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(com.exutech.chacha.R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(com.exutech.chacha.R.id.messengerImageView);
        }
    }

    public void bindHoder(MessageViewHolder viewHolder, FriendlyMessage friendlyMessage,int position) {
        viewHolder.messageTextView.setText(friendlyMessage.getText());
        if (!TextUtils.isEmpty(friendlyMessage.getName())) {
            viewHolder.messengerTextView.setText(friendlyMessage.getName());//用户名
        } else {
            viewHolder.messengerTextView.setText(friendlyMessage.getEmail());//用户名不存在设置账户
        }
        //如果是本人 特殊处理
        if (getItemViewType(position)==TYPEME) {
            //自己
            viewHolder.messengerTextView.setText("我");
        }

        if (friendlyMessage.getPhotoUrl() == null) {
            viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Glide.with(context)
                    .load(friendlyMessage.getPhotoUrl())
                    .into(viewHolder.messengerImageView);
        }
    }

}
