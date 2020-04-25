package com.eyal.togetherun.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Run.Run;
import com.eyal.togetherun.Run.Runner;

import java.util.List;

public class MeRecyclerAdapter extends RecyclerView.Adapter<MeRecyclerAdapter.MyViewHolder> {
    public static final String DISTANCE_UNIT = "Km";
    private Context context;
    private List<Run> runs;

    public MeRecyclerAdapter(Context context, List<Run> runs) {
        this.context = context;
        this.runs = runs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.record_item, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Run run  = runs.get(position);
        Runner runner = run.getRunner(DatabaseHandler.user);
        if (runner != null) {

            View item = holder.item;

            TextView runPinTextView = item.findViewById(R.id.runPinTextView);
            TextView distanceTextView = item.findViewById(R.id.distanceTextView);
            TextView timeTextView = item.findViewById(R.id.timeTextView);
            TextView paceTextView = item.findViewById(R.id.paceTextView);

            runPinTextView.setText(run.getUid());
            distanceTextView.setText(runner.formatDistance() + DISTANCE_UNIT);

            //todo: END of run update time and avgPace
//        timeTextView.setText(runner.getTime().toString());
            timeTextView.setText(runner.getTime().toString());
            paceTextView.setText(runner.getAvePace());
        }





    }

    @Override
    public int getItemCount() {
        return runs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View item;

        public MyViewHolder(View v) {
            super(v);
            item = v;
        }
    }
}
