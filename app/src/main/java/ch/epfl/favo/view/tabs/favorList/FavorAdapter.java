package ch.epfl.favo.view.tabs.favorList;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.user.UserUtil;
import ch.epfl.favo.util.CommonTools;

import static ch.epfl.favo.util.CommonTools.getUserName;

public class FavorAdapter extends ArrayAdapter<Favor> {

  public FavorAdapter(Context context, ArrayList<Favor> favors) {
    super(context, 0, favors);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
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
    TextView favorRequester = convertView.findViewById(R.id.item_requester);
    TextView favorCoins = convertView.findViewById(R.id.item_coins);

    // Populate the data into the template view using the data object
    assert favor.getTitle() != null;
    assert favor.getRequesterId() != null;

    favorTitle.setText(favor.getTitle());
    favorCoins.setText(favor.getReward() + " coins");
    UserUtil.getSingleInstance().findUser(favor.getRequesterId())
      .thenAccept(user -> favorRequester.setText(getUserName(user)));
    return convertView;
  }
}
