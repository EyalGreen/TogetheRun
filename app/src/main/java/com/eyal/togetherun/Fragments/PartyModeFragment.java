package com.eyal.togetherun.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.eyal.togetherun.R;

public class PartyModeFragment extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.party_fragment, container, false);

//        view.findViewById(R.id.btnPlayRandom).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Play Random Mode", Toast.LENGTH_SHORT).show();
//            }
//        });
//        view.findViewById(R.id.btnPlayWithFriend).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Play With User Mode", Toast.LENGTH_SHORT).show();
//                openFriendsFragment();
//            }
//        });
        view.findViewById(R.id.btnJoinMatch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinMatchFragment dialogFragment = new JoinMatchFragment();
                dialogFragment.setArguments(getArguments());
                dialogFragment.setCancelable(true);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "");
                dismiss();
            }
        });
        view.findViewById(R.id.btnCreateMatch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateGameProperties dialogFragment = new CreateGameProperties();
                dialogFragment.setArguments(getArguments());
                dialogFragment.setCancelable(true);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "");
                dismiss();
            }
        });


        return view;
    }


}
