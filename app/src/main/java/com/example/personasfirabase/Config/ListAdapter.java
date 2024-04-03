package com.example.personasfirabase.Config;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.personasfirabase.R;

import java.io.File;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<Personas> datos;
    private LayoutInflater inflater;
    private Context context;
    public static int selectedItem = -1;

    public ListAdapter(List<Personas> itemList, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.datos = itemList;
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View view = inflater.inflate(R.layout.disenio, null);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position) {
        holder.bindData(datos.get(position));

        //PARA QUE EL SELECTOR FUNCIONE
        final int currentPosition = position;
        holder.itemView.setSelected(position == selectedItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Actualizar el índice del elemento seleccionado
                selectedItem = currentPosition;

                // Notificar al adaptador de los cambios
                notifyDataSetChanged();
            }
        });
    }

    public void setItems(List<Personas> items) {
        datos = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nombre, correo, fechaNac;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView2);
            nombre = (TextView) itemView.findViewById(R.id.nombre);
            correo=(TextView) itemView.findViewById(R.id.txtCorreo);
            fechaNac=(TextView) itemView.findViewById(R.id.txtFecha);
        }

        void bindData(final Personas personas) {
            nombre.setText(personas.getNombre()+" "+personas.getApellido());
            correo.setText(personas.getCorreo());
            fechaNac.setText(personas.getFechanac());
            File foto=new File(personas.getFoto());
            imageView.setImageURI(Uri.fromFile(foto));
        }
    }

    public void setFilteredList(List<Personas> filteredList) {
        this.datos = filteredList;
        notifyDataSetChanged();
    }
}

