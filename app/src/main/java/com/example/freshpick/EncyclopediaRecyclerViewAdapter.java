package com.example.freshpick;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EncyclopediaRecyclerViewAdapter extends RecyclerView.Adapter<EncyclopediaRecyclerViewAdapter.ViewHolder> implements Filterable {

    private final List<EncyclopediaEntry> mValues;
    private List<EncyclopediaEntry> mFilteredValues;
    private final Filter mFilter;
    private final Context mContext;
    private final StorageReference storageRef;

    public EncyclopediaRecyclerViewAdapter(List<EncyclopediaEntry> data, Context context) {
        mValues = data;
        Collections.sort(mValues);
        mFilteredValues = new ArrayList<>();
        mFilteredValues.addAll(mValues);
        mFilter = new ItemFilter();
        mContext = context;
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
        holder.entry = mFilteredValues.get(position);
        holder.mNameView.setText(holder.entry.name);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof EncyclopediaActivity) {
                    ((EncyclopediaActivity) mContext).onItemClick(holder.entry);
                } else if (mContext instanceof GroceryListActivity){
                    ((GroceryListActivity) mContext).onItemClick(holder.entry);
                } else {
                    ((MainActivity) mContext).onItemClick(holder.entry);
                }
            }
        });
        holder.mAddToListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof EncyclopediaActivity) {
                    ((EncyclopediaActivity) mContext).onAddToListClick(holder.entry);
                } else if (mContext instanceof GroceryListActivity){
                    ((GroceryListActivity) mContext).onAddToListClick(holder.entry);
                } else {
                    ((MainActivity) mContext).onAddToListClick(holder.entry);
                }
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
        return mFilteredValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final ImageView mImageView;
        public final ImageView mAddToListView;
        public EncyclopediaEntry entry;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.imageView);
            mNameView = view.findViewById(R.id.grocery_item_name);
            mAddToListView = view.findViewById(R.id.add_to_list);
        }

        @Override
        public String toString() {
            return entry.name;
        }
    }

    public interface ItemClickListener {
        void onItemClick(EncyclopediaEntry entry);
    }

    public interface AddToListClickListener {
        void onAddToListClick(EncyclopediaEntry entry);
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            int count = mValues.size();
            final List<EncyclopediaEntry> newList = new ArrayList<>(count);

            for (EncyclopediaEntry entry : mValues) {
                if (entry.name.toLowerCase().startsWith(filterString.toLowerCase())) {
                    newList.add(entry);
                }
            }

            results.values = newList;
            results.count = newList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredValues = (ArrayList<EncyclopediaEntry>) results.values;
            notifyDataSetChanged();
        }

    }
}
