package com.app.avalon.courtyard.flats.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.app.avalon.courtyard.flats.R;
import com.app.avalon.courtyard.flats.beans.OwnerDetails;
import com.app.avalon.courtyard.flats.commons.AppLogs;
import com.app.avalon.courtyard.flats.listeners.CustomOnItemClickListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OwnerInfoAdapter extends RecyclerView.Adapter<OwnerInfoAdapter.OwnerInfoViewHolder> implements Filterable{

    private static final String TAG = OwnerInfoViewHolder.class.getSimpleName();
    private List<OwnerDetails> originalData = null;
    private List<OwnerDetails> filteredData = null;
    private CustomOnItemClickListener mCustomOnItemClickListener;

    public OwnerInfoAdapter(List<OwnerDetails> filteredData, Context mContext) {
        this.filteredData = filteredData;
        this.originalData = filteredData;
        this.mCustomOnItemClickListener = (CustomOnItemClickListener) mContext;
    }

    @Override
    public OwnerInfoViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);

        final OwnerInfoViewHolder ownerInfoViewHolder = new OwnerInfoViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomOnItemClickListener.onItemClick(v, ownerInfoViewHolder.getAdapterPosition(), filteredData.get(ownerInfoViewHolder.getAdapterPosition()));
            }
        });

        Log.e(OwnerInfoAdapter.class.getSimpleName(), "onCreateViewHolder");
        return ownerInfoViewHolder;
    }

    @Override
    public void onBindViewHolder(OwnerInfoViewHolder ownerInfoViewHolder, int position) {
        Log.e(OwnerInfoAdapter.class.getSimpleName(), "onBindViewHolder - " + position);

        StringBuilder builder = new StringBuilder();
        builder.append("\nSerial No : "+filteredData.get(position).Sr+"\nName : ").append(showInfo(filteredData.get(position).Name)).append("\nHouse No : ").append(showInfo(filteredData.get(position).Block)+"-"+filteredData.get(position).BlockNumber)
                .append("\nMobile No.1 : ").append(showInfo(filteredData.get(position).CellOne)).append("\nMobile No.2 : ").append(showInfo(filteredData.get(position).CellTwo)).append("\nTwo Wheeler 1 : ")
                .append(showInfo(filteredData.get(position).TwoWheelerOne)).append("\nTwo Wheeler 2 : ").append(showInfo(filteredData.get(position).TwoWheelerTwo))
                .append("\nFour Wheeler 1 : ").append(showInfo(filteredData.get(position).FourWheelerOne)).append("\nFour Wheeler 2 : ")
                .append(showInfo(filteredData.get(position).FourWheelerTwo)).append("\n");

        ownerInfoViewHolder.textViewInfo.setText(builder.toString());
    }

    private String showInfo(String info) {
        StringBuilder builder = new StringBuilder();
        if (info != null && !info.equalsIgnoreCase("")) {
            builder.append(info);
        }
        else{
            builder.append("N/A");
        }
        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    @Override
    public Filter getFilter() {
        return new ItemFilter();
    }

    public static class OwnerInfoViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewInfo;

        public OwnerInfoViewHolder(View view) {
            super(view);

            textViewInfo = (TextView) view.findViewById(R.id.text_info);
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
            List<OwnerDetails> filteredList = new ArrayList<OwnerDetails>();

            if (originalData == null) {
                originalData = new ArrayList<OwnerDetails>(filteredData); // saves the original data in mOriginalValues
            }

            if (constraint == null || constraint.length() == 0) {

                // set the Original result to return
                results.count = originalData.size();
                results.values = originalData;
            } else {
                String regex = "-|\\s"; //replace space & -
                constraint = constraint.toString().toLowerCase().replaceAll(regex,"");
                for (int i = 0; i < originalData.size(); i++) {
                    OwnerDetails data = originalData.get(i);
                    StringBuilder builder = new StringBuilder();
                    builder.append(data.Block.toLowerCase()).append(data.BlockNumber.toLowerCase());
                    if (data.Name.toLowerCase().contains(constraint.toString())
                            || data.CellOne.toLowerCase().contains(constraint.toString())
                            || builder.toString().contains(constraint.toString())
                            || data.CellTwo.toLowerCase().contains(constraint.toString())
                            || data.TwoWheelerOne.toLowerCase().replaceAll(regex,"").contains(constraint.toString())
                            || data.TwoWheelerTwo.toLowerCase().replaceAll(regex,"").contains(constraint.toString())
                            || data.FourWheelerOne.toLowerCase().replaceAll(regex,"").contains(constraint.toString())
                            || data.FourWheelerTwo.toLowerCase().replaceAll(regex,"").contains(constraint.toString())) {
                        filteredList.add(data);
                    }
                }
                // set the Filtered result to return
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<OwnerDetails>) results.values;
            notifyDataSetChanged();
        }
    }

    public void messageAdded(DataSnapshot dataSnapshot){
        if (dataSnapshot.getValue() != null) {
            OwnerDetails ownerDetails = dataSnapshot.getValue(OwnerDetails.class);
            ownerDetails.Sr = dataSnapshot.getKey();

            filteredData.add(ownerDetails);
            notifyItemInserted(filteredData.size() - 1);
        }
    }

    public void messageUpdated(DataSnapshot dataSnapshot){
        if (dataSnapshot.getValue() != null) {
            OwnerDetails updatedOwnerDetails = dataSnapshot.getValue(OwnerDetails.class);
            updatedOwnerDetails.Sr = dataSnapshot.getKey();

            int chatIndex = filteredData.indexOf(updatedOwnerDetails);

            if (chatIndex > -1) {
                filteredData.set(chatIndex, updatedOwnerDetails);
                notifyItemChanged(chatIndex);
            } else {
                AppLogs.w(TAG, "onChildChanged:unknown_child:" + updatedOwnerDetails);
            }
        }
    }

    public void messageDeleted(DataSnapshot dataSnapshot){
        if (dataSnapshot.getValue() != null) {
            OwnerDetails deletedOwnerDetails = dataSnapshot.getValue(OwnerDetails.class);
            deletedOwnerDetails.Sr = dataSnapshot.getKey();

            int chatIndex = filteredData.indexOf(deletedOwnerDetails);
            if (chatIndex > -1) {
                filteredData.remove(chatIndex);
                notifyItemRemoved(chatIndex);
            } else {
                AppLogs.w(TAG, "onChildRemoved:unknown_child:" + deletedOwnerDetails);
            }
        }
    }
}