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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;

public class GetEmailFragment extends DialogFragment implements DatabaseHandler.GetResultLisener {

    private EditText editText;
    private TextView errorTextView;
    private boolean isEmailExist = false;
    private ProgressBar pb;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.get_email_dialog, container, false);
        this.context = getContext();
        this.editText = view.findViewById(R.id.etEmail);
        this.errorTextView = view.findViewById(R.id.emailCheckProgressbar);
        errorTextView.setVisibility(View.GONE);



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_error);
                    editText.startAnimation(animation);
                    editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
                    return;
                }
                DatabaseHandler.checkIfEmailExist(s.toString(), GetEmailFragment.this);
            }
        });
        view.findViewById(R.id.btnConfirmUsernameDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmailExist) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_error);
                    editText.startAnimation(animation);
                    return;
                }
                String email = editText.getText().toString();
                DatabaseHandler.sendEmailToChangePassword(email);
                dismiss();

            }
        });
        view.findViewById(R.id.btnCancelUsernameDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        return view;
    }

    @Override
    public void onFinish(boolean result) {
        isEmailExist = result;
        if (result){
            errorTextView.setVisibility(View.GONE);
            editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_green_light), PorterDuff.Mode.SRC_ATOP);
        }else{
            errorTextView.setVisibility(View.VISIBLE);
            editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
        }
    }



}

