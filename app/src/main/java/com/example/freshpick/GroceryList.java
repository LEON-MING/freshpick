package com.example.freshpick;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class GroceryList extends AppCompatActivity {

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


    }
}
