package com.gc.contact.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class AlphabetScrollBar extends View {

    private Paint mPaint = new Paint();
    private String[] mAlphabet = new String[]{
            "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z",
            "#"
    };
    private boolean mPressed;
    private int mCurPosIdx = -1;
    private int mOldPosIdx = -1;
    private OnTouchBarListener mTouchListener;
    private TextView LetterNotice;

    public AlphabetScrollBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AlphabetScrollBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphabetScrollBar(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();
        int singleLetterH = height / mAlphabet.length;
        if (mPressed) {
            canvas.drawColor(Color.parseColor("#20000000"));  //按下背景颜色
        }
        for (int i = 0; i < mAlphabet.length; i++) {
            mPaint.setColor(Color.parseColor("#FF444444"));  //初始字体颜色
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(24);
            float x = width / 2 - mPaint.measureText(mAlphabet[i]) / 2;
            float y = singleLetterH * i + singleLetterH - 0.5f;
            if (i == mCurPosIdx) {
                mPaint.setColor(Color.parseColor("#FF5DA6FF"));  //设置按下字体颜色变化
                mPaint.setFakeBoldText(true);
            }
            canvas.drawText(mAlphabet[i], x, y, mPaint);
            mPaint.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        int action = arg0.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPressed = true;
                mCurPosIdx = (int) (arg0.getY() / this.getHeight() * mAlphabet.length);
                if (mTouchListener != null && mOldPosIdx != mCurPosIdx) {
                    if ((mCurPosIdx >= 0) && (mCurPosIdx < mAlphabet.length)) {
                        mTouchListener.onTouch(mAlphabet[mCurPosIdx]);
                        this.invalidate();
                    }
                    mOldPosIdx = mCurPosIdx;
                }
                if (LetterNotice != null) {
                    LetterNotice.setText(mAlphabet[mCurPosIdx]);
                    LetterNotice.getPaint().setFakeBoldText(true);
                    LetterNotice.setVisibility(View.VISIBLE);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (LetterNotice != null) {
                    LetterNotice.setVisibility(View.INVISIBLE);
                }
                mPressed = false;
                mCurPosIdx = -1;
                this.invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                mCurPosIdx = (int) (arg0.getY() / this.getHeight() * mAlphabet.length);
                if (mTouchListener != null && mCurPosIdx != mOldPosIdx) {
                    if ((mCurPosIdx >= 0) && (mCurPosIdx < mAlphabet.length)) {
                        mTouchListener.onTouch(mAlphabet[mCurPosIdx]);
                        this.invalidate();
                    }
                    mOldPosIdx = mCurPosIdx;
                }
                if (mCurPosIdx >= 0 && mCurPosIdx < mAlphabet.length) {
                    if (LetterNotice != null) {
                        LetterNotice.setText(mAlphabet[mCurPosIdx]);
                        LetterNotice.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            default:
                return super.onTouchEvent(arg0);
        }
    }

    public interface OnTouchBarListener {
        void onTouch(String letter);
    }

    public void setOnTouchBarListener(OnTouchBarListener listener) {
        mTouchListener = listener;
    }

    public void setTextView(TextView LetterNotice) {
        this.LetterNotice = LetterNotice;
    }
}
