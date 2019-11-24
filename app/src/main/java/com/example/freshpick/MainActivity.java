package com.example.freshpick;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationItemView groceryList = findViewById(R.id.grocery_list);
        setMenuItemOnClick(groceryList, MainActivity.class);

        BottomNavigationItemView home = findViewById(R.id.home);
        setMenuItemOnClick(home, MainActivity.class);

        BottomNavigationItemView encyclopedia = findViewById(R.id.encyclopedia);
        setMenuItemOnClick(encyclopedia, EncyclopediaActivity.class);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.home);
    }

    public void setMenuItemOnClick(BottomNavigationItemView m, final Class cls) {
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, cls));
            }
        });
    }
}
