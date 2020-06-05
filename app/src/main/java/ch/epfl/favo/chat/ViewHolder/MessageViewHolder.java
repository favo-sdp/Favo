package ch.epfl.favo.chat.ViewHolder;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.util.Date;

import ch.epfl.favo.R;
import ch.epfl.favo.chat.ChatPage;
import ch.epfl.favo.chat.Model.Message;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
  private TextView mNameField;
  private TextView mTimeField;
  private RelativeLayout mMessageContainer;
  private LinearLayout mMessage;
  private final int mGreen300;
  private final int mGray300;
  private String userId;

  MessageViewHolder(@NonNull View itemView) {
    super(itemView);
    mGreen300 = ContextCompat.getColor(itemView.getContext(), R.color.material_green_300);
    mGray300 = ContextCompat.getColor(itemView.getContext(), R.color.material_gray_300);
    mNameField = itemView.findViewById(R.id.name_text);
    mMessageContainer = itemView.findViewById(R.id.message_container);
    mMessage = itemView.findViewById(R.id.message_lin_layout);
    mTimeField = itemView.findViewById(R.id.date_text);
  }

  /**
   * bind Message view to Message model
   *
   * @param textMessage: message model given
   */
  public void bind(@NonNull Message textMessage) {
    FirebaseUser currentUser = DependencyFactory.getCurrentFirebaseUser();
    setIsSender(currentUser != null && textMessage.getUid().equals(currentUser.getUid()));
    userId = textMessage.getUid();
    SpannableString name = new SpannableString(textMessage.getName());;
    name.setSpan(new UnderlineSpan(), 0, name.length(), 0);
    mNameField.setText( name.subSequence(0, Math.min(name.length(), 10)));
    mNameField.setText(textMessage.getName());
    mNameField.setOnClickListener(this::navigateToUserPage);
    mTimeField.setText(
        DateFormat.getTimeInstance(DateFormat.SHORT)
            .format(textMessage.getTimestamp() != null ? textMessage.getTimestamp() : new Date()));
  }

  private void navigateToUserPage(View v) {
    Bundle userBundle = new Bundle();
    userBundle.putString(CommonTools.USER_ARGS, userId);
    Navigation.findNavController(v)
            .navigate(R.id.action_nav_chatView_to_UserInfoPage, userBundle);
  }

  private void setIsSender(boolean isSender) {
    final int color;
    if (isSender) {
      color = mGreen300;
      mMessageContainer.setGravity(Gravity.END);
    } else {
      color = mGray300;
      mMessageContainer.setGravity(Gravity.START);
    }
    ((GradientDrawable) mMessage.getBackground()).setColor(color);
  }
}
