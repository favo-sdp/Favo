package ch.epfl.favo.chat;

import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import ch.epfl.favo.R;
import ch.epfl.favo.util.DependencyFactory;

class ChatViewHolder extends RecyclerView.ViewHolder {
  private final TextView mNameField;
  private final TextView mTextField;
  private final RelativeLayout mMessageContainer;
  private final LinearLayout mMessage;
  private final int mGreen300;
  private final int mGray300;

  ChatViewHolder(@NonNull View itemView) {
    super(itemView);
    mNameField = itemView.findViewById(R.id.name_text);
    mTextField = itemView.findViewById(R.id.message_text);
    mMessageContainer = itemView.findViewById(R.id.message_container);
    mMessage = itemView.findViewById(R.id.message);
    mGreen300 = ContextCompat.getColor(itemView.getContext(), R.color.material_green_300);
    mGray300 = ContextCompat.getColor(itemView.getContext(), R.color.material_gray_300);
  }

  void bind(@NonNull ChatModel chatModel) {
    setName(chatModel.getName());
    setMessage(chatModel.getMessage());

    FirebaseUser currentUser = DependencyFactory.getCurrentFirebaseUser();
    setIsSender(currentUser != null && chatModel.getUid().equals(currentUser.getUid()));
  }

  private void setName(@Nullable String name) {
    mNameField.setText(name);
  }

  private void setMessage(@Nullable String text) {
    mTextField.setText(text);
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
