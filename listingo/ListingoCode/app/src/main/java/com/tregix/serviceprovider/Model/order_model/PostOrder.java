package com.tregix.serviceprovider.Model.order_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tregix.serviceprovider.Model.order_model.coupons_model.CouponDetails;

import java.util.ArrayList;
import java.util.List;


public class PostOrder {
    
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("customer_id")
    @Expose
    private String customerId;
    
    @SerializedName("sameAddress")
    @Expose
    private boolean sameAddress;
    
    @SerializedName("products")
    @Expose
    private List<OrderProducts> orderProducts = new ArrayList<>();
    @SerializedName("billing_info")
    @Expose
    private UserBilling billing;
    @SerializedName("shipping_info")
    @Expose
    private UserShipping shipping;
    @SerializedName("shipping_ids")
    @Expose
    private List<OrderShippingMethod> orderShippingMethods = new ArrayList<>();
    @SerializedName("coupons")
    @Expose
    private List<CouponDetails> orderCoupons = new ArrayList<>();

    String payment_method;
    String payment_method_title;
    String customer_note;
    String platform;

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public void setPayment_method_title(String payment_method_title) {
        this.payment_method_title = payment_method_title;
    }

    public void setCustomer_note(String customer_note) {
        this.customer_note = customer_note;

    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }



    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public boolean isSameAddress() {
        return sameAddress;
    }
    
    public void setSameAddress(boolean sameAddress) {
        this.sameAddress = sameAddress;
    }
    
    public List<OrderProducts> getOrderProducts() {
        return orderProducts;
    }
    
    public void setOrderProducts(List<OrderProducts> orderProducts) {
        this.orderProducts = orderProducts;
    }
    
    public UserBilling getBilling() {
        return billing;
    }
    
    public void setBilling(UserBilling billing) {
        this.billing = billing;
    }
    
    public UserShipping getShipping() {
        return shipping;
    }
    
    public void setShipping(UserShipping shipping) {
        this.shipping = shipping;
    }
    
    public List<OrderShippingMethod> getOrderShippingMethods() {
        return orderShippingMethods;
    }
    
    public void setOrderShippingMethods(List<OrderShippingMethod> orderShippingMethods) {
        this.orderShippingMethods = orderShippingMethods;
    }
    
    public List<CouponDetails> getOrderCoupons() {
        return orderCoupons;
    }
    
    public void setOrderCoupons(List<CouponDetails> orderCoupons) {
        this.orderCoupons = orderCoupons;
    }
}
