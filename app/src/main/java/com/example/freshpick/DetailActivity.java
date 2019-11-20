package com.example.freshpick;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String itemName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);



        //get intent stuff

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
                        Log.d("itemName", doc.getString("name"));

                        String docName = doc.getString("name");
                        if (docName.equals(itemName)) {

                            Log.d("itemName", doc.getString("name"));
                            item = doc;

                        }
                    }

                    Log.d("item", item.getString("name"));

                } else {
                    Log.d("error", "get failed with ", task.getException());
                }
            }
        });


    }
}
