package com.the_spartan.virtualdiary.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputLayout;
import com.the_spartan.virtualdiary.R;

public class CustomDialog {

    public TextView posBtn;
    public TextView negBtn;
    private Context context;
    private AlertDialog alertDialog;
    private ViewGroup viewGroup;
    private View dialogView;
    private ListView listView;
    private int layoutResId;
    private Animation openingAnimation;
    private Animation closingAnimation;
    private TextView titleView;
    private TextView messageView;
    private boolean defaultAnim;
    private boolean hasListView;

    private EditText edOldPassword;
    private EditText edNewPassword;
    private EditText edConfirmPassword;

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
                true,
                R.anim.scale_in,
                R.anim.scale_out,
                title == -1 ? null : context.getString(title),
                message == -1 ? null : context.getString(message),
                posText == -1 ? null : context.getString(posText),
                negText == -1 ? null : context.getString(negText),
                false);
    }

    public CustomDialog(Context context,
                        ViewGroup viewGroup,
                        int layout,
                        int title,
                        int message,
                        int posText,
                        int negText,
                        boolean hasListView) {

        this(context,
                viewGroup,
                layout,
                true,
                R.anim.scale_in,
                R.anim.scale_out,
                title == -1 ? null : context.getString(title),
                message == -1 ? null : context.getString(message),
                posText == -1 ? null : context.getString(posText),
                negText == -1 ? null : context.getString(negText),
                hasListView);
    }

    public CustomDialog(Context context,
                        ViewGroup viewGroup,
                        int layout,
                        boolean defaultAnim,
                        int openingAnimResId,
                        int closingAnimResId,
                        String title,
                        String message,
                        String posText,
                        String negText,
                        boolean hasListView) {

        this.context = context;
        this.viewGroup = viewGroup;
        this.layoutResId = layout;
        this.openingAnimation = AnimationUtils.loadAnimation(context, openingAnimResId);
        this.closingAnimation = AnimationUtils.loadAnimation(context, closingAnimResId);
        this.hasListView = hasListView;

        init(title, message, posText, negText);
    }

    private void init(String title, String message, String posText, String negText) {
        this.dialogView = LayoutInflater.from(context).inflate(layoutResId, viewGroup, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        this.alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (defaultAnim) {
            alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        } else {
            dialogView.startAnimation(openingAnimation);
        }
        alertDialog.setCancelable(false);

        this.titleView = dialogView.findViewById(R.id.dialog_title);
        if (title == null) {
            titleView.setVisibility(View.GONE);
        }
        titleView.setText(title);

        this.messageView = dialogView.findViewById(R.id.dialog_message);
        if (message == null && messageView != null) {
            messageView.setVisibility(View.GONE);
        }
        if (messageView != null) {
            messageView.setText(message);
        }

        this.listView = dialogView.findViewById(R.id.dialog_list_view);
        if (!hasListView && this.listView != null) {
            this.listView.setVisibility(View.GONE);
        }

        this.edOldPassword = dialogView.findViewById(R.id.ed_old_password);
        this.edNewPassword = dialogView.findViewById(R.id.ed_password);
        this.edConfirmPassword = dialogView.findViewById(R.id.ed_confirm_password);

        this.posBtn = dialogView.findViewById(R.id.pos_btn);
        if (posText == null) {
            posBtn.setVisibility(View.GONE);
        }
        posBtn.setText(posText);

        this.negBtn = dialogView.findViewById(R.id.neg_btn);
        if (negText == null) {
            negBtn.setVisibility(View.GONE);
        }
        negBtn.setText(negText);

        setNegBtnDefaultListener();
    }

    public ListView getListView() {
        return listView;
    }

    public void setListAdapter(ListAdapter adapter) {
        this.listView.setAdapter(adapter);
    }

    private void setNegBtnDefaultListener() {
        this.negBtn.setOnClickListener(view -> {
            if (defaultAnim) {
                dismiss();
                return;
            }

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
        });
    }

    public EditText getEdOldPassword() {
        return edOldPassword;
    }

    public EditText getEdNewPassword() {
        return edNewPassword;
    }

    public EditText getEdConfirmPassword() {
        return edConfirmPassword;
    }

    public TextInputLayout getOldPasswordTextInputLayout() {
        return dialogView.findViewById(R.id.til_old_password);
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
