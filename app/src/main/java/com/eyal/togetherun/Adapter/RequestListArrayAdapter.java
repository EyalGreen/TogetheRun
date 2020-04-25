package com.eyal.togetherun.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Request.FriendRequest;
import com.eyal.togetherun.Request.Request;
import com.eyal.togetherun.Request.RunRequest;
import com.eyal.togetherun.User;

import java.util.List;

public class RequestListArrayAdapter extends ArrayAdapter implements DatabaseHandler.DeleteRequestLisener {
    private Context context;
    private List<Request> requestList;

    public RequestListArrayAdapter(@NonNull Context context, List<Request> requestList) {
        super(context, R.layout.request_item, requestList);
        this.context = context;
        this.requestList = requestList;
    }


    public void refreshData(List<Request> newData){
        requestList.clear();
        requestList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Request request = requestList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.request_item, parent, false);

        TextView tvContent = view.findViewById(R.id.contentOfRequest);
        tvContent.setText(request.getContentOfRequest());

        TextView tvSenderUsername = view.findViewById(R.id.tvRequestSender);
        tvSenderUsername.setText(request.getNameOfSender());

        TextView tvTypeOfRequest = view.findViewById(R.id.typeOfRequest);
        tvTypeOfRequest.setText(request.getTypeOfRequest());


        view.findViewById(R.id.btnAcceptRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (request.getTypeOfRequest().equals(FriendRequest.FRIEND_REQUEST_TYPE)){
                    DatabaseHandler.addFriend(request.getNameOfSender(), request.getSenderUid());
                    DatabaseHandler.addCurrentUserAsFriend(request.getSenderUid());
                    DatabaseHandler.deleteRequest(request, RequestListArrayAdapter.this);
                }else if (request.getTypeOfRequest().equals(RunRequest.RUN_REQUEST_TYPE)){


                }

            }
        });
        view.findViewById(R.id.btnDeclineRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler.deleteRequest(request, RequestListArrayAdapter.this);
            }
        });

        return view;
    }

    @Override
    public void onFinish(Request deleted) {
        requestList.remove(deleted);
        notifyDataSetChanged();
    }
}
