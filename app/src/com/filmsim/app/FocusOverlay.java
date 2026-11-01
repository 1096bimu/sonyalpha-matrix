package com.filmsim.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/** Standard Android focus-area coordinates are normalized to -1000..1000.
 * Sony rectangles use that mapping provisionally; verify alignment on the body.
 * These are selected areas, not a claim that each area achieved focus. */
final class FocusOverlay extends View {
    private final Paint paint = new Paint();
    private final List<Rect> areas = new ArrayList<Rect>();
    private int state;
    FocusOverlay(Context context) { super(context); setWillNotDraw(false); }
    void areas(List<Rect> values) {
        areas.clear();
        if (values != null) for (Rect area : values) {
            if (area != null) areas.add(new Rect(area));
        }
        invalidate();
    }
    void state(int value) { state = value; invalidate(); }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (state == 0) return;
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        // Area outlines stay white: AF lock is a global result, not per-area proof.
        paint.setColor(0xffffffff);
        paint.setStyle(Paint.Style.STROKE);
        for (Rect r : areas) canvas.drawRect((r.left+1000)*getWidth()/2000f,
            (r.top+1000)*getHeight()/2000f, (r.right+1000)*getWidth()/2000f,
            (r.bottom+1000)*getHeight()/2000f, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(state == 1 ? 0xff40ff60 : 0xffffffff);
        paint.setTextSize(18 * getResources().getDisplayMetrics().scaledDensity);
        canvas.drawText(state == 1 ? "AF locked" : "AF focusing", 12, getHeight()/2f, paint);
    }
}
