package org.crbt.champyapp;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.crbt.champyapp.databinding.FgmCrearMapaOnLiveBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmCrearMapaOnLive#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmCrearMapaOnLive extends Fragment {

    FgmCrearMapaOnLiveBinding fgmBinding;
    MainViewModel mainViewModel;
    public JoystickView joystick;

    public boolean isCancelBtnClicked=false,isSaveBtnClicked=false,nodoGestionMapaDetenido=false,btnMenuPrincipalActivado=false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmCrearMapaOnLive() {
        // Required empty public constructor
    }

    public static FgmCrearMapaOnLive newInstance(){
        return new FgmCrearMapaOnLive();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmCrearMapaOnLive.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmCrearMapaOnLive newInstance(String param1, String param2) {
        FgmCrearMapaOnLive fragment = new FgmCrearMapaOnLive();
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
        //Forza al fragment a vizualizarse en posición horizontal
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Se inicializa fgmBinding
        fgmBinding= FgmCrearMapaOnLiveBinding.inflate(inflater,container,false);

        //Se inicializa la vista del joystick
        joystick=fgmBinding.getRoot().findViewById(R.id.joystick_ol);

        //Se inicia el nodo publicador del joystick
        joystick.setNodoPublicador(new PubJoystick());

        //Se obtiene la vista raiz
        return  fgmBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Se inicializa el viewModel
        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        activarListenerMapa();
        activarListenerSsh();
        activarListenerDatosRobot();
        activarListenerRespuestaRobot();

        fgmBinding.btnGuardarCmol.setOnClickListener(view1 -> {
            isSaveBtnClicked=true;
            deshabilitarBotones();

            fgmBinding.btnGuardarCmol.setTextColor(Color.BLACK);
            fgmBinding.btnGuardarCmol.setBackgroundColor(Color.YELLOW);
            fgmBinding.btnGuardarCmol.setText("Guardando...");

//            activarListenerRespuestaRobot();
            mainViewModel.solicitarTerminarMapeoEnRobot();
            Toast.makeText(getContext(),"Solicitando finalización de mapeo con publicador_app=5",Toast.LENGTH_SHORT).show();
        });

        fgmBinding.btnCancelarCmol.setOnClickListener(view -> {
            isCancelBtnClicked=true;
            deshabilitarBotones();
//            activarListenerRespuestaRobot();
            mainViewModel.solicitarTerminarMapeoEnRobot();

            fgmBinding.btnCancelarCmol.setTextColor(Color.BLACK);
            fgmBinding.btnCancelarCmol.setBackgroundColor(Color.YELLOW);
            fgmBinding.btnCancelarCmol.setText("Cancelando...");

            Toast.makeText(getContext(),"Solicitando finalización de mapeo con publicador_app=5",Toast.LENGTH_SHORT).show();
            });

        fgmBinding.btnMenuPrincipal.setOnClickListener(view -> {
            btnMenuPrincipalActivado=true;
            deshabilitarBotones();

            mainViewModel.solicitarTerminarMapeoEnRobot();
            Toast.makeText(getContext(),"Solicitando finalización de mapeo con publicador_app=5",Toast.LENGTH_SHORT).show();

            fgmBinding.btnMenuPrincipal.setTextColor(Color.BLACK);
            fgmBinding.btnMenuPrincipal.setBackgroundColor(Color.YELLOW);
            fgmBinding.btnMenuPrincipal.setText("Procesando...");
        });

        fgmBinding.btnMantenerMoverCmol.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction()== MotionEvent.ACTION_DOWN){
                if(joystick.getNodoPublicador()!=null){
                    fgmBinding.flJoystickOl.setVisibility(View.VISIBLE);
                    joystick.setTouchEnable(true);
                    joystick.activarJoystickConInactividad();
                }
            }
            else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                if(joystick.getNodoPublicador()!=null){
                    fgmBinding.flJoystickOl.setVisibility(View.GONE);
                    joystick.setTouchEnable(false);
                    joystick.desactivarJoystickConInactividad();
                }
            }
            return true;
        });


    }


    private void deshabilitarBotones(){
        fgmBinding.btnCancelarCmol.setEnabled(false);
        fgmBinding.btnGuardarCmol.setEnabled(false);
        fgmBinding.btnMenuPrincipal.setEnabled(false);
        fgmBinding.btnMantenerMoverCmol.setEnabled(false);

        fgmBinding.flJoystickOl.setVisibility(View.GONE);
        joystick.setTouchEnable(false);
        joystick.desactivarJoystickConInactividad();
    }

    public void activarListenerDatosRobot(){
        mainViewModel.getDatosRobot().observe(getViewLifecycleOwner(),datosRecibidos->{
            fgmBinding.tvBateria.setText("Batería: "+datosRecibidos[0]+"%; ");
            fgmBinding.tvVoltaje.setText("Voltaje: "+datosRecibidos[1]+"V; ");
            fgmBinding.tvCorriente.setText("Corriente: "+ datosRecibidos[2]+"A; ");
            fgmBinding.tvLoopClosure.setText("Lc: "+datosRecibidos[3]);
            fgmBinding.tvTagMapa.setText("Tag mapa: "+datosRecibidos[4]+"; ");
            fgmBinding.tvRefid.setText("RefId: "+datosRecibidos[5]+"; ");
            fgmBinding.tvTemperaturaCamara.setText("T cámara: "+datosRecibidos[6]+"°C; ");
            fgmBinding.tvTemperaturaGpu.setText("T GPU: "+datosRecibidos[7]+"°C");
        });
        mainViewModel.iniciarEscuchaDatosRobot();
    }

    private void activarListenerRespuestaRobot(){
        mainViewModel.getStatusRespuestaRobot().observe(getViewLifecycleOwner(),statusRespuestaRobot->{
            if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.DONE){
                if(isSaveBtnClicked){

                    fgmBinding.btnGuardarCmol.setBackgroundColor(Color.GREEN);
                    fgmBinding.btnGuardarCmol.setText("¡Guardado!");
                    irListaMapas();

                    mainViewModel.setNodoMapeoDesactivado();
                    Toast.makeText(getContext(),"El nodo de mapeo ha sido detenido; cargando lista de mapas",Toast.LENGTH_SHORT).show();
                    mainViewModel.reiniciarBanderaRespuestaRobot();
//                mainViewModel.terminarEscuchaRespuestaRobot();
//                mainViewModel.terminarSolicitudDeAppEnRobot();
                    mainViewModel.setNombreMapa("");
//                mainViewModel.terminarEscuchaMapeo();
                    joystick.unSetNodoPublicador();

                    mainViewModel.getMap().removeObservers(getViewLifecycleOwner());//Reseteo de mapa
                    mainViewModel.terminarEscuchaMapeo();//Reseteo de mapa
                    mainViewModel.resetearMapa();//Reseteo de mapa
                }

                else if (isCancelBtnClicked || btnMenuPrincipalActivado) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mainViewModel.sshEliminarMapa();
                    mainViewModel.setNodoMapeoDesactivado();
                    mainViewModel.reiniciarBanderaRespuestaRobot();
//                mainViewModel.terminarEscuchaRespuestaRobot();
//                mainViewModel.terminarSolicitudDeAppEnRobot();
                    mainViewModel.setNombreMapa("");
//                mainViewModel.terminarEscuchaMapeo();
                    joystick.unSetNodoPublicador();

                    mainViewModel.getMap().removeObservers(getViewLifecycleOwner());//Reseteo de mapa
                    mainViewModel.terminarEscuchaMapeo();//Reseteo de mapa
                    mainViewModel.resetearMapa();//Reseteo de mapa
                }
            }
            else if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.FAILED){
                Toast.makeText(getContext(),"Ha llegado la petición, pero no se pudo inicia mapeo",Toast.LENGTH_LONG).show();
            }
        });
        mainViewModel.iniciarEscuchaRespuestaRobot();
    }


    public void activarListenerSsh(){
        mainViewModel.seHanCompletadoLosComandos().observe(getViewLifecycleOwner(),sshCommandsStatus->{
            if(sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.DONE){

                irMenuPrincipal();

                Toast.makeText(getContext(),"Ssh: borrado de mapa exitoso",Toast.LENGTH_SHORT).show();

                mainViewModel.reiniciarBanderaComandosRealizados();

            }
            else if(sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.FAILED){
                fgmBinding.btnCancelarCmol.setText("Cancelar");
                fgmBinding.btnCancelarCmol.setBackgroundColor(Color.RED);
            }
        });
    }

    public void activarListenerMapa(){
        //Se escucha la respuesta del nodo de mapeo, el cual entrega una imagen del mapa
        mainViewModel.getMap().observe(getViewLifecycleOwner(),bitmapMapa -> {
            fgmBinding.ivMapa.setBitmap(bitmapMapa);
        });
        mainViewModel.iniciarEscuchaMapeo();
    }

    public void irListaMapas(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, FgmListaMapas.newInstance(),"fgm_listas_mapas")
                .addToBackStack(null)
                .commit();
    }

//    public void irMenuPrincipal(){
//        getActivity().getSupportFragmentManager().popBackStack("main_menu", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//    }

    public void irMenuPrincipal(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMainMenu.newInstance(),"fgm_menu_principal")
                .commit();
    }


    public void regresarFgmAnterior(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }
}