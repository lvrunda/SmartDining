package com.biot.smartdining;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;


public class DiningHallAdapter extends ArrayAdapter<DiningHallItem> {
    private Context mContext;
    private List<DiningHallItem> hallList;

    public DiningHallAdapter(@NonNull Context context, ArrayList<DiningHallItem> list) {
        super(context, 0, list);
        mContext = context;
        hallList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItem = convertView;


        if (listItem == null) {
            Log.d(TAG, "getView: Inflating listview...");
            listItem = LayoutInflater.from(mContext).inflate(R.layout.dining_hall_item, parent, false);
        } else {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.dining_hall_item, parent, false);
        }

        DiningHallItem currentHall = hallList.get(position);

        TextView hallName = listItem.findViewById(R.id.diningHallName);
        hallName.setText(currentHall.getdName());

        TextView peopleCount = listItem.findViewById(R.id.peopleCount);
        peopleCount.setText(mContext.getString(R.string.numberOfPeople) + currentHall.getpCount());

//        return super.getView(position, convertView, parent);
        return listItem;
    }
}
