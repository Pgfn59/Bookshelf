package com.example.bookshelf;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class VerticalTextView extends androidx.appcompat.widget.AppCompatTextView {
    public VerticalTextView(Context context) {
        super(context);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Paint paint = getPaint();
        String text = getText().toString();
        float textSize = getTextSize();
        float maxWidth = 0;
        float currentColumnWidth = 0;
        float currentLineHeight = 0;
        float totalHeight = 0;

        for (int i = 0; i < text.length(); i++) {
            String c = String.valueOf(text.charAt(i));
            float charWidth = paint.measureText(c);

            if (currentLineHeight + textSize > MeasureSpec.getSize(heightMeasureSpec)) {
                maxWidth += currentColumnWidth;
                currentLineHeight = 0;
                currentColumnWidth = 0;
            }

            currentLineHeight += textSize;
            currentColumnWidth = Math.max(currentColumnWidth, charWidth);
            totalHeight = Math.max(totalHeight, currentLineHeight);
        }
        maxWidth += currentColumnWidth;
        int desiredWidth = (int) (maxWidth + getPaddingLeft() + getPaddingRight());
        int desiredHeight = (int) (totalHeight + getPaddingTop() + getPaddingBottom());
        int width = resolveSize(desiredWidth, widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    protected  void onDraw(Canvas canvas) {
        Paint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        String text = getText().toString();
        float x = getWidth() - getPaddingRight() - getTextSize();
        float y = getTextSize();

        for (int i = 0; i < text.length(); i++) {
            String c = String.valueOf(text.charAt(i));
            canvas.drawText(c, x, y, paint);
            y += getTextSize();
            if (y + getTextSize() > getHeight() - getPaddingBottom()) {
                x -= getTextSize();
                y = getTextSize();
            }
        }
    }
}
