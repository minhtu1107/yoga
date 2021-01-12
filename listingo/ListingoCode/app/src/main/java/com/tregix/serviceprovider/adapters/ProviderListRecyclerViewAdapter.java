package com.tregix.serviceprovider.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tregix.serviceprovider.Interface.OnListInteractionListener;
import com.tregix.serviceprovider.Model.Provider.ProviderModel;
import com.tregix.serviceprovider.R;

import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;


public class ProviderListRecyclerViewAdapter extends RecyclerView.Adapter<ProviderListRecyclerViewAdapter.CustomViewHolder> {

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_LOADING = 2;
    private final List<ProviderModel> mValues;
    private final OnListInteractionListener mListener;

    public ProviderListRecyclerViewAdapter(List<ProviderModel> items, OnListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      /*  View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.provider_item, parent, false);
        return new ViewHolder(view);*/
        View root = null;
        if (viewType == VIEW_TYPE_ITEM) {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_item, parent, false);
            return new ViewHolder(root);
        } else {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progress_holder, parent, false);
            return new ProgressViewHolder(root);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mValues.get(position) != null)
            return VIEW_TYPE_ITEM;
        else
            return VIEW_TYPE_LOADING;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holderView, final int position) {
        if (holderView instanceof ViewHolder) {

            ViewHolder holder = (ViewHolder)holderView;
            holder.mItem = mValues.get(position);
            holder.mCategoryView.setText(Html.fromHtml(mValues.get(position).getCategory()));
            holder.mCompanyView.setText(Html.fromHtml(mValues.get(position).getUsername()));
            holder.mEmail.setText(mValues.get(position).getEmail());
            Picasso.get().load(mValues.get(position).getAvatar()).placeholder(R.drawable.placeholder)
                    .into(holder.mThumb);

            if (mValues.get(position).getPhone() != null && !mValues.get(position).getPhone().isEmpty()) {
                holder.mPhone.setText(mValues.get(position).getPhone());
            }

            if (mValues.get(position).getReviewData() != null) {
                if (mValues.get(position).getReviewData().getSpAverageRating() != null) {
                    holder.ratingBar.setRating(mValues.get(position).getReviewData().getSpAverageRating().floatValue());
                }
                if (mValues.get(position).getReviewData().getSpTotalPercentage() != 0 &&
                        mValues.get(position).getReviewData().getSpTotalRating() != null) {
                    String text = mValues.get(position).getReviewData().getSpTotalPercentage() + "% "
                            + "(" + mValues.get(position).getReviewData().getSpTotalRating() +
                            holder.ratingReview.getContext().getResources().getQuantityString(R.plurals.numberOfVotes,
                                    mValues.get(position).getReviewData().getSpTotalRating()) + ")";
                    holder.ratingReview.setText(text);
                }
            }

            if (holder.mItem.isfav()) {
                holder.favorite.setBackground(holder.favorite.getContext().getResources().getDrawable(R.drawable.ic_fav_filled));
            } else {
                holder.favorite.setBackground(holder.favorite.getContext().getResources().getDrawable(R.drawable.ic_heart));
            }

            holder.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onProviderFavorite(holder.mItem);
                    }
                }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onProviderListInteraction(mValues.get(position));
                    }
                }
            });
        }

    }

    public int getSize(){
       return mListener!=null? mValues.size():0;
    }
    public void addNullData() {
        mValues.add(null);
//        notifyItemInserted(mValues.size() - 1);
    }

    public void removeNull() {
        mValues.remove(mValues.size() - 1);
        notifyItemRemoved(mValues.size());
    }
    public void addNewData(List<ProviderModel> list){
        mValues.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(CustomViewHolder holderView) {
        super.onViewRecycled(holderView);
        if (holderView instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) holderView;
            holder.mCategoryView.setText("");
            holder.mCompanyView.setText("");
            holder.mPhone.setText("");
            holder.mEmail.setText("");
            holder.ratingBar.setRating(0);
            holder.ratingReview.setText(R.string.no_vote);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends CustomViewHolder {
        public final View mView;
        public final TextView mCategoryView;
        public final TextView mCompanyView;
        public final TextView mEmail;
        public final TextView mPhone;
        public final ImageView mThumb;
        public final MaterialRatingBar ratingBar;
        public final TextView ratingReview;
        public ProviderModel mItem;
        public final ImageView favorite;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCategoryView = (TextView) view.findViewById(R.id.provider_category);
            mCompanyView = (TextView) view.findViewById(R.id.provider_company);
            mEmail = (TextView) view.findViewById(R.id.provider_email);
            mPhone = (TextView) view.findViewById(R.id.provider_phone);
            mThumb =  view.findViewById(R.id.provider_thumbail);
            ratingBar = view.findViewById(R.id.provider_rating);
            ratingReview = view.findViewById(R.id.provider_rating_votes);
            favorite = view.findViewById(R.id.provider_fvrt);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCompanyView.getText() + "'";
        }
    }

    class ProgressViewHolder extends CustomViewHolder {

        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public CustomViewHolder(View itemView) {
            super(itemView);
        }
    }
}
