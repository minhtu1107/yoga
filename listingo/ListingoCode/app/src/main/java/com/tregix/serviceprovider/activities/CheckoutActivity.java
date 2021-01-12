package com.tregix.serviceprovider.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.tregix.serviceprovider.Interface.DialogInteractionListener;
import com.tregix.serviceprovider.Model.Login.User;
import com.tregix.serviceprovider.Model.Provider.ProviderModel;
import com.tregix.serviceprovider.Model.order_model.CreateOrder;
import com.tregix.serviceprovider.Model.order_model.GetAllPaymentMethod;
import com.tregix.serviceprovider.Model.order_model.OrderDetails;
import com.tregix.serviceprovider.Model.order_model.OrderProducts;
import com.tregix.serviceprovider.Model.order_model.OrderShippingMethod;
import com.tregix.serviceprovider.Model.order_model.PlaceOrderResponse;
import com.tregix.serviceprovider.Model.order_model.PostOrder;
import com.tregix.serviceprovider.Model.order_model.UserBilling;
import com.tregix.serviceprovider.Model.order_model.UserShipping;
import com.tregix.serviceprovider.Model.order_model.coupons_model.CouponDetails;
import com.tregix.serviceprovider.Model.order_model.get_tax.GetTax;
import com.tregix.serviceprovider.Model.order_model.get_tax.Product;
import com.tregix.serviceprovider.Model.packages.PackageItem;
import com.tregix.serviceprovider.R;
import com.tregix.serviceprovider.Retrofit.ProviderApi;
import com.tregix.serviceprovider.Retrofit.RetrofitUtil;
import com.tregix.serviceprovider.Utils.AppUtils;
import com.tregix.serviceprovider.Utils.Constants;
import com.tregix.serviceprovider.Utils.DatabaseUtil;
import com.tregix.serviceprovider.Utils.SharedPreferenceUtil;
import com.tregix.serviceprovider.Utils.ValidateInputs;
import com.tregix.serviceprovider.adapters.PaymentMethodAdapter;
import com.tregix.serviceprovider.fragments.PackagesRecyclerViewAdapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;

import static com.tregix.serviceprovider.Retrofit.RetrofitUtil.getUserProfile;
import static okhttp3.internal.Util.ISO_8859_1;


public class CheckoutActivity extends BaseActivity {
    
    View rootView;
  
    public static boolean CHECKOUT_CALL;
    
    WebView checkout_webView;
    
    OrderDetails orderDetails;
    
    LinearLayout main_checkout;
    
    String ORDER_ID;
    String ORDER_RECEIVED = "order-received";
    String CHECKOUT_URL = ProviderApi.BASE_SITE +"listingo-app-checkout/";
    String ORDER_ERROR = "error";

    String tax;
   
    String selectedPaymentMethod,selectedPaymentTitle;
   
    double checkoutSubtotal = 0.0, checkoutTax = 0.0, checkoutShipping= 0.0, checkoutShippingCost= 0.0, checkoutDiscount= 0, checkoutTotal = 0;
    
    Button checkout_paypal_btn;
    CardView card_details_layout;
    ProgressDialog progressDialog;
    NestedScrollView scroll_container;
    RecyclerView checkout_items_recycler;
   // RecyclerView checkout_coupons_recycler;
    Button checkout_coupon_btn, checkout_order_btn, checkout_cancel_btn;
    ImageButton edit_billing_Btn, edit_shipping_Btn, edit_shipping_method_Btn;
    EditText checkout_coupon_code, checkout_comments, checkout_card_number, checkout_card_cvv, checkout_card_expiry;
    TextView checkout_subtotal, checkout_tax, checkout_shipping, checkout_discount, checkout_total, demo_coupons_text;
    TextView billing_name, billing_street, billing_address, shipping_name, shipping_street, shipping_address, shipping_method, payment_method;
    
    List<CouponDetails> couponsList;

  //  CheckoutItemsAdapter checkoutItemsAdapter;
    
    List<GetAllPaymentMethod> paymentMethodsList;
    
    UserBilling billingAddress;
    UserShipping shippingAddress;
    List<OrderShippingMethod> shippingIDS;
    List<Product> allProducts;
   // CouponsAdapter couponsAdapter;
    OrderShippingMethod shippingMethod;
    GetTax getTaxModel;
    
    PackageItem aPackage ;

    boolean orderConfirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        CHECKOUT_CALL = true;
    
        orderDetails = new OrderDetails();
        getSupportActionBar().setTitle("Checkout");

        aPackage = (PackageItem) getIntent().getSerializableExtra(Constants.DATA);


        // Binding Layout Views
        checkout_order_btn = (Button)  findViewById(R.id.checkout_order_btn);
        checkout_cancel_btn = (Button)  findViewById(R.id.checkout_cancel_btn);
       
        edit_billing_Btn = (ImageButton)  findViewById(R.id.checkout_edit_billing);
        edit_shipping_Btn = (ImageButton)  findViewById(R.id.checkout_edit_shipping);
        edit_shipping_method_Btn = (ImageButton)  findViewById(R.id.checkout_edit_shipping_method);
        shipping_method = (TextView)  findViewById(R.id.shipping_method);
        payment_method = (TextView)  findViewById(R.id.payment_method);
        checkout_subtotal = (TextView)  findViewById(R.id.checkout_subtotal);
        checkout_tax = (TextView)  findViewById(R.id.checkout_tax);
        checkout_shipping = (TextView)  findViewById(R.id.checkout_shipping);
        checkout_discount = (TextView)  findViewById(R.id.checkout_discount);
        checkout_total = (TextView)  findViewById(R.id.checkout_total);
        shipping_name = (TextView)  findViewById(R.id.shipping_name);
        shipping_street = (TextView)  findViewById(R.id.shipping_street);
        shipping_address = (TextView)  findViewById(R.id.shipping_address);
        billing_name = (TextView)  findViewById(R.id.billing_name);
        billing_street = (TextView)  findViewById(R.id.billing_street);
        billing_address = (TextView)  findViewById(R.id.billing_address);
      
        checkout_comments = (EditText)  findViewById(R.id.checkout_comments);
        checkout_items_recycler = (RecyclerView)  findViewById(R.id.checkout_items_recycler);
        main_checkout = (LinearLayout)  findViewById(R.id.main_checkout);
        
        card_details_layout = (CardView)  findViewById(R.id.card_details_layout);
        checkout_paypal_btn = (Button)  findViewById(R.id.checkout_paypal_btn);
        checkout_card_number = (EditText)  findViewById(R.id.checkout_card_number);
        checkout_card_cvv = (EditText)  findViewById(R.id.checkout_card_cvv);
        checkout_card_expiry = (EditText)  findViewById(R.id.checkout_card_expiry);
        scroll_container = (NestedScrollView)  findViewById(R.id.scroll_container);
        
        //checkout_order_btn.setEnabled(false);
        card_details_layout.setVisibility(View.GONE);
        checkout_paypal_btn.setVisibility(View.GONE);
        
        
        checkout_items_recycler.setNestedScrollingEnabled(false);
       // checkout_coupons_recycler.setNestedScrollingEnabled(false);
        
        checkout_card_expiry.setKeyListener(null);


        showProgressDialog("Loading...");

        RetrofitUtil.createProviderAPI().getUserProfile(DatabaseUtil.getInstance().getUserID()).enqueue(getUserProfile(this));


        
    }
    
    
    
    //*********** Called when the fragment is no longer in use ********//
    
    @Override
    public void onDestroy() {
      //   CheckoutActivity.this.stopService(new Intent( CheckoutActivity.this, PayPalService.class));
        super.onDestroy();
    }
    
    
    
    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
    }
    
    
    
    //*********** Returns Final Price of User's Cart ********//
    
    private double getProductsSubTotal() {
        return Double.parseDouble(aPackage.getPrice());
    }
    
    
    
    //*********** Set Checkout's Subtotal, Tax, ShippingCost, Discount and Total Prices ********//
    
    private void setCheckoutTotal() {
    
        // Get Cart Total
        checkoutSubtotal = getProductsSubTotal();
        // Calculate Checkout Total
        checkoutTotal = checkoutSubtotal;
        
        // Set Checkout Details
        checkout_tax.setText(Constants.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutTax));
        checkout_shipping.setText(Constants.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutShipping));
        checkout_discount.setText(Constants.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutDiscount));
        
        checkout_subtotal.setText(Constants.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutSubtotal));
        checkout_total.setText(Constants.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutTotal));
        
    }
    
    //*********** Request the Server to For Calculating Tax ********//
    
/*    private void RequestTaxMethods(Map<String,Object> getTax) {
        
       showProgressDialog(getString(R.string.processing));
        String gson = new Gson().toJson(getTax);
        
        
        Call<Object> call = RetrofitUtil.getWocommerceApi()
                .getTax(gson);
        
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                
                if (response.isSuccessful()) {
    
                   // checkoutTax = response.body();
                    String URL = response.raw().request().url().toString();
                    
                    
                    tax = response.body().toString();
                   // tax = response.body().
    
                    checkoutTax = Double.parseDouble(tax);
                    // Set Checkout Total
                    setCheckoutTotal();
                    
                    hideProgressDialog();
                    
                } else {
                    // Unexpected Response from Server
                    hideProgressDialog();
                    Snackbar.make(rootView, getString(R.string.cannot_get_payment_methods), Snackbar.LENGTH_LONG).show();
                    Toast.makeText( CheckoutActivity.this, getString(R.string.cannot_get_payment_methods), Toast.LENGTH_SHORT).show();
                }
            }
            
            
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText( CheckoutActivity.this, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }*/
    
    
    //*********** Request the Server to get all payment methods ********//
    
    private void RequestPaymentMethods() {
        
       showProgressDialog(getString(R.string.processing));
        
        Call<List<GetAllPaymentMethod>> call = RetrofitUtil.getWocommerceApi()
                .getAllPaymentMethods();
        
        
        call.enqueue(new Callback<List<GetAllPaymentMethod>>() {
            @Override
            public void onResponse(Call<List<GetAllPaymentMethod>> call, retrofit2.Response<List<GetAllPaymentMethod>> response) {
    
                if (response.isSuccessful()) {
                    
                      for (int i = 0; i < response.body().size(); i++) {
    
                          GetAllPaymentMethod paymentMethodsInfo = response.body().get(i);
    
                          if (paymentMethodsInfo.getEnabled()) {
                              paymentMethodsList.add(paymentMethodsInfo);
                          }
      
                     }
                    hideProgressDialog();
        
                } else {
                    // Unexpected Response from Server
                    hideProgressDialog();
                    Snackbar.make(rootView, getString(R.string.cannot_get_payment_methods), Snackbar.LENGTH_LONG).show();
                    Toast.makeText( CheckoutActivity.this, getString(R.string.cannot_get_payment_methods), Toast.LENGTH_SHORT).show();
                }
            }
            
            
            @Override
            public void onFailure(Call<List<GetAllPaymentMethod>> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText( CheckoutActivity.this, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    //*********** Place the Order on the Server ********//
    
    public void LoadCheckoutPage(String order_id) {


        String username = SharedPreferenceUtil.getStringValue(this, Constants.USERNAME);
        String password = SharedPreferenceUtil.getStringValue(this, Constants.PASSWORD);

        String usernameAndPassword = username + ":" + password;
        String encoded = ByteString.encodeString(usernameAndPassword, ISO_8859_1).base64();

        String encodeAgain = Base64.encodeToString(encoded.getBytes(), Base64.DEFAULT);

        String url = CHECKOUT_URL+"?order_id="+order_id + "&token=" + encodeAgain;
    
        main_checkout.setVisibility(View.GONE);
        checkout_webView.setVisibility(View.VISIBLE);
        
        Log.i("Checkout_url", "url= "+url);
        
        //hideProgressDialog();
        checkout_webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                Log.i("progress", "progress: progress="+progress);
                if(!orderConfirm)
                showProgressDialog(getString(R.string.processing));
            }
        });

        checkout_webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    checkout_webView.loadUrl(request.getUrl().toString());
                    showProgressDialog(getString(R.string.processing));
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                showProgressDialog(getString(R.string.processing));
                checkout_webView.loadUrl(url);
                Log.i("shouldOverride", "onPageStarted: url="+url);

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i("onPageStarted", "onPageStarted: url= "+url);
                showProgressDialog(getString(R.string.processing));

                if (url.contains(ORDER_RECEIVED)) {
                    view.stopLoading();
                    view.clearFormData();
                    orderConfirm = true;
                    hideProgressDialog();
                    hideProgressDialog();
                    hideProgressDialog();
                    SharedPreferenceUtil.storeStringValue(CheckoutActivity.this,"sub_id",aPackage.getId()+"");
                    AppUtils.showDialog(CheckoutActivity.this, getString(R.string.package_updated), new DialogInteractionListener() {
                        @Override
                        public void onPositiveClick(String msg) {
                            finish();
                        }
                    });
                }else   if (url.contains(ORDER_ERROR)) {
                    orderConfirm = true;
                    hideProgressDialog();
                    hideProgressDialog();
                    AppUtils.showDialog(CheckoutActivity.this, getString(R.string.package_error), new DialogInteractionListener() {
                        @Override
                        public void onPositiveClick(String msg) {
                            finish();
                        }
                    });
                }
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideProgressDialog();
                Log.i("onPageFinished", "onPageFinished: url= "+url);
            }
            
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                hideProgressDialog();
                Log.i("onReceivedError", "onReceivedError: error= "+error);
            }
        });
        
        checkout_webView.setVerticalScrollBarEnabled(false);
        checkout_webView.setHorizontalScrollBarEnabled(false);
        checkout_webView.setBackgroundColor(Color.TRANSPARENT);
        checkout_webView.getSettings().setJavaScriptEnabled(true);
        checkout_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        checkout_webView.loadUrl(url);
        
    }
    
    
    public void prepareDataForPlaceOrder(){

        if(selectedPaymentMethod != null && !selectedPaymentMethod.isEmpty()) {
            // Binding Layout Views
            checkout_webView = (WebView) findViewById(R.id.checkout_webView);


            ORDER_ID = "";

            orderDetails.setBilling(billingAddress);
            orderDetails.setCustomerId(DatabaseUtil.getInstance().getUserID() + "");
            orderDetails.setShipping(shippingAddress);

            OrderProducts product = new OrderProducts();

            product.setPrice(aPackage.getPrice());
            product.setTotal(aPackage.getPrice());
            product.setProductId(aPackage.getId());
            product.setQuantity(1);

            List<OrderProducts> list = new ArrayList<>();
            list.add(product);
            orderDetails.setOrderProducts(list);

            PostOrder order = new PostOrder();
            order.setToken(orderDetails.getToken());
            order.setPayment_method(selectedPaymentMethod);
            order.setPayment_method_title(selectedPaymentTitle);
            order.setBilling(orderDetails.getBilling());
            order.setShipping(orderDetails.getShipping());
            order.setOrderProducts(orderDetails.getOrderProducts());
            order.setOrderShippingMethods(shippingIDS);
            order.setCustomer_note(checkout_comments.getText().toString());
            order.setCustomerId(orderDetails.getCustomerId());
            order.setPlatform("Android");
            order.setSameAddress(true);


            PlaceOrder(new CreateOrder(order));
        }else{
            AppUtils.showDialog(this,"Please select Payment Method",null);
        }
    }
    
    //*********** Place the Order on the Server ********//
    
    public void PlaceOrder(CreateOrder jsonData) {
        
        showProgressDialog(getString(R.string.processing));
        
        Call<PlaceOrderResponse> call = RetrofitUtil.createProviderAPI()
                .placeOrder(jsonData);
        
        call.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(Call<PlaceOrderResponse> call, retrofit2.Response<PlaceOrderResponse> response) {
                
                if (response.isSuccessful()) {
                    
                    if (response.body() != null  /*&&  !TextUtils.isEmpty(response.body())*/) {
                        ORDER_ID = response.body().getDataId() +"";
                        LoadCheckoutPage(ORDER_ID);
                    }
                    else {
                        hideProgressDialog();
                        Snackbar.make(rootView, getString(R.string.err_something_wrong), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    hideProgressDialog();
                    Toast.makeText( CheckoutActivity.this, "Error : "+response.message(), Toast.LENGTH_SHORT).show();
                }
                
            }
            
            @Override
            public void onFailure(Call<PlaceOrderResponse> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(CheckoutActivity.this, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    //*********** Show SnackBar with given Message  ********//
    
    private void showSnackBarForCoupon(String msg) {
        final Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
    
    
    //*********** Check if the given String exists in the given List ********//
    
    private boolean isStringExistsInList(String str, List<String> stringList) {
        boolean isExists = false;
        
        for (int i=0;  i<stringList.size();  i++) {
            if (stringList.get(i).equalsIgnoreCase(str)) {
                isExists = true;
            }
        }
        
        
        return isExists;
    }
    
    //*********** Validate Payment Card Inputs ********//
    
    private boolean validatePaymentCard() {
        if (!ValidateInputs.isValidNumber(checkout_card_number.getText().toString().trim())) {
            checkout_card_number.setError(getString(R.string.invalid_credit_card));
            return false;
        } else if (!ValidateInputs.isValidNumber(checkout_card_cvv.getText().toString().trim())) {
            checkout_card_cvv.setError(getString(R.string.invalid_card_cvv));
            return false;
        } else if (TextUtils.isEmpty(checkout_card_expiry.getText().toString().trim())) {
            checkout_card_expiry.setError(getString(R.string.select_card_expiry));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onProfileLoaded(ProviderModel item) {
        if (item != null) {

            couponsList = new ArrayList<>();
            paymentMethodsList = new ArrayList<>();


            shippingMethod = new OrderShippingMethod();
            billingAddress = new UserBilling();
            shippingAddress = new UserShipping();

            shippingIDS = new ArrayList<>();
            allProducts = new ArrayList<>();
            getTaxModel = new GetTax();

            User user = DatabaseUtil.getInstance().getUser();


            String[] name = user.getData().getData().getDisplayName().split(" ");

            billingAddress.setFirstName(name[0]);
            if(name.length > 1){
                billingAddress.setLastName(name[1]);
            }

            billingAddress.setEmail(user.getData().getData().getUserEmail());
            billingAddress.setPhone(user.getData().getData().getMeta().getPhone().first());
            billingAddress.setCity(item.getCity());

            String country = item.getCountry();

            if(country != null){
                country = country.replace("-"," ");
                country = AppUtils.getCapsSentences(country);
            }
            billingAddress.setCountry(country);
            billingAddress.setCompany(item.getCompanyName());
            billingAddress.setState(country);
            billingAddress.setAddress1(AppUtils.replaceString(item.getAddress()));

            shippingAddress.setFirstName(name[0]);
            if(name.length > 1){
                shippingAddress.setLastName(name[1]);
            }

            shippingAddress.setCity(item.getCity());
            shippingAddress.setCountry(country);
            shippingAddress.setCompany(item.getCompanyName());
            shippingAddress.setState(country);
            shippingAddress.setAddress1(AppUtils.replaceString(item.getAddress()));
            Product product = new Product();

            product.setPrice(aPackage.getPrice());
            product.setTotal(aPackage.getPrice());
            product.setProductId(aPackage.getId());
            product.setQuantity(1);
            //  product.setVariation_id(aPackage.getVariations().get(0).);

            getTaxModel.setShippingInfo(shippingAddress);
            getTaxModel.setBillingInfo(billingAddress);


            shippingIDS.add(shippingMethod);

            getTaxModel.setShippingIds(shippingIDS);


            allProducts.add(product);

            getTaxModel.setProducts(allProducts);


            HashMap<String, Object> params = new HashMap<>();
            params.put("billing_info", getTaxModel.getBillingInfo());
            params.put("shipping_info",getTaxModel.getShippingInfo());
            params.put("products",getTaxModel.getProducts());
            params.put("shipping_ids",getTaxModel.getShippingInfo());

            //End


            // Request Payment Methods
            RequestPaymentMethods();

            /*Request Get Tax*/

            //RequestTaxMethods(params);

            setCheckoutTotal();

            List<PackageItem> items = new ArrayList<PackageItem>();
            items.add(aPackage);


            // Initialize the AddressListAdapter for RecyclerView
            PackagesRecyclerViewAdapter cartItemsAdapter = new PackagesRecyclerViewAdapter(items, null );

            // Set the Adapter and LayoutManager to the RecyclerView
            checkout_items_recycler.setAdapter(cartItemsAdapter);
            checkout_items_recycler.setLayoutManager(new LinearLayoutManager( CheckoutActivity.this, RecyclerView.VERTICAL, false));
            cartItemsAdapter.notifyDataSetChanged();


            try {
                shipping_method.setText(shippingMethod.getMethodTitle());
                checkoutShipping = checkoutShippingCost = Double.parseDouble(shippingMethod.getTotal());
            }
            catch (Exception e){
                e.getCause();
            }

            // Set Billing Details
            shipping_name.setText(shippingAddress.getFirstName()+" "+shippingAddress.getLastName());
            shipping_address.setText(shippingAddress.getState()+", "+shippingAddress.getCountry());
            shipping_street.setText(shippingAddress.getAddress1().replace("'",""));

            // Set Billing Details
            billing_name.setText(billingAddress.getFirstName()+" "+billingAddress.getLastName());
            billing_address.setText(billingAddress.getState()+", "+billingAddress.getCountry());
            billing_street.setText(billingAddress.getAddress1().replace("'",""));

            checkout_order_btn.setEnabled(true);
            checkout_order_btn.setBackgroundColor(ContextCompat.getColor( CheckoutActivity.this, R.color.colorPrimaryDark));

            checkout_order_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prepareDataForPlaceOrder();
                }
            });
            // Handle the Click event of edit_payment_method_Btn
            payment_method.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(CheckoutActivity.this, paymentMethodsList);

                    AlertDialog.Builder dialog = new AlertDialog.Builder( CheckoutActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_list, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(true);

                    Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                    TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

                    dialog_button.setVisibility(View.GONE);

                    dialog_title.setText(getString(R.string.payment_method));
                    dialog_list.setAdapter(paymentMethodAdapter);


                    final AlertDialog alertDialog = dialog.create();
                    alertDialog.show();



                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            GetAllPaymentMethod userSelectedPaymentMethod = paymentMethodAdapter.getItem(position);

                            payment_method.setText(userSelectedPaymentMethod.getTitle());
                            selectedPaymentMethod = userSelectedPaymentMethod.getId();
                            selectedPaymentTitle = userSelectedPaymentMethod.getTitle();




                            scroll_container.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll_container.fullScroll(scroll_container.FOCUS_DOWN);
                                }
                            });


                            alertDialog.dismiss();

                        }
                    });

                }
            });




            // Handle Touch event of input_dob EditText
            checkout_card_expiry.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Get Calendar instance
                        final Calendar calendar = Calendar.getInstance();

                        // Initialize DateSetListener of DatePickerDialog
                        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                // Set the selected Date Info to Calendar instance
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);

                                // Set Date Format
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy", Locale.US);

                                // Set Date in input_dob EditText
                                checkout_card_expiry.setText(dateFormat.format(calendar.getTime()));
                            }
                        };



                        // Initialize DatePickerDialog
                        DatePickerDialog datePicker = new DatePickerDialog
                                (
                                        CheckoutActivity.this,
                                        date,
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                );

                        // Show datePicker Dialog
                        datePicker.show();
                    }

                    return false;
                }
            });





            // Handle the Click event of checkout_cancel_btn Button
            checkout_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        }
        hideProgressDialog();

    }
}

