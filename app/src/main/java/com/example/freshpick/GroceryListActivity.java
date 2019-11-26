package com.example.freshpick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        final ListView groceryList = findViewById(R.id.grocery_list);
        final List<ListViewItemObj> demoItemList = new ArrayList<>();

        ListViewItemObj item1 = new ListViewItemObj();
        item1.setChecked(false);
        item1.setItemText("Apples");
        demoItemList.add(item1);

        ListViewItemObj item2 = new ListViewItemObj();
        item1.setChecked(true);
        item1.setItemText("Potatoes");
        demoItemList.add(item2);

        ListViewItemObj item3 = new ListViewItemObj();
        item1.setChecked(false);
        item1.setItemText("Milk");
        demoItemList.add(item3);

        final ListViewItemCheckboxAdapter groceryListDataAdapter = new ListViewItemCheckboxAdapter(getApplicationContext(), demoItemList);

        groceryListDataAdapter.notifyDataSetChanged();

        groceryList.setAdapter(groceryListDataAdapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.grocery_list);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Class cls;
                int id = menuItem.getItemId();
                if (id == R.id.encyclopedia) {
                    cls = EncyclopediaActivity.class;
                } else if (id == R.id.home) {
                    cls = MainActivity.class;
                } else if (id == R.id.grocery_list){
                    cls = GroceryListActivity.class;
                } else {
                    cls = MainActivity.class;
                }
                startActivity(new Intent(GroceryListActivity.this, cls));
                Log.d("pageChange:", Integer.toString(menuItem.getItemId()));
                return true;
            }
        });
        bottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                // do nothing
            }
        });
    }
}
