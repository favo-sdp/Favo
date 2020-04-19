package ch.epfl.favo.view.tabs.favorList;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;

public class SearchBarCoordinator {
    private MainActivity activity;
    private Context context;
    private TextView tipTextView;
    private ListView listView;
    private SearchView searchView;
    private Map<String, Favor> favorsFound = new HashMap<>();
    private int screenWidth;
    private String mode;
    private boolean first;

    public SearchBarCoordinator(MainActivity activity, Context context, String mode){
        this.context = context;
        this.activity = activity;
        this.mode = mode;
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    public void setupAssets(TextView textView, ListView listView, SearchView searchView){
        this.tipTextView = textView;
        this.listView = listView;
        this.searchView = searchView;
        first = true;
    }

    public void setupSearchBar(View view){
        searchView.setIconifiedByDefault(true);
        // if returned from FavorDetail view, continue to show the search mode
        if(!favorsFound.isEmpty()){
            searchView.setIconified(false);
            searchView.clearFocus();
            enterSearchMode(view, context);
            displayFavorList(favorsFound, "");
        }
        searchView.setOnSearchClickListener((v)->{
            enterSearchMode(view, context);
        });
        searchView.setMaxWidth((int)(screenWidth*0.85));
        searchView.setOnQueryTextListener(new onQuery());
    }

    private void enterSearchMode(View view, Context context){
        displayFavorList(favorsFound, "");
        if(view.findViewById(R.id.spinner) != null)
            view.findViewById(R.id.spinner).setVisibility(View.INVISIBLE);
        activity.onBackPressedListener =
                () -> { searchView.setIconified(true);
                    CommonTools.hideKeyboardFrom(context, view);
                };
    }

    public void clearFoundFavors(){
        favorsFound.clear();
    }

    public void displayContent(String defaultContent, int position){
        if(!favorsFound.isEmpty())
            displayFavorList(favorsFound, defaultContent);
        else if(mode.equals("NearbyList"))
            displayFavorList(activity.otherActiveFavorsAround, defaultContent);
        else if(position == 0)
            displayFavorList(activity.activeFavors, defaultContent);
        else displayFavorList(activity.archivedFavors, defaultContent);
    }

    private void queryAndDisplay(String query){
        if(query.equals("")) favorsFound = new HashMap<>();
        else if (mode.equals("NearbyList")) favorsFound = CommonTools.doQuery(query, activity.otherActiveFavorsAround);
        else {
            favorsFound = CommonTools.doQuery(query, activity.activeFavors);
            favorsFound.putAll(CommonTools.doQuery(query, activity.archivedFavors));
        }
        displayFavorList(favorsFound, "No match found");
    }

    public class onQuery implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            queryAndDisplay(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(first) first = false;
            else queryAndDisplay(newText);
            return false;
        }
    }

    private void showText(String text) {
        tipTextView.setText(text);
        tipTextView.setVisibility(View.VISIBLE);
    }

    private void displayFavorList(Map<String, Favor> favors, String text) {
        if (favors.isEmpty()) showText(text);
        else tipTextView.setVisibility(View.INVISIBLE);
        listView.setAdapter(new FavorAdapter(context, new ArrayList<>(favors.values())));
    }
}
