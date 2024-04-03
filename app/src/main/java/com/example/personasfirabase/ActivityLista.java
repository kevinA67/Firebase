package com.example.personasfirabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.personasfirabase.Config.ListAdapter;
import com.example.personasfirabase.Config.Personas;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityLista extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;

    List<Personas> listPersonas;
    ListAdapter listAdapter;
    SearchView searchView;

    Button eliminar, actualizar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        firebaseFirestore = FirebaseFirestore.getInstance();

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.clearFocus();
        eliminar = (Button) findViewById(R.id.btnEliminar);
        actualizar=(Button) findViewById(R.id.btnActualizar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        eliminar.setOnClickListener(View ->{
            if (ListAdapter.getSelectedItem() != -1) {
                // Mostrar un diálogo de confirmación de eliminación
                alertaEliminar();
            } else {
                // Mostrar un mensaje si no se ha seleccionado ningún contacto
                Toast.makeText(ActivityLista.this, "Selecciona un contacto primero", Toast.LENGTH_SHORT).show();
            }
        });

        actualizar.setOnClickListener(View ->{
            Intent intent=new Intent(getApplicationContext(),ActivityUpdate.class);
            // Obtener el contacto seleccionado
            int selectedItemIndex = ListAdapter.getSelectedItem();
            if (selectedItemIndex != -1) {
                Personas personas = listPersonas.get(selectedItemIndex);
                intent.putExtra("id", personas.getId());
                intent.putExtra("nombre", personas.getNombre());
                intent.putExtra("apellido", personas.getApellido());
                intent.putExtra("correo", personas.getCorreo());
                intent.putExtra("fecha", personas.getFechanac());
                intent.putExtra("foto", personas.getFoto());
            }
            startActivity(intent);
            finish();
        });

        ObtenerDatos();
    }

    private void ObtenerDatos() {
        firebaseFirestore.collection("personas")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        listPersonas = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            try {
                                Personas persona = new Personas();
                                //Personas persona = documentSnapshot.toObject(Personas.class);
                                // Obtener el ID del documento
                                persona.setId(documentSnapshot.getId());
                                persona.setNombre(documentSnapshot.get("nombre").toString());
                                persona.setApellido(documentSnapshot.get("apellido").toString());
                                persona.setCorreo(documentSnapshot.get("correo").toString());
                                persona.setFechanac(documentSnapshot.get("fechanac").toString());
                                persona.setFoto(documentSnapshot.get("foto").toString());

                                listPersonas.add(persona);
                                //listPersonas.add(new Personas(persona.getId(), persona.getNombres(), persona.getApellidos(), persona.getCorreo(), persona.getFechanac()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        llenarLista();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar errores
                    }
                });
    }

    private void llenarLista() {
//        List<Personas> personas=new ArrayList<>();
//        personas.add(new Personas("Kevin"));
//        personas.add(new Personas("Alexis"));

        listAdapter = new ListAdapter(listPersonas, this);
        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }

    private void filter(String text) {
        List<Personas> filteredList = new ArrayList<>();
        for (Personas personas : listPersonas) {
            String nombre = personas.getNombre() + " " + personas.getApellido();
            if (nombre.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(personas);
            }
        }

        if (!filteredList.isEmpty()) {
            listAdapter.setFilteredList(filteredList);
        }
    }

    private void alertaEliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLista.this);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Desea eliminar los datos de la persona seleccionada?");

        // Agregar botón de actualizar
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener el contacto seleccionado
                int selectedItemIndex = ListAdapter.getSelectedItem();
                if (selectedItemIndex != -1) {
                    Personas personas = listPersonas.get(selectedItemIndex);
                    eliminarPersona(personas);
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario cancela la eliminación, no hacer nada
            }
        });

        builder.show();
    }

    private void eliminarPersona(Personas persona) {
        // Eliminar la persona de la colección "personas" en Firestore
        firebaseFirestore.collection("personas")
                .document(persona.getId()) // Utilizamos el ID de la persona para identificar el documento a eliminar
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La persona ha sido eliminada exitosamente
                        Toast.makeText(getApplicationContext(), "Persona eliminada exitosamente", Toast.LENGTH_SHORT).show();
                        ObtenerDatos();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al intentar eliminar la persona
                        Toast.makeText(getApplicationContext(), "Error al eliminar persona: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        // Puedes manejar el error de acuerdo a tus necesidades
                    }
                });
    }

}