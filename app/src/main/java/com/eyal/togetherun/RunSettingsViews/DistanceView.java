package com.eyal.togetherun.RunSettingsViews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eyal.togetherun.R;

import org.w3c.dom.Text;

public class DistanceView extends LinearLayout implements TextWatcher {
    public static float MAX_DISTANCE = 80;
    public static float MIN_DISTANCE = 0.1f;
    public static float CHANGE_AMOUNT = 0.5f;


    private View view;
    private EditText tvValue;
    private float distanceValue = 3.0f;
    private CloseKeyboardInterface lisener;

    Context context;

    public DistanceView(Context context, final CloseKeyboardInterface lisener) {
        super(context);
        this.context = context;
        this.lisener = lisener;

        view = LayoutInflater.from(context).inflate(R.layout.distance_view, this, false);
        tvValue = view.findViewById(R.id.distanceTV);
        updateText();
        tvValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    //done action
                    String value = v.getText().toString();
                    if (value.equals(String.valueOf(DistanceView.this.distanceValue)))
                        return true;
                    if (isNotNumber(value)) {
                        DistanceView.this.distanceValue = MIN_DISTANCE;
                    } else {
                        float fvalue = Float.valueOf(value);
                        if (fvalue < MIN_DISTANCE || fvalue > MAX_DISTANCE) {
                            DistanceView.this.distanceValue = MIN_DISTANCE;
                        }else{
                            DistanceView.this.distanceValue = fvalue;
                        }
                    }
                    updateText();
                    if (lisener != null) {
                        lisener.onCloseKeyboard();
                    }
                    return true;
                }
                return false;
            }
        });

        tvValue.addTextChangedListener(this);
        view.findViewById(R.id.btnPlus).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distanceValue + CHANGE_AMOUNT > MAX_DISTANCE)
                    return;
                distanceValue += CHANGE_AMOUNT;
                updateText();
            }
        });
        view.findViewById(R.id.btnMinus).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distanceValue - CHANGE_AMOUNT < MIN_DISTANCE)
                    return;
                distanceValue -= CHANGE_AMOUNT;
                updateText();
            }
        });

        this.addView(view);


    }

    public String getValue() {
        return tvValue.getText().toString();
    }

    private void updateText() {
        tvValue.setText(String.valueOf(distanceValue));
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {


    }




    private boolean isNotNumber(String value) {
        try{
            Float.parseFloat(value);
        }catch(NumberFormatException e){
            return true;
        }
        return false;
    }

    public interface CloseKeyboardInterface{
        void onCloseKeyboard();
    }
}
