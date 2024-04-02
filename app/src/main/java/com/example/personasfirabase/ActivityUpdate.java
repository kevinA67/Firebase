package com.example.personasfirabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.personasfirabase.Config.ListAdapter;
import com.example.personasfirabase.Config.Personas;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityUpdate extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    EditText nombre, apellido, correo, fecha;
    Button guardarCambios;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        firebaseFirestore = FirebaseFirestore.getInstance();

        nombre=(EditText) findViewById(R.id.txtNombreActualiza);
        apellido=(EditText) findViewById(R.id.txtApellidoActualiza);
        correo=(EditText) findViewById(R.id.txtCorreoActualiza);
        fecha=(EditText) findViewById(R.id.txtFechaActualiza);
        guardarCambios=(Button) findViewById(R.id.btnActualizarDatos);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        nombre.setText(intent.getStringExtra("nombre"));
        apellido.setText(intent.getStringExtra("apellido"));
        correo.setText(intent.getStringExtra("correo"));
        fecha.setText(intent.getStringExtra("fecha"));

        guardarCambios.setOnClickListener(View ->{

            String nombre = this.nombre.getText().toString().trim();
            String apellido = this.apellido.getText().toString().trim();
            String correo = this.correo.getText().toString().trim();
            String fechaNacimiento = fecha.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || fechaNacimiento.isEmpty()) {
                Toast.makeText(this, "Llenar todos los campos.", Toast.LENGTH_LONG).show();
            } else {
                Personas acualizarPersona = new Personas(nombre, apellido, correo, fechaNacimiento);
                actualizarPersona(acualizarPersona);
            }
        });
    }

    private void actualizarPersona(Personas persona) {
            // Obtener la referencia al documento de la persona en Firestore
            DocumentReference personaRef = firebaseFirestore.collection("personas").document(id);

            // Actualizar los campos de la persona en Firestore
            personaRef.update(
                            "nombre", persona.getNombre(),
                            "apellido", persona.getApellido(),
                            "correo", persona.getCorreo(),
                            "fechanac", persona.getFechanac())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // La persona ha sido actualizada exitosamente
                            Toast.makeText(getApplicationContext(), "Persona actualizada exitosamente", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getApplicationContext(), ActivityLista.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Ocurrió un error al intentar actualizar la persona
                            Toast.makeText(getApplicationContext(), "Error al actualizar persona: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            // Puedes manejar el error de acuerdo a tus necesidades
                        }
                    });
    }

}