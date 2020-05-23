package com.the_spartan.virtualdiary.util;

/**
 * Created by Spartan on 3/26/2018.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * A custom EditText that draws lines between each line of text that is displayed.
 */
public class LinedEditText extends androidx.appcompat.widget.AppCompatEditText {
    private Rect mRect;
    private Paint mPaint;

    // we need this constructor for LayoutInflater
    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setColor(0xFF747474);
        mPaint.setColor(0xFF4E4E4E);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        int count = getLineCount();
        Rect r = mRect;
        Paint paint = mPaint;
        int left = getLeft();
        int right = getRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = 0;
        int paddingRight = getPaddingRight();
        int height = getHeight();
        int lineHeight = getLineHeight();
        int count = getLineCount() + (height-paddingTop-paddingBottom) / lineHeight;

        for (int i = 0; i < count; i++) {
            int baseline = lineHeight * (i+1) + paddingTop;
            canvas.drawLine(left+paddingLeft, baseline, right-paddingRight, baseline, mPaint);

        }

        super.onDraw(canvas);
    }
}