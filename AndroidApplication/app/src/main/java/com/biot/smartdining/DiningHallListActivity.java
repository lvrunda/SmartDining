package com.biot.smartdining;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DiningHallListActivity extends AppCompatActivity {

    Context mContext;
    ListView listView;
    DiningHallAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dining_hall);
        mContext = DiningHallListActivity.this;
        setTitle("Dining Halls");

        // TODO Add a menu item to refresh the number of people

        // 1. Inflate the list of dining halls

        final ArrayList<DiningHallItem> diningHallList = new ArrayList<>();
        listView = findViewById(R.id.diningHallList);

        ParseQuery<ParseObject> diningHallQuery = ParseQuery.getQuery("DiningHalls");
        // Obtaining the information
        diningHallQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject diningHall : objects) { // Putting them in the ListView
                            diningHallList.add(new DiningHallItem(diningHall.getString("hallName"), diningHall.getInt("peopleCount"), diningHall.getObjectId()));
                            Log.d("Getting Dining Halls", "done: " + diningHall.getObjectId());
                            Log.d("After query", "onCreate: Setting up adapter");
                            mAdapter = new DiningHallAdapter(mContext, diningHallList);
                            listView.setAdapter(mAdapter);
                        }
                    }
                } else {
                    ParseErrorHandler.handleParseError(e, DiningHallListActivity.this);
                }
            }
        });


        // 2. Create onClick listener on list items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);

                //Store the diningHall list info

//                intent.putExtra("diningHalls", diningHallList);
//                intent.putExtra("location", position);

                intent.putExtra("location", diningHallList.get(position).getId());

                startActivity(intent);
            }
        });
    }
}
