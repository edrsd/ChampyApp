package org.crbt.champyapp;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.crbt.champyapp.databinding.FgmListaMisionesBinding;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmListaMisiones#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmListaMisiones extends Fragment {

    // vinculacion con el Layout
    FgmListaMisionesBinding fgmBinding;
    //manejo de datos de la app
    MainViewModel mainViewModel;

    ArrayList<String> listaMisiones;
    AdaptadorDeListas listMissionsAdapter;

    int posActualMisionSeleccionada;
    String misionSeleccionada;

    AdaptadorDeListas adaptadorListaMaquinas,adaptadorListasTempladores;
    ArrayList<String> listaTempladores,listaMaquinas;

    MissionEntity missionEntity;
    MissionSpotEntity missionSpotEntity;

    MissionSpotEntity currentSpotSelected;
    int posCurrentSpotSelected;

    boolean btnComenzarMisionActivado=false,btnEliminarMisionActivado=false,
            btnEditarMisionActivado=false,btnMenuPrincipalActivado=false, btnListaMapasActivado=false;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmListaMisiones() {
        // Required empty public constructor
    }

    //Se crea una instancia del Fragment
    public static FgmListaMisiones newInstance(){
        return  new FgmListaMisiones();
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmListaMisiones.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmListaMisiones newInstance(String param1, String param2) {
        FgmListaMisiones fragment = new FgmListaMisiones();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Se inicializa fgmBinding
        fgmBinding= FgmListaMisionesBinding.inflate(inflater,container,false);

        setupRecyclerViewMissionsList();
        setupRecyclerViewSpotLists();

        //Se obtiene la vista raiz
        return  fgmBinding.getRoot();
    }

    public void setupRecyclerViewMissionsList(){
        fgmBinding.rvListaMisiones.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext().getApplicationContext());
        fgmBinding.rvListaMisiones.setLayoutManager(linearLayoutManager);

        listaMisiones=new ArrayList<>();
        listaMisiones.add("Espere, cargando lista...");

        setupAdapterRecyclerViewList();
    }

    public void setupRecyclerViewSpotLists(){
        fgmBinding.rvListaMaquinasMain.setHasFixedSize(true);
        fgmBinding.rvListaTempladoresMain.setHasFixedSize(true);
        fgmBinding.rvListaMaquinasMain.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        fgmBinding.rvListaTempladoresMain.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));

        initArrayLists();


        setupAdaptersRecyclerViewMaquinas();
        setupAdaptersRecyclerViewTempladores();
    }

    public void initArrayLists(){
        listaMaquinas=new ArrayList<>();
        listaTempladores=new ArrayList<>();

        listaMaquinas.clear();
        listaTempladores.clear();
    }

    public void setupAdaptersRecyclerViewMaquinas(){

        adaptadorListaMaquinas=new AdaptadorDeListas(listaMaquinas);
        fgmBinding.rvListaMaquinasMain.setAdapter(adaptadorListaMaquinas);

//        adaptadorListaMaquinas.setOnClickListener(view1 -> {
//            posCurrentSpotSelected=fgmBinding.rvListaMaquinasMain.getChildLayoutPosition(view1);
//            currentSpotSelected=missionEntity.obtenerMaquinasDeMision().get(posCurrentSpotSelected);
//        });
    }

    public void setupAdaptersRecyclerViewTempladores(){
        adaptadorListasTempladores=new AdaptadorDeListas(listaTempladores);
        fgmBinding.rvListaTempladoresMain.setAdapter(adaptadorListasTempladores);

//        adaptadorListasTempladores.setOnClickListener(view -> {
//            posCurrentSpotSelected=fgmBinding.rvListaTempladoresMain.getChildLayoutPosition(view);
//            currentSpotSelected=missionEntity.obtenerTempladoresDeMision().get(posCurrentSpotSelected);
//        });
    }

    public void setupAdapterRecyclerViewList(){
        listMissionsAdapter=new AdaptadorDeListas(listaMisiones);
        fgmBinding.rvListaMisiones.setAdapter(listMissionsAdapter);

        listMissionsAdapter.setOnClickListener(view1 -> {

            posActualMisionSeleccionada=fgmBinding.rvListaMisiones.getChildLayoutPosition(view1);
            listMissionsAdapter.setItemPosition(posActualMisionSeleccionada);

            misionSeleccionada=listaMisiones.get(posActualMisionSeleccionada);

            mainViewModel.pedirMisionARobot(misionSeleccionada);

            fgmBinding.clContenedorBotones.setVisibility(View.VISIBLE);

//            Toast.makeText(getContext(),"mision "+misionSeleccionada+"seleccionada",Toast.LENGTH_SHORT).show();

        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        activarListenerListaMisiones();
        activarListenerInfoMision();
        activarListenerSsh();
        activarListenerRespuestaRobot();

        mainViewModel.solicitarMisiones();
        Toast.makeText(getContext(),"Solicitando lista de misiones con publicador_app=2",Toast.LENGTH_SHORT).show();


        fgmBinding.btnComenzarMision.setOnClickListener(view -> {
            btnComenzarMisionActivado=true;
            deshabilitarBotones();
            pintarBotonEliminar();

            fgmBinding.btnComenzarMision.setTextColor(Color.BLACK);
            fgmBinding.btnComenzarMision.setBackgroundColor(Color.YELLOW);
            fgmBinding.btnComenzarMision.setText("Procesando...");

            mainViewModel.sshCargarMision(misionSeleccionada);
            Toast.makeText(getContext(),"Solicitando inicio de mision con publicador_app en 6",Toast.LENGTH_LONG).show();
        });

//        fgmBinding.btnEditarMision.setOnClickListener(view -> {
////            btnEditarMisionActivado=true;
//        mainViewModel.setMissionEntity(missionEntity);
//            irMainCrearMision();
//        });

        fgmBinding.btnEliminarMision.setOnClickListener(view -> {
            btnEliminarMisionActivado=true;

            final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage("¿Desea eliminar la misión: "+misionSeleccionada+" ?").
                    setPositiveButton("Eliminar",(a,b)->{

                        deshabilitarBotones();

                        fgmBinding.btnEliminarMision.setTextColor(Color.BLACK);
                        fgmBinding.btnEliminarMision.setBackgroundColor(Color.YELLOW);
                        fgmBinding.btnEliminarMision.setText("Eliminando...");

                        mainViewModel.sshEliminarMision(misionSeleccionada);

                    }).
                    setNegativeButton("Cancelar",(a,b)->{
                        a.dismiss();
                    });
            dialog.show();

//            Toast.makeText(getContext(),"Ejecutando ssh rm ~/misiones/\"+nombreMision+\"nombre.yaml",Toast.LENGTH_LONG).show();
        });

        fgmBinding.btnVolverListaMapas.setOnClickListener(view -> {
            btnListaMapasActivado=true;
            deshabilitarBotones();
            pintarBotonEliminar();

            if(mainViewModel.isNodoLocalizacionActivado())
                mainViewModel.solicitarTerminarLocalizacion();
        });

        fgmBinding.btnVolverMenuPrincipal.setOnClickListener(view -> {
            btnMenuPrincipalActivado=true;
            deshabilitarBotones();
            pintarBotonEliminar();

            if(mainViewModel.isNodoLocalizacionActivado())
                mainViewModel.solicitarTerminarLocalizacion();
        });
    }


    private void deshabilitarBotones(){
        listMissionsAdapter.setClickable(false);

        fgmBinding.btnComenzarMision.setEnabled(false);
        fgmBinding.btnEliminarMision.setEnabled(false);
        fgmBinding.btnVolverListaMapas.setEnabled(false);
        fgmBinding.btnVolverMenuPrincipal.setEnabled(false);
    }

    private void habilitarBotones(){
        listMissionsAdapter.setClickable(true);

        fgmBinding.btnComenzarMision.setEnabled(true);
        fgmBinding.btnEliminarMision.setEnabled(true);
        fgmBinding.btnVolverListaMapas.setEnabled(true);
        fgmBinding.btnVolverMenuPrincipal.setEnabled(true);
    }

    public void pintarBotonEliminar(){
        fgmBinding.btnEliminarMision.setBackgroundColor(getResources().getColor(R.color.light_gray));
        fgmBinding.btnEliminarMision.setTextColor(getResources().getColor(R.color.dark_gray));
    }


    public void activarListenerListaMisiones(){
        mainViewModel.getListaMisionesSolicitada().observe(getViewLifecycleOwner(),listaRecibida->{

            ArrayList<String> listaTmp=new ArrayList<>();
            listaTmp.addAll(Arrays.asList(listaRecibida.split(",")));

            if(!listaMisiones.equals(listaTmp)){
                listaMisiones.clear();
                listaMisiones= (ArrayList<String>) listaTmp.clone();

                setupAdapterRecyclerViewList();
//                listMapsAdapter.notifyDataSetChanged();
//                fgmBinding.rvListaMapas.notify();
            }
            else if(listaTmp.get(0).equals("")){
                listaMisiones.add("No existen misiones que mostrar");
                listMissionsAdapter.notifyDataSetChanged();
            }

//            activarListenerRespuestaRobot();

            //Posiblemente habilitar vista de botones e imagen del mapa

            Toast.makeText(getContext(),"lista de misiones recibida",Toast.LENGTH_SHORT).show();

        });

        mainViewModel.iniciarEscuchaListaMisiones();
    }

    public void activarListenerInfoMision(){
        mainViewModel.getInfoMisionSolicitada().observe(getViewLifecycleOwner(),infoMisionRecibida->{

            if(infoMisionRecibida!=null){
                missionEntity=new MissionEntity();
                listaTempladores.clear();
                listaMaquinas.clear();

                int numeroTempladores=(int)infoMisionRecibida[0];//10 elementos
                int numeroMaquinas=(int)infoMisionRecibida[1];//16 elementos

                float[] templadorAux=new float[10];
                float[] maquinaAux=new float[18];

                for(int c=0;c<numeroTempladores;c++){
                    System.arraycopy(infoMisionRecibida,c*10+2,templadorAux,0,10);
                    MissionSpotEntity templador=new MissionSpotEntity(templadorAux);
                    missionEntity.agregarTempladorAMision(templador);
                }

                for(int c=0;c<numeroMaquinas;c++){
                    System.arraycopy(infoMisionRecibida,c*18+numeroTempladores*10+2,maquinaAux,0,18);
                    MissionSpotEntity maquina=new MissionSpotEntity(maquinaAux);
                    missionEntity.agregarMaquinaAMision(maquina);
                }
                listaTempladores.addAll(missionEntity.getListaStringTempladores());
                listaMaquinas.addAll(missionEntity.getListaStringMaquinas());

                fgmBinding.clListaMaquinasTempladores.setVisibility(View.VISIBLE);

                setupAdaptersRecyclerViewMaquinas();
                setupAdaptersRecyclerViewTempladores();

                mainViewModel.getInfoMisionSolicitada().removeObservers(getViewLifecycleOwner());
                mainViewModel.terminarEscuchaInfoMision();
                mainViewModel.resetearSolicitudMision();
            }
        });

        mainViewModel.iniciarEscuchaInfoMision();
    }

    public void activarListenerRespuestaRobot(){
        mainViewModel.getStatusRespuestaRobot().observe(getViewLifecycleOwner(),statusRespuestaRobot->{
            if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.DONE){

                if(btnComenzarMisionActivado){
                    btnComenzarMisionActivado=false;//*Iniciando en modo auto
                    fgmBinding.btnComenzarMision.setBackgroundColor(Color.GREEN);//*Iniciando en modo auto
                    fgmBinding.btnComenzarMision.setText("¡Listo!");//*Iniciando en modo auto
                    mainViewModel.setNodoAutoActivado();//*Iniciando en modo auto
                    irMisionEnDirecto();//*Iniciando en modo auto
                    Toast.makeText(getContext(),"Modo auto iniciado correctamente",Toast.LENGTH_SHORT).show();//*Iniciando en modo auto
                    mainViewModel.reiniciarBanderaRespuestaRobot();//*Iniciando en modo auto
                }
                else if(btnListaMapasActivado){
                    btnListaMapasActivado=false;

                    mainViewModel.setNodoLocalizacionDesactivado();
                    irListaMapas();
                    Toast.makeText(getContext(),"El nodo de localización ha sido detenido",Toast.LENGTH_SHORT).show();
                    mainViewModel.reiniciarBanderaRespuestaRobot();
                }
                else if(btnMenuPrincipalActivado){
                    btnListaMapasActivado=false;

                    mainViewModel.setNodoLocalizacionDesactivado();
                    irMenuPrincipal();
                    Toast.makeText(getContext(),"El nodo de localización ha sido detenido",Toast.LENGTH_SHORT).show();
                    mainViewModel.reiniciarBanderaRespuestaRobot();
                }

//                else{
//                    if(listaMisiones.size()==0){
//                        mainViewModel.reiniciarBanderaRespuestaRobot();
//                        mainViewModel.solicitarMisiones();
//                        Toast.makeText(getContext(),"Solicitando listas nuevamente",Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        Toast.makeText(getContext(),"Lista de mapas con elementos cargados",Toast.LENGTH_SHORT).show();
//                    }
//                }


            }
            else if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.FAILED){
                Toast.makeText(getContext(),"Ha llegado la petición, pero no se pudo iniciar la localización",Toast.LENGTH_LONG).show();
            }


        });



//            if(tipoOperacion.equals("9")&&isSuccess.equals("0")){
//                mainViewModel.solicitarIniciarMisionEnRobot();
//            }
//            else if(tipoOperacion.equals("9")&&isSuccess.equals("1")){
//
//                Toast.makeText(getContext(),"El nodo de navegación ha iniciado en el robot",Toast.LENGTH_LONG).show();
//
//                mainViewModel.actualizarEstadoARobotEnAuto();
//
//            }
//            else if(tipoOperacion.equals("11")&&isSuccess.equals("0")){
//                mainViewModel.actualizarEstadoARobotEnAuto();
//            }
//            else if(tipoOperacion.equals("11")&&isSuccess.equals("1")){
//
//                mainViewModel.getRespuestaRobot().removeObservers(getViewLifecycleOwner());
//                mainViewModel.terminarEscuchaRespuestaRobot();
//                mainViewModel.terminarSolicitudDeAppEnRobot();
//                mainViewModel.terminarEnvioEstadoARobot();
//
//                irMisionEnDirecto();
//
//            }
//        });
//        mainViewModel.iniciarEscuchaRespuestaRobot();
    }


    public void activarListenerSsh(){
        //Se espera la respuesta del estatus de los comandos mandados enviados por ssh
        mainViewModel.seHanCompletadoLosComandos().observe(getViewLifecycleOwner(),sshCommandsStatus->{

            //Si el estatus es DONE, entonces...
            if(sshCommandsStatus== SshRepositoryImpl.SshCommandsStatus.DONE){

                if(btnComenzarMisionActivado){

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mainViewModel.solicitarIniciarModoAuto();//*Iniciando en modo auto
//                    btnComenzarMisionActivado=false;//cambio en no iniciar en modo auto
//                    irMisionEnDirecto();//cambio en no iniciar en modo auto
                    Toast.makeText(getContext(),"SSH:Mision cargada",Toast.LENGTH_SHORT).show();
                }
                else if(btnEliminarMisionActivado) {
                    btnEliminarMisionActivado = false;

                    //habilitarr botones
                    habilitarBotones();

                    fgmBinding.btnEliminarMision.setTextColor(Color.WHITE);
                    fgmBinding.btnEliminarMision.setBackgroundColor(getResources().getColor(R.color.pavisa_blue));
                    fgmBinding.btnEliminarMision.setText("Eliminar misión");

                    fgmBinding.clContenedorBotones.setVisibility(View.GONE);
                    //Se actualiza la lista de misiones
                    listaMisiones.remove(posActualMisionSeleccionada);
                    listMissionsAdapter.notifyItemRemoved(posActualMisionSeleccionada);
                    setupRecyclerViewSpotLists();
                    Toast.makeText(getContext(), "SHH: misión eliminada", Toast.LENGTH_SHORT).show();

                    activarListenerInfoMision();
                }
                mainViewModel.reiniciarBanderaComandosRealizados();
            }
            else if(sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.FAILED){
                Toast.makeText(getContext(),"error en ssh",Toast.LENGTH_SHORT).show();

                //Se muestra una ventana para conectar de nuevo cuando el tiempo de conexión expira
//                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//                dialog.setMessage("No se ha podido establecer conexión")
//                        .setPositiveButton("Ok",(dialogInterface,i)->{ dialogInterface.dismiss(); });
//                dialog.show();
            }
        });
    }


    public void regresarFgmAnterior(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void irMenuPrincipal(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMainMenu.newInstance(),"fgm_menu_principal")
                .commit();
    }

    public void irListaMapas(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmListaMapas.newInstance(),"fgm_lista_mapas")
                .commit();
    }

    public void irMisionEnDirecto(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMisionOnLive.newInstance(),"fgm_mission_ol")
                .commit();
    }

    public void volverListaMisiones(){
        getActivity().getSupportFragmentManager().popBackStack("lista_misiones", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void irMainCrearMision(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMainCrearMision.newInstance(),"fgm_main_crear_mision")
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }
}