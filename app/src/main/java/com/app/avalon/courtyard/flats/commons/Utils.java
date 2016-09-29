package com.app.avalon.courtyard.flats.commons;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Lalit.Poptani on 9/26/2016.
 */

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
