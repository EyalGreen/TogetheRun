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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;
import com.eyal.togetherun.Request.FriendRequest;

public class AddFriendFragment extends DialogFragment implements DatabaseHandler.SearchFriendLisener {
    private EditText editText;
    private TextView tv;
    private boolean isExist = false;
    private String tUid;
    private Context context;

    public AddFriendFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_friend_dialog, container, false);

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
                DatabaseHandler.isUsernameExist(AddFriendFragment.this, s.toString());
                editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);

            }
        });

        view.findViewById(R.id.btnConfirmUsernameDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExist) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_error);
                    editText.startAnimation(animation);
                    return;
                }
//                DatabaseHandler.addFriend(tUid);
                DatabaseHandler.sendRequest(new FriendRequest(DatabaseHandler.user.getUsername(), DatabaseHandler.user.getUid()), tUid);
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
    public void onFinish(boolean result, String tUid) {
        if (tUid == null) return;
        this.tUid = tUid;
        isExist = result;
        if (result) {
            tv.setVisibility(View.GONE);
//            editText.setTextCursorDrawable(getContext().getColor(R.color.okET));

            editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_green_light), PorterDuff.Mode.SRC_ATOP);


        } else {
            tv.setVisibility(View.VISIBLE);
            editText.getBackground().mutate().setColorFilter(context.getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
//            editText.setTextCursorDrawable(getContext().getColor(R.color.errorET));

        }

    }


}
