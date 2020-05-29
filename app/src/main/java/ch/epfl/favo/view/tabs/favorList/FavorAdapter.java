package ch.epfl.favo.view.tabs.favorList;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.user.UserUtil;

import static ch.epfl.favo.util.CommonTools.getUserName;

public class FavorAdapter extends ArrayAdapter<Favor> {

  public FavorAdapter(Context context, ArrayList<Favor> favors) {
    super(context, 0, favors);
  }

  @NonNull
  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {

    Favor favor = getItem(position);
    assert favor != null;

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext())
              .inflate(R.layout.favor_list_item, parent, false);
    }

    TextView favorTitle = convertView.findViewById(R.id.item_title);
    favorTitle.setText(favor.getTitle());

    TextView favorRequester = convertView.findViewById(R.id.item_requester);
    UserUtil.getSingleInstance()
            .findUser(favor.getRequesterId())
            .thenAccept(user -> favorRequester.setText(getUserName(user)));

    TextView favorCoins = convertView.findViewById(R.id.item_coins);
    favorCoins.setText(getContext().getResources().getString(R.string.num_coins, (int) favor.getReward()));

    ImageView favorIcon = convertView.findViewById(R.id.item_icon);
    // Todo: load requester profile picture (@Daniel)

    return convertView;
  }
}
