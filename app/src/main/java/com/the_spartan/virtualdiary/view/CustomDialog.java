package com.the_spartan.virtualdiary.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.the_spartan.virtualdiary.R;

public class CustomDialog {

    public TextView posBtn;
    public TextView negBtn;
    private Context context;
    private AlertDialog alertDialog;
    private ViewGroup viewGroup;
    private View dialogView;
    private int layoutResId;
    private Animation openingAnimation;
    private Animation closingAnimation;
    private TextView titleView;
    private TextView messageView;

    public CustomDialog(Context context,
                        ViewGroup viewGroup,
                        int layout,
                        int title,
                        int message,
                        int posText,
                        int negText) {

        this(context,
                viewGroup,
                layout,
                R.anim.scale_in,
                R.anim.scale_out,
                title == 0 ? null : context.getString(title),
                message == 0 ? null : context.getString(message),
                context.getString(posText),
                context.getString(negText));
    }

    public CustomDialog(Context context,
                        ViewGroup viewGroup,
                        int layout,
                        int openingAnimResId,
                        int closingAnimResId,
                        String title,
                        String message,
                        String posText,
                        String negText) {
        this.context = context;
        this.viewGroup = viewGroup;
        this.layoutResId = layout;
        this.openingAnimation = AnimationUtils.loadAnimation(context, openingAnimResId);
        this.closingAnimation = AnimationUtils.loadAnimation(context, closingAnimResId);

        init(title, message, posText, negText);
    }

    private void init(String title, String message, String posText, String negText) {
        this.dialogView = LayoutInflater.from(context).inflate(layoutResId, viewGroup, false);
        dialogView.startAnimation(openingAnimation);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        this.alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);

        this.titleView = dialogView.findViewById(R.id.dialog_title);
        if (title == null) {
            titleView.setVisibility(View.GONE);
        }
        titleView.setText(title);

        this.messageView = dialogView.findViewById(R.id.dialog_message);
        if (message == null) {
            messageView.setVisibility(View.GONE);
        }
        messageView.setText(message);

        this.posBtn = dialogView.findViewById(R.id.pos_btn);
        posBtn.setText(posText);

        this.negBtn = dialogView.findViewById(R.id.neg_btn);
        negBtn.setText(negText);

        setNegBtnDefaultListener();
    }

    private void setNegBtnDefaultListener() {
        this.negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closingAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dismiss();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                startAnimation(closingAnimation);
            }
        });
    }

    public void startAnimation(int animationResId) {
        this.dialogView.startAnimation(AnimationUtils.loadAnimation(this.context, animationResId));
    }

    public void startAnimation(Animation animation) {
        this.dialogView.startAnimation(animation);
    }

    public void show() {
        this.alertDialog.show();
    }

    public void dismiss() {
        this.alertDialog.dismiss();
    }
}
