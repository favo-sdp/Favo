package ch.epfl.favo.view.tabs.addFavor;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ch.epfl.favo.R;
import ch.epfl.favo.user.User;
import ch.epfl.favo.util.CommonTools;

public class UserAdapter extends ArrayAdapter<User> {
  Context context;
  public UserAdapter(Context context, ArrayList<User> users) {
    super(context, 0, users);
    this.context = context;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // Get the data item for this position
    User user = getItem(position);
    assert user != null;
    // Check if an existing view is being reused, otherwise inflate the view
    if (convertView == null) {
      convertView =
          LayoutInflater.from(getContext()).inflate(R.layout.commit_user_list_item, parent, false);
    }
    // Lookup view for data population
    TextView UserNameView = convertView.findViewById(R.id.user_name_commit);
    ImageView UserProfilePic = convertView.findViewById(R.id.user_profile_picture_commit);

    // Populate the data into the template view using the data object
    if (user.getProfilePictureUrl() != null) {
      Glide.with(context)
              .load(user.getProfilePictureUrl())
              .into(UserProfilePic);
    }
    UserNameView.setText(user.getName() != null? user.getName() : CommonTools.emailToName(user.getEmail()));
    // UserProfilePic.setImageBitmap();
    // Return the completed view to render on screen
    return convertView;
  }
}
