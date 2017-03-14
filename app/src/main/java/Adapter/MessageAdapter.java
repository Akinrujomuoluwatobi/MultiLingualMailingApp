package Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.royalteck.progtobi.multilinguamailngapp.R;

import java.util.ArrayList;

import Modal.ChatMessage;

/**
 * Created by PROG. TOBI on 13-Oct-16.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private Context activity;
    private LayoutInflater inflater;
    ArrayList<ChatMessage> studdata = new ArrayList<>();

    /*public StudentAdapter(Context context, ArrayList<_User> studdata) {
        inflater = LayoutInflater.from(context);
        this.studdata = studdata;
    }*/

    public MessageAdapter(Context activity, ArrayList<ChatMessage> arrayList) {
        this.activity = activity;
        this.studdata = arrayList;

    }

    public MessageAdapter(ArrayList<ChatMessage> users) {
        this.studdata = users;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatMessage student = studdata.get(position);

        //holder.studimg.setImageResource(R.drawable.image_view);
        holder.msgchi.setText(student.getMessagechi());
        holder.msgeng.setText(student.getMessageeng());
        holder.sendername.setText(student.getMessageUser());
    }

    @Override
    public int getItemCount() {

        return studdata.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView msgchi, msgeng, sendername;

        public MyViewHolder(View itemView) {
            super(itemView);
            msgchi = (TextView) itemView.findViewById(R.id.message_textch);
            msgeng = (TextView) itemView.findViewById(R.id.message_texteng);
            sendername = (TextView) itemView.findViewById(R.id.message_user);

        }
    }
}
