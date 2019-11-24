package com.example.freshpick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

public class EncyclopediaActivity extends AppCompatActivity implements EncyclopediaRecyclerViewAdapter.ItemClickListener {

    public static final String GROCERY_NAME = "com.example.freshpick.GROCERY_NAME";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EncyclopediaEntry> encEntries = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        BottomNavigationItemView groceryList = findViewById(R.id.grocery_list);
        setMenuItemOnClick(groceryList, MainActivity.class);

        BottomNavigationItemView home = findViewById(R.id.home);
        setMenuItemOnClick(home, MainActivity.class);

        BottomNavigationItemView encyclopedia = findViewById(R.id.encyclopedia);
        setMenuItemOnClick(encyclopedia, EncyclopediaActivity.class);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.encyclopedia);
    }

    public void setMenuItemOnClick(BottomNavigationItemView m, final Class cls) {
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EncyclopediaActivity.this, cls));
            }
        });
    }

    public void onItemClick(EncyclopediaEntry entry) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(GROCERY_NAME, entry.name);
        startActivity(intent);
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
