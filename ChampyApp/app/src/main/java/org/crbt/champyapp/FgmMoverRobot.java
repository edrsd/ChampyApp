package org.crbt.champyapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import org.crbt.champyapp.Connection.ConnectionType;
import org.crbt.champyapp.databinding.FgmCrearMapaBinding;
import org.crbt.champyapp.databinding.FgmMoverRobotBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmMoverRobot#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmMoverRobot extends Fragment implements RosRepository.ConexionRealizada {

    // vinculacion con el Layout
    FgmMoverRobotBinding fgmBinding;
    public JoystickView joystick;
    //manejo de datos de la app
    MainViewModel mainViewModel;
    public PubJoystick pubJoystick;

    int velPinza,velStaker,velRuedas;

    boolean btnParoActivado=false;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmMoverRobot() {
        // Required empty public constructor
    }

    //Crea una nueva instancia del fragment
    public static FgmMoverRobot newInstance(){
        return new FgmMoverRobot();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmMoverRobot.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmMoverRobot newInstance(String param1, String param2) {
        FgmMoverRobot fragment = new FgmMoverRobot();
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
        fgmBinding= FgmMoverRobotBinding.inflate(inflater,container,false);

        //Se inicializa la vista del joystick
        joystick=fgmBinding.getRoot().findViewById(R.id.joystick_mr);

        //Se inicia el nodo publicador del joystick
        joystick.setNodoPublicador(new PubJoystick());

        //Se obtiene la vista raiz
        return  fgmBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        //Se inicializa el viewModel
        mainViewModel= new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.setControlManualActivado();

        activarListenerRespuestaRobot();
        activarListenerDatosRobot();


        fgmBinding.btnParoEmergencia.setOnClickListener(view -> {
            btnParoActivado=true;
//            activarListenerRespuestaRobot();
            mainViewModel.actualizarEstadoARobotEnParo();
            Toast.makeText(getContext(),"Enviando estado de paro activado ",Toast.LENGTH_SHORT).show();
        });

        fgmBinding.btnVolverMenu.setOnClickListener(view -> {
            irMenuPrincipal();
            joystick.unSetNodoPublicador();
        });

        fgmBinding.btnAbrirPinzas.setOnClickListener(view -> {
            mainViewModel.abrirPinzas(velPinza);

//            Toast.makeText(getContext(),"Abriendo pinzas",Toast.LENGTH_SHORT).show();
        });

        fgmBinding.btnCerrarPinzas.setOnClickListener(view -> {
            mainViewModel.cerrarPinza(velPinza);
//            Toast.makeText(getContext(),"Cerrando pinzas",Toast.LENGTH_SHORT).show();
        });

        fgmBinding.btnSacarStaker.setOnClickListener(view -> {
            mainViewModel.sacarStaker(velStaker);
//            Toast.makeText(getContext(),"Sacando staker staker",Toast.LENGTH_SHORT).show();
        });

        fgmBinding.btnMeterStaker.setOnClickListener(view -> {
            mainViewModel.meterStaker(velStaker);
//            Toast.makeText(getContext(),"Metiendo staker",Toast.LENGTH_SHORT).show();
        });

        fgmBinding.btnMantenerMover.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
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

        fgmBinding.sbVelocidadRobot.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int positionBar, boolean b) {
                velRuedas=positionBar;
                joystick.setVelocidadRuedas(velRuedas);
                float vel=velRuedas/100f+0.5f;
                Toast.makeText(getContext(),"Velocidad de ruedas: "+vel,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fgmBinding.sbVelocidadStaker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int positionBar, boolean b) {
                velStaker=positionBar;
                Toast.makeText(getContext(),"Velocidad de staker: "+Integer.toString(positionBar),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fgmBinding.sbVelocidadPinza.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int positionBar, boolean b) {
                velPinza=positionBar;
                Toast.makeText(getContext(),"Velocidad de pinza: "+Integer.toString(positionBar),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    public void activarListenerRespuestaRobot(){
        mainViewModel.getStatusRespuestaRobot().observe(getViewLifecycleOwner(),statusRespuestaRobot->{
            if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.DONE){
                if(btnParoActivado){
                    btnParoActivado=false;

                    irParoDeEmergencia();
                    mainViewModel.reiniciarBanderaRespuestaRobot();
                }


            }
            else if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.FAILED){
                Toast.makeText(getContext(),"Ha llegado la petición, pero no se pudo inicia mapeo",Toast.LENGTH_LONG).show();
            }

        });

        //Se registra el nodo suscriptor que recibe la respuesta del robot
        mainViewModel.iniciarEscuchaRespuestaRobot();
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


    public void irParoDeEmergencia(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .hide(getActivity().getSupportFragmentManager().findFragmentByTag("fgm_control_manual"))
                .add(R.id.main_container,FgmDesbloquearParo.newInstance(),"fgm_desbloquear_paro")
                .commit();
    }

    public void irMenuPrincipal(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMainMenu.newInstance(),"fgm_menu_principal")
                .commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }

    @Override
    public void onConnectionOk(ConnectionType connectionType) {
    }
}