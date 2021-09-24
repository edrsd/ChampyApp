package org.crbt.champyapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;

import geometry_msgs.Pose;
import geometry_msgs.PoseStamped;
import geometry_msgs.Quaternion;
import nav_msgs.MapMetaData;
import nav_msgs.OccupancyGrid;

public class SubMapping extends AbstractNodeMain {

    private static final java.lang.String TAG = SubMapping.class.getSimpleName();

    private Bitmap bitmapMap, copyBitmapMap;

    MapMetaData info;
    ChannelBuffer buffer;
    int mapWidth;
    int mapHeight;
    int centroX;
    int centroY;
    int[] pixels;
    private double[] posicionRobot;
    private int orientacionRobot;

    private final int colorBlanco= Color.WHITE;
    private final int colorNegro= Color.BLACK;
    private final int colorRojo=Color.RED;
    private final int colorGris= Color.GRAY;
    private boolean initFlag=false;

    private final MapeoListener listener;

    public SubMapping(MapeoListener escuchador){
        this.listener=escuchador;
    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("suscriptorDeMapeo");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<PoseStamped> susPosicion=connectedNode.newSubscriber("slam_out_pose", PoseStamped._TYPE);

        susPosicion.addMessageListener( posicion->{
            PoseStamped poseStamped=(PoseStamped)posicion;
            Pose pose=poseStamped.getPose();
            Quaternion quaternion=pose.getOrientation();

            this.orientacionRobot= quaternionToEuler(quaternion);
            this.posicionRobot = new double[]{pose.getPosition().getX(), pose.getPosition().getY(), pose.getPosition().getZ()};

            if(bitmapMap!=null){

                copyBitmapMap=bitmapMap.copy(bitmapMap.getConfig(),true);

                Canvas canvas=new Canvas(copyBitmapMap);

                double resolucion=0.1;
                double anchoPxMarcador=.6/resolucion;

                int posicioRX= (int) (posicionRobot[0]/resolucion);
                int posicioRY= (int) (posicionRobot[1]/resolucion);

                Paint paint=new Paint();
                paint.setColor(colorRojo);
                paint.setStyle(Paint.Style.FILL);

                Path path=new Path();
                path.moveTo(centroX+posicioRX,centroY-posicioRY-(int)(anchoPxMarcador/2));
                path.lineTo(centroX+posicioRX+(int)(anchoPxMarcador/2),centroY-posicioRY+(int)(anchoPxMarcador/2));
                path.lineTo(centroX+posicioRX,centroY-posicioRY+(int)(anchoPxMarcador/4));
                path.lineTo(centroX+posicioRX-(int)(anchoPxMarcador/2),centroY-posicioRY+(int)(anchoPxMarcador/2));
                path.close();

                Matrix mMatrix = new Matrix();
                RectF bounds = new RectF();
                path.computeBounds(bounds, true);
                mMatrix.postRotate(-(orientacionRobot-90), bounds.centerX(), bounds.centerY());
                path.transform(mMatrix);

                canvas.drawPath(path,paint);

                listener.unMapaNuevoEscuchado(copyBitmapMap);
            }

        });

        Subscriber<OccupancyGrid> susMapa = connectedNode.newSubscriber("map", OccupancyGrid._TYPE);
        susMapa.addMessageListener( mapa->{
            buffer = mapa.getData();
            if(initFlag==false){
                initFlag=true;
                info = mapa.getInfo();
                mapWidth = info.getWidth();
                mapHeight = info.getHeight();
                centroX=mapWidth/2;
                centroY=mapHeight/2;
                bitmapMap = Bitmap.createBitmap(info.getWidth(), info.getHeight(), Bitmap.Config.ARGB_8888);
                pixels=new int[mapWidth];
            }
            createBitmapMap();
        });}


    @Override
    public void onShutdown(Node node) {
        node.removeListeners();
        node.shutdown();
        super.onShutdown(node);
    }


    public interface MapeoListener{
        void unMapaNuevoEscuchado(Bitmap bitmap);
    }


    public static int  quaternionToEuler(Quaternion q){
        int angulosEuler;

        double qX=q.getX();
        double qY=q.getY();
        double qZ=q.getZ();
        double qW=q.getW();

//        double qw2 = q.getW() * q.getW();
//        double qx2 = q.getX() * q.getX();
        double qy2 = q.getY() * q.getY();
        double qz2 = q.getZ() * q.getZ();

//        angulosEuler[0] = (float)Math.atan2(2f * qX *qZ + 2f * qY * q.Z, 1 - 2f * (sqz  + sqw));     // Yaw
//        angulosEuler[1] = (float)Math.asin(2f * ( q.getX() * q.getZ() - q.getW() * q.getY() ) );                             // Pitch
        angulosEuler = (int) Math.toDegrees(Math.atan2(2f * qX * qY + 2f * qZ * qW, 1 - 2f * (qy2 + qz2)));      // Roll

        return angulosEuler;
    }

    public void createBitmapMap(){
        byte bytePixel;
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                // Pixels are ARGB packed ints.
                bytePixel = buffer.readByte();
                if (bytePixel == -1) {
                    pixels[x] = colorGris;
                } else {
                    pixels[x]=bytePixel<50 ? colorBlanco:colorNegro;
                }
            }
            bitmapMap.setPixels(pixels, 0, mapWidth, 0, mapHeight-1-y, mapWidth, 1);
        }
//        escuchador.unMensajeNuevo(bitmapMap);
    }

}