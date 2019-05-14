package com.biot.smartdining;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class Navigation extends AppCompatActivity {

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            return true;
        }
        return false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.recognition:
//                    Intent intent = new Intent(Navigation.this, DiningHallListActivity.class);
                    fragment = new ScanFragment();
                    break;
                case R.id.orders:
                    fragment = new FoodFragment();
                    break;
//                    Intent intent1 = new Intent(Navigation.this, DiningHallListActivity.class);
                case R.id.profile:
//                    Intent intent2 = new Intent(Navigation.this, DiningHallListActivity.class);
                    fragment = new ProfileFragment();
                    break;
            }

            return loadFragment(fragment);
//            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        loadFragment(new ScanFragment());
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
