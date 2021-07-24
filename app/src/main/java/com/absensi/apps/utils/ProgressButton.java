package com.absensi.apps.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.absensi.apps.R;

public class ProgressButton {

    private final CardView cardView;
    private final ConstraintLayout layout;
    private final ProgressBar progressBar;
    private final TextView tv;

    Animation fade_in;

    public ProgressButton(Context ct, View view) {

        fade_in = AnimationUtils.loadAnimation(ct, R.anim.fade_in);

        cardView = view.findViewById(R.id.card_view);
        layout = view.findViewById(R.id.constraint_layout);
        progressBar = view.findViewById(R.id.progressBar2);
        tv = view.findViewById(R.id.textView);

    }

    public void setTextButton(String text, int colorText, int colorButton) {
        progressBar.setVisibility(View.GONE);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setTextColor(colorText);
        layout.setBackgroundColor(colorButton);
    }

    public void buttonActivated() {
        progressBar.setAnimation(fade_in);
        progressBar.setVisibility(View.VISIBLE);
        tv.setAnimation(fade_in);
        tv.setText(R.string.please_wait);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(cardView.getResources().getColor(R.color.loading_color));
    }

    public void buttonFinished() {
        layout.setBackgroundColor(cardView.getResources().getColor(R.color.green));
        progressBar.setVisibility(View.GONE);
        tv.setText(R.string.done);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setTextColor(Color.WHITE);
    }
}
