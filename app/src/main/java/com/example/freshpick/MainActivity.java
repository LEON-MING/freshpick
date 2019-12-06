package com.example.freshpick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements EncyclopediaRecyclerViewAdapter.ItemClickListener,
        EncyclopediaRecyclerViewAdapter.AddToListClickListener, SearchView.OnQueryTextListener {

    private RecyclerView mRecyclerView;
    private ScrollView mHomeScrollView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EncyclopediaActivity.EncyclopediaEntry> encEntries = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    mAdapter = new EncyclopediaRecyclerViewAdapter(encEntries, MainActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                    Log.d("Names", encEntries.toString());
                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });

        mHomeScrollView = findViewById(R.id.home_scroll);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.home);
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
                startActivity(new Intent(MainActivity.this, cls));
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
        searchView.setOnQueryTextListener(MainActivity.this);

        mRecyclerView.setVisibility(View.GONE);
    }

    public void setMenuItemOnClick(BottomNavigationItemView m, final Class cls) {
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, cls));
            }
        });
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
            mHomeScrollView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mHomeScrollView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        ((EncyclopediaRecyclerViewAdapter) mAdapter).getFilter().filter(newText);
        return true;
    }
}
