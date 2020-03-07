package ch.epfl.favo.presenter.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ch.epfl.favo.R;

/**
 * This view will contain account information of the user.
 * It will contain a user information where the user can
 * modify his/her age, main address, profile photo, etc.
 * It will also contain settings such as the radius of
 * favor request.
 * It might in the future also allow the user to add a list
 * of skills that they can provide.
 * This object is a simple {@link Fragment} subclass.
 */
public class Tab3 extends Fragment {

    public Tab3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab3_user_account, container, false);
    }
}
