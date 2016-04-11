package com.androiddev.thirtyseven.studybuddy.Sessions.Whiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.androiddev.thirtyseven.studybuddy.R;

/**
 * Created by Joseph Elliott on 2/21/2016.
 */
public class WhiteboardView extends View {

    // drawing path
    private Path drawPath;
    // drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    // paint color
    private int paintColor;
    // canvas
    private Canvas drawCanvas;
    // canvas bitmap
    private Bitmap canvasBitmap;
    // eraser
    private boolean eraser = false;

    public WhiteboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize path and paint
        drawPath = new Path();
        drawPaint = new Paint();
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        // Set up the paints
        paintColor = ContextCompat.getColor(getContext(), R.color.black);
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        // Initialize the size and color
        setStrokeWidth(5f);
        setPaintColor("black");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public Bitmap getCanvasBitMap() {
        return canvasBitmap;
    }

    public void setPaintColor(String newColor) {
        invalidate();
        switch (newColor) {
            case "red":
                paintColor = ContextCompat.getColor(getContext(), R.color.red);
                break;
            case "orange":
                paintColor = ContextCompat.getColor(getContext(), R.color.orange);
                break;
            case "yellow":
                paintColor = ContextCompat.getColor(getContext(), R.color.yellow);
                break;
            case "green":
                paintColor = ContextCompat.getColor(getContext(), R.color.green);
                break;
            case "blue":
                paintColor = ContextCompat.getColor(getContext(), R.color.blue);
                break;
            case "indigo":
                paintColor = ContextCompat.getColor(getContext(), R.color.indigo);
                break;
            case "violet":
                paintColor = ContextCompat.getColor(getContext(), R.color.violet);
                break;
            case "black":
                paintColor = ContextCompat.getColor(getContext(), R.color.black);
                break;
            case "white":
                paintColor = ContextCompat.getColor(getContext(), R.color.white);
                break;
        }
        drawPaint.setColor(paintColor);
        canvasPaint.setColor(paintColor);
    }

    public void setStrokeWidth(float f) {
        // set the size for the pen stroke
        if (eraser) {
            drawPaint.setStrokeWidth(f * 6f);
            canvasPaint.setStrokeWidth(f * 6f);
        } else {
            drawPaint.setStrokeWidth(f * 2f);
            canvasPaint.setStrokeWidth(f * 2f);
        }
    }

    public void toggleEraser() {
        eraser = !eraser;
        if (eraser) {
            setPaintColor("white");
            setStrokeWidth(10f);
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            setPaintColor("black");
            setStrokeWidth(5f);
            drawPaint.setXfermode(null);
        }
    }

    public boolean isEraser() {
        return eraser;
    }


}
