package org.crbt.champyapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class JoystickView extends View {
    public static final String TAG = JoystickView.class.getSimpleName();

    Paint outerPaint;
    Paint linePaint;
    Paint joystickPaint;

    float joystickRadius;
    float posX;
    float posY;

    RosRepository rosRepository;
    PubJoystick nodoPublicador;

    JoystickPos listener;
    boolean touchEnable=true;

    public static JoystickView joystickView;

    public interface JoystickPos{
        public void onJysticPosChange(String posicion);
    }


    public JoystickView(Context context) {
        super(context);
        rosRepository=RosRepository.getInstance(context);
        init();

    }

    public JoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rosRepository=RosRepository.getInstance(context);
        init();

    }


    private void init(){
        joystickRadius = cmToPx(getContext(), 1)/2;
        joystickPaint = new Paint();
        joystickPaint.setColor(getResources().getColor(R.color.purple_200));

        outerPaint = new Paint();
        outerPaint.setColor(getResources().getColor(R.color.purple_700));
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setStrokeWidth(dpToPx(getContext(), 3));

        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.purple_500));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAlpha(50);
        linePaint.setStrokeWidth(dpToPx(getContext(), 2));

//        nodoPublicador=new NodoPublicador();
//        rosRepository.registrarNodoJoystick(nodoPublicador);


    }

    public float cmToPx(Context context, float cm) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, cm*10, dm);

        return px;
    }

    public static float dpToPx(Context context, float dp){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);

        return px;
    }

    // Move to polarCoordinates
    public void moveTo(float x, float y){
        posX = x;
        posY = y;

        float velocidadLineal=y*0.8f;
        float velocidadAngular=x*0.8f;

        if (y > 0.2 || y < -0.2) {
//            float velocidadLineal=y*coeficienteVelLineal;
//            float velocidadAngular=x*coeficienteVelAngular;

            if(nodoPublicador!=null){
                nodoPublicador.actualizarPosJoystick(velocidadLineal,velocidadAngular);
            }
        }
        else{
//            float velocidadAngular=x*coeficienteVelAngular;

            if(nodoPublicador!=null){
                nodoPublicador.actualizarPosJoystick(0,velocidadAngular);
            }
        }

        // Redraw
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(touchEnable){
            float eventX = event.getX();
            float eventY = event.getY();
            float[] polars = convertFromPxToPolar(eventX, eventY);

            switch(event.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                    moveTo(0, 0);
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_DOWN:
                    moveTo(polars[0], polars[1]);
                    break;

                default:
                    return false;
            }
            return true;
        } else{
            moveTo(0, 0);
            return true;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        float width = getWidth();
        float height = getHeight();

        float[] px = convertFromPolarToPx(posX, posY);

        // Outer ring
        canvas.drawCircle(width/2, height/2, width/2-joystickRadius, outerPaint);

        // Inner drawings
        canvas.drawCircle(width/2, height/2, width/4-joystickRadius/2, linePaint);
        canvas.drawLine(joystickRadius, height/2, width-joystickRadius, height/2,  linePaint);
        canvas.drawLine(width/2, height/2 - width/2 + joystickRadius ,
                width/2, height/2 + width/2 - joystickRadius,  linePaint);

        // Stick
        canvas.drawCircle(px[0], px[1], joystickRadius, joystickPaint);
    }


    private float[] convertFromPxToPolar(float x, float y) {
        float middleX = getWidth()/2f;
        float middleY = getHeight()/2f;
        float r = middleX -joystickRadius;

        float dx = x - middleX;
        float dy = y - middleY;
        double rad = Math.atan2(dy, dx);

        double len = Math.sqrt(dx*dx + dy*dy)/r;
        len = Math.min(1, len);

        float[] polar = new float[2];

        polar[0] = (float) (Math.cos(rad)*len);
        polar[1] = (float) (-Math.sin(rad)*len);

        return polar;
    }

    private float[] convertFromPolarToPx(float x, float y){
        float middleX = getWidth()/2f;
        float middleY = getHeight()/2f;
        float r = middleX -joystickRadius;

        float[] px = new float[2];
        px[0] = middleX + x*r;
        px[1] = middleY - y*r;

        return px;
    }

    public void setNodoPublicador(PubJoystick nodoPublicador){
        if(nodoPublicador!=null){
            this.nodoPublicador=nodoPublicador;
            rosRepository.registrarNodoJoystick(nodoPublicador);
        }
    }

    public void unSetNodoPublicador(){
        if(nodoPublicador!=null){
            rosRepository.borrarNodoJoystick(nodoPublicador);
            nodoPublicador=null;
        }
    }

    public void setJoystickPosListener(JoystickPos listener){
        this.listener=listener;
    }

    public void setTouchEnable(boolean habilitarTouchListener){
        this.touchEnable=habilitarTouchListener;
    }

    public boolean isTouchEnable(){
        return this.touchEnable;
    }

    public void activarJoystickConInactividad(){
        nodoPublicador.activarJoystickConInactividad();
    }

    public void desactivarJoystickConInactividad(){
        nodoPublicador.desactivarJoystickConInactividad();
    }

    public PubJoystick getNodoPublicador(){
        return this.nodoPublicador;
    }
}
