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
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import static com.parse.Parse.getApplicationContext;


public class FoodFragment extends Fragment {

    Context mContext;
    Activity mActivity;
    ListView listView;
    DiningHallAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_dining_hall, container, false);
        listView = view.findViewById(R.id.diningHallList);
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mActivity = getActivity();

        if (mActivity != null) {
            mActivity.setTitle("Dining Halls");
        }
        // TODO Add a menu item to refresh the number of people

        // 1. Inflate the list of dining halls

        final ArrayList<DiningHallItem> diningHallList = new ArrayList<>();


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
                    ParseErrorHandler.handleParseError(e, mContext);
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
