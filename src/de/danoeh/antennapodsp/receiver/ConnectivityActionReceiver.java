package de.danoeh.antennapodsp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import de.danoeh.antennapodsp.AppConfig;
import de.danoeh.antennapodsp.storage.DBTasks;
import de.danoeh.antennapodsp.storage.DownloadRequester;
import de.danoeh.antennapodsp.util.NetworkUtils;

public class ConnectivityActionReceiver extends BroadcastReceiver {
    private static final String TAG = "ConnectivityActionReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (AppConfig.DEBUG)
                Log.d(TAG, "Received intent");

            if (NetworkUtils.autodownloadNetworkAvailable(context)) {
                if (AppConfig.DEBUG)
                    Log.d(TAG,
                            "auto-dl network available, starting auto-download");
                DBTasks.autodownloadUndownloadedItems(context);
            } else { // if new network is Wi-Fi, finish ongoing downloads,
                // otherwise cancel all downloads
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni == null || ni.getType() != ConnectivityManager.TYPE_WIFI) {
                    if (AppConfig.DEBUG)
                        Log.i(TAG,
                                "Device is no longer connected to Wi-Fi. Cancelling ongoing downloads");
                    DownloadRequester.getInstance().cancelAllDownloads(context);
                }

            }
        }
    }
}
