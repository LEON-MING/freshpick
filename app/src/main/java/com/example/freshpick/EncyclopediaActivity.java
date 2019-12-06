package com.example.freshpick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncyclopediaActivity extends AppCompatActivity implements EncyclopediaRecyclerViewAdapter.ItemClickListener, SearchView.OnQueryTextListener {

    public static final String GROCERY_NAME = "com.example.freshpick.GROCERY_NAME";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EncyclopediaEntry> encEntries = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);

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
                                                                      encEntries.add(new EncyclopediaEntry(doc.getString("name"), ""));
                                                                  }
                                                                  mAdapter = new EncyclopediaRecyclerViewAdapter(encEntries, EncyclopediaActivity.this);
                                                                  mRecyclerView.setAdapter(mAdapter);
                                                                  Log.d("Names", encEntries.toString());
                                                              } else {
                                                                  Log.d("error", "get failed with ", task.getException());
                                                              }
                                                          }
                                                      });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.encyclopedia);
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
                startActivity(new Intent(EncyclopediaActivity.this, cls));
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
        searchView.setOnQueryTextListener(EncyclopediaActivity.this);
    }

    public void onItemClick(EncyclopediaEntry entry) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(GROCERY_NAME, entry.name);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("onQueryTextSubmit:", query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("onQueryTextChange:", newText);
        ((EncyclopediaRecyclerViewAdapter) mAdapter).getFilter().filter(newText);
        return true;
    }

    class EncyclopediaEntry implements Comparable<EncyclopediaEntry> {
        public String name;
        public String imageUrl;

        EncyclopediaEntry(String name, String imageUrl) {
            this.name = name;
            this.imageUrl = imageUrl;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public int compareTo(EncyclopediaEntry other) {
            return this.name.compareTo(other.name);
        }
    }
}
