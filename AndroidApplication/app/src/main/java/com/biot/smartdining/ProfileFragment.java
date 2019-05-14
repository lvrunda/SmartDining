package com.biot.smartdining;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ProfileFragment";

    Context mContext;
    Activity mActivity;
    TextView userName;
    TextView userCredits;
    Button refreshButton;
    Button logoutButton;
    String nameValue;
    String creditValue;

    private void getCredit() {
        ParseQuery<ParseUser> creditQuery = ParseUser.getQuery();
        creditQuery.whereEqualTo("username", nameValue);
        creditQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects.size() == 1) {
                    creditValue = Integer.toString(objects.get(0).getInt("credits"));
                }
                userCredits.setText(creditValue);
            }
        });

    }

    private void setLogoutButton() {
        ParseUser.logOut();
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshButton:
                getCredit();
                break;
            case R.id.logoutButton:
                setLogoutButton();
                break;
            default:
                Log.d(TAG, "onClick: User clicked: " + v.getId());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userName = view.findViewById(R.id.nameValue);
        userCredits = view.findViewById(R.id.creditValue);
        refreshButton = view.findViewById(R.id.refreshButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            refreshButton.setOnClickListener(this);
            logoutButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        mActivity = getActivity();
        mActivity.setTitle("User Profile");

        nameValue = ParseUser.getCurrentUser().getUsername().toString();

        userName.setText(nameValue);

        getCredit();

    }
}
