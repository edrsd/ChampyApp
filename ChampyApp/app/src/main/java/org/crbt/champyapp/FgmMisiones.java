package org.crbt.champyapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.crbt.champyapp.databinding.FgmCrearMapaBinding;
import org.crbt.champyapp.databinding.FgmMainMenuBinding;
import org.crbt.champyapp.databinding.FgmMisionesBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FgmMisiones#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FgmMisiones extends Fragment {

    FgmMisionesBinding fgmBinding;
    MainViewModel mainViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FgmMisiones() {
        // Required empty public constructor
    }

    public static FgmMisiones newInstances(){
        return new FgmMisiones();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FgmMisiones.
     */
    // TODO: Rename and change types and number of parameters
    public static FgmMisiones newInstance(String param1, String param2) {
        FgmMisiones fragment = new FgmMisiones();
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
        fgmBinding= FgmMisionesBinding.inflate(inflater,container,false);
        return  fgmBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainViewModel=new ViewModelProvider(this).get(MainViewModel.class);

        fgmBinding.btnMenuCrearMision.setOnClickListener(view -> {

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fgmBinding = null;
    }
}