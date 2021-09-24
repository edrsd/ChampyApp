package org.crbt.champyapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;

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
import sensor_msgs.CompressedImage;
import sensor_msgs.Image;

public class SubCamera extends AbstractNodeMain {

    private static final java.lang.String TAG = SubMapping.class.getSimpleName();

    private Bitmap bitmapMap, copyBitmapMap,bitmapCamara;

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

    private final CamaraListener listener;

    public SubCamera(CamaraListener escuchador){
        this.listener=escuchador;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("suscriptorDeCamara");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
//        Subscriber<Image> susCamara=connectedNode.newSubscriber("camera/rgb/image_raw",Image._TYPE);
        Subscriber<Image> susCamara=connectedNode.newSubscriber("zed/rgb/image_rect_color",Image._TYPE);
        susCamara.addMessageListener( cameraFrame->{
            if(cameraFrame instanceof CompressedImage) {
                bitmapCamara=crearBitmapCompressedImage((CompressedImage) cameraFrame);
            } else if (cameraFrame instanceof Image) {
                bitmapCamara=crearBitmapImage((Image) cameraFrame);
            } else {
                return;
            }
            listener.unFrameEscuchado(bitmapCamara);
        });
    }


    @Override
    public void onShutdown(Node node) {
        node.removeListeners();
        node.shutdown();
        super.onShutdown(node);
    }


    public interface CamaraListener{
        void unFrameEscuchado(Bitmap bitmap);
    }


    public Bitmap crearBitmapCompressedImage(CompressedImage image){
        ChannelBuffer buffer = image.getData();
        return BitmapFactory.decodeByteArray(buffer.array(), buffer.arrayOffset(), buffer.readableBytes());
    }


    public Bitmap crearBitmapImage(Image image){

        Bitmap.Config config = null;

        // Get the data
        byte[] data = image.getData().array();
        int height = image.getHeight();
        int width = image.getWidth();
        int step = image.getStep();

        // Get the starting point of the data
        int dataStart = data.length - (height * step);
        int pixelBytesNum = step / width;

        // Encode Byte and transform to image
        int iStep, iWidth, dataStep, iColor;
        long lColor;

        // Storage capacities
        int[] intArray = new int[height * width];
        long[] longArray = new long[height * width];
        int iR, iG, iB, iA, iM;
        long lR, lG, lB, lA, lM;

        // Init data extraction steps
        int monoX0, monoX1, rx0, rx1, gx0, gx1, bx0 , bx1, ax0 , ax1;

        switch (image.getEncoding()) {
            case "rgb8":
                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j * pixelBytesNum;
                        iR = data[dataStep];
                        iG = data[dataStep+1];
                        iB = data[dataStep+2];

                        iColor = -16777216 | (iR & 0xff) << 16 | (iG & 0xff) << 8 | (iB & 0xff);
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "rgba8":
                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j * pixelBytesNum;
                        iR = data[dataStep];
                        iG = data[dataStep+1];
                        iB = data[dataStep+2];
                        iA = data[dataStep+3];

                        iColor = ((iA & 0xff) << 24 | (iR & 0xff) << 16 | (iG & 0xff) << 8 | (iB & 0xff));
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "bgr8":
                iA = 255;

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j*pixelBytesNum;
                        iB = data[dataStep];
                        iG = data[dataStep+1];
                        iR = data[dataStep+2];

                        iColor = ((iA & 0xff) << 24 | (iR & 0xff) << 16 | (iG & 0xff) << 8 | (iB & 0xff));
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "bgra8":
                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j*pixelBytesNum;
                        iB = data[dataStep];
                        iG = data[dataStep+1];
                        iR = data[dataStep+2];
                        iA = data[dataStep+3];

                        iColor = ((iA & 0xff) << 24 | (iR & 0xff) << 16 | (iG & 0xff) << 8 | (iB & 0xff));
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "mono8":
                iA = 255;

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j*pixelBytesNum;
                        iM = data[dataStep];

                        iColor = ((iA & 0xff) << 24 | (iM & 0xff) << 16 | (iM & 0xff) << 8 | (iM & 0xff));
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "rgb16":
                lA = 65535;

                if(image.getIsBigendian() == 0) {
                    rx0 = 0; rx1 = 1; gx0 = 2; gx1 = 3; bx0 = 4; bx1 = 5;
                } else {
                    rx0 = 1; rx1 = 0; gx0 = 3; gx1 = 2; bx0 = 5; bx1 = 4;
                }

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j * pixelBytesNum;
                        lR = data[dataStep + rx0] | data[dataStep + rx1] << 8;
                        lG = data[dataStep + gx0] | data[dataStep + gx1] << 8;
                        lB = data[dataStep + bx0] | data[dataStep + bx1] << 8;

                        lColor = ((lR & 0xffff) << 48 | (lG & 0xffff) << 32 | (lB & 0xffff) << 16 | (lA & 0xffff));
                        longArray[iWidth + j] = lColor;
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config = Bitmap.Config.RGBA_F16;
                }

                break;

            case "rgba16":
                if(image.getIsBigendian() == 0) {
                    rx0 = 0; rx1 = 1; gx0 = 2; gx1 = 3; bx0 = 4; bx1 = 5; ax0 = 6; ax1 = 7;
                } else {
                    rx0 = 1; rx1 = 0; gx0 = 3; gx1 = 2; bx0 = 5; bx1 = 4; ax0 = 7; ax1 = 6;
                }

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j*pixelBytesNum;
                        long R = data[dataStep + rx0] | data[dataStep + rx1] << 8;
                        long G = data[dataStep + gx0] | data[dataStep + gx1] << 8;
                        long B = data[dataStep + bx0] | data[dataStep + bx1] << 8;
                        long A = data[dataStep + ax0] | data[dataStep + ax1] << 8;

                        lColor = ((R & 0xffff) << 48 | (G & 0xffff) << 32 | (B & 0xffff) << 16 | (A & 0xffff));
                        longArray[iWidth + j] = lColor;
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config = Bitmap.Config.RGBA_F16;
                }

                break;

            case "bgr16":
                lA = 65535;

                if(image.getIsBigendian() == 0) {
                    bx0 = 0; bx1 = 1; gx0 = 2; gx1 = 3; rx0 = 4; rx1 = 5;
                } else {
                    bx0 = 1; bx1 = 0; gx0 = 3; gx1 = 2; rx0 = 5; rx1 = 4;
                }

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j*pixelBytesNum;

                        lB = data[dataStep + bx0] | data[dataStep + bx1] << 8;
                        lG = data[dataStep + gx0] | data[dataStep + gx1] << 8;
                        lR = data[dataStep + rx0] | data[dataStep + rx1] << 8;

                        lColor = ((lR & 0xffff) << 48 | (lG & 0xffff) << 32 | (lB & 0xffff) << 16 | (lA & 0xffff));
                        longArray[iWidth + j] = lColor;
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config = Bitmap.Config.RGBA_F16;
                }

                break;

            case "bgra16":
                if(image.getIsBigendian() == 0) {
                    bx0 = 0; bx1 = 1; gx0 = 2; gx1 = 3; rx0 = 4; rx1 = 5; ax0 = 6; ax1 = 7;
                } else {
                    bx0 = 1; bx1 = 0; gx0 = 3; gx1 = 2; rx0 = 5; rx1 = 4; ax0 = 7; ax1 = 6;
                }

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j*pixelBytesNum;
                        lB = data[dataStep + bx0] | data[dataStep + bx1] << 8;
                        lG = data[dataStep + gx0] | data[dataStep + gx1] << 8;
                        lR = data[dataStep + rx0] | data[dataStep + rx1] << 8;
                        lA = data[dataStep + ax0] | data[dataStep + ax1] << 8;

                        lColor = ((lR & 0xffff) << 48 | (lG & 0xffff) << 32 | (lB & 0xffff) << 16 | (lA & 0xffff));
                        longArray[iWidth + j] = lColor;
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config = Bitmap.Config.RGBA_F16;
                }

                break;

            case "mono16":
                long A = 65535;

                if(image.getIsBigendian() == 0) {
                    monoX0 = 0; monoX1 = 1;
                }else{
                    monoX0 = 1; monoX1 = 0;
                }

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j*pixelBytesNum;
                        lM = data[dataStep + monoX0] | data[dataStep + monoX1] << 8;

                        lColor = ((lM & 0xffff) << 48 | (lM & 0xffff) << 32 | (lM & 0xffff) << 16 | (A & 0xffff));
                        longArray[iWidth + j] = lColor;
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config = Bitmap.Config.RGBA_F16;
                }

                break;

            default:
                Log.i(TAG, "No compatible encoding!");
        }

        // Create the bitmap if config is set and image is creatable
        if (config != null) {
            return Bitmap.createBitmap(intArray, width, height, config);

        } else {
            return null;
        }
    }
}
