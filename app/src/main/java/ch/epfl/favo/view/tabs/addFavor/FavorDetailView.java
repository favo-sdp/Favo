package ch.epfl.favo.view.tabs.addFavor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;



import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.view.ViewController;


/**
 * A simple {@link Fragment} subclass. Use the {@link FavorRequestView#newInstance} factory method
 * to create an instance of this fragment.
 */
public class FavorDetailView extends Fragment implements View.OnClickListener {
    private final static String FAVOR_ARGS = "FAVOR_ARGS";
    private Favor favor;

    public static FavorDetailView newInstance(Favor favor){
        FavorDetailView fragment = new FavorDetailView();
        Bundle args = new Bundle();
        args.putParcelable(FAVOR_ARGS,favor);
        fragment.setArguments(args);
        return fragment;
    }

    public FavorDetailView() {
        // create favor detail from a favor
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupView();
        View rootView = inflater.inflate(R.layout.fragment_favor_accept_view, container, false);
        Button confirmFavorBtn = rootView.findViewById(R.id.accept_button);
        confirmFavorBtn.setOnClickListener(this);
        if (favor == null){
            favor = getArguments().getParcelable(FAVOR_ARGS);
        }
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

        TextView greeting = rootView.findViewById(R.id.favor_greeting_str2);
        greeting.setText(greetingStr);

        TextView location = rootView.findViewById(R.id.location2);
        location.setText(locationStr);

        TextView time = rootView.findViewById(R.id.datetime2);
        time.setText(timeStr);

        TextView title = rootView.findViewById(R.id.title2);
        title.setText(titleStr);

        TextView details = rootView.findViewById(R.id.details2);
        details.setText(descriptionStr);
    }

    @Override
    public void onClick(View view) {
        // The following inspection warning is suppressed. More cases will be added soon.
        // noinspection SwitchStatementWithTooFewBranches
        switch (view.getId()) {
            case R.id.accept_button:
                CommonTools.showSnackbar(requireView().findViewById(R.id.fragment_favor_accept_view),
                        getString(R.string.favor_respond_success_msg));
        }
    }

    private void setupView(){
        ((ViewController) getActivity()).setupViewBotDestTab();
    }

}

