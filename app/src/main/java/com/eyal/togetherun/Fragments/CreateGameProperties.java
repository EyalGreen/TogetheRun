package com.eyal.togetherun.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.MaxPlayersView;
import com.eyal.togetherun.R;

import com.eyal.togetherun.Run.Run;
import com.eyal.togetherun.RunSettingsViews.TargetOfRunView;

public class CreateGameProperties extends DialogFragment implements DatabaseHandler.GetResultLisener {



    private Context context;
    private MaxPlayersView maxPlayersView;
    private TargetOfRunView targetOfRunView;
    private ProgressBar pb;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_game_properties, container, false);
        this.context = getContext();
        this.targetOfRunView = view.findViewById(R.id.targetOfRunView);
        targetOfRunView.setActivity(getActivity());
        this.maxPlayersView = view.findViewById(R.id.maxPlayersView);
        this.pb = view.findViewById(R.id.progress_bar);
        pb.setVisibility(View.GONE);
        double width = getResources().getDisplayMetrics().widthPixels * 0.9;
        view.setMinimumWidth((int)width);


        view.findViewById(R.id.createMatchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                createGame();
            }
        });



        return view;
    }

    private void createGame() {
        Run run = new Run(targetOfRunView.getTargetOfRun(), maxPlayersView.getCurrentPlayers());
        DatabaseHandler.createRun(this, run);

    }



    @Override
    public void onFinish(boolean result) {
        if (result){
            pb.setVisibility(View.GONE);

            LobbyBeforeGameFragment fragment = new LobbyBeforeGameFragment();
            fragment.setCancelable(false);
            fragment.setArguments(targetOfRunView.getValue());
            fragment.show(getActivity().getSupportFragmentManager(), "");
            dismiss();
        }else{
            createGame();
        }

    }
}
