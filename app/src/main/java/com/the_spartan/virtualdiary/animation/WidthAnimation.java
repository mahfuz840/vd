package com.the_spartan.virtualdiary.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class WidthAnimation extends Animation {

    private int startWidth;
    private float deltaWidth;
    private View view;

    public WidthAnimation(View view) {
        this.view = view;
        this.startWidth = view.getLayoutParams().width;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().width = (int) (startWidth + deltaWidth * interpolatedTime);
        view.requestLayout();
    }

    public void setParams(int endWidth) {
        this.deltaWidth = endWidth - startWidth;
    }

    @Override
    public void setDuration(long durationMillis) {
        super.setDuration(durationMillis);
    }
}
