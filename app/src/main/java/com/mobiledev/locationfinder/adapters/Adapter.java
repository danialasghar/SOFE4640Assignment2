package com.mobiledev.locationfinder.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiledev.locationfinder.R;
import com.mobiledev.locationfinder.UpdateLocationActivity;
import com.mobiledev.locationfinder.models.Location;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> implements Filterable {

    Context context;
    Activity activity;
    List<Location> locationsList;
    List<Location> searchList;
    MyViewHolder.OnNoteListener myOnNoteListener;

    public Adapter(Context context, Activity activity, List<Location> locationsList, MyViewHolder.OnNoteListener onNoteListener) {
        this.context = context;
        this.activity = activity;
        this.locationsList = locationsList;
        this.myOnNoteListener = onNoteListener;
        searchList = new ArrayList<>(locationsList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_layout, parent, false);
        return new MyViewHolder(view, myOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.address.setText(locationsList.get(position).getAddress());
        holder.latitude.setText(locationsList.get(position).getLatitude());
        holder.longitude.setText(locationsList.get(position).getLongitude());
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    public Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Location> filteredResults = new ArrayList<>();
            if (constraint == null | constraint.length()==0){
                filteredResults.addAll(searchList);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Location item: searchList){
                    if (item.getAddress().toLowerCase().contains(filterPattern)){
                        filteredResults.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values=filteredResults;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            locationsList.clear();
            locationsList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView address, latitude, longitude;
        RelativeLayout layout;
        OnNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            address = itemView.findViewById(R.id.location_title);
            latitude = itemView.findViewById(R.id.location_latitude);
            longitude = itemView.findViewById(R.id.location_longitude);
            layout = itemView.findViewById(R.id.location_list_layout);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }

        public interface OnNoteListener{
            void onNoteClick(int position);
        }
    }
}
