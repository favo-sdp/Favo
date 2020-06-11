package ch.epfl.favo.chat.ViewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import ch.epfl.favo.R;
import ch.epfl.favo.chat.Model.Message;

@SuppressLint("NewApi")
public class ImageMessageViewHolder extends MessageViewHolder {
  private ImageView mImageView;
  private Context mContext;
  private View rootView;

  public ImageMessageViewHolder(@NonNull View itemView) {
    super(itemView);
    rootView = itemView;
    mContext = itemView.getContext();
    mImageView = itemView.findViewById(R.id.chat_msg_image);
  }

  @Override
  public void bind(@NonNull Message message) {
    super.bind(message);
    String picturePath = message.getPicturePath();
    if (picturePath != null) {
      View loadingPanelView = rootView.findViewById(R.id.loading_panel_chat);
      loadingPanelView.setVisibility(View.VISIBLE);
      Glide.with(mContext)
          .load(picturePath)
          .fitCenter()
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .listener(
              new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(
                    GlideException e,
                    Object model,
                    Target<Drawable> target,
                    boolean isFirstResource) {
                  loadingPanelView.setVisibility(View.GONE);
                  return false;
                }

                @Override
                public boolean onResourceReady(
                    Drawable resource,
                    Object model,
                    Target<Drawable> target,
                    DataSource dataSource,
                    boolean isFirstResource) {
                  loadingPanelView.setVisibility(View.GONE);
                  return false;
                }
              })
          .into(mImageView);
    }
  }
}
