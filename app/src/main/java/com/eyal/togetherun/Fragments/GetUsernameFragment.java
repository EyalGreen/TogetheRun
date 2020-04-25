package com.eyal.togetherun.Fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;

public class GetUsernameFragment extends DialogFragment implements DatabaseHandler.CheckIsUniqUsernameLisener {
    private EditText editText;
    private TextView tv;
    private ConfirmLisener lisener;
    private boolean isUniqUsername = false;
    private Context context;

    public GetUsernameFragment(ConfirmLisener lisener) {
        this.lisener = lisener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.username_view_alert_dialog, container, false);
        this.context = getContext();
        this.editText = view.findViewById(R.id.etUsername);
        this.tv = view.findViewById(R.id.usernameCheckProgressbar);
        tv.setVisibility(View.GONE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


//                    DatabaseHandler.removeEvenedListener();
                    if (s.toString().equals("")) {
                        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_error);
                        editText.startAnimation(animation);
                        editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);

                        return;
                    }
                     DatabaseHandler.checkIsUniqUsername(s.toString(), GetUsernameFragment.this);

            }
        });
        view.findViewById(R.id.btnConfirmUsernameDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUniqUsername) return;
                String value = editText.getText().toString();
                if (lisener != null) {
                    lisener.onConfirm(value);
                }
                dismiss();
            }
        });
        view.findViewById(R.id.btnCancelUsernameDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lisener != null) {
                    lisener.onCancle();
                }else{
                    dismiss();
                }

            }
        });

        return view;
    }

    @Override
    public void onFinish(boolean result) {
        isUniqUsername = result;
        if (context == null) return;
        if (result){
            tv.setVisibility(View.GONE);
            editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_green_light), PorterDuff.Mode.SRC_ATOP);
        }
        else {
            tv.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.shake_error);
            editText.startAnimation(animation);
            editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);

        }

    }


    public interface ConfirmLisener {
        void onConfirm(String value);
        void onCancle();
    }
}
