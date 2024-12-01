// Generated by view binder compiler. Do not edit!
package com.example.titans_project.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.titans_project.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ContentEnrollableEventsBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonDecline;

  @NonNull
  public final Button buttonEnroll;

  @NonNull
  public final TextView eventName;

  private ContentEnrollableEventsBinding(@NonNull LinearLayout rootView,
      @NonNull Button buttonDecline, @NonNull Button buttonEnroll, @NonNull TextView eventName) {
    this.rootView = rootView;
    this.buttonDecline = buttonDecline;
    this.buttonEnroll = buttonEnroll;
    this.eventName = eventName;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ContentEnrollableEventsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ContentEnrollableEventsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.content_enrollable_events, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ContentEnrollableEventsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button_decline;
      Button buttonDecline = ViewBindings.findChildViewById(rootView, id);
      if (buttonDecline == null) {
        break missingId;
      }

      id = R.id.button_enroll;
      Button buttonEnroll = ViewBindings.findChildViewById(rootView, id);
      if (buttonEnroll == null) {
        break missingId;
      }

      id = R.id.event_name;
      TextView eventName = ViewBindings.findChildViewById(rootView, id);
      if (eventName == null) {
        break missingId;
      }

      return new ContentEnrollableEventsBinding((LinearLayout) rootView, buttonDecline,
          buttonEnroll, eventName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
