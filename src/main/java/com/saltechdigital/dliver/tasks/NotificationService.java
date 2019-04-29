package com.saltechdigital.dliver.tasks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.saltechdigital.dliver.NotificationActivity;
import com.saltechdigital.dliver.R;
import com.saltechdigital.dliver.models.Notifications;
import com.saltechdigital.dliver.storage.SessionManager;
import com.saltechdigital.dliver.utils.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.app.NotificationCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class NotificationService extends FirebaseMessagingService {

    private CompositeDisposable compositeDisposable;
    private DeliverApi deliverApi;

    private DisposableSingleObserver<Notifications> addNotification() {
        return new DisposableSingleObserver<Notifications>() {
            @Override
            public void onSuccess(Notifications value) {
                Log.d(Config.TAG, "onSuccess: addNotifications" + value.getTitle());
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(NotificationService.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                Log.d(Config.TAG, "onError: addNotifications", e);
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        compositeDisposable = new CompositeDisposable();
        deliverApi = DeliverApiService.createDeliverApi(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            if (compositeDisposable.isDisposed()) {
                compositeDisposable = new CompositeDisposable();
            }
            // 1 - Get message sent by Firebase
            String message = remoteMessage.getNotification().getBody();
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            sendVisualNotification(notification);
            //2 - Show message in console
            Date d = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String s = f.format(d);
            Log.e("TAG NOTIFICATION", message);
            Notifications notif = new Notifications();
            notif.setAgent("PRI");
            notif.setDate(s);
            notif.setClient(new SessionManager(this).getClientID());
            notif.setTitle(remoteMessage.getNotification().getTitle());
            notif.setShortDesc(remoteMessage.getNotification().getBody());
            notif.setDescription(remoteMessage.getNotification().getBody());
            notif.setIcon(remoteMessage.getNotification().getIcon());
            compositeDisposable.add(
                    deliverApi.addNotification(notif)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(addNotification())
            );
        }
    }

    private void sendVisualNotification(RemoteMessage.Notification notification) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("" + notification.getTitle());
        inboxStyle.addLine("" + notification.getBody());

        // 3 - Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(notification.getTitle())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.default_notification_channel_id);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        notificationManager.notify(Config.NOTIFICATION_TAG, Config.NOTIFICATION_ID, notificationBuilder.build());
    }
}
