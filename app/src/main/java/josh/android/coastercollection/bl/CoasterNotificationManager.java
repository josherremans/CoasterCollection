package josh.android.coastercollection.bl;

import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import josh.android.coastercollection.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Jos on 22/07/2017.
 */

public class CoasterNotificationManager {
    private static final int COASTER_COLLECTION_NOTIFICATION_ID = 123;

    public static void checkForNotifications(Context cx) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(cx)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Coaster Collection Notification")
                        .setColor(cx.getResources().getColor(R.color.colorAccent))
                        .setTicker("CoasterCollection!")
                        .setContentText("There are some collection issues!")
                        .setOnlyAlertOnce(true)
                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) cx.getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        // Remark: max 7 events (lines), if more: 8th line with "..." will be shown.
        String[] events = {"CoasterID gaps: ..", "Missing measurements: ..", "Missing images: ..",
                "HTML tags occurences: ..", "Missing quality indications: ..", "Unnecessary escaped strings: ..",
                "Xxxx", "Yyyy", "Zzzz", "Aaaa", "Bbbb", "Cccc"};

        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Collection issues:");

        // Moves events into the expanded layout
        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        // Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);

        // Builds the notification and issues it.
        mNotifyMgr.notify(COASTER_COLLECTION_NOTIFICATION_ID, mBuilder.build());
    }
}
