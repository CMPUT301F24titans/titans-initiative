// Generated by view binder compiler. Do not edit!
package com.example.titans_project.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public final class FragmentWaitlistBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonGenerateLottery;

  @NonNull
  public final ImageButton buttonSendNotification;

  @NonNull
  public final EditText editTextLotterySize;

  @NonNull
  public final Button returnButton;

  @NonNull
  public final TextView textViewLotterySize;

  @NonNull
  public final TextView title;

  @NonNull
  public final RecyclerView waitlistRecyclerView;

  private FragmentWaitlistBinding(@NonNull LinearLayout rootView,
      @NonNull Button buttonGenerateLottery, @NonNull ImageButton buttonSendNotification,
      @NonNull EditText editTextLotterySize, @NonNull Button returnButton,
      @NonNull TextView textViewLotterySize, @NonNull TextView title,
      @NonNull RecyclerView waitlistRecyclerView) {
    this.rootView = rootView;
    this.buttonGenerateLottery = buttonGenerateLottery;
    this.buttonSendNotification = buttonSendNotification;
    this.editTextLotterySize = editTextLotterySize;
    this.returnButton = returnButton;
    this.textViewLotterySize = textViewLotterySize;
    this.title = title;
    this.waitlistRecyclerView = waitlistRecyclerView;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentWaitlistBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentWaitlistBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_waitlist, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentWaitlistBinding bind(@NonNull View rootView) {
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

      id = R.id.editTextLotterySize;
      EditText editTextLotterySize = ViewBindings.findChildViewById(rootView, id);
      if (editTextLotterySize == null) {
        break missingId;
      }

      id = R.id.returnButton;
      Button returnButton = ViewBindings.findChildViewById(rootView, id);
      if (returnButton == null) {
        break missingId;
      }

      id = R.id.textViewLotterySize;
      TextView textViewLotterySize = ViewBindings.findChildViewById(rootView, id);
      if (textViewLotterySize == null) {
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

      return new FragmentWaitlistBinding((LinearLayout) rootView, buttonGenerateLottery,
          buttonSendNotification, editTextLotterySize, returnButton, textViewLotterySize, title,
          waitlistRecyclerView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}