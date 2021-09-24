package org.crbt.champyapp;

import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import geometry_msgs.Twist;
import geometry_msgs.Vector3;

public class PubJoystick extends AbstractNodeMain {

    double velLinealX=0, velGiroZ=0;
    boolean joystickInactivo=false, btnManterPMPresionado=false;

    private static final java.lang.String TAG = PubJoystick.class.getSimpleName();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("PublicadorJoystick");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        final Publisher<Twist> publicador=connectedNode.newPublisher("cmd_vel",Twist._TYPE);
//
        connectedNode.executeCancellableLoop(new CancellableLoop() {
            Twist velocidad=publicador.newMessage();
            Vector3 velLineal,velAngular;

            @Override
            protected void setup() {
                velLineal=velocidad.getLinear();
                velAngular=velocidad.getAngular();
            }

            @Override
            protected void loop() throws InterruptedException {
                Log.i(TAG, " Entrando en loop publicador ");

                if(btnManterPMPresionado){
                    velLineal.setX(velLinealX);
                    velAngular.setZ(velGiroZ);

                    velocidad.setLinear(velLineal);
                    velocidad.setAngular(velAngular);

                    if(velLinealX==0 && velGiroZ==0 && !joystickInactivo){
                        joystickInactivo=true;
                        publicador.publish(velocidad);
                    }
                    else if(velLinealX!=0 || velGiroZ!=0){
                        joystickInactivo=false;
                    }

                    if(!joystickInactivo)
                        publicador.publish(velocidad);

                    Thread.sleep(20);
                }
                else{
                    velLineal.setX(0);
                    velAngular.setZ(0);
                    velocidad.setLinear(velLineal);
                    velocidad.setAngular(velAngular);
                    publicador.publish(velocidad);
                    Thread.sleep(20);
                }
            }
        });
    }

    public void actualizarPosJoystick(float velLineal,float velAngular){
        velLinealX=velLineal;
        velGiroZ=velLinealX<0 ? velAngular:-velAngular;
    }

    public void activarJoystickConInactividad(){
        btnManterPMPresionado=true;
    }

    public void desactivarJoystickConInactividad(){
        btnManterPMPresionado=false;
    }

    @Override
    public void onShutdown(Node node) {
        Log.i(TAG, " Nodo publicador cerrado ");
        node.removeListeners();
        node.shutdown();
        super.onShutdown(node);
    }
}