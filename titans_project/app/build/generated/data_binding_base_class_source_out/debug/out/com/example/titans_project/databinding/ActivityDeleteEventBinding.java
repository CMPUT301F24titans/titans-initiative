// Generated by view binder compiler. Do not edit!
package com.example.titans_project.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.titans_project.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityDeleteEventBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView deleteEventTitle;

  @NonNull
  public final Button noButton;

  @NonNull
  public final Button yesButton;

  private ActivityDeleteEventBinding(@NonNull RelativeLayout rootView,
      @NonNull TextView deleteEventTitle, @NonNull Button noButton, @NonNull Button yesButton) {
    this.rootView = rootView;
    this.deleteEventTitle = deleteEventTitle;
    this.noButton = noButton;
    this.yesButton = yesButton;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityDeleteEventBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityDeleteEventBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_delete_event, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityDeleteEventBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.deleteEventTitle;
      TextView deleteEventTitle = ViewBindings.findChildViewById(rootView, id);
      if (deleteEventTitle == null) {
        break missingId;
      }

      id = R.id.noButton;
      Button noButton = ViewBindings.findChildViewById(rootView, id);
      if (noButton == null) {
        break missingId;
      }

      id = R.id.yesButton;
      Button yesButton = ViewBindings.findChildViewById(rootView, id);
      if (yesButton == null) {
        break missingId;
      }

      return new ActivityDeleteEventBinding((RelativeLayout) rootView, deleteEventTitle, noButton,
          yesButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
