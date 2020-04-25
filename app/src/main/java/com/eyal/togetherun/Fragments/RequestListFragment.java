package com.eyal.togetherun.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eyal.togetherun.Adapter.FriendListArrayAdapter;
import com.eyal.togetherun.Adapter.RequestListArrayAdapter;
import com.eyal.togetherun.Adapter.RequestListRecycleAdapter;
import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Request.Request;

import java.util.ArrayList;
import java.util.List;

public class RequestListFragment extends DialogFragment implements DatabaseHandler.GetListOfRequestsLisener {
    private Context context;
    private ProgressBar pb;
    private RecyclerView recyclerView;
    private RequestListRecycleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FragmentManager fragmentManager;

    public RequestListFragment(FragmentManager supportFragmentManager) {
        fragmentManager = supportFragmentManager;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.requests_fragment, container, false);
        context = getContext();
        recyclerView = view.findViewById(R.id.allRequestsList);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        pb = view.findViewById(R.id.progress_bar_friendsList);
        pb.setVisibility(View.VISIBLE);
        DatabaseHandler.getListOfAllRequests(this);
        double height = getResources().getDisplayMetrics().heightPixels * 0.8;
        view.setMinimumHeight((int)height);

        double width = getResources().getDisplayMetrics().widthPixels * 0.8;
        view.setMinimumWidth((int)width);


//        RequestListArrayAdapter arrayAdapter = new RequestListArrayAdapter(getContext(), );
//        listView.setAdapter(arrayAdapter);
        view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onFinish(List<Request> requests) {
        if (requests != null) {
            pb.setVisibility(View.GONE);
            if (adapter != null) {
                adapter.refreshData(requests);
            }else{

                adapter = new RequestListRecycleAdapter(getContext(), requests, this);
                recyclerView.setAdapter(adapter);
            }

        }else{
            pb.setVisibility(View.VISIBLE);
            DatabaseHandler.getListOfAllRequests(this);

        }
    }
}
