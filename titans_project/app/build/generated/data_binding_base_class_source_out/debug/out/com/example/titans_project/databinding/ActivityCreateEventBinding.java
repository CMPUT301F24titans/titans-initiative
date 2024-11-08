// Generated by view binder compiler. Do not edit!
package com.example.titans_project.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.titans_project.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCreateEventBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final EditText eventDateEdit;

  @NonNull
  public final EditText eventDetailsEdit;

  @NonNull
  public final EditText eventTitleEdit;

  @NonNull
  public final EditText organizerEdit;

  @NonNull
  public final Button submitButton;

  private ActivityCreateEventBinding(@NonNull ScrollView rootView, @NonNull EditText eventDateEdit,
      @NonNull EditText eventDetailsEdit, @NonNull EditText eventTitleEdit,
      @NonNull EditText organizerEdit, @NonNull Button submitButton) {
    this.rootView = rootView;
    this.eventDateEdit = eventDateEdit;
    this.eventDetailsEdit = eventDetailsEdit;
    this.eventTitleEdit = eventTitleEdit;
    this.organizerEdit = organizerEdit;
    this.submitButton = submitButton;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCreateEventBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCreateEventBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_create_event, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCreateEventBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.eventDateEdit;
      EditText eventDateEdit = ViewBindings.findChildViewById(rootView, id);
      if (eventDateEdit == null) {
        break missingId;
      }

      id = R.id.eventDetailsEdit;
      EditText eventDetailsEdit = ViewBindings.findChildViewById(rootView, id);
      if (eventDetailsEdit == null) {
        break missingId;
      }

      id = R.id.eventTitleEdit;
      EditText eventTitleEdit = ViewBindings.findChildViewById(rootView, id);
      if (eventTitleEdit == null) {
        break missingId;
      }

      id = R.id.organizerEdit;
      EditText organizerEdit = ViewBindings.findChildViewById(rootView, id);
      if (organizerEdit == null) {
        break missingId;
      }

      id = R.id.submitButton;
      Button submitButton = ViewBindings.findChildViewById(rootView, id);
      if (submitButton == null) {
        break missingId;
      }

      return new ActivityCreateEventBinding((ScrollView) rootView, eventDateEdit, eventDetailsEdit,
          eventTitleEdit, organizerEdit, submitButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
