package com.example.freshpick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        final ListView groceryList = findViewById(R.id.grocery_list);
        final List<ListViewItemObj> demoItemList = new ArrayList<>();

        ListViewItemObj item1 = new ListViewItemObj();
        item1.setChecked(false);
        item1.setItemText("Apples");
        demoItemList.add(item1);

        ListViewItemObj item2 = new ListViewItemObj();
        item2.setChecked(true);
        item2.setItemText("Potatoes");
        demoItemList.add(item2);

        ListViewItemObj item3 = new ListViewItemObj();
        item3.setChecked(false);
        item3.setItemText("Milk");
        demoItemList.add(item3);

        final ListViewItemCheckboxAdapter groceryListDataAdapter = new ListViewItemCheckboxAdapter(getApplicationContext(), demoItemList);

        groceryListDataAdapter.notifyDataSetChanged();

        groceryList.setAdapter(groceryListDataAdapter);

        // When list view item is clicked.
        groceryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {
                // Get user selected item.
                Object itemObject = adapterView.getAdapter().getItem(itemIndex);

                // Translate the selected item to DTO object.
                ListViewItemObj itemDto = (ListViewItemObj)itemObject;

                // Get the checkbox.
                CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.list_view_item_checkbox);

                // Reverse the checkbox and clicked item check state.
                if(itemDto.isChecked())
                {
                    itemCheckbox.setChecked(false);
                    itemDto.setChecked(false);
                }else
                {
                    itemCheckbox.setChecked(true);
                    itemDto.setChecked(true);
                }

            }
        });

        Button selectAllButton = (Button)findViewById(R.id.clearButton);
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                demoItemList.clear();
                groceryListDataAdapter.notifyDataSetChanged();
            }
        });


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
