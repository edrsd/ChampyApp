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

import org.crbt.champyapp.databinding.FgmListaMapasBinding;
import org.crbt.champyapp.databinding.FgmListaMisionesBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmListaMapas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmListaMapas extends Fragment
//        implements SubListenerListRequest.ListaListener
{

    // vinculacion con el Layout
    FgmListaMapasBinding fgmBinding;
    //manejo de datos de la app
    MainViewModel mainViewModel;

    ArrayList<String> listaMapas;
    AdaptadorDeListas listMapsAdapter;

    int posActualMapaSeleccionado;
    String mapaSeleccionado;

    boolean btnCrearMisionActivado=false,btnEliminarActivado=false,btnMenuPrincipalActivado=false,btnListaMisionesActivado=false;

    SubListenerListRequest subListenerListRequest;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmListaMapas() {
        // Required empty public constructor
    }

    //Se crea una instancia del Fragment
    public static FgmListaMapas newInstance(){
        return new FgmListaMapas();
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmListaMapas.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmListaMapas newInstance(String param1, String param2) {
        FgmListaMapas fragment = new FgmListaMapas();
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
        fgmBinding= FgmListaMapasBinding.inflate(inflater,container,false);

        setupRecyclerViewMapsList();

        //Se obtiene la vista raiz
        return  fgmBinding.getRoot();
    }

    public void setupRecyclerViewMapsList(){
        fgmBinding.rvListaMapas.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext().getApplicationContext());
        fgmBinding.rvListaMapas.setLayoutManager(linearLayoutManager);

        listaMapas=new ArrayList<>();

        listaMapas.add("Cargando lista...");

        setupAdapterRecyclerViewList();

    }


    public void setupAdapterRecyclerViewList(){
        listMapsAdapter=new AdaptadorDeListas(listaMapas);
        fgmBinding.rvListaMapas.setAdapter(listMapsAdapter);

        listMapsAdapter.setOnClickListener(view1 -> {

            posActualMapaSeleccionado=fgmBinding.rvListaMapas.getChildLayoutPosition(view1);
            mapaSeleccionado=listaMapas.get(posActualMapaSeleccionado);

            fgmBinding.clContenedorBotones.setVisibility(View.VISIBLE);

//            fgmBinding.btnEliminarMapa.setTextColor(Color.WHITE);
//            fgmBinding.btnEliminarMapa.setBackgroundColor(getResources().getColor(R.color.pavisa_blue));
//            fgmBinding.btnEliminarMapa.setText("Eliminar");


            Toast.makeText(getContext(),"mapa "+mapaSeleccionado+"seleccionado",Toast.LENGTH_SHORT).show();


        });
    }



//-------------------------------------onActivityCreated----------------------------------------

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        activarListenerListaMapas();
        activarListenerRespuestaRobot();
        activarListenerSsh();



//        subListenerListRequest=new SubListenerListRequest(this);
//        mainViewModel.iniciarNodoListRequest( subListenerListRequest);

        mainViewModel.solicitarMapas();

        Toast.makeText(getContext(),"Solicitando lista de mapas con publicador_app=1",Toast.LENGTH_SHORT).show();

        fgmBinding.btnCrearMision.setOnClickListener(view -> {
            if(mapaSeleccionado!=null){
                btnCrearMisionActivado=true;
                desabilitarBotones();

                mainViewModel.sshCargarMapa(mapaSeleccionado);

                fgmBinding.btnCrearMision.setTextColor(Color.BLACK);
                fgmBinding.btnCrearMision.setBackgroundColor(Color.YELLOW);
                fgmBinding.btnCrearMision.setText("Creando...");

                Toast.makeText(getContext(),"Enviando comando ssh para cargar mapa",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(),"Debe seleccionar un mapa para crear misión",Toast.LENGTH_SHORT).show();
            }

        });

        fgmBinding.btnListaMisiones.setOnClickListener(view -> {
            btnListaMisionesActivado=true;
            desabilitarBotones();

            mainViewModel.sshCargarMapa(mapaSeleccionado);

            fgmBinding.btnListaMisiones.setTextColor(Color.BLACK);
            fgmBinding.btnListaMisiones.setBackgroundColor(Color.YELLOW);
            fgmBinding.btnListaMisiones.setText("Cargando...");

        });

        fgmBinding.btnEliminarMapa.setOnClickListener(view -> {
            if(mapaSeleccionado!=null){
                btnEliminarActivado=true;

                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setMessage("¿Desea eliminar el mapa: "+mapaSeleccionado+" ?").
                        setPositiveButton("Eliminar",(a,b)->{

                            desabilitarBotones();

                            fgmBinding.btnEliminarMapa.setTextColor(Color.BLACK);
                            fgmBinding.btnEliminarMapa.setBackgroundColor(Color.YELLOW);
                            fgmBinding.btnEliminarMapa.setText("Eliminando...");
                            mainViewModel.setNombreMapa(mapaSeleccionado);
                            mainViewModel.sshEliminarMapa();

                        }).
                        setNegativeButton("Cancelar",(a,b)->{
                            a.dismiss();
                        });
                dialog.show();
            }
            else{
                Toast.makeText(getContext(),"Debe seleccionar un mapa para eliminarlo",Toast.LENGTH_SHORT).show();
            }
        });

        fgmBinding.btnVolverMenuPrincipal.setOnClickListener(view -> {
            btnMenuPrincipalActivado=true;
            desabilitarBotones();

            irMenuPrincipal();
        });
    }

//----------------------------------------------------------------------------------------------

    private void desabilitarBotones(){
        fgmBinding.btnCrearMision.setEnabled(false);
        fgmBinding.btnListaMisiones.setEnabled(false);
        fgmBinding.btnEliminarMapa.setEnabled(false);
        fgmBinding.btnVolverMenuPrincipal.setEnabled(false);
    }

    private void habilitarBotones(){
        fgmBinding.btnCrearMision.setEnabled(true);
        fgmBinding.btnListaMisiones.setEnabled(true);
        fgmBinding.btnEliminarMapa.setEnabled(true);
        fgmBinding.btnVolverMenuPrincipal.setEnabled(true);
    }


    public void activarListenerListaMapas(){
        mainViewModel.getListaSolicitada().observe(getViewLifecycleOwner(),listaRecibida->{

            ArrayList<String> listaTmp=new ArrayList<>();
            listaTmp.addAll(Arrays.asList(listaRecibida.split(",")));

            if(!listaMapas.equals(listaTmp) && !listaTmp.equals("")){
                listaMapas.clear();
                listaMapas= (ArrayList<String>) listaTmp.clone();

                setupAdapterRecyclerViewList();
//                listMapsAdapter.notifyDataSetChanged();
//                fgmBinding.rvListaMapas.notify();
            }
            else if(listaTmp.get(0).equals("")){
                listaMapas.clear();
                listaMapas.add("No existen mapas que mostrar");
                setupAdapterRecyclerViewList();

            }

//            activarListenerRespuestaRobot();


            //Posiblemente habilitar vista de botones e imagen del mapa

            Toast.makeText(getContext(),"lista de mapas recibida",Toast.LENGTH_SHORT).show();

        });

        mainViewModel.iniciarEscuchaListaRobot();
    }

    public void activarListenerRespuestaRobot(){
        mainViewModel.getStatusRespuestaRobot().observe(getViewLifecycleOwner(),statusRespuestaRobot->{
            if(statusRespuestaRobot== RosRepository.RespuestaRobotStatus.DONE){

                if(btnCrearMisionActivado ){
                    btnCrearMisionActivado=false;

                    fgmBinding.btnCrearMision.setBackgroundColor(Color.GREEN);
                    fgmBinding.btnCrearMision.setText("¡Creada!");
                    irCrearMision();

                    mainViewModel.setNodoLocalizacionActivado();
                    Toast.makeText(getContext(),"Nodo de localización iniciado correctamente",Toast.LENGTH_SHORT).show();

                    mainViewModel.reiniciarBanderaRespuestaRobot();

//                    mainViewModel.terminarEscuchaListaRobot();//*
                }
                else if(btnListaMisionesActivado){
                    btnListaMisionesActivado=false;

                    fgmBinding.btnListaMisiones.setBackgroundColor(Color.GREEN);
                    fgmBinding.btnListaMisiones.setText("¡Listo!");
                    irListaMisiones();

                    mainViewModel.setNodoLocalizacionActivado();
                    Toast.makeText(getContext(),"Nodo de localización iniciado correctamente",Toast.LENGTH_SHORT).show();

                    mainViewModel.reiniciarBanderaRespuestaRobot();
                }

//                else{
//                    mainViewModel.terminarEscuchaListaRobot();
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if(listaMapas.size()==0){
//                        mainViewModel.reiniciarBanderaRespuestaRobot();
//                        mainViewModel.getListaSolicitada().removeObservers(getViewLifecycleOwner());
//                        activarListenerListaMapas();
//                        mainViewModel.solicitarMapas();
//                        Toast.makeText(getContext(),"Solicitando lista de mapa nuevamente",Toast.LENGTH_SHORT).show();
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
        mainViewModel.iniciarEscuchaRespuestaRobot();
    }

    public void activarListenerSsh(){
        //Se espera la respuesta del estatus de los comandos mandados enviados por ssh
        mainViewModel.seHanCompletadoLosComandos().observe(getViewLifecycleOwner(),sshCommandsStatus->{

            //Si el estatus es DONE, entonces...
            if(sshCommandsStatus== SshRepositoryImpl.SshCommandsStatus.DONE){

                if(btnEliminarActivado){
                    btnEliminarActivado=false;

                    //habilitarr botones
                    habilitarBotones();

                    fgmBinding.btnEliminarMapa.setTextColor(Color.WHITE);
                    fgmBinding.btnEliminarMapa.setBackgroundColor(getResources().getColor(R.color.pavisa_blue));
                    fgmBinding.btnEliminarMapa.setText("Eliminar");

                    fgmBinding.clContenedorBotones.setVisibility(View.GONE);
                    //Se actualiza la lista de misiones
                    listaMapas.remove(posActualMapaSeleccionado);
                    listMapsAdapter.notifyItemRemoved(posActualMapaSeleccionado);
                    Toast.makeText(getContext(),"SHH: mapa eliminado",Toast.LENGTH_SHORT).show();
                    mainViewModel.reiniciarBanderaComandosRealizados();

                }
                else if(btnCrearMisionActivado || btnListaMisionesActivado){
                    mainViewModel.solicitarIniciarLocalizacion();
                    Toast.makeText(getContext(),"SSH: Mapa cargado; solicitando inicio de localizacion con publicador_app=8",Toast.LENGTH_SHORT).show();
                    mainViewModel.reiniciarBanderaComandosRealizados();
                }

            }
            else if(sshCommandsStatus == SshRepositoryImpl.SshCommandsStatus.FAILED){
                Toast.makeText(getContext(),"SSH: Tiempo expirado",Toast.LENGTH_SHORT).show();


//                //Se muestra una ventana para conectar de nuevo cuando el tiempo de conexión expira
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

    public void irCrearMision(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmMainCrearMision.newInstance(),"fgm_main_crear_mision")
                .commit();
    }

    public void irListaMisiones(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,FgmListaMisiones.newInstance(),"fgm_lista_misiones")
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

//    @Override
//    public void unaNuevaLista(String respuestaRecibida) {
//        ArrayList<String> listaTmp=new ArrayList<>();
//        listaTmp.addAll(Arrays.asList(respuestaRecibida.split(",")));
//
//        if(!listaMapas.equals(listaTmp)){
//            listaMapas.clear();
//            listaMapas= (ArrayList<String>) listaTmp.clone();
//
//            setupAdapterRecyclerViewList();
////                listMapsAdapter.notifyDataSetChanged();
////                fgmBinding.rvListaMapas.notify();
//        }
//        else if(listaTmp.get(0).equals("")){
//            listaMapas.add("No existen mapas que mostrar");
//            listMapsAdapter.notifyDataSetChanged();
//
//        }
//
//    }
}