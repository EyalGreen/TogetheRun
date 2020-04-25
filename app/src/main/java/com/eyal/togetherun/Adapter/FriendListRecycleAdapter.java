package com.eyal.togetherun.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Request.RunRequest;
import com.eyal.togetherun.Run.Target.TargetOfRun;
import com.eyal.togetherun.User;

import java.util.List;

public class FriendListRecycleAdapter extends RecyclerView.Adapter<FriendListRecycleAdapter.MyViewHolder> {
    public static final String YOU_FRIEND_IS_NOT_ONLINE = "You Friend Is Not Online";
    private Context context;
    private List<User> userList;
    private DialogFragment fragment;
    private boolean canInviteForRun;

    public FriendListRecycleAdapter(Context context, List<User> userList, TargetOfRun targetOfRun,  DialogFragment fragment, boolean canInviteForRun) {
        this.context = context;
        this.userList = userList;
        this.targetOfRun = targetOfRun;
        this.fragment = fragment;
        this.canInviteForRun = canInviteForRun;
    }

    private TargetOfRun targetOfRun;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.friend_item_listview, parent, false);

        MyViewHolder vh = new MyViewHolder(item);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        View item = holder.item;
        final User user = userList.get(position);
        TextView tvFriendName = item.findViewById(R.id.friendName);
        tvFriendName.setText(user.getUsername());

        TextView tvFriendRating = item.findViewById(R.id.friendRating);
        tvFriendRating.setText(String.valueOf(user.getRank()));

        TextView tvFriendTeamName = item.findViewById(R.id.friendTeamName);
        tvFriendTeamName.setText(user.getTeamName());

        ImageView friendProfilePic = item.findViewById(R.id.friendProfilePic);
//        friendProfilePic.setImageBitmap(user.getBitmap()); //todo: ProfilePic

        ImageView inGameIV = item.findViewById(R.id.inMatchBtn);
        if (user.isInGame()){
            inGameIV.setVisibility(View.VISIBLE);
        }else{
            inGameIV.setVisibility(View.INVISIBLE);
        }
        if (canInviteForRun){
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user.isOnline()) {
                        showAlertDialogToInviteFriend(user);
                    }else{
                        Toast.makeText(context, YOU_FRIEND_IS_NOT_ONLINE, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void showAlertDialogToInviteFriend(final User friend) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Invite Friend For a Run");
        builder.setMessage("Do you want to invite " + friend.getUsername() + " for a the run? " +
                "The target of the run is " + targetOfRun.toString());
        builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User user = DatabaseHandler.user;
                RunRequest runRequest = new RunRequest(user.getUsername(), user.getUid(), targetOfRun, DatabaseHandler.currentRun.getUid());
//                DatabaseHandler.sendRunRequest(friend, runRequest);
                //DatabaseHandler.sendRequest(new FriendRequest(DatabaseHandler.user.getUsername(), DatabaseHandler.user.getUid()), tUid);
                DatabaseHandler.sendRunRequest(runRequest, friend.getUid());
                dialog.dismiss();
                fragment.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View item;
        public MyViewHolder(View v) {
            super(v);
            item = v;
        }
    }
    public void refreshData(List<User> newData){
        userList.clear();
        userList.addAll(newData);
        notifyDataSetChanged();
    }

}
