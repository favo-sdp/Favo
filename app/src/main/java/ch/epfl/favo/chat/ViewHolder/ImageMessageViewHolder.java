package ch.epfl.favo.chat.ViewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ch.epfl.favo.R;
import ch.epfl.favo.chat.Model.Message;

@SuppressLint("NewApi")
public class ImageMessageViewHolder extends MessageViewHolder {
  private ImageView mImageView;
  private Context mContext;

  public ImageMessageViewHolder(@NonNull View itemView) {
    super(itemView);
    mContext = itemView.getContext();
    mImageView = itemView.findViewById(R.id.chat_msg_image);
  }

  @Override
  public void bind(@NonNull Message message) {
    super.bind(message);
    String picturePath = message.getPicturePath();
    if (picturePath != null) {
      Glide.with(mContext)
          .load(picturePath)
          .fitCenter()
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(mImageView);
    }
  }
}
