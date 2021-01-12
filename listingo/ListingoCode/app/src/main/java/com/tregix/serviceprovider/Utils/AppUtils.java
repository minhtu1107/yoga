package com.tregix.serviceprovider.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tregix.serviceprovider.Interface.DialogInteractionListener;
import com.tregix.serviceprovider.Model.Login.User;
import com.tregix.serviceprovider.Model.TokenRequestParam;
import com.tregix.serviceprovider.R;
import com.tregix.serviceprovider.Retrofit.RetrofitUtil;
import com.tregix.serviceprovider.ServiceProviderApplication;
import com.tregix.serviceprovider.activities.NavigationDrawerActivity;
import com.tregix.serviceprovider.activities.WebviewActivity;
import com.tregix.serviceprovider.chat.UserObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tregix.serviceprovider.Retrofit.RetrofitUtil.sendRequest;
import static com.tregix.serviceprovider.Utils.Constants.EMPTY_STRING;
import static com.tregix.serviceprovider.chat.ChatActivity.META_DATA;
import static com.tregix.serviceprovider.chat.ChatActivity.USERS_CHILD;

/**
 * Created by Tregix on 12/28/2017.
 */

public class AppUtils {

    public static void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public static String getDay(String day) {
        Date date = getDate(day);
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String goal = outFormat.format(date);

        return goal;
    }

    @Nullable
    private static Date getDate(String day) {
        SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = inFormat.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getParsedData(String day) {
        Date date = getDate(day);
        SimpleDateFormat outFormat = new SimpleDateFormat("MMM d, yyyy");
        String goal = outFormat.format(date);

        return goal;
    }


    public static String longToDate(String val, String pattern, int multiplier) {
        try {
            if (val != null && !val.isEmpty()) {
                Date date = new Date(Long.parseLong(val) * multiplier);
                SimpleDateFormat df2 = new SimpleDateFormat(pattern);
                return df2.format(date);
            } else {
                return EMPTY_STRING;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return EMPTY_STRING;

    }

    public static void openWebview(Context context, String url, String title) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra(Constants.URL, url);
        intent.putExtra(Constants.TITLE, title);
        context.startActivity(intent);
    }

    public static void updateUserToken(int uid, String token) {
        if (token != null && !token.isEmpty()) {
            RetrofitUtil.createProviderAPI().saveUserToken(new TokenRequestParam(token, uid))
                    .enqueue(sendRequest(null));
        }
    }

    public static void sendUserTokenRequest(String token) {
        User user = DatabaseUtil.getInstance().getUser();

        if (user != null) {
            updateUserToken(user.getData().getID(), token);
            updateTokenOnFirebase(token, user);
        }

    }

    public static void updateTokenOnFirebase(String token, User user) {
        UserObject sender = new UserObject();
        sender.setName(user.getData().getData().getDisplayName());
        sender.setImageUrl(user.getData().getData().getAvatar());
        sender.setOnline(true);
        sender.setDeviceID(token);
        sender.setUserId(user.getData().getData().getID());
        updateMeta(user, sender);
    }


    public static void updateMeta(User user, UserObject sender) {
        FirebaseDatabase.getInstance().getReference()
                .child(USERS_CHILD).child(user.getData().getID() + "").child(META_DATA)
                .setValue(sender);
    }

    public static void updateOnline(boolean val) {
        try {
            Map<String, Object> hopperUpdates = new HashMap<>();
            hopperUpdates.put("online", val);

            if (DatabaseUtil.getInstance().getUser() != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(USERS_CHILD).child(DatabaseUtil.getInstance().getUser().getData().getID() + "")
                        .child(META_DATA);
                ref.updateChildren(hopperUpdates);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showDialog(Context context, String msg, final DialogInteractionListener listener) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onPositiveClick(null);
                        }
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    public static String getProviderId(String path, String id) {
        String providerID = path.replace(id, "");
        providerID = providerID.replace("_", "");
        return providerID;
    }

    public static String getSmsTodayYestFromMilli(long msgTimeMillis) {

        Calendar messageTime = Calendar.getInstance();
        messageTime.setTimeInMillis(msgTimeMillis);
        // get Currunt time
        Calendar now = Calendar.getInstance();

        final String strTimeFormate = "h:mm aa";
        final String strDateFormate = "dd/MM/yyyy h:mm aa";

        if (now.get(Calendar.DATE) == messageTime.get(Calendar.DATE)
                &&
                ((now.get(Calendar.MONTH) == messageTime.get(Calendar.MONTH)))
                &&
                ((now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR)))
                ) {

            return "today at " + DateFormat.format(strTimeFormate, messageTime);

        } else if (
                ((now.get(Calendar.DATE) - messageTime.get(Calendar.DATE)) == 1)
                        &&
                        ((now.get(Calendar.MONTH) == messageTime.get(Calendar.MONTH)))
                        &&
                        ((now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR)))
                ) {
            return "yesterday at " + DateFormat.format(strTimeFormate, messageTime);
        } else {
            return DateFormat.format(strDateFormate, messageTime).toString();
        }
    }

    public static boolean isconnected() {
        ConnectivityManager connMan = (ConnectivityManager) ServiceProviderApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMan != null) {
            NetworkInfo active = connMan.getActiveNetworkInfo();
            return active != null && active.isConnectedOrConnecting();
        } else {
            return false;
        }
    }

    public static String getCountryCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String CountryID = "";
        String CountryZipCode = "";

        if(telephonyManager != null && telephonyManager.getSimCountryIso() != null) {
            CountryID = telephonyManager.getSimCountryIso().toUpperCase();
            String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
            for (int i = 0; i < rl.length; i++) {
                String[] g = rl[i].split(",");
                if (g[1].trim().equals(CountryID.trim())) {
                    CountryZipCode = "+" + g[0];
                    break;
                }
            }
        }

        return CountryZipCode;
    }

    public static final int NOTIFICATION_REQUEST_CODE = 100;



    public static void showNewNotification(Context context, Intent intent, String title, String msg) {

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent;

        if (intent != null) {
            notificationIntent = intent;
        }
        else {
            notificationIntent = new Intent(context.getApplicationContext(), NavigationDrawerActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity((context), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);


        Notification.Builder builder = new Notification.Builder(context);



        // Create Notification
        Notification notification = builder
                .setContentTitle(title)
                .setContentText(msg)
                .setTicker(context.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(notificationSound)
                .setLights(Color.RED, 3000, 3000)
                .setVibrate(new long[] { 1000, 1000 })
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();


        notificationManager.notify(NOTIFICATION_REQUEST_CODE, notification);

    }

    public static String replaceString(String string) {
        return string.replaceAll("[;/:*?\"<>|&']","");
    }

    public static String getCapsSentences(String tagName) {
        String[] splits = tagName.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            String eachWord = splits[i];
            if (i > 0 && eachWord.length() > 0) {
                sb.append(" ");
            }
            String cap = eachWord;
            if(eachWord.length() > 1) {
                cap = eachWord.substring(0, 1).toUpperCase()
                        + eachWord.substring(1);
            }else{
                cap = eachWord.toUpperCase();
            }
            sb.append(cap);
        }
        return sb.toString();
    }
}
