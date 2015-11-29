package billfold.com.synerzip.billfold.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by synerzip on 28/11/15.
 */
public class Utils {

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void showToast(final Context context) {
        Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();
    }

}
