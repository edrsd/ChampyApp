package org.crbt.champyapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.crbt.champyapp.databinding.FgmCrearMisionBinding;
import org.crbt.champyapp.databinding.FgmMisionOnLiveBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmMisionOnLive#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmMisionOnLive extends Fragment {
    FgmMisionOnLiveBinding fgmBinding;
    MainViewModel mainViewModel;
    JoystickView joystick;

    boolean btnDetenerMisionActivado=false,btnCambiarModoActivado=false,btnParoActivado=false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmMisionOnLive() {
        // Required empty public constructor
    }

    public static FgmMisionOnLive newInstance(){
        return new FgmMisionOnLive();
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmMisionOnLive.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmMisionOnLive newInstance(String param1, String param2) {
        FgmMisionOnLive fragment = new FgmMisionOnLive();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fgmBinding= FgmMisionOnLiveBinding.inflate(inflater,container,false);

        //Se inicializa la vista del joystick
        joystick=fgmBinding.getRoot().findViewById(R.id.joystick);
        joystick.setNodoPublicador(new PubJoystick());//*Cambio a iniciar modo manual por defecto

        return  fgmBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Se inicializa el viewModel
        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        activarListenerMapa();
        activarListenerDatosRobot();
        activarListenerRespuestaRobot();

        fgmBinding.btnDetenerMision.setOnClickListener(view -> {

            final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage("¿Desea detener la misión?").
                    setPositiveButton("Aceptar",(a,b)->{ detenerMision(); }).
                    setNegativeButton("Cancelar",(a,b)->{
                        btnDetenerMisionActivado=false;
                        a.dismiss();
                    });
            dialog.show();
        });

        fgmBinding.btnCambiarModo.setOnClickListener(view -> {
            btnCambiarModoActivado=true;
//            activarListenerRespuestaRobot();
            if(fgmBinding.btnCambiarModo.getText().toString().equals("Modo manual")){
                mainViewModel.actualizarEstadoARobotEnManual();
                Toast.makeText(getContext(),"Enviando cambio de estado del robot con set_estado a manual",Toast.LENGTH_SHORT).show();

            }
            else if(fgmBinding.btnCambiarModo.getText().toString().equals("Modo auto")){
                mainViewModel.actualizarEstadoARobotEnAuto();
                Toast.makeText(getContext(),"Enviando estado del robot con set_estado a auto",Toast.LENGTH_SHORT).show();
            }
        });

        fgmBinding.btnParoEmergencia.setOnClickListener(view -> {
            btnParoActivado=true;
//            activarListenerRespuestaRobot();
            mainViewModel.actualizarEstadoARobotEnParo();
            Toast.makeText(getContext(),"Enviando estado de paro activado ",Toast.LENGTH_SHORT).show();
        });

        //--------------------Botón activar cámara
        fgmBinding.btnActivarCamara.setOnClickListener(view -> {
            activarCamara();
        });
        //------------------------------------

        fgmBinding.btnMantenerParaMover.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction()== MotionEvent.ACTION_DOWN){
                if(joystick.getNodoPublicador()!=null){
                    fgmBinding.flJoystick.setVisibility(View.VISIBLE);
                    joystick.setTouchEnable(true);
                    joystick.activarJoystickConInactividad();
                }
            }
            else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                if(joystick.getNodoPublicador()!=null){
                    fgmBinding.flJoystick.setVisibility(View.GONE);
                    joystick.setTouchEnable(false);
                    joystick.desactivarJoystickConInactividad();
                }
            }
            return true;
        });
    }



    public void activarListenerRespuestaRobot(){
        mainViewModel.getStatusRespuestaRobot().observe(getViewLifecycleOwner(),statusRespuestaRobot->{
            if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.DONE){

                if(btnCambiarModoActivado){
                        btnCambiarModoActivado=false;
                        if(fgmBinding.btnCambiarModo.getText().toString().equals("Modo manual")){
                            fgmBinding.btnCambiarModo.setText("Modo auto");

                            joystick.setNodoPublicador(new PubJoystick());
                            fgmBinding.btnMantenerParaMover.setVisibility(View.VISIBLE);
                        }
                        else if(fgmBinding.btnCambiarModo.getText().toString().equals("Modo auto")){
//                            fgmBinding.flJoystick.setVisibility(View.GONE);
                            fgmBinding.btnMantenerParaMover.setVisibility(View.GONE);
                            fgmBinding.btnCambiarModo.setText("Modo manual");

                            //Se detine el nodo publicador del joystick
                            joystick.unSetNodoPublicador();
                        }
                    Toast.makeText(getContext(),"Se ha cambiado el modo correctamente",Toast.LENGTH_SHORT).show();
                    mainViewModel.reiniciarBanderaRespuestaRobot();
                    }
                else if(btnDetenerMisionActivado){
                    btnDetenerMisionActivado=false;

                    irListaMisiones();

                    mainViewModel.getMap().removeObservers(getViewLifecycleOwner());//Reseteo de mapa
                    mainViewModel.terminarEscuchaMapeo();//Reseteo de mapa
                    mainViewModel.resetearMapa();//Reseteo de mapa

                    mainViewModel.setNodoAutoDesactivado();
                    Toast.makeText(getContext(),"Se ha detenido el modo auto correctamente",Toast.LENGTH_SHORT).show();

                    mainViewModel.reiniciarBanderaRespuestaRobot();
//                    mainViewModel.terminarEscuchaRespuestaRobot();
//                    mainViewModel.terminarSolicitudDeAppEnRobot();
//                    mainViewModel.terminarEscuchaMapeo();
                    joystick.unSetNodoPublicador();

                }
               else if(btnParoActivado){
                   btnParoActivado=false;

                   irParoDeEmergencia();
                    mainViewModel.reiniciarBanderaRespuestaRobot();
//                    mainViewModel.terminarEscuchaRespuestaRobot();
//                    mainViewModel.terminarSolicitudDeAppEnRobot();
//                    mainViewModel.terminarEscuchaMapeo();
//                    joystick.unSetNodoPublicador();
                }
            }
            else if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.FAILED){
                Toast.makeText(getContext(),"Ha llegado la petición, pero no se pudo inicia mapeo",Toast.LENGTH_LONG).show();
            }

        });

        //Se registra el nodo suscriptor que recibe la respuesta del robot
        mainViewModel.iniciarEscuchaRespuestaRobot();
    }

    public void activarListenerMapa(){
        //Se escucha la respuesta del nodo de mapeo, el cual entrega una imagen del mapa
        mainViewModel.getMap().observe(getViewLifecycleOwner(),bitmapMapa -> {
            fgmBinding.ivMapa.setBitmap(bitmapMapa);
        });
        mainViewModel.iniciarEscuchaMapeo();
    }

    public void activarListenerDatosRobot(){
        mainViewModel.getDatosRobot().observe(getViewLifecycleOwner(),datosRecibidos->{
            fgmBinding.tvBateria.setText((int) datosRecibidos[0]);
            fgmBinding.tvVoltaje.setText((int) datosRecibidos[1]);
            fgmBinding.tvCorriente.setText((int) datosRecibidos[2]);
        });
//        mainViewModel.iniciarEscuchaDatosRobot();
    }

    public void detenerMision(){
        btnDetenerMisionActivado=true;
        mainViewModel.solicitarTerminarMisionEnRobot();
        Toast.makeText(getContext(),"Solicitando termino de misión con publicador_app=7",Toast.LENGTH_SHORT).show();
    }


    public void irParoDeEmergencia(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmDesbloquearParo.newInstance(),"fgm_desbloquear_paro")
                .commit();
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .hide(getActivity().getSupportFragmentManager().findFragmentByTag("fgm_mission_ol"))
//                .add(R.id.main_container,FgmDesbloquearParo.newInstance(),"fgm_desbloquear_paro")
//                .commit();

    }

    public void irListaMisiones(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmListaMisiones.newInstance(),"fgm_lista_misiones")
                .commit();
    }


    public void activarCamara(){
        mainViewModel.setVentanaAnterior("fgm_mission_ol");
        getActivity().getSupportFragmentManager().beginTransaction()
                .hide(this)
                .add(R.id.main_container,FgmVistaCamara.newInstance(),"fgm_vista_camara")
                .commit();

//        getActivity().getSupportFragmentManager().beginTransaction()
//                .hide(getActivity().getSupportFragmentManager().findFragmentByTag("fgm_mision_ol"))
//                .add(R.id.main_container,FgmVistaCamara.newInstance(),"fgm_vista_camara")
//                .commit();
    }

//    public void regresarListaMisiones(){
//        getActivity().getSupportFragmentManager().popBackStack();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }
}