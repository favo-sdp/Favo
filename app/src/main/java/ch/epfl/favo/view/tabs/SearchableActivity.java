package ch.epfl.favo.view.tabs;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import ch.epfl.favo.R;

public class SearchableActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_favor_accept_view);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // doMySearch(query);
        }
    }
}
