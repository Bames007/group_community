package com.example.groupcommunity.adapter;

import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.groupcommunity.models.placeApi;

import java.util.ArrayList;

public class placeSuggestion extends ArrayAdapter implements Filterable {

    ArrayList<String> results;

    int resource;
    FragmentActivity fragmentActivity;
    com.example.groupcommunity.models.placeApi placeApi = new placeApi();


    public placeSuggestion(@NonNull FragmentActivity fragmentActivity, int resId) {
        super(fragmentActivity, resId);
        this.fragmentActivity = fragmentActivity;
        this.resource = resId;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public String getItem(int pos) {
        return results.get(pos);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    results = placeApi.autocomplete(constraint.toString());

                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
