package com.tregix.serviceprovider.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.tregix.serviceprovider.Model.order_model.OrderDetails;
import com.tregix.serviceprovider.Model.packages.PackageItem;
import com.tregix.serviceprovider.R;
import com.tregix.serviceprovider.Utils.Constants;
import com.tregix.serviceprovider.adapters.PackageDetailsAdapter;

public class PackageDetailActivity extends BaseActivity {

    ShimmerRecyclerView recyclerView;
    PackageItem item;
    OrderDetails orderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_detail);
        orderDetails = new OrderDetails();

        item = (PackageItem) getIntent().getSerializableExtra(Constants.DATA);

        getSupportActionBar().setTitle(item.getName());
        initViews();


        findViewById(R.id.btn_buy_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }
        });
    }

    private void initViews() {

        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PackageDetailsAdapter(item.getMetaData()));

        TextView title = (TextView) findViewById(R.id.tv_package_title);
        TextView  price = (TextView) findViewById(R.id.tv_package_price);
        TextView limit = (TextView) findViewById(R.id.tv_package_limit);

        findViewById(R.id.current_pkg).setVisibility(View.GONE);
        title.setText(item.getName());
        price.setText(Html.fromHtml(item.getPriceHtml()));

        if(item.getMetaData() != null && item.getMetaData().size() >=2) {
            limit.setText(" for " + Html.fromHtml(item.getMetaData().get(1).getValue().toString()) + " days");
        }
    }

    private void createOrder() {
        Intent detailActiivtyIntent = new Intent(this, CheckoutActivity.class);
        detailActiivtyIntent.putExtra(Constants.DATA, item);
        startActivity(detailActiivtyIntent);
    }

}
