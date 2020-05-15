package ch.epfl.favo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class NonClickableToolbar extends Toolbar {

  public NonClickableToolbar(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    return false;
  }
}
