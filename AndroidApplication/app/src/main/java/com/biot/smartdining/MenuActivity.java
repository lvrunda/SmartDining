package com.biot.smartdining;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Context mContext;
    ListView mListView;
    MenuAdapter mAdapter;
    TextView costPrompt;
    int totalCost;
    private JSONObject orderInfo;
    private JSONArray orderList;
    ArrayList<MenuItem> menuItemList;
    boolean[] orderItemFlag;
    EditText commentsEdittext;
    private String comments;
    LinearLayout orderButtons;

    protected int getTotalCost() {
        return totalCost;
    }

    private class SendOrder extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... order) {

            String data = "";

            HttpURLConnection httpURLConnection = null;

            try {

                httpURLConnection = (HttpURLConnection) new URL(order[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                // Specify the JSON format for sending and receiving
                // If not specifying the JSON format, then the server will have to deal with a normal string
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept","application/json");
                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(order[1]);
                wr.flush();
                wr.close();
                Log.d("POST", "doInBackground: Done");

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
                if (res.getString("orderStatus").toString().equals("Success")) {
                    Toast.makeText(mContext, "Order created successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "Order creation failed, insufficient credits!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            Log.d("TAG", result); // We can make a Toast here indicating that the order has been created successfully
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menuSubtitle:
            case R.id.dishList:
            case R.id.commentTitle:
                hideKeyboard(this);
                break;
            case R.id.confirmButton:
                placeOrder();
                break;
            case R.id.cancelButton:
                cancelOrder();
                break;
            default:
                hideKeyboard(this);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            hideKeyboard(this);
//            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        return false;
    }

    protected void displayTotalCost(int cost) {
        totalCost += cost;
        costPrompt.setText(Integer.toString(totalCost));
    }

    protected void putInOrder(int position) {
        orderItemFlag[position] = true;
    }

    protected void removeFromOrder(int position) {
        orderItemFlag[position] = false;
    }

    /* The following two methods are obsolete because they will update the JSON object each time the
     * user updates the order, which costs unnecessary resources. Therefore, I used an array to speed
     * it up, which is using little space to trade for speed
     */
/*    protected void putInOrder(int position) {
        JSONObject orderItem = new JSONObject();
        try {
            orderItem.put("name", menuItemList.get(position).getfName());
            orderItem.put("cost", menuItemList.get(position).getCost());
            orderList.put(orderItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

/*    protected void removeFromOrder(int position){
        JSONArray newOrderList = new JSONArray();
        try {
            if (orderList.length() > 0 && orderList != null) {
                for (int i = 0; i < orderList.length(); i++) {
                    JSONObject orderItem = orderList.getJSONObject(i);
                    if(!orderItem.get("name").toString().equals(menuItemList.get(position).getfName())){
                        newOrderList.put(orderItem);
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        if (newOrderList.length() > 0) {
            orderList = newOrderList;
        }
    }*/

    protected void placeOrder() {
        if (totalCost > 0) {

            // Inflating the JSON object of the order
            try {
                Log.d("Placing Order...", "placeOrder: " + totalCost);
                for (int i = 0; i < menuItemList.size(); i++) {
                    if (orderItemFlag[i]) {
                        JSONObject orderItem = new JSONObject();
                        orderItem.put("name", menuItemList.get(i).getfName());
                        orderItem.put("cost", menuItemList.get(i).getCost());
                        orderList.put(orderItem);
                    }
                }

                JSONObject orderDetail = new JSONObject();
                comments = commentsEdittext.getText().toString();
                orderDetail.put("items", orderList);
                orderDetail.put("comments", comments);
                orderInfo.put("orderDetail", orderDetail);
                new SendOrder().execute("http://server-address:portnumber/api/order/" + ParseUser.getCurrentUser().getUsername().toString(), orderInfo.toString());
                Log.d("HttpRequest", "http://server-address:portnumber/api/order/" + ParseUser.getCurrentUser().getUsername().toString());

                Log.d("Placing order", orderInfo.toString());


            } catch (Exception e) {
                e.printStackTrace();
            }



            Intent intent = new Intent(mContext, Navigation.class);

            startActivity(intent);
        }
    }

    protected void cancelOrder() {
        ListView listView = findViewById(R.id.dishList);
        CheckBox cb;
        for(int i=0; i<listView.getChildCount();i++) {
            cb = (CheckBox) listView.getChildAt(i).findViewById(R.id.foodCheckBox);
            cb.setChecked(false);
        }
        mAdapter.notifyDataSetChanged();
    }

    protected static void hideKeyboard(Activity activity) {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);


        // Setting up context and get info from last activity

        mContext = MenuActivity.this;
        Intent intent = getIntent();


        // Obtain/set UI elements

        setTitle("Placing Orders ");
        orderButtons = findViewById(R.id.orderButtons);
        orderButtons.setVisibility(View.INVISIBLE);
        final ViewGroup.LayoutParams params = orderButtons.getLayoutParams();
        final int oHeight = params.height;
        String location = intent.getStringExtra("location").toString();
        Log.d("Getting Location", "onCreate: " + location);
        costPrompt = findViewById(R.id.totalCost);
        commentsEdittext = findViewById(R.id.commentsEdittext);

        // Total cost for the selection of menu items
        totalCost = 0;

        displayTotalCost(0);

        // Initialize orderInfo
/*
        JSON Object Format
        {
            "userId": "biot",
            "diningHallID": "lDsEB36bz5",
            "orderDetail": {
            "items": [
            {
                "cost": "100",
                "name": "Pasta"
            },
            {
                "cost": "100",
                "name": "Burrito"
            }],
            "comments" : "hoison sesame pasta with tomatoes and chicken; no sauce and everything else in burrito"
        }*/

        orderInfo = new JSONObject();
        orderList = new JSONArray();
        comments = "";

        try {
            orderInfo.put("username", ParseUser.getCurrentUser().getUsername().toString());
            orderInfo.put("diningHallID", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Fetch items in the food menu from the Parse server

        menuItemList = new ArrayList<>();
        mListView = findViewById(R.id.dishList);

        ParseQuery<ParseObject> foodList = ParseQuery.getQuery(location);
        // Obtaining the information
        foodList.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.d("foodList", "done: Entering Callback");
                if (e == null) {
                    Log.d("No Exception", "done: " + objects + objects.size());
                    if (objects.size() > 0) {
                        for (ParseObject foodItem : objects) { // Putting them in the ListView
                            menuItemList.add(new MenuItem(foodItem.getString("foodName"), foodItem.getInt("foodCost")));
                            Log.d("Getting Food Info", "done: " + foodItem.getObjectId());
                        }
                        orderItemFlag = new boolean[menuItemList.size()]; // Default values should be false
                    }
                } else {
                    Log.d("Exception", "done: " + e);
                    ParseErrorHandler.handleParseError(e, MenuActivity.this);
                }
            }
        });


        // Inflate the menu

        Log.d("menuList", "Obtained menuList: " + menuItemList.size());
        mAdapter = new MenuAdapter(mContext, menuItemList);
        mListView.setAdapter(mAdapter);

        // Click listeners for the checkboxes are in the MenuAdapter file

        findViewById(R.id.menuSubtitle).setOnClickListener(this);
        findViewById(R.id.commentTitle).setOnClickListener(this);
        findViewById(R.id.orderButtons).setOnClickListener(this);
        findViewById(R.id.confirmButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);

        mAdapter.setOnDataChangeListener(new MenuAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged(int cost, int position) {
                // Why onDataChanged can access displayTotalCost()?
                displayTotalCost(cost);
                if (cost > 0) {
                    putInOrder(position);
                } else {
                    removeFromOrder(position);
                }

                //Hide the buttons if nothing is selected

                // Changes the height and width to the specified *pixels*

                if (getTotalCost() <= 0){
                    orderButtons.setVisibility(View.INVISIBLE);
                }
                else{
                    orderButtons.setVisibility(View.VISIBLE);
                }
            }
        });



    }

}
