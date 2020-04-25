package com.eyal.togetherun.RunSettingsViews;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.eyal.togetherun.R;
import com.eyal.togetherun.Run.Target.TargetOfRun;

public class TargetOfRunView extends LinearLayout implements AdapterView.OnItemSelectedListener, DistanceView.CloseKeyboardInterface {

    public static String DISTANCE_STR = "Target Distance";
    public static String DURATION_STR = "Target Duration";

    private Activity activity;
    private String selectedMode;
    private Context context;
    private Spinner runSettingsSpinner;
    private LinearLayout runSettingsLayout;


    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public TargetOfRunView(Context context) {
        super(context);
        this.context = context;

        setPointer();

    }

    public TargetOfRunView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setPointer();
    }

    public TargetOfRunView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setPointer();
    }

    public TargetOfRunView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        setPointer();
    }
    private void setPointer(){
        View view = LayoutInflater.from(context).inflate(R.layout.target_of_run_view, this, false);

        runSettingsLayout = view.findViewById(R.id.runsettingsLayout);
        runSettingsSpinner = view.findViewById(R.id.runsettingSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.run_settings_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        runSettingsSpinner.setAdapter(adapter);
        runSettingsSpinner.setOnItemSelectedListener(this);
        this.addView(view);
    }


    public Bundle getValue(){
        Bundle intent = new Bundle();
        if (selectedMode.equals(DISTANCE_STR)) {
            DistanceView distanceView = this.findViewWithTag("targetView");
            intent.putString("targetDistance", distanceView.getValue());

        } else if (selectedMode.equals(DURATION_STR)) {
            DurationView durationView = this.findViewWithTag("targetView");
            intent.putString("targetDuration", durationView.getValue());
        }
        return intent;
    }

    public String getSelectedMode() {
        return selectedMode;
    }

    public TargetOfRun getTargetOfRun(){
        if (selectedMode.equals(DISTANCE_STR)) {
            DistanceView distanceView = runSettingsLayout.findViewWithTag("targetView");
            return new TargetOfRun(Double.parseDouble(distanceView.getValue()));
        } else if (selectedMode.equals(DURATION_STR)) {
            DurationView durationView = runSettingsLayout.findViewWithTag("targetView");
            return new TargetOfRun(durationView.getTime());

        }
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (runSettingsLayout == null) {
            runSettingsLayout = view.findViewById(R.id.runsettingsLayout);
        }
        runSettingsLayout.removeAllViews();
        String selectedItem = parent.getAdapter().getItem(position).toString();
//        Toast.makeText(context, selectedItem, Toast.LENGTH_SHORT).show();
        selectedMode = selectedItem;

        if (selectedMode.equals(DISTANCE_STR)) {
            DistanceView distanceView = new DistanceView(context, this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            layoutParams.gravity = Gravity.CENTER;
            distanceView.setLayoutParams(layoutParams);
            distanceView.setTag("targetView");
            runSettingsLayout.addView(distanceView);


        } else if (selectedMode.equals(DURATION_STR)) {
            DurationView durationView = new DurationView(context, this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            layoutParams.gravity = Gravity.CENTER;
            durationView.setLayoutParams(layoutParams);
            durationView.setTag("targetView");
            runSettingsLayout.addView(durationView);

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public void onCloseKeyboard() {
            hideKeyboard(activity);
    }



}
