package com.example.freshpick;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference goodImagesRef = storageRef.child("Good Produce Images");
    StorageReference badImagesRef = storageRef.child("Bad Produce Images");
    String itemName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);



        //get intent stuff
//        Intent intent  = getIntent();
//        this.itemName = intent.getStringExtra("selectedItem");

        this.itemName = "Apples";


        //get item from the db
        CollectionReference produceCollection = db.collection("produce");
        produceCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot produceQuery = task.getResult();


                    QueryDocumentSnapshot item = null;
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


    }
}
