// Generated by view binder compiler. Do not edit!
package com.example.titans_project.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.titans_project.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityWaitlistBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonGenerateLottery;

  @NonNull
  public final ImageButton buttonSendNotification;

  @NonNull
  public final TextView notificationsCounter;

  @NonNull
  public final Button returnButton;

  @NonNull
  public final TextView title;

  @NonNull
  public final RecyclerView waitlistRecyclerView;

  private ActivityWaitlistBinding(@NonNull LinearLayout rootView,
      @NonNull Button buttonGenerateLottery, @NonNull ImageButton buttonSendNotification,
      @NonNull TextView notificationsCounter, @NonNull Button returnButton, @NonNull TextView title,
      @NonNull RecyclerView waitlistRecyclerView) {
    this.rootView = rootView;
    this.buttonGenerateLottery = buttonGenerateLottery;
    this.buttonSendNotification = buttonSendNotification;
    this.notificationsCounter = notificationsCounter;
    this.returnButton = returnButton;
    this.title = title;
    this.waitlistRecyclerView = waitlistRecyclerView;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityWaitlistBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityWaitlistBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_waitlist, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityWaitlistBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonGenerateLottery;
      Button buttonGenerateLottery = ViewBindings.findChildViewById(rootView, id);
      if (buttonGenerateLottery == null) {
        break missingId;
      }

      id = R.id.buttonSendNotification;
      ImageButton buttonSendNotification = ViewBindings.findChildViewById(rootView, id);
      if (buttonSendNotification == null) {
        break missingId;
      }

      id = R.id.notifications_counter;
      TextView notificationsCounter = ViewBindings.findChildViewById(rootView, id);
      if (notificationsCounter == null) {
        break missingId;
      }

      id = R.id.returnButton;
      Button returnButton = ViewBindings.findChildViewById(rootView, id);
      if (returnButton == null) {
        break missingId;
      }

      id = R.id.title;
      TextView title = ViewBindings.findChildViewById(rootView, id);
      if (title == null) {
        break missingId;
      }

      id = R.id.waitlistRecyclerView;
      RecyclerView waitlistRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (waitlistRecyclerView == null) {
        break missingId;
      }

      return new ActivityWaitlistBinding((LinearLayout) rootView, buttonGenerateLottery,
          buttonSendNotification, notificationsCounter, returnButton, title, waitlistRecyclerView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
