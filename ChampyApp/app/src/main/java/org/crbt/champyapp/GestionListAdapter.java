package org.crbt.champyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GestionListAdapter extends RecyclerView.Adapter<GestionListAdapter.ListViewHolder> {

    ArrayList<String> elementosLista;
    Context context;

    public GestionListAdapter(Context context, ArrayList<String> elementos){
        this.context=context;
        this.elementosLista=elementos;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.formato_view_listas, null, false);
        ListViewHolder viewHolder = new ListViewHolder(vista);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.tvNombreElemento.setText(elementosLista.get(position));

        holder.btnAbrirElemento.setOnClickListener(view -> {
            Toast.makeText(context,"Abriendo elemento "+holder.tvNombreElemento.getText().toString(),Toast.LENGTH_SHORT).show();
        });
        holder.btnEliminarElemento.setOnClickListener(view -> {
            Toast.makeText(context,"Eliminando elemento "+holder.tvNombreElemento.getText().toString(),Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return elementosLista.size();
    }

    static public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreElemento;
        Button btnAbrirElemento,btnEliminarElemento;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreElemento=itemView.findViewById(R.id.tv_mission_spot);



        }
    }
}
