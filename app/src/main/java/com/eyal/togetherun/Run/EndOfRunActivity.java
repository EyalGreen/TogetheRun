package com.eyal.togetherun.Run;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eyal.togetherun.Adapter.ScoreboardRecycleAdapter;
import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;

import java.util.List;

public class EndOfRunActivity extends AppCompatActivity implements DatabaseHandler.GetRunnersLisener, DatabaseHandler.RunEndLisener {
    private Context context;
    private TextView paceTextView, distanceTextView, timeTextView;
    private RecyclerView scoreboardRecyclerView;
    private ScoreboardRecycleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Run run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_run);
        run = DatabaseHandler.currentRun;

        setPointer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHandler.currentRun = null;
        finish();

    }

    private void setPointer() {
        context = this;
        paceTextView = findViewById(R.id.tvPace);
        distanceTextView = findViewById(R.id.tvDistance);
        timeTextView = findViewById(R.id.tvTime);
        scoreboardRecyclerView = findViewById(R.id.recycler_view);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Runner currentRunner = run.getRunner(DatabaseHandler.user);
        paceTextView.setText(currentRunner.getPace().toString());
        distanceTextView.setText(currentRunner.formatDistance());
        timeTextView.setText(currentRunner.getTime().toString());

        DatabaseHandler.getRunners(this);
        if (!run.getTarget().isByDistance())
            DatabaseHandler.getUpdateRunEnd(this);
        DatabaseHandler.exitRun();
    }

    @Override
    public void onFinish(List<Runner> runners) {
        run.setNewRunners(run.getMap(runners));
        if (adapter == null) {
            scoreboardRecyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(context);
            scoreboardRecyclerView.setLayoutManager(layoutManager);
            adapter = new ScoreboardRecycleAdapter(context, run.getScoreboard(), run);
            scoreboardRecyclerView.setAdapter(adapter);
        } else {

            scoreboardRecyclerView.setAdapter(adapter);

        }
    }

    @Override
    public void onEnd(Boolean result) {
        if (result == this.run.isEndOfTime())
            return;

        adapter.refreshData(result);

    }
}
