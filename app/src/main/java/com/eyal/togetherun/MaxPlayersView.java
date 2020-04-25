package com.eyal.togetherun;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MaxPlayersView extends LinearLayout {
    public final static int MIN_PLAYERS = 1;
    public final static int MAX_PLAYERS = 10;

    private Context context;
    private TextView tvMaxPlayers;
    private int currentPlayers = 2;

    public MaxPlayersView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setPointer();
    }

    public MaxPlayersView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setPointer();
    }

    public MaxPlayersView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        setPointer();
    }

    public MaxPlayersView(Context context) {
        super(context);
        this.context = context;
        setPointer();


    }

    private void setPointer() {
        View mainView = LayoutInflater.from(context).inflate(R.layout.max_players_view, this, false);
        tvMaxPlayers = mainView.findViewById(R.id.tvPlayersAmount);
        updateText();
        mainView.findViewById(R.id.btnPlus).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPlayers == MAX_PLAYERS) return;
                currentPlayers++;
                updateText();
            }
        });
        mainView.findViewById(R.id.btnMinus).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPlayers == MIN_PLAYERS) return;
                currentPlayers--;
                updateText();
            }
        });
        this.addView(mainView);
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    private void updateText() {
        tvMaxPlayers.setText(String.valueOf(currentPlayers));
    }

}
