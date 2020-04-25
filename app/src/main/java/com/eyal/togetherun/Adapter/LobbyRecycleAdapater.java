package com.eyal.togetherun.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eyal.togetherun.R;
import com.eyal.togetherun.Run.Runner;
import com.eyal.togetherun.User;

import java.util.List;

public class LobbyRecycleAdapater extends RecyclerView.Adapter<LobbyRecycleAdapater.MyViewHolder>  {
    private List<Runner> runners;
    private Context context;

    public LobbyRecycleAdapater(List<Runner> runners, Context context) {
        this.runners = runners;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.player_item_lobby, parent, false);
        MyViewHolder vh = new MyViewHolder(item);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        View item = holder.item;
        final Runner runner = runners.get(position);
        final User user = runner.getUser();
        TextView tvName = item.findViewById(R.id.runnerName);
        tvName.setText(user.getUsername());
        TextView tvTeam = item.findViewById(R.id.runnerTeamName);
        tvTeam.setText(user.getTeamName());
        TextView PlayerRating = item.findViewById(R.id.PlayerRating);
        PlayerRating.setText(String.valueOf(user.getRank()));
        //todo: ProfilePic
        ImageView imageView = item.findViewById(R.id.runnerProfilePic);
        imageView.setImageResource(R.drawable.defualt_pic);


        if (runner.isReady()){
            //green border
            item.setBackground(context.getDrawable(R.drawable.ready));
        }else{
            //gray border
            item.setBackground(context.getDrawable(R.drawable.not_ready));
        }


    }

    @Override
    public int getItemCount() {
        return runners.size();
    }

    public void refreshData(List<Runner> newData) {
        runners.clear();
        runners.addAll(newData);
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View item;

        public MyViewHolder(View item) {
            super(item);
            this.item = item;
        }
    }
}
