package com.biot.smartdining;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;



public class MenuAdapter extends ArrayAdapter<MenuItem> {

    private Context mContext;
    private ArrayList<MenuItem> menuList;

    public MenuAdapter(@NonNull Context context, ArrayList<MenuItem> list) {
        super(context, 0, list);
        mContext = context;
        menuList = list;
    }

    protected interface OnDataChangeListener{
        public void onDataChanged(int cost, int position);
    }

    // Listener to notify the MenuActivity that the total cost have changed
    OnDataChangeListener mOnDataChangeListener;
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }

    private void OnCheckChange(CheckBox checkBox, MenuItem menuItem, int position){
        int cost;
        if (checkBox.isChecked()) {
            cost = menuItem.getCost();
        } else {
            cost = -menuItem.getCost();
        }
        if (mOnDataChangeListener != null) {
            mOnDataChangeListener.onDataChanged(cost, position);
        }
    }

    @NonNull

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        View listItem = convertView;

        if(listItem == null){
            listItem = LayoutInflater.from(mContext).inflate(R.layout.menu_item, parent, false);
        }

        // Obtain the food info from the ArrayList
        final MenuItem menuItem = menuList.get(position);

        TextView foodName = listItem.findViewById(R.id.foodName);

        TextView foodInfo = listItem.findViewById(R.id.foodInfo);

        // Set food name and cost info

        foodName.setText(menuItem.getfName());

        foodInfo.setText(mContext.getString(R.string.foodCost) + menuItem.getCost());

        final CheckBox checkBox = listItem.findViewById(R.id.foodCheckBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OnCheckChange(checkBox, menuItem, position);
            }
        });

//        checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkBox.setChecked(!checkBox.isChecked());
//            }
//        });



        return listItem;
    }

}
