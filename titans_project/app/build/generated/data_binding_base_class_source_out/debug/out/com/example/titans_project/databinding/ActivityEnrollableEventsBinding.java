// Generated by view binder compiler. Do not edit!
package com.example.titans_project.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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

public final class ActivityEnrollableEventsBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final Button buttonReturn;

  @NonNull
  public final ListView listviewEvents;

  @NonNull
  public final TextView title;

  private ActivityEnrollableEventsBinding(@NonNull RelativeLayout rootView,
      @NonNull Button buttonReturn, @NonNull ListView listviewEvents, @NonNull TextView title) {
    this.rootView = rootView;
    this.buttonReturn = buttonReturn;
    this.listviewEvents = listviewEvents;
    this.title = title;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityEnrollableEventsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityEnrollableEventsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_enrollable_events, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityEnrollableEventsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button_return;
      Button buttonReturn = ViewBindings.findChildViewById(rootView, id);
      if (buttonReturn == null) {
        break missingId;
      }

      id = R.id.listview_events;
      ListView listviewEvents = ViewBindings.findChildViewById(rootView, id);
      if (listviewEvents == null) {
        break missingId;
      }

      id = R.id.title;
      TextView title = ViewBindings.findChildViewById(rootView, id);
      if (title == null) {
        break missingId;
      }

      return new ActivityEnrollableEventsBinding((RelativeLayout) rootView, buttonReturn,
          listviewEvents, title);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
