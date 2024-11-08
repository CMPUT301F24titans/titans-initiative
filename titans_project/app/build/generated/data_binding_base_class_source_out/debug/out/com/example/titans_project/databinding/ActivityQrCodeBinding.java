// Generated by view binder compiler. Do not edit!
package com.example.titans_project.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.titans_project.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityQrCodeBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ImageButton backButton;

  @NonNull
  public final ImageView qrCodeImageView;

  @NonNull
  public final Button regenerateButton;

  @NonNull
  public final Button saveToLocalButton;

  private ActivityQrCodeBinding(@NonNull RelativeLayout rootView, @NonNull ImageButton backButton,
      @NonNull ImageView qrCodeImageView, @NonNull Button regenerateButton,
      @NonNull Button saveToLocalButton) {
    this.rootView = rootView;
    this.backButton = backButton;
    this.qrCodeImageView = qrCodeImageView;
    this.regenerateButton = regenerateButton;
    this.saveToLocalButton = saveToLocalButton;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityQrCodeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityQrCodeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_qr_code, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityQrCodeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.backButton;
      ImageButton backButton = ViewBindings.findChildViewById(rootView, id);
      if (backButton == null) {
        break missingId;
      }

      id = R.id.qrCodeImageView;
      ImageView qrCodeImageView = ViewBindings.findChildViewById(rootView, id);
      if (qrCodeImageView == null) {
        break missingId;
      }

      id = R.id.regenerateButton;
      Button regenerateButton = ViewBindings.findChildViewById(rootView, id);
      if (regenerateButton == null) {
        break missingId;
      }

      id = R.id.saveToLocalButton;
      Button saveToLocalButton = ViewBindings.findChildViewById(rootView, id);
      if (saveToLocalButton == null) {
        break missingId;
      }

      return new ActivityQrCodeBinding((RelativeLayout) rootView, backButton, qrCodeImageView,
          regenerateButton, saveToLocalButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}