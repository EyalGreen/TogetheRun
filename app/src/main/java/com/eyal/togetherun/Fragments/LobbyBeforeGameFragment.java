package com.eyal.togetherun.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eyal.togetherun.Adapter.LobbyRecycleAdapater;
import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.MainActivity;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Run.Runner;
import com.eyal.togetherun.Run.Target.TargetOfRun;
import com.eyal.togetherun.RunActivity;

import java.util.List;

import static com.eyal.togetherun.MainActivity.DURATION_STR;

public class LobbyBeforeGameFragment extends DialogFragment implements DatabaseHandler.GetRunnersLisener, DatabaseHandler.StartRunLisener {
    public static final String ARE_YOU_SURE_YOU_WANT_TO_QUIT = "Are You Sure You Want To Quit?";
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView tvGamePin;
    private ProgressBar pb;
    private LobbyRecycleAdapater adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lobby_before_game_fragment, container, false);

        this.context = getContext();

        this.pb = view.findViewById(R.id.progress_bar);
        pb.setVisibility(View.VISIBLE);
        tvGamePin = view.findViewById(R.id.tv_gamepin);
        tvGamePin.setText(DatabaseHandler.currentRun.getUid());
        recyclerView = view.findViewById(R.id.playersRecycleView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.GONE);
        double height = getResources().getDisplayMetrics().heightPixels * 0.8;
        view.setMinimumHeight((int)height);

        double width = getResources().getDisplayMetrics().widthPixels * 0.8;
        view.setMinimumWidth((int)width);

        view.findViewById(R.id.btnInviteFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFriendsFragment();
            }
        });
        view.findViewById(R.id.btnReady).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notAllInvitedJoined()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Not all invited runners joined");
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Ready", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            DatabaseHandler.setRunnerReady();
                        }
                    });
                    builder.show();

                }else{
                    DatabaseHandler.setRunnerReady();
                }

            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(ARE_YOU_SURE_YOU_WANT_TO_QUIT);
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        closeFrag();
                        DatabaseHandler.quitRun();
                    }
                });
                builder.show();
            }
        });


        DatabaseHandler.getRunners(this);
        DatabaseHandler.setStartRunLisener(this);
        DatabaseHandler.getUpdatedRun();

        return view;
    }

    private boolean notAllInvitedJoined() {
        return DatabaseHandler.currentRun.getAllRunRequestUidForThisRun().size() != 0;
    }

    private void closeFrag() {
        dismiss();
    }

    private void openFriendsFragment() {

        FriendModeListFragment dialogFragment = new FriendModeListFragment(true);
//        dialogFragment.setArguments(getArguments());
        dialogFragment.setCancelable(true);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "");

    }

    @Override
    public void onStartRun() {
        if (getActivity() == null)
            return;
        dismiss();

        TargetOfRun targetOfRun = DatabaseHandler.currentRun.getTarget();
        String selectedMode = targetOfRun.getSelectedMode();
        Intent intent = new Intent(context, RunActivity.class);

        if (selectedMode.equals(MainActivity.DISTANCE_STR)) {
            intent.putExtra("targetDistance", targetOfRun.getValue());

        } else if (selectedMode.equals(DURATION_STR)) {
            intent.putExtra("targetDuration", targetOfRun.getValue());
        }

        startActivity(intent);
    }



    @Override
    public void onFinish(List<Runner> runners) {
        if (runners != null) {
            if (adapter != null) {
                adapter.refreshData(runners);
            }else{
                recyclerView.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
                adapter = new LobbyRecycleAdapater(runners, context);
                recyclerView.setAdapter(adapter);
            }

        }else{
            DatabaseHandler.getRunners(this);
        }
    }
}
