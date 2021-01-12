package com.tregix.serviceprovider.Model.order_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateOrder implements Serializable {

    @SerializedName("checkout_json")
    @Expose
    private PostOrder order;

    public PostOrder getOrder() {
        return order;
    }

    public void setOrder(PostOrder order) {
        this.order = order;
    }

    public CreateOrder(PostOrder order){
        this.order = order;
    }

    public CreateOrder(){}
}
