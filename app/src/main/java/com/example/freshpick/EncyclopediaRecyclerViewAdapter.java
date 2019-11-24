package com.example.freshpick;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.freshpick.EncyclopediaActivity.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class EncyclopediaRecyclerViewAdapter extends RecyclerView.Adapter<EncyclopediaRecyclerViewAdapter.ViewHolder> {

    private final List<EncyclopediaEntry> mValues;
    private final EncyclopediaActivity mContext;
    private final StorageReference storageRef;

    public EncyclopediaRecyclerViewAdapter(List<EncyclopediaEntry> data, Context context) {
        mValues = data;
        Collections.sort(mValues);
        mContext = (EncyclopediaActivity) context;
        storageRef = FirebaseStorage.getInstance().getReference().child("Encyclopedia Images");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_encyclopedia_row, parent, false);
        return new EncyclopediaRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.entry = mValues.get(position);
        holder.mNameView.setText(holder.entry.name);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.onItemClick(holder.entry);
            }
        });
        storageRef.child(holder.entry.name + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                String photo_ref_url =  uri.toString();
                Picasso.get().load(photo_ref_url).into(holder.mImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("enc image error:", exception.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final ImageView mImageView;
        public EncyclopediaEntry entry;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.imageView);
            mNameView = view.findViewById(R.id.grocery_item_name);
        }

        @Override
        public String toString() {
            return entry.name;
        }
    }

    public interface ItemClickListener {
        void onItemClick(EncyclopediaEntry entry);
    }
}
