package com.biot.smartdining;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

import static com.parse.ParseException.CONNECTION_FAILED;
import static com.parse.ParseException.INVALID_SESSION_TOKEN;
import static com.parse.ParseException.TIMEOUT;

public class ParseErrorHandler  {
    public static void handleParseError(ParseException e, Context context) {
        switch (e.getCode()) {
            case INVALID_SESSION_TOKEN: handleInvalidSessionToken();
                break;
            case TIMEOUT:
                Log.d("ParseException", e.getMessage());
                Toast.makeText(context, "Timeout. Please check your Internet connection", Toast.LENGTH_LONG).show();
            case CONNECTION_FAILED:
                Toast.makeText(context, "Connection Failed. Please check your Internet connection", Toast.LENGTH_LONG).show();
       // Other Parse API errors that you want to explicitly handle

            default:
                Log.d("ParseException", Integer.toString(e.getCode()));
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static void handleInvalidSessionToken() {
        //--------------------------------------
        // Option 1: Show a message asking the user to log out and log back in.
        //--------------------------------------
        // If the user needs to finish what they were doing, they have the opportunity to do so.
        //
        // new AlertDialog.Builder(getActivity())
        //   .setMessage("Session is no longer valid, please log out and log in again.")
        //   .setCancelable(false).setPositiveButton("OK", ...).create().show();

        //--------------------------------------
        // Option #2: Show login screen so user can re-authenticate.
        //--------------------------------------
        // You may want this if the logout button could be inaccessible in the UI.
        //
        // startActivityForResult(new ParseLoginBuilder(getActivity()).build(), 0);

        new AlertDialog.Builder(MainActivity.mContext).setMessage("Session is no longer valid, logging out...")
        .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.logOut();
            }
        }).create().show();


    }
}

/*
// In all API requests, call the global error handler, e.g.
query.findInBackground(new FindCallback<ParseObject>() {
public void done(List<ParseObject> results, ParseException e) {
        if (e == null) {
        // Query successful, continue other app logic
        } else {
        // Query failed
        ParseErrorHandler.handleParseError(e);
        }
        }
});
*/
