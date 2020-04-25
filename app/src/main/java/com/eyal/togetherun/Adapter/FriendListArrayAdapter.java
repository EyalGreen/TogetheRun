package com.eyal.togetherun.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.Request.RunRequest;
import com.eyal.togetherun.Run.Run;
import com.eyal.togetherun.Run.Target.TargetOfRun;
import com.eyal.togetherun.User;
import com.eyal.togetherun.R;

import java.util.ArrayList;
import java.util.List;

public class FriendListArrayAdapter extends ArrayAdapter<User> {
    public static final String YOU_FRIEND_IS_NOT_ONLINE = "You Friend Is Not Online";
    private Context context;
    private List<User> userList;
    private TargetOfRun targetOfRun;

    public FriendListArrayAdapter(@NonNull Context context, List<User> users, TargetOfRun targetOfRun) {
        super(context, R.layout.friend_item_listview, users);
        this.context = context;
        this.userList = users;
        this.targetOfRun = targetOfRun;
    }

    public void refreshData(List<User> newData) {
        userList.clear();
        userList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final User user = userList.get(position);
        View item = LayoutInflater.from(context).inflate(R.layout.friend_item_listview, parent, false);
        TextView tvFriendName = item.findViewById(R.id.friendName);
        tvFriendName.setText(user.getUsername());

        TextView tvFriendRating = item.findViewById(R.id.friendRating);
        tvFriendRating.setText(String.valueOf(user.getRank()));

        TextView tvFriendTeamName = item.findViewById(R.id.friendTeamName);
        tvFriendTeamName.setText(user.getTeamName());

        ImageView friendProfilePic = item.findViewById(R.id.friendProfilePic);
//        friendProfilePic.setImageBitmap(user.getBitmap());

        ImageView inGameIV = item.findViewById(R.id.inMatchBtn);
        if (user.isInGame()) {
            inGameIV.setVisibility(View.VISIBLE);
        } else {
            inGameIV.setVisibility(View.INVISIBLE);
        }
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.isOnline()) {
                    showAlertDialogToInviteFriend(user);
                } else {
                    Toast.makeText(context, YOU_FRIEND_IS_NOT_ONLINE, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return item;
    }

    private void showAlertDialogToInviteFriend(final User friend) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Invite Friend For a Run");
//        builder.setMessage("Do you want to invite the friend " + friend.getUsername() + " for a run?" +
//                "The target of the run is " + targetOfRun.toString());
//        builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                User user = DatabaseHandler.user;
//                RunRequest runRequest = new RunRequest(user.getUsername(), user.getUid(), targetOfRun);
////                DatabaseHandler.sendRunRequest(friend, runRequest);
//                //DatabaseHandler.sendRequest(new FriendRequest(DatabaseHandler.user.getUsername(), DatabaseHandler.user.getUid()), tUid);
//                DatabaseHandler.sendRequest(runRequest, friend.getUid());
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//
    }
}
