package com.tregix.serviceprovider.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.libraries.places.api.model.Place;
import com.tregix.serviceprovider.DataManager.CategoryDataManager;
import com.tregix.serviceprovider.R;
import com.tregix.serviceprovider.Utils.AppUtils;
import com.tregix.serviceprovider.Utils.Constants;
import com.tregix.serviceprovider.Utils.DatabaseUtil;
import com.tregix.serviceprovider.Utils.UtilFirebaseAnalytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.tregix.serviceprovider.Utils.Constants.EMPTY_STRING;
import static com.tregix.serviceprovider.fragments.HomeFragment.REQUEST_CODE_CATEGORY;
import static com.tregix.serviceprovider.fragments.HomeFragment.REQUEST_CODE_CITY;
import static com.tregix.serviceprovider.fragments.HomeFragment.REQUEST_CODE_COUNTRIES;
import static com.tregix.serviceprovider.fragments.HomeFragment.REQUEST_CODE_GENDER;
import static com.tregix.serviceprovider.fragments.HomeFragment.REQUEST_CODE_LANGUAGE;
import static com.tregix.serviceprovider.fragments.HomeFragment.REQUEST_CODE_SUB_CATEGORY;

public class AdvanceSearch extends BaseActivity {

    private EditText keyword;
    private TextView location;
    private EditText zipCode;
    private TextView category;
    private TextView subCategory;
    private TextView country;
    private TextView city;
    private Button search;
    private TextView radiusSearch;

    private LinearLayout countryView;
    private LinearLayout cityView;
    private LinearLayout categoryView;
    private LinearLayout subCategoryView;
    private String place;
    private String latitude;
    private String longitude;
    private ImageView currentLocation;

    private TextView language;
    private TextView gender;
    private TextView searchDate;
    private TextView searchTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_search);
        initViews();
        addListener();
        getBundleData();
        getSupportActionBar().setTitle(R.string.advance_search);
        new CategoryDataManager().loadDataFromServer(this,false);
    }

    private void initViews() {
        keyword = findViewById(R.id.search_keyword);
//        location = findViewById(R.id.search_location);
//        country = findViewById(R.id.search_country);
        city = findViewById(R.id.search_city);
        zipCode = findViewById(R.id.search_zipcode);
        category = findViewById(R.id.search_category);
//        subCategory = findViewById(R.id.search_sub_category);
        search = findViewById(R.id.search);
//        radiusSearch = findViewById(R.id.search_radius);

//        countryView = findViewById(R.id.adv_search_country);
        cityView = findViewById(R.id.adv_search_city);
        categoryView = findViewById(R.id.adv_search_category);
//        subCategoryView = findViewById(R.id.adv_search_sub_category);

//        currentLocation = findViewById(R.id.current_location);

        language = findViewById(R.id.search_language);
        gender = findViewById(R.id.search_gender);
        searchDate = findViewById(R.id.search_date);
        searchTime = findViewById(R.id.search_time);
    }

    private void getBundleData(){
        if(getIntent() != null) {
            Bundle bundle = getIntent().getBundleExtra(Constants.DATA);
            if(bundle != null) {
                category.setText(bundle.getString(Constants.CATEGORY));
                keyword.setText(bundle.getString(Constants.KEYWORD));
//                location.setText(bundle.getString(Constants.LOCATION));
//                country.setText(bundle.getString(Constants.COUNTRY));
                city.setText(bundle.getString(Constants.CITY));
                zipCode.setText(bundle.getString(Constants.ZIP_CODE));
//                radiusSearch.setText(bundle.getString(Constants.DISTANCE));
            }
        }
    }

    private void addListener() {
        search.setOnClickListener(this);
        category.setOnClickListener(this);
//        subCategory.setOnClickListener(this);
//        country.setOnClickListener(this);
        city.setOnClickListener(this);

//        countryView.setOnClickListener(this);
//        subCategoryView.setOnClickListener(this);
        categoryView.setOnClickListener(this);
        cityView.setOnClickListener(this);
//        currentLocation.setOnClickListener(this);
//        location.setOnClickListener(this);
//        radiusSearch.setOnClickListener(this);

        language.setOnClickListener(this);
        gender.setOnClickListener(this);
        searchDate.setOnClickListener(this);
        searchTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
//if(id>0) {
//    Intent intent = new Intent(this, ProfileActivity.class);
//    startActivity(intent);
//    return;
//}
        switch (id){
            case R.id.search:
                Bundle data = getData();
                openAcitivty(data,SearchResultActivity.class);
                UtilFirebaseAnalytics.logEvent(Constants.EVENT_ADVANCE_SEARCH,data);
                break;
            case R.id.search_category:
            case R.id.adv_search_category:
                selectCategory();
                break;
//            case R.id.search_sub_category:
//            case R.id.adv_search_sub_category:
//                selectSubCategory();
//                break;
//            case R.id.search_country:
//            case R.id.adv_search_country:
//                selectCountry();
//                break;
            case R.id.search_city:
            case R.id.adv_search_city:
                selectCity();
                break;
//            case R.id.current_location:
//                getUserLocation();
//                break;
//            case R.id.search_location:
//                pickLocation();
//                break;
//            case R.id.search_radius:
//                showRadiusDialog(this,radiusSearch.getText().toString());
//                break;
            case R.id.search_language:
                selectLanguage();
                break;
            case R.id.search_gender:
                selectGender();
                break;
            case R.id.search_date:
                showDatePickerDialog();
                break;
            case R.id.search_time:
                showTimePickerDialog();
                break;

        }

    }

    private void selectCountry() {
        String selectedCountry = EMPTY_STRING;
        if(city.getText() != null) {
            selectedCountry = subCategory.getText().toString();
        }
        openAcitivty(DatabaseUtil.getInstance().getCountries()
                ,SelectableItemActivity.class,REQUEST_CODE_COUNTRIES,getString(R.string.title_country), selectedCountry);
    }

    private void selectCategory() {
        String cat = EMPTY_STRING;
        if(category.getText() != null) {
            cat = category.getText().toString();
        }
        openAcitivty(DatabaseUtil.getInstance().getCategories(),
                SelectableItemActivity.class,REQUEST_CODE_CATEGORY,getString(R.string.title_category), cat);
    }

    private void selectLanguage() {
        String lang = EMPTY_STRING;
        if(language.getText() != null) {
            lang = language.getText().toString();
        }
        openAcitivty(DatabaseUtil.getInstance().getLanguages(),
                SelectableItemActivity.class,REQUEST_CODE_LANGUAGE,"Language", lang);
    }

    private void selectGender() {
        String gen = EMPTY_STRING;
        if(gender.getText() != null) {
            gen = gender.getText().toString();
        }
        openAcitivty(DatabaseUtil.getInstance().getGenders(),
                SelectableItemActivity.class,REQUEST_CODE_GENDER,"Gender", gen);
    }
    @Override
    public void onPositiveClick(String msg) {
        super.onPositiveClick(msg);
        radiusSearch.setText(msg);
    }

    protected void setLocation(String name, Place plac){
        location.setText(name);
        place = name;
        latitude = plac.getLatLng().latitude +"";
        longitude = plac.getLatLng().longitude +"";
    }

    protected void setLocation(String name, String lat, String lng) {
        location.setText(name);
        place = name;
        latitude = lat;
        longitude = lng;
    }

    private void selectCity() {
        if(!country.getText().toString().isEmpty()) {
            String selectedCity = EMPTY_STRING;
            if(city.getText() != null) {
                selectedCity = city.getText().toString();
            }
            openAcitivty(DatabaseUtil.getInstance().getCities(country.getText().toString())
                    , SelectableItemActivity.class, REQUEST_CODE_CITY,getString(R.string.title_city), selectedCity);
        }else{
            AppUtils.showDialog(this,getString(R.string.msg_select_country),null);
        }
    }

    private void selectSubCategory() {
        if(!category.getText().toString().isEmpty()) {
            String selectedSubCat = EMPTY_STRING;
            if(subCategory.getText() != null) {
                selectedSubCat = subCategory.getText().toString();
            }
            openAcitivty(DatabaseUtil.getInstance().getSubCategories(category.getText().toString())
                    , SelectableItemActivity.class, REQUEST_CODE_SUB_CATEGORY,getString(R.string.title_sub_category), selectedSubCat);
        }else{
            AppUtils.showDialog(this,getString(R.string.msg_select_category),null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_CATEGORY) {

                List categoryList = data.getStringArrayListExtra(Constants.DATA);
                if(categoryList != null && !categoryList.isEmpty()) {
                    String name = categoryList.get(0).toString();
                    if (category.getText() != null && !category.getText().toString().equals(name)) {
//                            subCategory.setText(Constants.EMPTY_STRING);
                    }
                    if (!categoryList.isEmpty()) {
                        category.setText(name);
                    }
                }else{
                    category.setText(EMPTY_STRING);
//                        subCategory.setText(EMPTY_STRING);
                }
            }

            if (requestCode == REQUEST_CODE_SUB_CATEGORY) {
//                    List categoryList = data.getStringArrayListExtra(Constants.DATA);
//                    if (categoryList != null && !categoryList.isEmpty()) {
//                        subCategory.setText(categoryList.get(0).toString());
//                    }else{
//                        subCategory.setText(EMPTY_STRING);
//                    }
            }

//                if (requestCode == REQUEST_CODE_COUNTRIES) {
//                    List categoryList = data.getStringArrayListExtra(Constants.DATA);
//                    if(categoryList != null && !categoryList.isEmpty()) {
//                        String name = categoryList.get(0).toString();
//                        if (country.getText() != null && !country.getText().toString().equals(name)) {
//                            city.setText(Constants.EMPTY_STRING);
//                        }
//                        if (!categoryList.isEmpty()) {
//                            country.setText(name);
//                        }
//                    }else{
//                        country.setText(EMPTY_STRING);
//                        city.setText(EMPTY_STRING);
//                    }
//                }

            if (requestCode == REQUEST_CODE_CITY) {
                List categoryList = data.getStringArrayListExtra(Constants.DATA);
                if (categoryList != null && !categoryList.isEmpty()) {
                    city.setText(categoryList.get(0).toString());
                }else{
                    city.setText(EMPTY_STRING);
                }
            }

            if (requestCode == REQUEST_CODE_LANGUAGE) {
                List lang = data.getStringArrayListExtra(Constants.DATA);
                if (lang != null && !lang.isEmpty()) {
                    language.setText(lang.get(0).toString());
                }else{
                    language.setText(EMPTY_STRING);
                }
            }

            if (requestCode == REQUEST_CODE_GENDER) {
                List gen = data.getStringArrayListExtra(Constants.DATA);
                if (gen != null && !gen.isEmpty()) {
                    gender.setText(gen.get(0).toString());
                }else{
                    gender.setText(EMPTY_STRING);
                }
            }
        }
    }


    private Bundle getData(){
        Bundle bundle = new Bundle();

        if(keyword.getText() != null)
        bundle.putString(Constants.KEYWORD,keyword.getText().toString());

//        if(location.getText() != null && !location.getText().toString().isEmpty()) {
//            bundle.putString(Constants.LOCATION, place);
//            bundle.putString(Constants.LATITUDE, latitude);
//            bundle.putString(Constants.LONGITUDE, longitude);
//        }

//        if(country.getText() != null)
//            bundle.putString(Constants.COUNTRY,country.getText().toString());

        if(city.getText() != null)
            bundle.putString(Constants.CITY,city.getText().toString());

        if(zipCode.getText() != null)
            bundle.putString(Constants.ZIP_CODE,zipCode.getText().toString());

        if(category.getText() != null) {
            bundle.putString(Constants.CATEGORY, category.getText().toString());
            bundle.putInt(Constants.CATEGORY_ID,DatabaseUtil.getInstance().getCategoryID(category.getText().toString()));
        }

//        if(subCategory.getText() != null)
//            bundle.putString(Constants.SUB_CATEGORY,subCategory.getText().toString());

//        if(radiusSearch.getText() != null)
//            bundle.putString(Constants.DISTANCE,radiusSearch.getText().toString());

        return bundle;
    }

    private void showDatePickerDialog() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        if(searchDate!=null && !searchDate.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date d = simpleDateFormat.parse(searchDate.getText().toString());
                c.setTime(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        DatePickerDialog dateDlg = new DatePickerDialog(this,
                R.style.DatePickerDialogTheme,
                (datePicker, year, monthOfYear, dayOfMonth)
                        -> searchDate.setText(String.format("%02d-%02d-%d", dayOfMonth, monthOfYear + 1, year))
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dateDlg.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        dateDlg.show();
    }

    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        if(searchTime!=null && !searchTime.getText().toString().isEmpty()) {
            try {
                String[] selectedTime = searchTime.getText().toString().split("-");
                c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(selectedTime[0]));
                c.set(Calendar.MINUTE, Integer.valueOf(selectedTime[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        TimePickerDialog timeDlg = new TimePickerDialog(this,
                R.style.TimePickerDialogTheme,
                (timePicker, hourOfDay, minute) -> {
                    searchTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        timeDlg.setTitle("Select Time");
        timeDlg.show();
    }
}
