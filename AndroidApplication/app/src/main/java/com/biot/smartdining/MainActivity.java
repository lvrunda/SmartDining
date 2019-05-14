package com.biot.smartdining;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    static Context mContext;
    EditText usernameEditText;
    EditText passwordEditText;

    ImageView logoImageView;
    ConstraintLayout backgroundLayout;

    public void showDiningHallList(){
//        Intent intent = new Intent(getApplicationContext(), DiningHallListActivity.class);
        Intent intent = new Intent(getApplicationContext(), Navigation.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logoImageView:
            case R.id.backgroundLayout:
                // Hide the keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
//            signUpClicked(v);
            loginClicked(v);
        }

        return false;
    }

    public void signUpClicked(View view) {


        if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){
            Toast.makeText(this, "Please enter valid user name and password!", Toast.LENGTH_LONG).show();

        }
        else {
            ParseUser user = new ParseUser();
            user.setUsername(usernameEditText.getText().toString());
            user.setPassword(passwordEditText.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        Log.d("Signup", "Successfully signed up");
                        Toast.makeText(getBaseContext(), "Successfully signed up!", Toast.LENGTH_LONG).show();
                        showDiningHallList();
                    } else {
//                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                        if (e.getCode() == ParseException.INVALID_SESSION_TOKEN) {
//                            ParseErrorHandler.handleParseError(e, MainActivity.this);
//                        }
                        ParseErrorHandler.handleParseError(e, MainActivity.this);
                    }
                }
            });
        }
    }

    public void loginClicked(View view) {
//        usernameEditText = findViewById(R.id.usernameEditText);
//        passwordEditText = findViewById(R.id.passwordEditText);

        if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){
            Toast.makeText(this, "Please enter valid user name and password!", Toast.LENGTH_LONG).show();

        }
        else {
            ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e != null || user == null) {
/*                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        if (e.getCode() == ParseException.INVALID_SESSION_TOKEN) {
                            ParseErrorHandler.handleParseError(e, MainActivity.this);
                        }*/
                        ParseErrorHandler.handleParseError(e, MainActivity.this);
                    }
                    else{
                        Log.d("Login", "OK!");
                        showDiningHallList();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        passwordEditText.setOnKeyListener(this);

        // If logged in, then proceed to the next activity
        if (ParseUser.getCurrentUser() != null) {
            showDiningHallList();
        }

        logoImageView = findViewById(R.id.logoImageView);
        backgroundLayout = findViewById(R.id.backgroundLayout);

        logoImageView.setOnClickListener(this);
        backgroundLayout.setOnClickListener(this);


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }


}
