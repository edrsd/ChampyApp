package org.crbt.champyapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyGridMapView extends View {

    Matrix matrix;
    //    MatrixGestureDetector myGestureDetector;
    MyGestureDetector myGestureDetector;
    // Rectangle Surrounding
    private Paint borderPaint;
    private Paint gridPaint;
    private Paint paintBackground;
    private float cornerWidth;
    private RectF drawRect;

    private Bitmap bitmap;
    private boolean firstTime=true;

//    // We can be in one of these 3 states
//    static final int NONE = 0;
//    static final int DRAG = 1;
//    static final int ZOOM = 2;
//    int mode = NONE;
//
//    // Remember some things for zooming
//    PointF last = new PointF();
//    PointF start = new PointF();
//    float minScale = 1f;
//    float maxScale = 3f;
//    float[] m;
//
//    int viewWidth, viewHeight;
//    static final int CLICK = 3;
//    float saveScale = 1f;
//    protected float origWidth, origHeight;
//    int oldMeasuredWidth, oldMeasuredHeight;


    public MyGridMapView(Context context) {
        super(context);
        init();
    }

    public MyGridMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        this.matrix = new Matrix();
//        this.matrix.setScale(1, -1);

//        this.myGestureDetector = new MatrixGestureDetector(matrix, matrix -> {
//            this.invalidate();
//        });

        this.myGestureDetector = new MyGestureDetector(matrix, matrix -> {
            this.invalidate();
        });

        this.cornerWidth = 10;

        this.borderPaint = new Paint();
        this.borderPaint.setColor(Color.RED);
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeWidth(10);

        // Background color
        this.paintBackground = new Paint();
        this.paintBackground.setColor(Color.argb(100, 0, 0, 0));
//        this.paintBackground.setColor(Color.WHITE);
        this.paintBackground.setStyle(Paint.Style.FILL);

        this.drawRect = new RectF(0, 0, 0, 0);

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                myGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Border the view
        float width = canvas.getWidth();
        float height = canvas.getHeight();
//        canvas.drawPaint(paintBackground);

        if(bitmap!=null){
            if(firstTime){
                int centroX=((int)width-bitmap.getWidth())/2;
                int centroY=((int)height-bitmap.getHeight())/2;
                matrix.setTranslate(centroX,centroY);
                firstTime=false;
            }
            canvas.drawBitmap(bitmap, matrix,null);
        }


        // Do the canvas drawing
        drawRect.set(0, 0, width, height);

        canvas.drawRoundRect(drawRect, cornerWidth, cornerWidth, borderPaint);
    }



}
