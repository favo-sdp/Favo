package ch.epfl.favo.view.tabs.favorList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;

public class FavorAdapter extends ArrayAdapter<Favor> {

  public FavorAdapter(Context context, ArrayList<Favor> favors) {
    super(context, 0, favors);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // Get the data item for this position
    Favor favor = getItem(position);
    assert favor != null;
    // Check if an existing view is being reused, otherwise inflate the view
    if (convertView == null) {
      convertView =
          LayoutInflater.from(getContext()).inflate(R.layout.favor_list_item, parent, false);
    }
    // Lookup view for data population
    TextView favorTitle = convertView.findViewById(R.id.item_title);
    TextView favorDesc = convertView.findViewById(R.id.item_desc);
    // Populate the data into the template view using the data object
    assert favor.getTitle() != null;
    assert favor.getDescription() != null;
    favorTitle.setText(favor.getTitle());
    favorDesc.setText(favor.getDescription());
    // Return the completed view to render on screen
    return convertView;
  }
}
