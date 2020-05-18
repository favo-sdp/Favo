package ch.epfl.favo.chat.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ch.epfl.favo.R;
import ch.epfl.favo.chat.Model.Message;

public class TextMessageViewHolder extends MessageViewHolder {

  private final TextView mTextField;

  public TextMessageViewHolder(@NonNull View itemView) {
    super(itemView);
    mTextField = itemView.findViewById(R.id.message_text);
  }

  @Override
  public void bind(@NonNull Message textMessage) {
    super.bind(textMessage);
    setMessage(textMessage.getMessage());
  }

  private void setMessage(@Nullable String text) {
    mTextField.setText(text);
  }
}
