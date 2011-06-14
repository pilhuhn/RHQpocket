/*
 * RHQ Management Platform
 * Copyright (C) 2005-2011 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.pocket;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.codehaus.jackson.map.ObjectMapper;

import org.rhq.core.domain.rest.MetricAggregate;

/**
 * View to draw charts on
 * @author Heiko W. Rupp
 */
public class ChartView extends SurfaceView {

    private static final int RADIUS = 2;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    MetricAggregate metrics;
    private Context context;
    private AttributeSet attrs;
    private ObjectMapper mapper;
    SurfaceHolder surfaceHolder;
    private int mHeight;
    private int mWidth;

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        surfaceHolder=getHolder();
    }

    public void setMetrics(MetricAggregate metrics) {

        this.metrics = metrics;
    }

    public void repaint() {
        Canvas c = null;
        try {
            c = surfaceHolder.lockCanvas();
            paint(c);
        } finally {
            if (c != null) {
                surfaceHolder.unlockCanvasAndPost(c);
            }
        }
}


    private void paint(Canvas canvas) {

        if (metrics==null)
            return; // TODO display some "no metrics" message ?"

        mHeight = canvas.getHeight();
        mWidth = canvas.getWidth();

        // clean up
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0,0,mWidth,mHeight,mPaint);

        // now draw again

        double band = metrics.getMax()-metrics.getMin();
        double yDelta = band/ mHeight;

        int numDots = metrics.getDataPoints().size();
        int xPos = 0;
        int xOffset = mWidth / numDots;
        for (MetricAggregate.DataPoint point : metrics.getDataPoints()) {
            if (!Double.isNaN(point.getValue())) {

                float yPos = (float) (mHeight -
                                    (point.getValue()-metrics.getMin())/yDelta);

                mPaint.setColor(Color.GREEN);
                canvas.drawCircle(xPos,yPos, RADIUS+1,mPaint);

                if (!Double.isNaN(point.getHigh())) {
                    float hyPos = (float) (mHeight -
                                    (point.getHigh()-metrics.getMin())/yDelta);
                    mPaint.setColor(Color.BLUE);

                    canvas.drawCircle(xPos,hyPos, RADIUS,mPaint);
                    float lyPos = (float) (mHeight -
                                    (point.getLow()-metrics.getMin())/yDelta);
                    mPaint.setColor(Color.CYAN);
                    canvas.drawCircle(xPos,lyPos, RADIUS,mPaint);

                    mPaint.setColor(Color.GRAY);
                    canvas.drawLine(xPos,lyPos,xPos,hyPos,mPaint);
                }
            }
            xPos += xOffset;
        }

        // now show the avg line
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{4,2},0));

        float aPos = (float) (mHeight -
                            (metrics.getAvg()-metrics.getMin())/yDelta);

        canvas.drawLine(0,aPos, mWidth,aPos,mPaint);
    }
}
