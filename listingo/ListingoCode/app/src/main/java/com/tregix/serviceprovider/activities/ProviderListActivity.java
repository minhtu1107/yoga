package com.tregix.serviceprovider.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tregix.serviceprovider.Interface.PaginationScrollListener;
import com.tregix.serviceprovider.Model.Login.Data;
import com.tregix.serviceprovider.Model.Login.User;
import com.tregix.serviceprovider.Model.Provider.MarkFavoriteParam;
import com.tregix.serviceprovider.Model.Provider.ProviderModel;
import com.tregix.serviceprovider.Model.ReviewProvider;
import com.tregix.serviceprovider.R;
import com.tregix.serviceprovider.Retrofit.RetrofitUtil;
import com.tregix.serviceprovider.Utils.Constants;
import com.tregix.serviceprovider.Utils.DatabaseUtil;
import com.tregix.serviceprovider.Utils.SharedPreferenceUtil;
import com.tregix.serviceprovider.adapters.ProviderListRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class ProviderListActivity extends CommonProviderInfoActivity {

    public static final int NUMBER_OF_POST = 25;
    private String category = Constants.EMPTY_STRING;
    private String keyword = Constants.EMPTY_STRING;
    private String location = Constants.EMPTY_STRING;
    private String country = Constants.EMPTY_STRING;
    private String city = Constants.EMPTY_STRING;
    private String zipCode = Constants.EMPTY_STRING;
    private String subCategory = Constants.EMPTY_STRING;
    private String isFeatured = Constants.EMPTY_STRING;
    private String radius = Constants.EMPTY_STRING;
    private String latitude = Constants.EMPTY_STRING;
    private String longitude = Constants.EMPTY_STRING;
    private int categoryId = -1;

    private int userId;
    private ProviderModel provider;
    private ProviderListRecyclerViewAdapter adapter;
    List<ProviderModel> data = new ArrayList<>();

    @Override
    protected void setAdapter() {
        loadData();
    }

    protected void loadData() {
        if (getIntent() != null) {
            Bundle bundle = getIntent().getBundleExtra(Constants.DATA);
            if (bundle != null) {
                category = bundle.getString(Constants.CATEGORY);
                keyword = bundle.getString(Constants.KEYWORD);
                country = bundle.getString(Constants.COUNTRY);
                city = bundle.getString(Constants.CITY);
                location = bundle.getString(Constants.LOCATION);
                zipCode = bundle.getString(Constants.ZIP_CODE);
                radius = bundle.getString(Constants.DISTANCE);
                isFeatured = bundle.getString(Constants.IS_FEATURED);
                latitude = bundle.getString(Constants.LATITUDE);
                longitude = bundle.getString(Constants.LONGITUDE);
                categoryId = bundle.getInt(Constants.CATEGORY_ID);
                subCategory = bundle.getString(Constants.SUB_CATEGORY);
            }
        }

        adapter = new ProviderListRecyclerViewAdapter(data,ProviderListActivity.this);

        User user = DatabaseUtil.getInstance().getUser();
        if(SharedPreferenceUtil.getBoolen(this, Constants.ISUSERLOGGEDIN)
                && user != null) {
            userId = user.getData().getID();
        }

        loadDataFromServer(1);
    }

    protected void loadDataFromServer(int currentPage) {
        adapter.addNullData();
        getRecyclerView().post(new Runnable() {
            public void run() {
                // There is no need to use notifyDataSetChanged()
                adapter.notifyItemInserted(adapter.getSize() - 1);
            }
        });
        if(isFeatured == null || isFeatured.isEmpty()) {
            if(categoryId == -1) {
                RetrofitUtil.createProviderAPI().searchProvider(currentPage, NUMBER_OF_POST,"application/json",userId,
                        keyword,
                        location,
                        radius,
                        country,
                        city,
                        zipCode,
                        latitude,
                        longitude).enqueue(dataCallBack);
            }else{
                RetrofitUtil.createProviderAPI().searchProvider(currentPage, NUMBER_OF_POST,"application/json",
                        userId,
                        categoryId,
                        DatabaseUtil.getInstance().getSubCategorySlug(categoryId,subCategory),
                        keyword,
                        location,
                        radius,
                        country,
                        city,
                        zipCode,
                        latitude,
                        longitude).enqueue(dataCallBack);
            }
        }else{
            RetrofitUtil.createProviderAPI().
                    getFeaturedProviders(currentPage, NUMBER_OF_POST,userId, Constants.IS_FEATURED_CODE).enqueue(dataCallBack);
        }
    }

    @Override
    public void onProviderListInteraction(ProviderModel item) {
        Intent detailActiivtyIntent = new Intent(this, ProviderDetailActivity.class);
        detailActiivtyIntent.putExtra(Constants.DATA, item);
        provider = item;
        startActivityForResult(detailActiivtyIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            provider.setIsfav(((ProviderModel) data.getSerializableExtra(Constants.DATA)).isfav());
            getRecyclerView().getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onProviderFavorite(ProviderModel item) {
        if(SharedPreferenceUtil.getBoolen(ProviderListActivity.this, Constants.ISUSERLOGGEDIN)) {
            showProgressDialog(getString(R.string.msg_updating_fvrt));
            RetrofitUtil.createProviderAPI().updateUserFavorites(
                    new MarkFavoriteParam(item.getID(),
                            DatabaseUtil.getInstance().getUser().getData().getID(),
                            !item.isfav())).enqueue(RetrofitUtil.updateUserFavorites(item,this));
        }else{
            showDialogSignedUp(getString(R.string.err_login_to_fvrt),this);
        }
    }

    @Override
    public void onUpdateFavorites(ProviderModel item) {
        item.setIsfav(!item.isfav());
        if (getRecyclerView().getAdapter() != null) {
            getRecyclerView().getAdapter().notifyDataSetChanged();
        }
        hideProgressDialog();
    }

    @Override
    public void onPositiveClick(String msg) {
        super.onPositiveClick(msg);
        openActivityForResult(LoginActivity.class);
    }

    Callback<List<ProviderModel>> dataCallBack = new Callback<List<ProviderModel>>() {
        @Override
        public void onResponse(Call<List<ProviderModel>> call, Response<List<ProviderModel>> response) {
            isLoading = false;
                adapter.removeNull();
            if (response.isSuccessful()) {

                if (response.body() != null && !response.body().isEmpty()) {
                    if(data.isEmpty()){
                        getRecyclerView().setAdapter(adapter);
                      //  data.addAll(response.body());
                    }
                    adapter.addNewData(response.body());
                    isLoading = false;
                } else if(currentPage == 1){
                    if(data == null || data.isEmpty())
                        showNoData();
                }else{
                    isLastPage = true;
                }
            } else {
                adapter.removeNull();
                Log.d("QuestionsCallback", "Code: " + response.code() + " Message: " + response.message());
                if(data == null || data.isEmpty())
                    showNoData();
                isLastPage = true;

                getRecyclerView().hideShimmerAdapter();
            }
        }

        @Override
        public void onFailure(Call<List<ProviderModel>> call, Throwable t) {
            t.printStackTrace();
            adapter.removeNull();
            if(data == null || data.isEmpty())
            showNoData();
            isLoading = false;
            isLastPage = true;

            getRecyclerView().hideShimmerAdapter();
        }
    };

}
