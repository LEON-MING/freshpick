package com.example.freshpick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends AppCompatActivity implements EncyclopediaRecyclerViewAdapter.ItemClickListener,
        EncyclopediaRecyclerViewAdapter.AddToListClickListener, SearchView.OnQueryTextListener {

    private List<EncyclopediaActivity.EncyclopediaEntry> encEntries = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mRecyclerView;
    private ListView mGroceryListView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ListViewItemCheckboxAdapter mGroceryListAdapter;
    final static List<ListViewItemObj> demoItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        mGroceryListView = findViewById(R.id.grocery_list);

        if (demoItemList.size() == 0) {
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
        }

        mGroceryListAdapter = new ListViewItemCheckboxAdapter(getApplicationContext(), demoItemList);

        mGroceryListAdapter.notifyDataSetChanged();

        mGroceryListView.setAdapter(mGroceryListAdapter);

        // When list view item is clicked.
        mGroceryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        mRecyclerView = findViewById(R.id.encItems);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        CollectionReference produceCollection = db.collection("produce");
        produceCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot produceQuery = task.getResult();
                    for (QueryDocumentSnapshot doc : produceQuery) {
                        encEntries.add(new EncyclopediaActivity.EncyclopediaEntry(doc.getString("name"), ""));
                    }
                    mAdapter = new EncyclopediaRecyclerViewAdapter(encEntries, GroceryListActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                    Log.d("Names", encEntries.toString());
                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });

        MaterialButton clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new MaterialButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroceryListActivity.demoItemList.clear();
                mGroceryListAdapter.notifyDataSetChanged();
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

        SearchView searchView = findViewById(R.id.encSearch);
        searchView.setOnQueryTextListener(GroceryListActivity.this);

        mRecyclerView.setVisibility(View.GONE);
    }

    public void onItemClick(EncyclopediaActivity.EncyclopediaEntry entry) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EncyclopediaActivity.GROCERY_NAME, entry.name);
        startActivity(intent);
    }

    public void onAddToListClick(EncyclopediaActivity.EncyclopediaEntry entry) {
        ListViewItemObj item1 = new ListViewItemObj();
        item1.setChecked(false);
        item1.setItemText(entry.name);
        GroceryListActivity.demoItemList.add(item1);
        Toast toast = Toast.makeText(getApplicationContext(), "Added " + entry.name + " to grocery list.", Toast.LENGTH_SHORT);
        toast.show();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("onQueryTextSubmit:", query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("onQueryTextChange:", newText);
        if (newText.length() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mGroceryListView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mGroceryListView.setVisibility(View.GONE);
        }
        ((EncyclopediaRecyclerViewAdapter) mAdapter).getFilter().filter(newText);
        return true;
    }
}
