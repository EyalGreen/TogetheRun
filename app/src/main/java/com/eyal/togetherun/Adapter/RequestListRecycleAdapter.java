package com.eyal.togetherun.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.Fragments.LobbyBeforeGameFragment;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Request.FriendRequest;
import com.eyal.togetherun.Request.Request;
import com.eyal.togetherun.Request.RunRequest;
import com.eyal.togetherun.Run.Runner;

import java.util.List;

public class RequestListRecycleAdapter extends RecyclerView.Adapter<RequestListRecycleAdapter.MyViewHolder> implements DatabaseHandler.DeleteRequestLisener, DatabaseHandler.AddToRunLisener {
    private Context context;
    private List<Request> requestList;
    private ProgressBar pb;
    private DialogFragment fragment;

    public RequestListRecycleAdapter(Context context, List<Request> requestList, DialogFragment fragment) {
        this.context = context;
        this.requestList = requestList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.request_item, parent, false);
        MyViewHolder vh = new MyViewHolder(item);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Request request = requestList.get(position);
        View view = holder.item;

        TextView tvContent = view.findViewById(R.id.contentOfRequest);
        tvContent.setText(request.getContentOfRequest());

        TextView tvSenderUsername = view.findViewById(R.id.tvRequestSender);
        tvSenderUsername.setText(request.getNameOfSender());

        TextView tvTypeOfRequest = view.findViewById(R.id.typeOfRequest);
        tvTypeOfRequest.setText(request.getTypeOfRequest());

        pb = view.findViewById(R.id.progress_bar);

        view.findViewById(R.id.btnAcceptRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (request.getTypeOfRequest().equals(FriendRequest.FRIEND_REQUEST_TYPE)) {
                    DatabaseHandler.addFriend(request.getNameOfSender(), request.getSenderUid());
                    DatabaseHandler.addCurrentUserAsFriend(request.getSenderUid());
                    DatabaseHandler.deleteRequest(request, RequestListRecycleAdapter.this);
                } else if (request.getTypeOfRequest().equals(RunRequest.RUN_REQUEST_TYPE)) {
                    pb.setVisibility(View.VISIBLE);
                    DatabaseHandler.joinRun = true;
                    RunRequest runRequest = (RunRequest) request;
                    DatabaseHandler.addToRun(runRequest, new Runner(DatabaseHandler.user), RequestListRecycleAdapter.this); //add this user to the run


                }

            }
        });
        view.findViewById(R.id.btnDeclineRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler.deleteRequest(request, RequestListRecycleAdapter.this);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void refreshData(List<Request> newData) {
        requestList.clear();
        requestList.addAll(newData);
        notifyDataSetChanged();
    }



    //added to run
    @Override
    public void onFinish(RunRequest request, boolean result) {
        if (fragment.getActivity() == null) return; //if the fragment already closed.
        DatabaseHandler.joinRun = false;
        if (result) {
            pb.setVisibility(View.GONE);
            DatabaseHandler.deleteRequest(request, RequestListRecycleAdapter.this);
            LobbyBeforeGameFragment lobbyBeforeGameFragment = new LobbyBeforeGameFragment();
            lobbyBeforeGameFragment.setCancelable(false);
            lobbyBeforeGameFragment.show(fragment.getActivity().getSupportFragmentManager(), "");
            fragment.dismiss();

        } else {
            RunRequest runRequest = request;
            DatabaseHandler.joinRun = true;
            DatabaseHandler.addToRun(runRequest, new Runner(DatabaseHandler.user), RequestListRecycleAdapter.this); //add this user to the run
        }
    }

    @Override
    public void runIsFull() {
        Toast.makeText(context, "Run Is Full", Toast.LENGTH_SHORT).show();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View item;

        public MyViewHolder(View v) {
            super(v);
            item = v;
        }
    }

    //request deleted
    @Override
    public void onFinish(Request deleted) {
        DatabaseHandler.joinRun = false;
        requestList.remove(deleted);
        notifyDataSetChanged();
    }
}
