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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.codehaus.jackson.map.ObjectMapper;

import org.rhq.core.domain.measurement.MeasurementUnits;
import org.rhq.core.domain.rest.MetricAggregate;

/**
 * View to draw charts on
 * @author Heiko W. Rupp
 */
public class ChartView extends SurfaceView {

    private static final int RADIUS = 1;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    MetricAggregate metrics;
    private Context context;
    private AttributeSet attrs;
    private ObjectMapper mapper;
    SurfaceHolder surfaceHolder;
    MeasurementUnits mUnits;

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        surfaceHolder=getHolder();
    }

    public void setMetrics(MetricAggregate metrics) {

        this.metrics = metrics;
    }

    public void setUnit(String units) {
        mUnits = MeasurementUnits.getUsingDisplayUnits(units);
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

        if (metrics==null) {
            Log.w(getClass().getName(),"Got no metrics");
            return; // TODO display some "no metrics" message ?"
        }

        int mHeight = canvas.getHeight()-20;
        int mWidth = canvas.getWidth()-60;

        // clean up
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0,0, canvas.getWidth(), canvas.getHeight(),mPaint);

        // now draw again

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(null); // clear out any previous effect
        double band = metrics.getMax()-metrics.getMin();
        double yDelta = band/ mHeight;

        int numDots = metrics.getDataPoints().size();
        int xPos = 0;
        int xOffset = mWidth / numDots;
        for (MetricAggregate.DataPoint point : metrics.getDataPoints()) {
            if (!Double.isNaN(point.getValue())) {
                mPaint.setStrokeWidth(2f);

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

                    mPaint.setStrokeWidth(1f);
                    mPaint.setColor(Color.GRAY);
                    canvas.drawLine(xPos,lyPos,xPos,hyPos,mPaint);
                }
            }
            xPos += xOffset;
        }

        // now show the avg line
        mPaint.setColor(Color.LTGRAY);
//        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{4,2},0));

        float aPos = (float) (mHeight -
                            (metrics.getAvg()-metrics.getMin())/yDelta);

        canvas.drawLine(0,aPos, mWidth,aPos,mPaint);

        // now add some units
        mPaint.setPathEffect(null);
        String units = mUnits.getName();

        // TODO take font metrics into account
        // TODO take string length into account
        String tmp = scaleValue(metrics.getAvg(),mUnits);
        canvas.drawText(tmp, mWidth, aPos, mPaint);

        tmp = scaleValue(metrics.getMin(),mUnits);
        canvas.drawText(tmp,mWidth,mHeight,mPaint);

        tmp = scaleValue(metrics.getMax(),mUnits);
        canvas.drawText(tmp,mWidth,15,mPaint);

        // and the time stamps along with some tick marks
        canvas.drawText((String) DateFormat.format("h:mma", new Date(metrics.getMinTimeStamp())),0,mHeight+20,mPaint);
        long t2= (metrics.getMaxTimeStamp()+metrics.getMinTimeStamp())/2;
        canvas.drawText((String) DateFormat.format("h:mma",new Date(t2)),mWidth/2,mHeight+20,mPaint);
        canvas.drawText((String) DateFormat.format("h:mma",new Date(metrics.getMaxTimeStamp())),mWidth,mHeight+20,mPaint);

        canvas.drawLine(0,mHeight+10,0,mHeight,mPaint);
        canvas.drawLine(mWidth/2,mHeight+10,mWidth/2,mHeight,mPaint);
        canvas.drawLine(mWidth,mHeight+10,mWidth,mHeight,mPaint);

    }

    private  String scaleValue(Double x, MeasurementUnits mu) {
//        System.out.println(x);
        if (x.isInfinite() || x.isNaN())
            return "??";

        BigDecimal bd = BigDecimal.valueOf(x);
//        System.out.println(bd.scale());
//        System.out.println(bd.precision());
        int vorkomma = bd.precision()-bd.scale();
        int dreier = vorkomma /3;
        BigDecimal bd2 = bd.movePointLeft(dreier*3);
//        System.out.println(bd2);
        BigDecimal bd3 = bd2.setScale(2,BigDecimal.ROUND_HALF_DOWN);
//        System.out.println(bd3);


        List<MeasurementUnits> matching = new ArrayList<MeasurementUnits>();
        for (MeasurementUnits mu2 : MeasurementUnits.values())
        {
            if (!mu2.isComparableTo(mu))
                continue;
            matching.add(mu2);
        }

        MeasurementUnits targetUnit=null;
        for (int i = 0; i< matching.size();i++) {
            if (matching.get(i).equals(mu)) {
                targetUnit = matching.get(i+dreier);
                break;
            }
        }
        if (targetUnit==null) {
            System.err.println("No target unit found");
            targetUnit = mu.getBaseUnits();
        }
//        System.out.println("Target unit " + targetUnit.toString());
        double d = MeasurementUnits.scaleUp(x,targetUnit);
//        System.out.println(d);

        String result = bd3.toString() + " " + targetUnit.toString();

//        System.out.println("---");
        return result;
    }

}
