package com.exutech.chacha;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.exutech.chacha.bean.FriendlyMessage;
import com.exutech.chacha.utils.LogUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by EXUTech on 16/6/3.
 */
public  class FirebaseLisener  {
    private  int firstPosition =0;
    public static FirebaseLisener firebaseLisener = null;
    public static FirebaseLisener getInstance() {
        if (null== firebaseLisener) firebaseLisener = new FirebaseLisener();
        return firebaseLisener;
    }

    public void onFirebaseLisener(final RecyclerView mMessageRecyclerView2, final LinearLayoutManager mLinearLayoutManager2, DatabaseReference mFirebaseDatabaseReference, String MESSAGES_CHILD, final CallbackFriendlyMessage callbackFriendlyMessage){

        mLinearLayoutManager2.setStackFromEnd(true);


//        DatabaseReference child = mFirebaseDatabaseReference.child(MESSAGES_CHILD);


        final RecyclerView.Adapter  mFirebaseAdapter2 = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(
                FriendlyMessage.class,
                com.exutech.chacha.R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage friendlyMessage, int position) {
                LogUtil.getInstance().d(friendlyMessage.getText());
//                if (firstPosition==0) {
//                    firstPosition=position;
//                }else if (position>firstPosition)
                    callbackFriendlyMessage.finish(friendlyMessage);
            }
        };
        mFirebaseAdapter2.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter2.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager2.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView2.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView2.setLayoutManager(mLinearLayoutManager2);
        mMessageRecyclerView2.setAdapter(mFirebaseAdapter2);
    }


    public interface  CallbackFriendlyMessage{ void finish(FriendlyMessage friendlyMessage);}
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

}
