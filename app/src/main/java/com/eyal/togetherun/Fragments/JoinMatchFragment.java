package com.eyal.togetherun.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Run.Runner;

public class JoinMatchFragment extends DialogFragment implements DatabaseHandler.JoinRunResultLisener {
    public static final String RUN_IS_FULL_ERROR_MSG = "Run Is Full";
    private EditText editText;
    private ProgressBar pb;
    private Context context;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_match, container, false);
        context = getContext();
        editText = view.findViewById(R.id.etGamePin);
        pb = view.findViewById(R.id.progress_bar);
        view.findViewById(R.id.btnJoinMatch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);

                DatabaseHandler.joinRun = true;
                DatabaseHandler.joinGame(editText.getText().toString(), JoinMatchFragment.this, new Runner(DatabaseHandler.user));

            }
        });

        view.findViewById(R.id.btnJoinRandom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }



    @Override
    public void onFinish(boolean result) {
        if (getActivity() == null) return; //not in the fragment anymore
        DatabaseHandler.joinRun = false;
        if (result){
            //run exits and not full
            pb.setVisibility(View.GONE);
            editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_green_light), PorterDuff.Mode.SRC_ATOP);

            LobbyBeforeGameFragment fragment = new LobbyBeforeGameFragment();
            fragment.setCancelable(false);
            fragment.show(getActivity().getSupportFragmentManager(), "");
            dismiss();
        }else{
            //run not exits
            pb.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_error);
            editText.startAnimation(animation);
            editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);

        }
    }

    @Override
    public void runIsFull() {
        //run is full
        pb.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_error);
        editText.startAnimation(animation);
        editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
        editText.setError(RUN_IS_FULL_ERROR_MSG);
        editText.requestFocus();
    }
}
