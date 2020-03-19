package ch.epfl.favo.util;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.favo.R;

public class CommonTools {
    public static void showSnackbar(View view, String errorMessageRes) {
        Snackbar.make(
                view, errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }

    public static void replaceFragment(int id, FragmentManager fragmentManager, Fragment newFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(id, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        //transaction.remove(this);
    }

    public static String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss"); return format.format(date);
    }
}
