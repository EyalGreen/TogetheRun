package com.eyal.togetherun.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.Pace;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Run.Run;
import com.eyal.togetherun.Run.Runner;
import com.eyal.togetherun.Run.Target.DistanceTarget;
import com.eyal.togetherun.Run.Target.DuratoinTarget;
import com.eyal.togetherun.Run.Target.TargetOfRun;
import com.eyal.togetherun.Run.Target.Time;
import com.eyal.togetherun.RunActivity;

import java.lang.annotation.Target;

public class RunFragment extends Fragment implements
        RunActivity.UpdateScreenLisener {


    public static final String ARE_YOU_SURE_YOU_WANT_TO_QUIT_THE_RUN = "Are you sure you want to quit the run?";
    private Context context;
    private View mainView;
    private TextView tvPace, tvTime, tvDis;
    private View pb;
    private RunActivity activity;
    private ImageView lockBtn, pauseButton;
    private boolean canClickBtnStop = false;
    private Time time;
    private Pace pace;
    private String distance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.run_fragment, container, false);

        context = getContext();


        tvDis = mainView.findViewById(R.id.tvDistancePassed);
        tvPace = mainView.findViewById(R.id.tvPace);
        tvTime = mainView.findViewById(R.id.tvTime);

        if (time != null) {
            updateTime(time);
            updateDistance(distance);
            updatePace(pace);

        }
        pb = mainView.findViewById(R.id.progress_bar);
        pb.setVisibility(View.GONE);
        lockBtn = mainView.findViewById(R.id.lockBtn);
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RunFragment.this.canClickBtnStop){
                    canClickBtnStop = true;
                    lockBtn.setImageResource(R.drawable.openlock);

                }else{
                    canClickBtnStop = false;
                    lockBtn.setImageResource(R.drawable.lock_btn);
                }
            }
        });
        pauseButton = mainView.findViewById(R.id.stopBtn);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClickBtnStop){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(ARE_YOU_SURE_YOU_WANT_TO_QUIT_THE_RUN);
                    builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            RunFragment.this.activity.endOfRun();
                        }
                    });
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }




            }
        });

        return mainView;
    }

    public RunFragment(RunActivity activity) {
        this.activity = activity;
        this.activity.setRunScreenLisener(this);
    }

    public RunFragment(RunActivity activity, Time time, Pace pace, String distance) {
        this.activity = activity;
        this.activity.setRunScreenLisener(this);
        this.time = time;
        this.pace = pace;
        this.distance = distance;
    }


    @Override
    public void updateTime(Time time) {
        tvTime.setText(time.toString());
    }

    @Override
    public void updatePace(Pace pace) {
        tvPace.setText(pace.toString());
    }

    @Override
    public void updateDistance(String distance) {
        tvDis.setText(distance);
    }
}

