package com.example.freshpick;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements DetailRecyclerViewAdapter.ItemClickListener{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference goodImagesRef = storageRef.child("Good Produce Images");
    StorageReference badImagesRef = storageRef.child("Bad Produce Images");
    String itemName = "";

    DetailRecyclerViewAdapter adapter;

    QueryDocumentSnapshot item = null;

    ArrayList<String> reviews = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);



        //get intent stuff
        Intent intent  = getIntent();
        this.itemName = intent.getStringExtra(EncyclopediaActivity.GROCERY_NAME);
//        this.itemName = "Apples";


        //get item from the db
        CollectionReference produceCollection = db.collection("produce");
        produceCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot produceQuery = task.getResult();



                    for (QueryDocumentSnapshot doc: produceQuery) {
                        String docName = doc.getString("name");
                        if (docName.equals(itemName)) {

                            Log.d("itemName", doc.getString("name"));
                            item = doc;

                        }
                    }

                    TextView nameText = (TextView) findViewById(R.id.produceName);
                    TextView priceText = (TextView) findViewById(R.id.PriceText);
                    TextView inSeasonText = (TextView) findViewById(R.id.inSeasonText);



                    nameText.setText(item.getString("name"));
                    priceText.setText("Average Price: $" + item.get("price") + " each");


                    boolean inSeason = item.getBoolean("inSeason");
                    if (inSeason) {
                        inSeasonText.setText("In season");
                        inSeasonText.setTextColor(getResources().getColor(R.color.inSeasonColor));
                    } else {
                        inSeasonText.setText("Not in season");
                        inSeasonText.setTextColor(getResources().getColor(R.color.notInSeasonColor));
                    }


                    reviews = (ArrayList<String>) item.get("reviews");
                    Collections.reverse(reviews);

                    Log.d("reviews:", reviews.toString());



                    // set up the RecyclerView
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    adapter = new DetailRecyclerViewAdapter(getApplicationContext(), reviews);
                    recyclerView.setAdapter(adapter);



                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });

        // Get good image.
        goodImagesRef.child(itemName + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                ImageView goodImage = (ImageView) findViewById(R.id.GoodImage);

                String photo_ref_url =  uri.toString();
                Picasso.get().load(photo_ref_url).into(goodImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("good image error:", exception.toString());
            }
        });


        // Get bad image.
        badImagesRef.child(itemName + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                ImageView badImage = (ImageView) findViewById(R.id.BadImage);

                String photo_ref_url =  uri.toString();
                Picasso.get().load(photo_ref_url).into(badImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("bad image error:", exception.toString());
            }
        });

        Button submitTips = (Button) findViewById(R.id.submitTipsButton);

        submitTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               detailPopup(view);
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
                startActivity(new Intent(DetailActivity.this, cls));
                Log.d("pageChange:", Integer.toString(menuItem.getItemId()));
                return true;
            }
        });
        bottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                startActivity(new Intent(DetailActivity.this, EncyclopediaActivity.class));
            }
        });

    }

    public void setMenuItemOnClick(BottomNavigationItemView m, final Class cls) {
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this, cls));
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
    }

    public void detailPopup(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.tip_popup, null);

        // create the popup window
        int width = 1000;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);



//                // dismiss the popup window when touched
//                popupView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        popupWindow.dismiss();
//                        return true;
//                    }
//                });

        Button submitTip = (Button) popupView.findViewById(R.id.submitTipButton);
        final EditText userTip = (EditText)  popupView.findViewById(R.id.userTip);

        submitTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference itemRef = db.collection("produce").document(item.getId());
                Log.d("item id:", item.getId());
                Log.d("user tip:", userTip.getText().toString());

                itemRef.update("reviews", FieldValue.arrayUnion(userTip.getText().toString()));

                reviews.add(0, userTip.getText().toString());

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new DetailRecyclerViewAdapter(getApplicationContext(), reviews);
                recyclerView.setAdapter(adapter);

                popupWindow.dismiss();
            }
        });

        popupWindow.setElevation(30);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

    }
}
