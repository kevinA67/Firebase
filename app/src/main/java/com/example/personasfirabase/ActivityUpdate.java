package com.example.personasfirabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.personasfirabase.Config.ListAdapter;
import com.example.personasfirabase.Config.Personas;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityUpdate extends AppCompatActivity {

    static final int REQUEST_IMAGE = 101;
    static final int ACCESS_CAMERA = 201;
    FirebaseFirestore firebaseFirestore;
    EditText nombre, apellido, correo, fecha;
    Button guardarCambios, tomarFoto;
    String id;
    String currentPhotoPath;
    ImageView imageView;
    File foto, foto2;

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
        tomarFoto=(Button) findViewById(R.id.btnTomarFoto);
        imageView = (ImageView) findViewById(R.id.imageView);


        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        nombre.setText(intent.getStringExtra("nombre"));
        apellido.setText(intent.getStringExtra("apellido"));
        correo.setText(intent.getStringExtra("correo"));
        fecha.setText(intent.getStringExtra("fecha"));
        foto2=new File(intent.getStringExtra("foto"));
        imageView.setImageURI(Uri.fromFile(foto2));

        guardarCambios.setOnClickListener(View ->{

            String nombre = this.nombre.getText().toString().trim();
            String apellido = this.apellido.getText().toString().trim();
            String correo = this.correo.getText().toString().trim();
            String fechaNacimiento = fecha.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || fechaNacimiento.isEmpty()) {
                Toast.makeText(this, "Llenar todos los campos.", Toast.LENGTH_LONG).show();
            } else {

                if(foto==null){
                    Personas acualizarPersona = new Personas(nombre, apellido, correo, fechaNacimiento, foto2.toString());
                    actualizarPersona(acualizarPersona);

                }else{
                    Personas acualizarPersona = new Personas(nombre, apellido, correo, fechaNacimiento, foto.toString());
                    actualizarPersona(acualizarPersona);

                }
            }
        });

        tomarFoto.setOnClickListener(View ->{
            PermisosCamara();
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
                            "fechanac", persona.getFechanac(),
                            "foto", persona.getFoto())
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




    private void PermisosCamara() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, ACCESS_CAMERA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.personasfirabase.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getApplicationContext(), "Se necesita permiso de la camara.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            try {
                foto = new File(currentPhotoPath);
                imageView.setImageURI(Uri.fromFile(foto));
            } catch (Exception ex) {
                ex.toString();
            }
        }
    }
}