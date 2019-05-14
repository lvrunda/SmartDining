package com.biot.smartdining;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.parse.ParseUser;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScanFragment extends Fragment {
    Context mContext;
    Activity mActivity;
    Button scanButton;
    TextView prompt;

    private class SendRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... request) {

            String data = "";

            HttpURLConnection httpURLConnection = null;

            try {

                httpURLConnection = (HttpURLConnection) new URL(request[0]).openConnection();
                httpURLConnection.setRequestMethod("GET");

                // Specify the JSON format for sending and receiving
                // If not specifying the JSON format, then the server will have to deal with a normal string
//                httpURLConnection.setRequestProperty("Content-Type", "application/json");
//                httpURLConnection.setRequestProperty("Accept","application/json");
//                httpURLConnection.setDoOutput(true);
//
//                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
//                wr.writeBytes(request[1]);
//                wr.flush();
//                wr.close();
//                Log.d("POST", "doInBackground: Done");

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                JSONObject res = new JSONObject(result);
                if (res.getString("Status").toString().equals("Success")) {
                    Toast.makeText(mContext, "Empty plate scanned successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "Empty plate recognition failed!", Toast.LENGTH_LONG).show();
                }
                scanButton.setVisibility(View.VISIBLE);
                prompt.setVisibility(View.INVISIBLE);
            } catch (Exception e){
                e.printStackTrace();
            }

            Log.d("TAG", result); // We can make a Toast here indicating that the order has been created successfully
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        scanButton = view.findViewById(R.id.scanButton);
        prompt = view.findViewById(R.id.retrieving);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendRequest().execute("http://54.152.189.230:5000/api/plate/" + ParseUser.getCurrentUser().getUsername().toString(), "");
                Toast.makeText(mContext, "Sending scan request...", Toast.LENGTH_LONG).show();
                scanButton.setVisibility(View.INVISIBLE);
                prompt.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        mActivity = getActivity();

        if (mActivity != null) {
            mActivity.setTitle("Plate Scan");
        }


    }
}
