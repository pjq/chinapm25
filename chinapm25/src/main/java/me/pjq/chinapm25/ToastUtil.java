
package me.pjq.chinapm25;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static Toast sToastInstance;

    private static Toast createToast(final Context context, String text, int duration) {
        if (null == sToastInstance) {
            sToastInstance = Toast.makeText(context, text, duration);
        }

        return sToastInstance;
    }

    public static void showToast(final Context context, String text, int duration) {
        if (sToastInstance != null) {
            sToastInstance.setText(text);
        } else {
            sToastInstance = createToast(context, text, duration);
        }

        sToastInstance.show();
    }

    public static void showToast(String text, int duration) {
        Context context = BaseApplication.getContext();

        if (sToastInstance != null) {
            sToastInstance.setText(text);
        } else {
            sToastInstance = createToast(context, text, duration);
        }

        sToastInstance.show();
    }

    public static void showToast(int nText, int duration) {
        Context context = BaseApplication.getContext();
        String text = context.getString(nText);

        if (sToastInstance != null) {
            sToastInstance.setText(text);
        } else {
            sToastInstance = createToast(context, text, Toast.LENGTH_SHORT);
        }

        sToastInstance.show();
    }

    public static void showToast(final Context context, String text) {
        if (sToastInstance != null) {
            sToastInstance.setText(text);
        } else {
            sToastInstance = createToast(context, text, Toast.LENGTH_SHORT);
        }

        sToastInstance.show();
    }

    public static void showToast(String s) {
        showToast(s, Toast.LENGTH_SHORT);
    }
}
