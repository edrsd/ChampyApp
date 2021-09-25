package org.crbt.champyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.crbt.champyapp.databinding.FgmMainCrearMisionBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmMainCrearMision#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmMainCrearMision extends Fragment {
    FgmMainCrearMisionBinding fgmBinding;
    ArrayAdapter<String> adaptadorDropDown;
    MissionEntity missionEntity;
    MainViewModel mainViewModel;
    MissionSpotEntity missionSpotEntity;

    String periodo,velBotella,velStaker,idTemplador,idAccion,idAccionPosterior,tiempoAlerta,tiempoEspera;
    String posicionX,posicionY,quaternionZ,quaternionW;
    String idTagEstacionado,anguloTagEstacionado,distaciaTagEstacionado,alturaTagEstacionado;

    Boolean periodoIsReady,velBotellaIsReady,velStakerIsReady,idTempladorIsReady,idAccionIsReady,idAccionPosteriorIsReady,tiempoAlertaIsReady,tiempoEsperaIsReady;

    SharedPreferences.Editor editor;
    int idCounterMaquina=0, idCounterTemplador=0;
    AdaptadorDeListas adaptadorListaMaquinas,adaptadorListasTempladores;
    ArrayList<String> listaMaquinas,listaTempladores;


    MissionSpotEntity currentSpotSelected;
    int posCurrentSpotSelected;

    LinearLayoutManager linearLayoutManagerMaquinas, linearLayoutManagerTempladores;

    boolean btnGuardarPuntoPresionado=false,btnEliminarPresionado=false,btnGuardarMisionPresionado=false,
        btnListaMisionesActivado=false;

    JoystickView joystick;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmMainCrearMision() {
        // Required empty public constructor
    }

    public static FgmMainCrearMision newInstance(){
        return new FgmMainCrearMision();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmMainCrearMision.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmMainCrearMision newInstance(String param1, String param2) {
        FgmMainCrearMision fragment = new FgmMainCrearMision();
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

//        SharedPreferences pref=getActivity().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
//        editor= pref.edit();
//        idCounterMaquina= pref.getInt("id_counter_maquina",0);
//        idCounterTemplador=pref.getInt("id_counter_templador",0);

    }

    //------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fgmBinding= FgmMainCrearMisionBinding.inflate(inflater,container,false);

        //Se inicializa la vista del joystick
        joystick=fgmBinding.getRoot().findViewById(R.id.joystick);

        //Se inicia el nodo publicador del joystick
        joystick.setNodoPublicador(new PubJoystick());

        adaptadorDropDown=new ArrayAdapter<>(getContext(),R.layout.formato_view_dropdown,getResources().getStringArray(R.array.dropdown_options));
        fgmBinding.actvIdAccion.setAdapter(adaptadorDropDown);

        setupRecyclerViewLists();

        return  fgmBinding.getRoot();
    }
    //-------------------------------------------------------

    public void setupRecyclerViewLists(){
        fgmBinding.rvListaMaquinasMain.setHasFixedSize(true);
        fgmBinding.rvListaTempladoresMain.setHasFixedSize(true);
        linearLayoutManagerMaquinas=new LinearLayoutManager(getContext().getApplicationContext());
        linearLayoutManagerTempladores=new LinearLayoutManager(getContext().getApplicationContext());
        fgmBinding.rvListaMaquinasMain.setLayoutManager(linearLayoutManagerMaquinas);
        fgmBinding.rvListaTempladoresMain.setLayoutManager(linearLayoutManagerTempladores);

        initArrayLists();

        setupAdaptersRecyclerViewMaquinas();
        setupAdaptersRecyclerViewTempladores();
    }

    public void initArrayLists(){
        listaMaquinas=new ArrayList<>();
        listaTempladores=new ArrayList<>();
    }

    public void setupAdaptersRecyclerViewMaquinas(){
        adaptadorListaMaquinas=new AdaptadorDeListas(listaMaquinas);
        fgmBinding.rvListaMaquinasMain.setAdapter(adaptadorListaMaquinas);

        adaptadorListaMaquinas.setOnClickListener(view1 -> {
            posCurrentSpotSelected=fgmBinding.rvListaMaquinasMain.getChildLayoutPosition(view1);
            currentSpotSelected=missionEntity.obtenerMaquinasDeMision().get(posCurrentSpotSelected);

            missionSpotEntity=currentSpotSelected;

            showViewSpotMissionSelected(currentSpotSelected);
            setSpotParamsNotEditable();

        });
    }

    public void setupAdaptersRecyclerViewTempladores(){
        adaptadorListasTempladores=new AdaptadorDeListas(listaTempladores);
        fgmBinding.rvListaTempladoresMain.setAdapter(adaptadorListasTempladores);

        adaptadorListasTempladores.setOnClickListener(view -> {
            posCurrentSpotSelected=fgmBinding.rvListaTempladoresMain.getChildLayoutPosition(view);
            currentSpotSelected=missionEntity.obtenerTempladoresDeMision().get(posCurrentSpotSelected);

            missionSpotEntity=currentSpotSelected;

            showViewSpotMissionSelected(currentSpotSelected);
        });
    }

    //--------------------------------------onActivity
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        missionEntity=new MissionEntity();

        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        missionEntity=mainViewModel.getMissionEntity();
        if(missionEntity!=null){
            listaTempladores=missionEntity.getListaStringTempladores();
            listaMaquinas=missionEntity.getListaStringMaquinas();
            setupAdaptersRecyclerViewMaquinas();
            setupAdaptersRecyclerViewTempladores();
        }
        else{
            missionEntity=new MissionEntity();
        }

        activarListenerMapa();
        activarListenerDatosRobot();
        activarListenerRespuestaDelRobot();


        //-------------------BOTÓN GUARDAR MISION
        fgmBinding.btnGuardarMisionMain.setOnClickListener(view -> {
            if(missionEntity.obtenerTempladoresDeMision().size()>0 && missionEntity.obtenerMaquinasDeMision().size()>0){
                btnGuardarMisionPresionado=true;

//                mainViewModel.sendFullMissiontoRobot(missionEntity);//Envio de todos los puntos de la misión
                mainViewModel.sendMisionToRobot(missionEntity);//Envio solo del numero de puntos de misión
                Toast.makeText(getContext(),"Enviando la misión al robot con set_mission",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(),"Es necesario tener al menos un templador y una máquina",Toast.LENGTH_LONG).show();
            }
        });
        //-------------------------------------------


        //--------------------Botón agregar Máquina
        fgmBinding.btnAgregarMaquina.setOnClickListener(view -> {
            resetViewAddSpotM();
            enableViewAddSpotM();
            missionSpotEntity=new MissionSpotEntity("maquina");
            missionSpotEntity.setId(idCounterMaquina++);
        });
        //------------------------------------

        //--------------------Botón agregar Templador
        fgmBinding.btnAgregarTemplador.setOnClickListener(view -> {
            enableViewAddSpotT();
            missionSpotEntity=new MissionSpotEntity("templador");
            missionSpotEntity.setId(idCounterTemplador++);


        });
        //------------------------------------

        //--------------------Botón volver menú misión
        fgmBinding.btnVolverMenuMision.setOnClickListener(view -> {
            irFgmListaMisiones();

            mainViewModel.getMap().removeObservers(getViewLifecycleOwner());//Reseteo de mapa
            mainViewModel.terminarEscuchaMapeo();//Reseteo de mapa
            mainViewModel.resetearMapa();//Reseteo de mapa

            joystick.unSetNodoPublicador();



//            btnListaMisionesActivado=true;
//            if(mainViewModel.isNodoLocalizacionActivado())
//                mainViewModel.solicitarTerminarLocalizacion();
        });
        //------------------------------------


        //--------------------Botón activar cámara
        fgmBinding.btnActivarCamara.setOnClickListener(view -> {
            activarCamara();
        });
        //------------------------------------

        //--------------------Botón activar cámara
        fgmBinding.btnSavePosBeforeTag.setOnClickListener(view -> {

            if(missionSpotEntity.getTipo().equals("maquina")){
                // Obtener posicion antes del estacionado de presición
                periodo=fgmBinding.etPeriodo.getText().toString();
                velBotella=fgmBinding.etVelBotella.getText().toString();
                velStaker=fgmBinding.etVelStaker.getText().toString();
                idTemplador=fgmBinding.etIdTemplador.getText().toString();
                idAccion=fgmBinding.actvIdAccion.getText().toString();
                idAccionPosterior=fgmBinding.etIdAccionPosterior.getText().toString();
                tiempoAlerta=fgmBinding.etTiempoAlerta.getText().toString();
                tiempoEspera=fgmBinding.etTiempoAlerta.getText().toString();


                periodoIsReady=(!periodo.isEmpty() && !periodo.equals(""))? true:false;
                velBotellaIsReady=(!velBotella.isEmpty() && !velStaker.equals(""))?true:false;
                velStakerIsReady=(!velStaker.isEmpty() && !velStaker.equals(""))?true:false;
                idTempladorIsReady=(!idTemplador.isEmpty() && !idTemplador.equals(""))?true:false;
                idAccionIsReady=(!idAccion.isEmpty() && !idAccion.equals(""))?true:false;
                idAccionPosteriorIsReady=(!idAccionPosterior.isEmpty() && !idAccionPosterior.equals(""))?true:false;
                tiempoAlertaIsReady=(!tiempoAlerta.isEmpty() && !tiempoAlerta.equals(""))?true:false;
                tiempoEsperaIsReady=(!tiempoEspera.isEmpty() && !tiempoEspera.equals(""))?true:false;

                if(idAccion.equals("Máquina-Operador")){
                    if(periodoIsReady && velStakerIsReady && velStakerIsReady && idTempladorIsReady
                            && idAccionIsReady && idTempladorIsReady && tiempoAlertaIsReady && tiempoEsperaIsReady){
                        getRobotPosition();
//                    getRobotPositionOnceDummy();
                    }
                    else{
                        Toast.makeText(getContext(),"Rellene todos los campos para leer la posición",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(idAccion.equals("Máquina-Tag")){
                    if(periodoIsReady && velStakerIsReady && velStakerIsReady && idTempladorIsReady && idAccionIsReady && idTempladorIsReady){
                        getRobotPosition();
//                    getRobotPositionOnceDummy();
                    }
                    else{
                        Toast.makeText(getContext(),"Rellene todos los campos para leer la posición",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(idAccion.equals("")||idAccion.isEmpty()){
                    Toast.makeText(getContext(),"Rellene todos los campos para leer la posición",Toast.LENGTH_SHORT).show();
                }
            }
            else if(missionSpotEntity.getTipo().equals("templador")){
                getRobotPosition();
//                getRobotPositionOnceDummy();
            }
        });
        //------------------------------------

        //--------------------Botón guardar parametros
        fgmBinding.btnSaveTagParams.setOnClickListener(view -> {
            getTagParams();
//            getTagParamsDummy();
        });
        //------------------------------------

//-------------------------------------BOTON GUARDAR PUNTO DE MISION
        fgmBinding.btnSaveSpotMissionParams.setOnClickListener(view -> {
            btnGuardarPuntoPresionado=true;//Guardar misión v1
//            activarListenerRespuestaDelRobot();//Guardar misión v1
            mainViewModel.sendMissionSpotToRobot(missionSpotEntity);//Guardar misión v1

            //------------------Guardar misión V2-------------------------------------------------
//            if(missionSpotEntity.getTipo().equals("maquina")){
//                missionEntity.agregarMaquinaAMision(missionSpotEntity);
//
//                listaMaquinas.add(missionSpotEntity.getTipo()+(int)missionSpotEntity.getId());
//                if(listaMaquinas.size()==1){
//                    adaptadorListaMaquinas.notifyDataSetChanged();
//                }
//                else{
//                    adaptadorListaMaquinas.notifyItemInserted(listaMaquinas.size()-1);
//                }
//
//                resetViewAddSpotM();
//                disableViewAddSpot();
//                Toast.makeText(getContext(),"Maquina agregada correctamente",Toast.LENGTH_SHORT).show();
//
//            }
//            else if(missionSpotEntity.getTipo().equals("templador")){
//                missionEntity.agregarTempladorAMision(missionSpotEntity);
//
//                listaTempladores.add(missionSpotEntity.getTipo()+(int)missionSpotEntity.getId());
//                if(listaTempladores.size()==1){
//                    adaptadorListasTempladores.notifyDataSetChanged();
//                }
//                else{
//                    adaptadorListasTempladores.notifyItemInserted(listaTempladores.size()-1);
//                }
//
//                resetViewAddSpotT();
//                disableViewAddSpot();
//                Toast.makeText(getContext(),"Templador agregado correctamente",Toast.LENGTH_SHORT).show();
//            }
            //------------------Guardar misión V2-------------------------------------------------
        });
//-------------------------------------------------------------------

        //---------------------Botón cancelar
        fgmBinding.btnCancelar.setOnClickListener(view -> {
            if(missionSpotEntity.getTipo().equals("maquina")){
                idCounterMaquina--;
            }
            else if(missionSpotEntity.getTipo().equals("templador")){
                idCounterTemplador--      ;
        }

        missionSpotEntity=null;
        disableViewAddSpot();
    });
    //------------------------------------------

    //---------------------Botón eliminar
        fgmBinding.btnEliminar.setOnClickListener(view -> {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage("¿Desea eliminar "+currentSpotSelected.getTipo()+(int)currentSpotSelected.getId()+"?").
                setPositiveButton("Eliminar",(a,b)->{
                    //---------------------Guardar misión V1----------------------------------
                    mainViewModel.deleteMissionSpotFromRobot(missionSpotEntity);
                    btnEliminarPresionado=true;
                    if(missionSpotEntity.getTipo().equals("maquina")){
                        idCounterMaquina--;
                    }
                    else if(missionSpotEntity.getTipo().equals("templador")){
                        idCounterTemplador--;
                    }
                    //---------------------Guardar misión V1----------------------------------

                    //----------------------Guardar misión V2--------------------------------
//                    if(currentSpotSelected.getTipo().equals("maquina")){
//                        idCounterMaquina--;
//                        missionEntity.borrarMaquinaDeMision(posCurrentSpotSelected);
//                        listaMaquinas.remove(posCurrentSpotSelected);
//                        adaptadorListaMaquinas.notifyItemRemoved(posCurrentSpotSelected);
//                        Toast.makeText(getContext(),"Máquina eliminada correctamente",Toast.LENGTH_SHORT).show();
//
//                    }
//                    else if(currentSpotSelected.getTipo().equals("templador")){
//                        idCounterTemplador--;
//                        missionEntity.borrarTempladorDeMision(posCurrentSpotSelected);
//                        listaTempladores.remove(posCurrentSpotSelected);
//                        adaptadorListasTempladores.notifyItemRemoved(posCurrentSpotSelected);
//                        Toast.makeText(getContext(),"Templador eliminado correctamente",Toast.LENGTH_SHORT).show();
//
//                    }
                    //----------------------Guardar misión V2--------------------------------
                    disableViewAddSpot();

                }).
                setNegativeButton("Cancelar",(a,b)->{
                    a.dismiss();
                });
        dialog.show();

    });
    //------------------------------------------

    //---------------------------Botón mantener para mover---------------------

        fgmBinding.btnMantenerMover.setOnTouchListener((view, motionEvent) -> {
        if(motionEvent.getAction()== MotionEvent.ACTION_DOWN){
            if(joystick.getNodoPublicador()!=null){
                fgmBinding.flJoystickMcm.setVisibility(View.VISIBLE);
                joystick.setTouchEnable(true);
                joystick.activarJoystickConInactividad();
            }
        }
        else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
            if(joystick.getNodoPublicador()!=null){
                fgmBinding.flJoystickMcm.setVisibility(View.GONE);
                joystick.setTouchEnable(false);
                joystick.desactivarJoystickConInactividad();
            }
        }
        return true;
    });

    //-------------------------------------------------------------------------

    //---------------------Botón Dropdown de campo de texto IdAccion
        fgmBinding.actvIdAccion.setOnClickListener(view -> {
        fgmBinding.actvIdAccion.showDropDown();
    });

        fgmBinding.actvIdAccion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(i==0){
                missionSpotEntity.setAccionId(0);
                fgmBinding.tilTiempoAlerta.setVisibility(View.GONE);
                fgmBinding.tilTiempoEspera.setVisibility(View.GONE);
            }
            else{
                missionSpotEntity.setAccionId(1);
                fgmBinding.tilTiempoAlerta.setVisibility(View.VISIBLE);
                fgmBinding.tilTiempoEspera.setVisibility(View.VISIBLE);
            }
        }
    });

}
    //-------------------------------------------------------------

    //-------------------------------------onActivity


    public void activarListenerRespuestaDelRobot(){

        mainViewModel.getStatusRespuestaRobot().observe(getViewLifecycleOwner(),statusRespuestaRobot->{
            if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.DONE){

                if(btnGuardarPuntoPresionado){
                    //------------------Guardar misión----------------------------------------------------
                    btnGuardarPuntoPresionado=false;

                    if(missionSpotEntity.getTipo().equals("maquina")){
                        missionEntity.agregarMaquinaAMision(missionSpotEntity);

                        listaMaquinas.add(missionSpotEntity.getTipo()+(int)missionSpotEntity.getId());
                        if(listaMaquinas.size()==1){
                            adaptadorListaMaquinas.notifyDataSetChanged();
                        }
                        else{
                            adaptadorListaMaquinas.notifyItemInserted(listaMaquinas.size()-1);
                        }

                        resetViewAddSpotM();
                        disableViewAddSpot();
                        Toast.makeText(getContext(),"Maquina agregada correctamente",Toast.LENGTH_SHORT).show();

                    }
                    else if(missionSpotEntity.getTipo().equals("templador")){
                        missionEntity.agregarTempladorAMision(missionSpotEntity);

                        listaTempladores.add(missionSpotEntity.getTipo()+(int)missionSpotEntity.getId());
                        if(listaTempladores.size()==1){
                            adaptadorListasTempladores.notifyDataSetChanged();
                        }
                        else{
                            adaptadorListasTempladores.notifyItemInserted(listaTempladores.size()-1);
                        }

                        resetViewAddSpotT();
                        disableViewAddSpot();
                        Toast.makeText(getContext(),"Templador agregado correctamente",Toast.LENGTH_SHORT).show();
                        //------------------Guardar misión----------------------------------------------------
                    }
                    mainViewModel.reiniciarBanderaRespuestaRobot();
                }
                else if(btnEliminarPresionado){
                    //---------------------------Guardar misión V1------------------------------------------
                    btnEliminarPresionado=false;

                    if(currentSpotSelected.getTipo().equals("maquina")){
                        missionEntity.borrarMaquinaDeMision(posCurrentSpotSelected);
                        listaMaquinas.remove(posCurrentSpotSelected);
                        adaptadorListaMaquinas.notifyItemRemoved(posCurrentSpotSelected);
                        Toast.makeText(getContext(),"Máquina eliminada correctamente",Toast.LENGTH_SHORT).show();

                    }
                    else if(currentSpotSelected.getTipo().equals("templador")){
                        missionEntity.borrarTempladorDeMision(posCurrentSpotSelected);
                        listaTempladores.remove(posCurrentSpotSelected);
                        adaptadorListasTempladores.notifyItemRemoved(posCurrentSpotSelected);
                        Toast.makeText(getContext(),"Templador eliminado correctamente",Toast.LENGTH_SHORT).show();

                    }
                    disableViewAddSpot();
                    mainViewModel.reiniciarBanderaRespuestaRobot();
                    //---------------------------Guardar misión V1------------------------------------------
                }
                else  if(btnGuardarMisionPresionado){
                    btnGuardarMisionPresionado=false;
//                    mainViewModel.teminarEnvioInfoMision();
                    irCrearMision();

                    Toast.makeText(getContext(),"Misión guardada correctamente",Toast.LENGTH_SHORT).show();

                    mainViewModel.reiniciarBanderaRespuestaRobot();
//                    mainViewModel.terminarEscuchaRespuestaRobot();
//                    mainViewModel.terminarEscuchaMapeo();
                    mainViewModel.getMap().removeObservers(getViewLifecycleOwner());//Reseteo de mapa
                    mainViewModel.terminarEscuchaMapeo();//Reseteo de mapa
                    mainViewModel.resetearMapa();//Reseteo de mapa
                    joystick.unSetNodoPublicador();
                }
//                else if(btnListaMisionesActivado){
//                    btnListaMisionesActivado=false;
//
//                    mainViewModel.setNodoLocalizacionDesactivado();
//                    irFgmListaMisiones();
//                    Toast.makeText(getContext(),"El nodo de localización ha sido detenido",Toast.LENGTH_SHORT).show();
//                    mainViewModel.reiniciarBanderaRespuestaRobot();
//                }
            }

            else if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.FAILED){
//                if(btnGuardarMisionPresionado){
//                    mainViewModel.sendMisionToRobot(missionEntity);
//                }
//                else if(btnEliminarPresionado){
//                    mainViewModel.deleteMissionSpotFromRobot(missionSpotEntity);
//                }
//                else if(btnGuardarPuntoPresionado){
//                    mainViewModel.sendMissionSpotToRobot(missionSpotEntity);
//                }
                Toast.makeText(getContext(),"Repitiendo envio de operación",Toast.LENGTH_SHORT).show();

            }

        });

        //Se registra el nodo suscriptor que recibe la respuesta del robot
        mainViewModel.iniciarEscuchaRespuestaRobot();

    }

    public void manejoRespuestaRecibida(){

    }

    private void getRobotPositionOnceDummy(){
        posicionX="1";
        posicionY="2";
        quaternionZ="3";
        quaternionW="4";

        missionSpotEntity.setPosX(Float.valueOf(posicionX));
        missionSpotEntity.setPosY(Float.valueOf(posicionY));
        missionSpotEntity.setQz(Float.valueOf(quaternionZ));
        missionSpotEntity.setQw(Float.valueOf(quaternionW));

        fgmBinding.tvPosX.setText(posicionX);
        fgmBinding.tvPosY.setText(posicionY);
        fgmBinding.tvQz.setText(quaternionZ);
        fgmBinding.tvQw.setText(quaternionW);

        fgmBinding.clSpotParams.setVisibility(View.VISIBLE);
        fgmBinding.clParametrosPosicionTv.setVisibility(View.VISIBLE);

        String tipoDeMision=missionSpotEntity.getTipo();

        if(tipoDeMision.equals("maquina")&&missionSpotEntity.getAccionId()==0 ){
            missionSpotEntity.setPeriodo(Float.valueOf(periodo));
            missionSpotEntity.setVelBotella(Float.valueOf(velBotella));
            missionSpotEntity.setVelStaker(Float.valueOf(velStaker));
            missionSpotEntity.setTempladorId(Float.valueOf(idTemplador));
            missionSpotEntity.setAccionBotonId(Float.valueOf(idAccionPosterior));

            fgmBinding.btnSavePosBeforeTag.setVisibility(View.GONE);
            fgmBinding.btnSaveTagParams.setVisibility(View.VISIBLE);
        }
        else if(tipoDeMision.equals("templador")){
            fgmBinding.btnSavePosBeforeTag.setVisibility(View.GONE);
            fgmBinding.btnSaveTagParams.setVisibility(View.VISIBLE);
        }
        else if(tipoDeMision.equals("maquina")&&missionSpotEntity.getAccionId()== 1){
            missionSpotEntity.setPeriodo(Float.valueOf(periodo));
            missionSpotEntity.setVelBotella(Float.valueOf(velBotella));
            missionSpotEntity.setVelStaker(Float.valueOf(velStaker));
            missionSpotEntity.setTempladorId(Float.valueOf(idTemplador));
            missionSpotEntity.setAccionBotonId(Float.valueOf(idAccionPosterior));

            fgmBinding.btnSavePosBeforeTag.setVisibility(View.GONE);
            fgmBinding.btnSaveSpotMissionParams.setVisibility(View.VISIBLE);
        }
    }


    private void getRobotPosition(){

        mainViewModel.getPosition().observe(getViewLifecycleOwner(),posicion->{

            if(posicion!=null && missionSpotEntity!=null){
                String [] elementsPos=posicion.split(";");
                posicionX=elementsPos[0];
                posicionY=elementsPos[1];
                quaternionZ=elementsPos[2];
                quaternionW=elementsPos[3];

                missionSpotEntity.setPosX(Float.valueOf(posicionX));
                missionSpotEntity.setPosY(Float.valueOf(posicionY));
                missionSpotEntity.setQz(Float.valueOf(quaternionZ));
                missionSpotEntity.setQw(Float.valueOf(quaternionW));

                fgmBinding.tvPosX.setText("Posicion X:"+posicionX);
                fgmBinding.tvPosY.setText("Posicion Y:"+posicionY);
                fgmBinding.tvQz.setText("Posicion Qz:"+quaternionZ);
                fgmBinding.tvQw.setText("Posicion Qw:"+quaternionW);

                fgmBinding.clParametrosPosicionTv.setVisibility(View.VISIBLE);


                String tipoDeMision=missionSpotEntity.getTipo();

                if(tipoDeMision.equals("maquina")&&missionSpotEntity.getAccionId()==0 ){
                    missionSpotEntity.setPeriodo(Float.valueOf(periodo));
                    missionSpotEntity.setVelBotella(Float.valueOf(velBotella));
                    missionSpotEntity.setVelStaker(Float.valueOf(velStaker));
                    missionSpotEntity.setTempladorId(Float.valueOf(idTemplador));
                    missionSpotEntity.setAccionBotonId(Float.valueOf(idAccionPosterior));

                    fgmBinding.btnSavePosBeforeTag.setVisibility(View.GONE);
                    fgmBinding.btnSaveTagParams.setVisibility(View.VISIBLE);
                }
                else if(tipoDeMision.equals("templador")){
                    fgmBinding.btnSavePosBeforeTag.setVisibility(View.GONE);
                    fgmBinding.btnSaveTagParams.setVisibility(View.VISIBLE);
                }
                else if(tipoDeMision.equals("maquina")&&missionSpotEntity.getAccionId()== 1){
                    missionSpotEntity.setPeriodo(Float.valueOf(periodo));
                    missionSpotEntity.setVelBotella(Float.valueOf(velBotella));
                    missionSpotEntity.setVelStaker(Float.valueOf(velStaker));
                    missionSpotEntity.setTempladorId(Float.valueOf(idTemplador));
                    missionSpotEntity.setAccionBotonId(Float.valueOf(idAccionPosterior));
                    missionSpotEntity.setTiempoAlerta(Float.valueOf(tiempoAlerta));
                    missionSpotEntity.setTiempoEspera(Float.valueOf(tiempoEspera));

                    fgmBinding.btnSavePosBeforeTag.setVisibility(View.GONE);
                    fgmBinding.btnSaveSpotMissionParams.setVisibility(View.VISIBLE);
                }

                mainViewModel.getPosition().removeObservers(getViewLifecycleOwner());
                mainViewModel.terminarEscuchaPosicion();
                mainViewModel.resetearPosicion();
            }
        });
        mainViewModel.iniciarEscuchaPosicion();
    }

    private void getTagParamsDummy(){
        idTagEstacionado="4";
        anguloTagEstacionado="3";
        distaciaTagEstacionado="2";
        alturaTagEstacionado="1";

        missionSpotEntity.setIdTagEstacionado(Float.valueOf(idTagEstacionado));
        missionSpotEntity.setDistanciaEstacionado(Float.valueOf(distaciaTagEstacionado));
        missionSpotEntity.setAnguloEstacionado(Float.valueOf(anguloTagEstacionado));
        missionSpotEntity.setAlturaTemplador((Float.valueOf(alturaTagEstacionado)));

        fgmBinding.tvIdTagEstacionado.setText("Id del tag: "+idTagEstacionado);
        fgmBinding.tvAnguloTagEstacionado.setText("Ángulo del tag: "+anguloTagEstacionado);
        fgmBinding.tvDistanciaTagEstacionado.setText("Distancia del tag: "+distaciaTagEstacionado);
        fgmBinding.tvAlturaTagEstacionado.setText("Altura: "+alturaTagEstacionado);

        fgmBinding.clParametrosTagTv.setVisibility(View.VISIBLE);

        fgmBinding.btnSaveTagParams.setVisibility(View.GONE);
        fgmBinding.btnSaveSpotMissionParams.setVisibility(View.VISIBLE);
    }

    private void getTagParams(){
        //            Obtener parametros del tag: id, distancia y angulo
        mainViewModel.getTagParams().observe(getViewLifecycleOwner(),tagParams->{

            if(tagParams!=null){
                String [] elementsTagParams=tagParams.split(";");
                idTagEstacionado=elementsTagParams[0];
                anguloTagEstacionado=elementsTagParams[1];
                distaciaTagEstacionado=elementsTagParams[2];
                alturaTagEstacionado=elementsTagParams[3];

                missionSpotEntity.setIdTagEstacionado(Float.valueOf(idTagEstacionado));
                missionSpotEntity.setDistanciaEstacionado(Float.valueOf(distaciaTagEstacionado));
                missionSpotEntity.setAnguloEstacionado(Float.valueOf(anguloTagEstacionado));
                missionSpotEntity.setAlturaTemplador((Float.valueOf(alturaTagEstacionado)));

                fgmBinding.tvIdTagEstacionado.setText("Id del tag: "+idTagEstacionado);
                fgmBinding.tvAnguloTagEstacionado.setText("Ángulo del tag: "+anguloTagEstacionado);
                fgmBinding.tvDistanciaTagEstacionado.setText("Distancia del tag: "+distaciaTagEstacionado);
                fgmBinding.tvAlturaTagEstacionado.setText("Altura: "+alturaTagEstacionado);

                fgmBinding.clParametrosTagTv.setVisibility(View.VISIBLE);

                fgmBinding.btnSaveTagParams.setVisibility(View.GONE);
                fgmBinding.btnSaveSpotMissionParams.setVisibility(View.VISIBLE);

                fgmBinding.btnSaveTagParams.setVisibility(View.GONE);
                fgmBinding.btnSaveSpotMissionParams.setVisibility(View.VISIBLE);

                mainViewModel.getTagParams().removeObservers(getViewLifecycleOwner());
                mainViewModel.terminarEscuchaTagParams();
                mainViewModel.resetearTag();
            }

        });
        mainViewModel.iniciarEscuchaTagParams();
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

    public void setSpotParamsNotEditable(){
        fgmBinding.tilIdAccion.setEndIconMode(TextInputLayout.END_ICON_NONE);
        fgmBinding.tilIdAccion.setEnabled(false);
        fgmBinding.tilPeriodo.setEnabled(false);
        fgmBinding.tilVelBotella.setEnabled(false);
        fgmBinding.tilVelStaker.setEnabled(false);
        fgmBinding.tilIdTemplador.setEnabled(false);
        fgmBinding.etIdAccionPosterior.setEnabled(false);
        fgmBinding.etTiempoAlerta.setEnabled(false);
        fgmBinding.etTiempoEspera.setEnabled(false);
    }

    public void showViewSpotMissionSelected(MissionSpotEntity missionSpot){
        if(missionSpot.getTipo().equals("maquina")){
            if(missionSpot.getAccionId()==0){
                fgmBinding.actvIdAccion.setText("Máquina-Tag");
                fgmBinding.tvIdTagEstacionado.setText(Float.toString(missionSpot.getIdTagEstacionado()));
                fgmBinding.tvAnguloTagEstacionado.setText(Float.toString(missionSpot.getIdTagEstacionado()));
                fgmBinding.tvDistanciaTagEstacionado.setText(Float.toString(missionSpot.getDistanciaEstacionado()));
                fgmBinding.tvAlturaTagEstacionado.setText(Float.toString(missionSpot.getAlturaTemplador()));
                fgmBinding.clParametrosTagTv.setVisibility(View.VISIBLE);
            }
            else{
                fgmBinding.actvIdAccion.setText("Máquina-Operador");
                fgmBinding.clParametrosTagTv.setVisibility(View.GONE);
            }

            fgmBinding.etPeriodo.setText(Float.toString(missionSpot.getPeriodo()));
            fgmBinding.etVelBotella.setText(Float.toString(missionSpot.getVelBotella()));
            fgmBinding.etVelStaker.setText(Float.toString(missionSpot.getVelStaker()));
            fgmBinding.etIdTemplador.setText(Float.toString(missionSpot.getTempladorId()));
            fgmBinding.etIdAccionPosterior.setText(Float.toString(missionSpot.getAccionBotonId()));
            fgmBinding.etTiempoAlerta.setText(Float.toString(missionSpot.getTiempoAlerta()));
            fgmBinding.etTiempoEspera.setText(Float.toString(missionSpot.getTiempoEspera()));

            fgmBinding.tvPosX.setText(Float.toString(missionSpot.getPosX()));
            fgmBinding.tvPosY.setText(Float.toString(missionSpot.getPosY()));
            fgmBinding.tvQz.setText(Float.toString(missionSpot.getQz()));
            fgmBinding.tvQw.setText(Float.toString(missionSpot.getQw()));

            fgmBinding.llContainerBotones.setVisibility(View.GONE);
            fgmBinding.btnMantenerMover.setVisibility(View.GONE);
            fgmBinding.clListaMaquinasTempladores.setVisibility(View.GONE);

            fgmBinding.clSpotParams.setVisibility(View.VISIBLE);
            fgmBinding.clParametrosMaquinaTil.setVisibility(View.VISIBLE);
            fgmBinding.clParametrosPosicionTv.setVisibility(View.VISIBLE);

            fgmBinding.btnSaveSpotMissionParams.setVisibility(View.GONE);
            fgmBinding.btnSavePosBeforeTag.setVisibility(View.GONE);
            fgmBinding.btnSaveTagParams.setVisibility(View.GONE);

            fgmBinding.btnEliminar.setVisibility(View.VISIBLE);
            fgmBinding.btnCancelar.setVisibility(View.VISIBLE);

        }

        else if(missionSpot.getTipo().equals("templador")){
            fgmBinding.tvPosX.setText(Float.toString(missionSpot.getPosX()));
            fgmBinding.tvPosY.setText(Float.toString(missionSpot.getPosY()));
            fgmBinding.tvQz.setText(Float.toString(missionSpot.getQz()));
            fgmBinding.tvQw.setText(Float.toString(missionSpot.getQw()));
            fgmBinding.tvIdTagEstacionado.setText(Float.toString(missionSpot.getIdTagEstacionado()));
            fgmBinding.tvAnguloTagEstacionado.setText(Float.toString(missionSpot.getIdTagEstacionado()));
            fgmBinding.tvDistanciaTagEstacionado.setText(Float.toString(missionSpot.getDistanciaEstacionado()));
            fgmBinding.tvAlturaTagEstacionado.setText(Float.toString(missionSpot.getAlturaTemplador()));

            fgmBinding.llContainerBotones.setVisibility(View.GONE);
            fgmBinding.btnMantenerMover.setVisibility(View.GONE);
            fgmBinding.clListaMaquinasTempladores.setVisibility(View.GONE);

            fgmBinding.clSpotParams.setVisibility(View.VISIBLE);
            fgmBinding.clParametrosMaquinaTil.setVisibility(View.GONE);
            fgmBinding.clParametrosPosicionTv.setVisibility(View.VISIBLE);
            fgmBinding.clParametrosTagTv.setVisibility(View.VISIBLE);

            fgmBinding.btnSaveSpotMissionParams.setVisibility(View.GONE);
            fgmBinding.btnSavePosBeforeTag.setVisibility(View.GONE);
            fgmBinding.btnSaveTagParams.setVisibility(View.GONE);

            fgmBinding.btnEliminar.setVisibility(View.VISIBLE);
            fgmBinding.btnCancelar.setVisibility(View.VISIBLE);
        }
    }


    public void enableViewAddSpotM(){
        fgmBinding.tvMissionSpot.setText("Parámetros de Máquina");
        fgmBinding.tilIdAccion.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
        fgmBinding.tilIdAccion.setEnabled(true);
        fgmBinding.tilPeriodo.setEnabled(true);
        fgmBinding.tilVelBotella.setEnabled(true);
        fgmBinding.tilVelStaker.setEnabled(true);
        fgmBinding.tilIdTemplador.setEnabled(true);
        fgmBinding.etIdAccionPosterior.setEnabled(true);
        fgmBinding.etTiempoAlerta.setEnabled(true);
        fgmBinding.etTiempoEspera.setEnabled(true);

        fgmBinding.llContainerBotones.setVisibility(View.GONE);
//        fgmBinding.btnMantenerMover.setVisibility(View.GONE);
        fgmBinding.clListaMaquinasTempladores.setVisibility(View.GONE);

        fgmBinding.clSpotParams.setVisibility(View.VISIBLE);

        fgmBinding.clParametrosMaquinaTil.setVisibility(View.VISIBLE);
        fgmBinding.clParametrosPosicionTv.setVisibility(View.GONE);
        fgmBinding.clParametrosTagTv.setVisibility(View.GONE);

        fgmBinding.btnSavePosBeforeTag.setVisibility(View.VISIBLE);
        fgmBinding.btnSaveTagParams.setVisibility(View.GONE);
        fgmBinding.btnSaveSpotMissionParams.setVisibility(View.GONE);
        fgmBinding.btnCancelar.setVisibility(View.VISIBLE);
        fgmBinding.btnEliminar.setVisibility(View.GONE);

        fgmBinding.etPeriodo.requestFocus();
    }

    public void enableViewAddSpotT(){
        fgmBinding.tvMissionSpot.setText("Parámetros de Templador");
        fgmBinding.llContainerBotones.setVisibility(View.GONE);
//        fgmBinding.btnMantenerMover.setVisibility(View.GONE);
        fgmBinding.clListaMaquinasTempladores.setVisibility(View.GONE);

        fgmBinding.clSpotParams.setVisibility(View.VISIBLE);
        fgmBinding.clParametrosMaquinaTil.setVisibility(View.GONE);
        fgmBinding.clParametrosPosicionTv.setVisibility(View.GONE);
        fgmBinding.clParametrosTagTv.setVisibility(View.GONE);

        fgmBinding.btnSaveTagParams.setVisibility(View.GONE);
        fgmBinding.btnSaveSpotMissionParams.setVisibility(View.GONE);
        fgmBinding.btnEliminar.setVisibility(View.GONE);

        fgmBinding.btnSavePosBeforeTag.setVisibility(View.VISIBLE);
        fgmBinding.btnCancelar.setVisibility(View.VISIBLE);
    }

    public void disableViewAddSpot(){
        fgmBinding.llContainerBotones.setVisibility(View.VISIBLE);
        fgmBinding.btnMantenerMover.setVisibility(View.VISIBLE);
        fgmBinding.clListaMaquinasTempladores.setVisibility(View.VISIBLE);
        fgmBinding.clSpotParams.setVisibility(View.GONE);
    }

    public void resetViewAddSpotM(){
        fgmBinding.actvIdAccion.setText("");
        fgmBinding.etPeriodo.setText("");
        fgmBinding.etVelBotella.setText("");
        fgmBinding.etVelStaker.setText("");
        fgmBinding.etIdTemplador.setText("");
        fgmBinding.etIdAccionPosterior.setText("");
        fgmBinding.etTiempoAlerta.setText("");
        fgmBinding.etTiempoEspera.setText("");

        resetViewAddSpotT();
    }

    public void resetViewAddSpotT(){
        fgmBinding.tvPosX.setText("");
        fgmBinding.tvPosY.setText("");
        fgmBinding.tvQz.setText("");
        fgmBinding.tvQw.setText("");
        fgmBinding.tvIdTagEstacionado.setText("");
        fgmBinding.tvAnguloTagEstacionado.setText("");
        fgmBinding.tvDistanciaTagEstacionado.setText("");
        fgmBinding.tvAlturaTagEstacionado.setText("");

        fgmBinding.btnSavePosBeforeTag.setVisibility(View.VISIBLE);
        fgmBinding.btnSaveTagParams.setVisibility(View.GONE);
        fgmBinding.btnSaveSpotMissionParams.setVisibility(View.GONE);
        fgmBinding.btnCancelar.setVisibility(View.VISIBLE);
        fgmBinding.btnEliminar.setVisibility(View.GONE);

    }


    public void irCrearMision(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .hide(getActivity().getSupportFragmentManager().findFragmentByTag("fgm_main_crear_mision"))
                .add(R.id.main_container,FgmCrearMision.newInstance(),"fgm_crear_mision")
                .commit();
    }



    public void irFgmListaMisiones(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmListaMisiones.newInstance(),"fgm_lista_misiones")
                .commit();
    }

    public void regresarFgmAnterior(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void activarCamara(){
        mainViewModel.setVentanaAnterior("fgm_main_crear_mision");
        getActivity().getSupportFragmentManager().beginTransaction()
                .hide(this)
                .add(R.id.main_container,FgmVistaCamara.newInstance(),"fgm_vista_camara")
                .commit();


//        getActivity().getSupportFragmentManager().beginTransaction()
//                .hide(getActivity().getSupportFragmentManager().findFragmentByTag("fgm_main_crear_mision"))
//                .add(R.id.main_container,FgmVistaCamara.newInstance(),"fgm_vista_camara")
//                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }
}