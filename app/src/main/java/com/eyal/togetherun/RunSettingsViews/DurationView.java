package com.eyal.togetherun.RunSettingsViews;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eyal.togetherun.R;
import com.eyal.togetherun.Run.Target.Time;

import javax.xml.datatype.Duration;

public class DurationView extends LinearLayout {

    public static int MAX_SEC = 60;
    public static int MAX_HOUR = 23;
    public static int MIN_VALUE = 0;
    public static int MIN_TIME = 15;
    private View durationView;
    private EditText tvHour, tvMin, tvSec;
    private int hourValue = 0, minValue = 0, secValue = MIN_TIME;
    private DistanceView.CloseKeyboardInterface lisener;
    Context context;


    public DurationView(Context context, final DistanceView.CloseKeyboardInterface lisener) {
        super(context);
        this.context = context;
        this.lisener = lisener;
        durationView = LayoutInflater.from(context).inflate(R.layout.duration_view, this, false);

        tvHour = durationView.findViewById(R.id.tvHour);
        tvMin = durationView.findViewById(R.id.tvMin);
        tvSec = durationView.findViewById(R.id.tvSec);

        tvSec.setText(addZero((MIN_TIME)));
        tvHour.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //done action
                    String value = v.getText().toString();
                    if (v.equals(String.valueOf(DurationView.this.hourValue)))
                        return true;

                    if (isNotNumber(value)) {
                        DurationView.this.hourValue = MIN_VALUE;
                    } else {
                        int iValue = Integer.valueOf(value);
                        if (isHourValid(iValue))
                            DurationView.this.hourValue = iValue;
                        else
                            DurationView.this.hourValue = MIN_VALUE;
                    }

                    updateText();
                    DurationView.this.lisener.onCloseKeyboard();
                    return true;


                }

                return false;
            }
        });


        tvMin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //done action
                    String value = v.getText().toString();
                    if (v.equals(String.valueOf(DurationView.this.minValue)))
                        return true;

                    if (isNotNumber(value)) {
                        DurationView.this.minValue = MIN_VALUE;
                    } else {
                        int iValue = Integer.valueOf(value);
                        if (isMinuteValid(iValue))
                            DurationView.this.minValue = iValue;
                        else
                            DurationView.this.minValue = MIN_VALUE;
                    }

                    updateText();
                    DurationView.this.lisener.onCloseKeyboard();
                    return true;


                }

                return false;
            }
        });
        tvSec.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //done action
                    String value = v.getText().toString();
                    if (v.equals(String.valueOf(DurationView.this.secValue)))
                        return true;

                    if (isNotNumber(value)) {
                        DurationView.this.secValue = MIN_VALUE;
                    } else {
                        int iValue = Integer.valueOf(value);
                        if (isMinuteValid(iValue))
                            DurationView.this.secValue = iValue;
                        else
                            DurationView.this.secValue = MIN_VALUE;
                    }

                    updateText();
                    DurationView.this.lisener.onCloseKeyboard();
                    return true;


                }

                return false;
            }
        });

        this.addView(durationView);

    }

    private boolean isMinuteValid(int value) {
        return MIN_VALUE <= value && value <= MAX_SEC;
    }

    private String addZero(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }

    private void updateText() {
        if (hourValue == minValue && minValue == 0 && secValue < MIN_TIME) { //if the time is before 00:00:15
            secValue = MIN_TIME;
            Toast.makeText(context, "The Minimum Time Is 00:00:" + addZero(MIN_TIME) , Toast.LENGTH_SHORT).show();
        }
        this.tvHour.setText(addZero(this.hourValue));
        this.tvMin.setText(addZero(this.minValue));
        this.tvSec.setText(addZero(this.secValue));
    }

    private boolean isHourValid(int iValue) {
        return MIN_VALUE <= iValue && iValue <= MAX_HOUR;
    }

    private boolean isNotNumber(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

    public String getValue() {
        return new Time(hourValue, minValue, secValue).toString();
    }

    public Time getTime() {
        return new Time(hourValue, minValue, secValue);
    }


}
