/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.material.motion;

import com.google.android.material.R;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.view.View;
import androidx.activity.BackEventCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.core.view.animation.PathInterpolatorCompat;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

/**
 * Base helper class for views that support back handling, which assists with common animation
 * details and back event validation.
 *
 * @hide
 */
@RestrictTo(Scope.LIBRARY_GROUP)
public abstract class MaterialBackAnimationHelper<V extends View> {

  private static final int HIDE_DURATION_MAX_DEFAULT = 300;
  private static final int HIDE_DURATION_MIN_DEFAULT = 150;
  private static final int CANCEL_DURATION_DEFAULT = 100;

  @NonNull private final TimeInterpolator progressInterpolator;

  @NonNull protected final V view;
  protected final int hideDurationMax;
  protected final int hideDurationMin;
  protected final int cancelDuration;

  @Nullable private BackEventCompat backEvent;

  public MaterialBackAnimationHelper(@NonNull V view) {
    this.view = view;

    Context context = view.getContext();
    progressInterpolator =
        MotionUtils.resolveThemeInterpolator(
            context,
            R.attr.motionEasingStandardDecelerateInterpolator,
            PathInterpolatorCompat.create(0, 0, 0, 1));
    hideDurationMax =
        MotionUtils.resolveThemeDuration(
            context, R.attr.motionDurationMedium2, HIDE_DURATION_MAX_DEFAULT);
    hideDurationMin =
        MotionUtils.resolveThemeDuration(
            context, R.attr.motionDurationShort3, HIDE_DURATION_MIN_DEFAULT);
    cancelDuration =
        MotionUtils.resolveThemeDuration(
            context, R.attr.motionDurationShort2, CANCEL_DURATION_DEFAULT);
  }

  protected float interpolateProgress(float progress) {
    return progressInterpolator.getInterpolation(progress);
  }

  protected void onStartBackProgress(@NonNull BackEventCompat backEvent) {
    this.backEvent = backEvent;
  }

  protected void onUpdateBackProgress(@NonNull BackEventCompat backEvent) {
    if (this.backEvent == null) {
      throw new IllegalStateException("Must call startBackProgress() before updateBackProgress()");
    }
    this.backEvent = backEvent;
  }

  @Nullable
  public BackEventCompat onHandleBackInvoked() {
    BackEventCompat finalBackEvent = this.backEvent;
    this.backEvent = null;
    return finalBackEvent;
  }

  @CanIgnoreReturnValue
  @NonNull
  protected BackEventCompat onCancelBackProgress() {
    if (this.backEvent == null) {
      throw new IllegalStateException(
          "Must call startBackProgress() and updateBackProgress() before cancelBackProgress()");
    }
    BackEventCompat finalBackEvent = this.backEvent;
    this.backEvent = null;
    return finalBackEvent;
  }
}
