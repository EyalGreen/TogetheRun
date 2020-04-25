package com.eyal.togetherun.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.Pace;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Run.LinesView;
import com.eyal.togetherun.Run.Run;
import com.eyal.togetherun.Run.Target.Time;
import com.eyal.togetherun.RunActivity;

public class RunLinesFragment extends Fragment implements
        RunActivity.UpdateScreenLisener {

    private Time time;
    private TextView tvTime;
    private RunActivity activity;

    public RunLinesFragment(RunActivity activity) {
        this.activity = activity;
    }

    public RunLinesFragment(RunActivity activity, Time time) {
        this.activity = activity;
        this.time = time;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.run_lines_fragment, container, false);
        LinesView linesView = new LinesView(getContext(), new Run(DatabaseHandler.currentRun));
        LinearLayout linesLayout = view.findViewById(R.id.linesLayout);
        linesLayout.addView(linesView);
        tvTime = view.findViewById(R.id.tvTime);
        if (time != null)
            updateTime(time);
        activity.setRunScreenLisener(this);

        return view;
    }

    @Override
    public void updateTime(Time time) {
        tvTime.setText(time.toString());
    }

    @Override
    public void updatePace(Pace pace) {

    }

    @Override
    public void updateDistance(String distance) {

    }
}
