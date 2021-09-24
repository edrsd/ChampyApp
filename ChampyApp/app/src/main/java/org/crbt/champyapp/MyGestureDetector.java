package org.crbt.champyapp;

import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;

public class MyGestureDetector {

    private static final String TAG = "MatrixGestureDetector";

    private int ptpIdx = 0;
    private final Matrix mTempMatrix = new Matrix();
    private final Matrix mMatrix;
    private final OnChangeListener mListener;
    private final float[] mSrc = new float[4];
    private final float[] mDst = new float[4];
    private int mCount;

    private int gesto;
    boolean flag=false;

    public interface OnChangeListener {
        void onChange(Matrix matrix);
    }

    public MyGestureDetector(Matrix matrix, OnChangeListener listener) {
        this.mMatrix = matrix;
        this.mListener=listener;
    }


    public void onTouchEvent(MotionEvent event) {
        int dedosEnTouch=event.getPointerCount();

        if (dedosEnTouch > 2) {
            return;
        }

        int action = event.getActionMasked();
        int index = event.getActionIndex();

        int idx;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG,"Toque detectado");
                mSrc[0]=event.getX();
                mSrc[1]=event.getY();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG,"Toque de segundo dedo detectado");
                mSrc[0] = event.getX(0);
                mSrc[1] = event.getY(0);
                mSrc[2] = event.getX(1);
                mSrc[3] = event.getY(1);
                break;

            case MotionEvent.ACTION_MOVE:
                if(dedosEnTouch==1) {
                    Log.d(TAG,"Movimento de un dedo con indice:"+index);
                    if(flag==true){
                        Log.d(TAG,"Movimento de un dedo detectado con flag activo");
                        mDst[2] = event.getX(0);
                        mDst[3] = event.getY(0);
                        mTempMatrix.setPolyToPoly(mSrc,2 , mDst, 2, 1);
                    }
                    else{
                        Log.d(TAG,"Movimento de un dedo detectado sin flag");
                        mDst[0] = event.getX(0);
                        mDst[1] = event.getY(0);
                        mTempMatrix.setPolyToPoly(mSrc, 0, mDst, 0, 1);
                    }
                }
                else if(dedosEnTouch==2){
                    Log.d(TAG,"Movimento de dos dedos detectado");
                    mDst[0] = event.getX(0);
                    mDst[1] = event.getY(0);
                    mDst[2] = event.getX(1);
                    mDst[3] = event.getY(1);
                    mTempMatrix.setPolyToPoly(mSrc, 0, mDst, 0, 2);
                }

                mMatrix.postConcat(mTempMatrix);

                if(mListener != null) {
                    mListener.onChange(mMatrix);
                }

                System.arraycopy(mDst, 0, mSrc, 0, mDst.length);
                break;

            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG,"Gesto cancelado");
                break;

            case MotionEvent.ACTION_UP:
                if(event.getPointerId(index)==0)
                    Log.d(TAG,"Dedo  de indice cero levantado");
                else{
                    flag=false;
                    Log.d(TAG,"Dedo  de indice uno levantado");
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if(event.getPointerId(index)==0){
                    flag=true;
                    Log.d(TAG,"Toque del segundo dedo con indice cero levantado");
                }
                else{
                    Log.d(TAG,"Toque del segundo dedo con indice uno levantado");
                    flag=false;
                }
                break;
        }
    }
}
