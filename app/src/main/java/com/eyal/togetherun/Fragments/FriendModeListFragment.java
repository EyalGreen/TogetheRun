package com.eyal.togetherun.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eyal.togetherun.Adapter.FriendListRecycleAdapter;
import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.Run.Target.DistanceTarget;
import com.eyal.togetherun.Run.Target.DuratoinTarget;
import com.eyal.togetherun.Run.Target.TargetOfRun;
import com.eyal.togetherun.Run.Target.Time;
import com.eyal.togetherun.User;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Adapter.FriendListArrayAdapter;

import java.util.List;

public class FriendModeListFragment extends DialogFragment implements DatabaseHandler.GetListOfFriendsLisener {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressBar pb;
    private FriendListRecycleAdapter adapter;
    private TargetOfRun targetOfRun;
    private boolean canInviteForRun;

    public FriendModeListFragment(boolean canInviteForRun) {
        this.canInviteForRun = canInviteForRun;
    }

    private TargetOfRun getTargetOfRun(){
        Bundle extras = getArguments();
        if (extras != null) {
            if (extras.containsKey("targetDuration")) {
                Time time = new Time((String) extras.get("targetDuration"));
                return new DuratoinTarget(time);
            } else {
                double distance = Double.valueOf((String) extras.get("targetDistance"));
                return new DistanceTarget(distance);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.friend_mode_fragment, container, false);
        if (canInviteForRun){
            targetOfRun = getTargetOfRun();
            if (targetOfRun == null)
                targetOfRun = DatabaseHandler.currentRun.getTarget();
        }
//        List<User> users = new ArrayList<>();
//        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
//                R.drawable.party_photo);
//        users.add(new User("eyal", "go team", 4000, icon));
//        users.add(new User("eyal", "go team", 4000, icon, true));
//        users.add(new User("eyal", "go team", 4000, icon));
//        users.add(new User("eyal", "go team", 4000, icon));
//        users.add(new User("eyal", "go team", 4000, icon));
//        users.add(new User("eyal", "go team", 4000, icon, true));
//        users.add(new User("eyal", "go team", 4000, icon));
//        users.add(new User("eyal", "go team", 4000, icon));
//        users.add(new User("eyal", "go team", 4000, icon));
//        users.add(new User("eyal", "go team", 4000, icon));
//        users.add(new User("eyal", "go team", 4000, icon));
//        users.add(new User("eyal", "go team", 4000, icon));
        pb = view.findViewById(R.id.progress_bar_friendsList);
        pb.setVisibility(View.VISIBLE);
        DatabaseHandler.getListOfAllFriends(this);
        recyclerView = view.findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        double height = getResources().getDisplayMetrics().heightPixels * 0.8;
        view.setMinimumHeight((int)height);

        view.findViewById(R.id.addFriendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendFragment dialogFragment = new AddFriendFragment(getContext());
                dialogFragment.setCancelable(true);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "");
            }
        });

        view.findViewById(R.id.cloaseFragBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;

    }
    @Override
    public void onFinish(List<User> friends) {
        if (friends != null) {
            pb.setVisibility(View.GONE);
            if (adapter != null) {

                adapter.refreshData(friends);
            }else{

                adapter = new FriendListRecycleAdapter(getContext(), friends, targetOfRun, this, canInviteForRun);
                recyclerView.setAdapter(adapter);
            }

        }else{
            pb.setVisibility(View.VISIBLE);
            DatabaseHandler.getListOfAllFriends(this);

        }

    }
}
