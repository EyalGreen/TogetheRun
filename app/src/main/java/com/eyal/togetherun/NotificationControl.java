package com.eyal.togetherun;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.eyal.togetherun.Request.FriendRequest;
import com.eyal.togetherun.Request.Request;
import com.eyal.togetherun.Request.RunRequest;

import java.util.List;


public class NotificationControl implements DatabaseHandler.GetListOfRequestsLisener {

    public static final String OPEN_REQUEST_LIST = "openRequestList";
    private Context context;
    private MainActivity activity;
    public NotificationControl(Context context, MainActivity activity) {
        this.context = context;
        this.activity = activity;
        //add lisener on requests
        DatabaseHandler.getListOfAllRequests(this);
    }


    @Override
    public void onFinish(List<Request> requests) {
        if (requests != null) {
            if (requests.size() > 0){
                activity.changeBell(true);
            }else{
                activity.changeBell(false);
            }
            DatabaseHandler.user.setRequestsFromList(requests);
            for (Request request : requests) {
                if (request.getTypeOfRequest().equals(FriendRequest.FRIEND_REQUEST_TYPE)) { //Friend Request
                    sendNotificationRequest(request.getTypeOfRequest(), request.getNameOfSender(), request.getContentOfRequest(), R.drawable.alert_icon_friend_request);
                }else if (request.getTypeOfRequest().equals(RunRequest.RUN_REQUEST_TYPE)){ //Run Request
                    RunRequest runRequest = (RunRequest) request;
                    sendNotificationRequest(runRequest.getTypeOfRequest(), runRequest.getNameOfSender(), runRequest.getContentOfRequest(), R.drawable.shoes_icon);
                }
            }

        }
    }
    public void closeNotifications(){
        NotificationManager notificationControl = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationControl.cancelAll();
    }
    private void sendNotificationRequest(String typeOfRequest, String nameOfSender, String content, int icon) {
        int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("friendRequest", "alertNot", importance);;

        channel.setDescription("description");
        final android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(OPEN_REQUEST_LIST, true);
        // Next, let's trun this into PendingIntent
        // public static PendingIntent getActivity(Context context, int requestCode, Intent intent, int flags)
        int requestID = (int)System.currentTimeMillis(); //unqie ID
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; //cancel old intent and create new one
        PendingIntent pendingIntent = PendingIntent.getActivity(context,requestID,intent,flags);
        //now we can attach the pendingIntent to a new notificationusing setContentIntent
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "friendRequest")
                .setSmallIcon(icon)
                .setContentTitle(typeOfRequest + " From " + nameOfSender)
                .setContentText(content)
                .addAction(R.drawable.ok,"show me",pendingIntent)
                .setAutoCancel(true); //hides the notification after its been selected

        //create the notification
        notificationManager.notify(1, builder.build());
    }


}
