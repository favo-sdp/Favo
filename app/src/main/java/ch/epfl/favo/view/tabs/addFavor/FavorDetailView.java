package ch.epfl.favo.view.tabs.addFavor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.OffsetTime;
import java.util.Date;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.view.tabs.BottomDestinationTab;

/**
 * A simple {@link Fragment} subclass. Use the {@link FavorRequestView#newInstance} factory method
 * to create an instance of this fragment.
 */
public class FavorDetailView extends BottomDestinationTab implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Favor favor;

    public FavorDetailView() {
        // Required empty public constructor
    }

    public FavorDetailView(Favor favor) {
        this.favor = favor;
        // create favor detail from a favor
    }

    public FavorDetailView(String param1, String param2) {
        mParam1 = param1;
        mParam2 = param2;
        // create favor detail from some parameters
    }
    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * <p>// * @param param1 Parameter 1. // * @param param2 Parameter 2.
     *
     * @return A new instance of fragment FavorRequestView.
     */
    // TODO: Rename and change types and number of parameters
    public static FavorDetailView newInstance(String param1, String param2) {
        return new FavorDetailView(param1, param2);
    }
    //        FavorRequestView fragment = new FavorRequestView();
    //        Bundle args = new Bundle();
    //        args.putString(ARG_PARAM1, param1);
    //        args.putString(ARG_PARAM2, param2);
    //        fragment.setArguments(args);
    //        return fragment;
    //    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupView();
        View rootView = inflater.inflate(R.layout.fragment_favor, container, false);

        Button confirmFavorBtn = rootView.findViewById(R.id.add_button);
        confirmFavorBtn.setOnClickListener(this);

        if(favor != null){
            confirmFavorBtn.setText("Respond");
            displayFromFavor(rootView ,favor);
        }


        return rootView;
    }

    public void displayFromFavor(View rootView, Favor favor){
        String greetingStr = "favor of " + favor.getRequesterId();
        String locationStr = "latitude: " + String.format("%.4f", favor.getLocation().getLatitude())
                + " longitude: " + String.format("%.4f", favor.getLocation().getLongitude());
        String timeStr = CommonTools.convertTime(favor.getLocation().getTime());
        String titleStr = favor.getTitle();
        String descriptionStr = favor.getDescription();

        TextView greeting = rootView.findViewById(R.id.favor_greeting_str);
        greeting.setText(greetingStr);

        TextView location = rootView.findViewById(R.id.location);
        location.setText(locationStr);

        TextView time = rootView.findViewById(R.id.datetime);
        time.setText(timeStr);

        TextView title = rootView.findViewById(R.id.title);
        title.setText(titleStr);

        TextView details = rootView.findViewById(R.id.details);
        details.setText(descriptionStr);
    }

    @Override
    public void onClick(View view) {
        // The following inspection warning is suppressed. More cases will be added soon.
        // noinspection SwitchStatementWithTooFewBranches
        switch (view.getId()) {
            case R.id.add_button:
                CommonTools.showSnackbar(requireView().findViewById(R.id.fragment_favor),
                        getString(R.string.favor_respond_success_msg));
        }
    }

}

