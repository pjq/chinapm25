package me.pjq.chinapm25;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ExchangeHistoryListAdapter.Callbacks {
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.pjq_me_72);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            fragment = new CityListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onListItemClicked(PM25Object pm25Object) {
        Intent intent = MapActivity.newItent(this, pm25Object);
        startActivity(intent);
    }
}
