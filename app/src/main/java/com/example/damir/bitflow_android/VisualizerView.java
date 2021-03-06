package com.example.damir.bitflow_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by damir on 09/12/15.
 */
class VisualizerView extends View {

    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();
    private Paint mForePaint = new Paint();

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mBytes = null;

        mForePaint.setStrokeWidth(5f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(255, 255, 255));
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
        cycleColor();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBytes == null) {
            return;
        }
        
        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        mRect.set(0, 0, getWidth(), getHeight());

        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / 2
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
        }

        canvas.drawLines(mPoints, mForePaint);
    }

    private float colorCounter = 0;
    private void cycleColor()
    {
        int r = (int)Math.floor(128*(Math.sin(colorCounter) + 3));
        int g = (int)Math.floor(128*(Math.sin(colorCounter + 1) + 1));
        int b = (int)Math.floor(128*(Math.sin(colorCounter + 7) + 1));
        mForePaint.setColor(Color.argb(128, r, g, b));
        colorCounter += 0.03;
    }

}
