package com.example.whatsappclone.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.Models.MessageModel;
import com.example.whatsappclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {
    ArrayList<MessageModel> messagemodels;
    Context context;
    String recId;


    int SENDER_VIEW_TYPE=1;
    int RECEIVER_VIEW_TYPE=2;

    public ChatAdapter(ArrayList<MessageModel> messagemodels, Context context) {
        this.messagemodels = messagemodels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messagemodels, Context context, String recId) {
        this.messagemodels = messagemodels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SENDER_VIEW_TYPE){
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewholder(view);
        }else
        {
            View view=LayoutInflater.from(context).inflate(R.layout.sample_reciver,parent,false);
            return new ReciverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messagemodels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messagemodel=messagemodels.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database=FirebaseDatabase.getInstance();
                                String senderRoom=FirebaseAuth.getInstance().getUid()+recId;
                                database.getReference().child("chats").child(senderRoom)
                                        .child(messagemodel.getMessageId())
                                        .setValue(null);

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return false;
            }
        });

        if(holder.getClass()==SenderViewholder.class){
            ((SenderViewholder) holder).senderMsg.setText(messagemodel.getMessage());
            Date date=new Date(messagemodel.getTimeStamp());
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("h:mm a");
            String strDate=simpleDateFormat.format(date);
            ((SenderViewholder) holder).senderTime.setText(strDate.toString());

        }else{
            ((ReciverViewHolder) holder).receiverMsg.setText(messagemodel.getMessage());
            Date date=new Date(messagemodel.getTimeStamp());
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("h:mm a");
            String strDate=simpleDateFormat.format(date);
            ((ReciverViewHolder) holder).receiverTime.setText(strDate.toString());
        }
    }

    @Override
    public int getItemCount() {
        return messagemodels.size();
    }

    public class ReciverViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMsg,receiverTime;
        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg=itemView.findViewById(R.id.receiverText);
            receiverTime=itemView.findViewById(R.id.receiverTime);

        }
    }
    public class SenderViewholder extends RecyclerView.ViewHolder{
        TextView senderMsg,senderTime;
        public SenderViewholder(@NonNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.senderText);
            senderTime=itemView.findViewById(R.id.senderTime);
        }
    }
}
