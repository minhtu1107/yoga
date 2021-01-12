package com.tregix.serviceprovider.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tregix.serviceprovider.Interface.OnListInteractionListener;
import com.tregix.serviceprovider.Model.Provider.BusinessHours;
import com.tregix.serviceprovider.R;

import java.util.List;

/**
 * Created by Tregix on 11/29/2017.
 */

public class ProviderBusinessHoursAdapter extends RecyclerView.Adapter<ProviderBusinessHoursAdapter.ViewHolder> {
    private final BusinessHours mValues;
    private final OnListInteractionListener mListener;

    public ProviderBusinessHoursAdapter(BusinessHours items,
                                        OnListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ProviderBusinessHoursAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_hours_item, parent, false);
        return new ProviderBusinessHoursAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ProviderBusinessHoursAdapter.ViewHolder holder, int position) {
        if (position == 0) {
            holder.day.setText(R.string.monday);
            if (mValues.getMonday() != null) {
                holder.time.setText(getHours(mValues.getMonday().getStarttime(), mValues.getMonday().getEndtime()));

            }
        }else if (position == 1) {
            holder.day.setText(R.string.tuesday);
            if (mValues.getTuesday() != null) {
                holder.time.setText(getHours(mValues.getTuesday().getStarttime(), mValues.getTuesday().getEndtime()));

            }
        }else if (position == 2) {
            holder.day.setText(R.string.wed);
            if (mValues.getWednesday() != null) {
                holder.time.setText(getHours(mValues.getWednesday().getStarttime(), mValues.getWednesday().getEndtime()));

            }
        }else if (position == 3) {
            holder.day.setText(R.string.thursday);
            if (mValues.getThursday() != null) {
                holder.time.setText(getHours(mValues.getThursday().getStarttime(), mValues.getThursday().getEndtime()));

            }
        }else  if (position == 4) {
            holder.day.setText(R.string.friday);
            if (mValues.getFriday() != null) {
                holder.time.setText(getHours(mValues.getFriday().getStarttime(), mValues.getFriday().getEndtime()));

            }
        }else  if (position == 5) {
            holder.day.setText(R.string.sat);
            if (mValues.getSaturday() != null) {
                holder.time.setText(getHours(mValues.getSaturday().getStarttime(), mValues.getSaturday().getEndtime()));

            }
        }else  if (position == 6) {
            holder.day.setText(R.string.sun);
            if (mValues.getSunday() != null) {
                holder.time.setText(getHours(mValues.getSunday().getStarttime(), mValues.getSunday().getEndtime()));

            }
        }


    }

    private String getHours(List<String> starttime, List<String> endTime) {
        String hours = "";
        if(starttime != null && endTime != null) {
            for (int i = 0; i < starttime.size(); i++) {
                if (!hours.equals("")) {
                    hours = hours.concat("\n");
                }
                hours = hours + starttime.get(i) + " - " + endTime.get(i);
            }
        }

        return hours;
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView day;
        public final TextView time;
        public BusinessHours mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            day = (TextView) view.findViewById(R.id.business_hour_day);
            time = (TextView) view.findViewById(R.id.business_hour_time);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + day.getText() + "'";
        }
    }
}
