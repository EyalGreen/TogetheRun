package com.eyal.togetherun.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Run.LinesView;
import com.eyal.togetherun.Run.Pair;
import com.eyal.togetherun.Run.Run;
import com.eyal.togetherun.Run.Runner;


public class ScoreboardRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String DISTANCE_UNIT = "Km";
    public static final int[] colors = {R.color.one, R.color.two, R.color.three, R.color.four};
    public static final int[] images = {R.drawable.place_one, R.drawable.place_four, R.drawable.place_three, R.drawable.place_four};
    public static final int FINISH_TYPE = 1;
    public static final int NOT_FINISH_TYPE = 2;
    public static final int DROPPED_OUT_TYPE = 3;

    private Context context;
    private Run run;
    private Runner[] runners;
    private Pair[] places;

    public ScoreboardRecycleAdapter(Context context, Runner[] runners, Run run) {
        this.context = context;
        this.runners = runners;
        this.run = run;
        this.places = run.getPlacesSorted();
    }


    @Override
    public int getItemViewType(int position) {
        Runner runner = runners[position];
        Pair place = places[position];
        if (run.isDropped(place.getDistance()))
            return DROPPED_OUT_TYPE;
        else if (run.isFinish(place.getDistance()))
            return FINISH_TYPE;

        return NOT_FINISH_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FINISH_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_podium_finish, parent, false);
            return new FinishViewHolder(view);

        } else if (viewType == DROPPED_OUT_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_podium_dropped_out, parent, false);
            return new DroppedOutViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_podium_not_finish, parent, false);
            return new NotFinishViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Runner runner = runners[position];
        if (holder instanceof FinishViewHolder) {
            ((FinishViewHolder) holder).bind(runner, position);
        } else if (holder instanceof NotFinishViewHolder) {
            ((NotFinishViewHolder) holder).bind(runner, position);
        } else if (holder instanceof DroppedOutViewHolder) {
            ((DroppedOutViewHolder) holder).bind(runner, position);
        }
    }

    @Override
    public int getItemCount() {
        return runners.length;
    }


    public void refreshData(boolean result) {
        this.run.setEndOfTime(result);
        notifyDataSetChanged();


    }

    public void setRun(Run run) {
        this.run = run;
        this.runners = run.getScoreboard();
        this.places = run.getPlacesSorted();
    }

    public Run getRun() {
        return run;
    }

    private static class NotFinishViewHolder extends RecyclerView.ViewHolder {

        public View item;

        public NotFinishViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
        }

        public void bind(Runner runner, int position) {
            LinearLayout linesLayout = item.findViewById(R.id.lines);
            LinesView linesView = new LinesView(item.getContext(), new Run(DatabaseHandler.currentRun), runner.getUser().getUsername());
            linesView.setTag("LinesView");
            linesView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            linesLayout.addView(linesView);
            TextView usernameTextView = item.findViewById(R.id.usernameTextView);
            usernameTextView.setText(runner.getUser().getUsername());

        }
    }

    private static class FinishViewHolder extends RecyclerView.ViewHolder {

        public View item;

        public FinishViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
        }

        public void bind(Runner runner, int position) {
//            TextView place = item.findViewById(R.id.place);
            ImageView place = item.findViewById(R.id.place);
            TextView distanceTextView = item.findViewById(R.id.distanceTextView);
            TextView timeTextView = item.findViewById(R.id.timeTextView);
            TextView paceTextView = item.findViewById(R.id.paceTextView);
            TextView usernameTextView = item.findViewById(R.id.usernameTextView);
            usernameTextView.setText(runner.getUser().getUsername());

            place.setImageResource(images[Math.min(position, images.length - 1)]);
            distanceTextView.setText(runner.formatDistance() + DISTANCE_UNIT);
            timeTextView.setText(runner.getTime().toString());
            paceTextView.setText(runner.getPace().toString());

        }
    }

    private static class DroppedOutViewHolder extends RecyclerView.ViewHolder {

        public View item;

        public DroppedOutViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
        }

        public void bind(Runner runner, int position) {
            TextView distanceTextView = item.findViewById(R.id.distanceTextView);
            TextView timeTextView = item.findViewById(R.id.timeTextView);
            TextView paceTextView = item.findViewById(R.id.paceTextView);
            TextView usernameTextView = item.findViewById(R.id.usernameTextView);
            usernameTextView.setText(runner.getUser().getUsername());
            distanceTextView.setText(runner.formatDistance() + DISTANCE_UNIT);

            timeTextView.setText(runner.getTime().toString());
            paceTextView.setText(runner.getPace().toString());

        }
    }


}
