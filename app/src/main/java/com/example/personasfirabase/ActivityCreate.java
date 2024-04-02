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

import com.example.personasfirabase.Config.Personas;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityCreate extends AppCompatActivity {

    static final int REQUEST_IMAGE = 101;
    static final int ACCESS_CAMERA = 201;
    String currentPhotoPath;
    Button contactos, salvarContacto, tomarFoto;
    EditText nombre, apellido, correo, fecha;
    ImageView imageView;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        firebaseFirestore = FirebaseFirestore.getInstance();

        contactos = (Button) findViewById(R.id.btnContactos);
        tomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        salvarContacto = (Button) findViewById(R.id.btnSalvar);
        imageView = (ImageView) findViewById(R.id.imageView);
        nombre = (EditText) findViewById(R.id.txtNombre);
        apellido = (EditText) findViewById(R.id.txtApellido);
        correo = (EditText) findViewById(R.id.txtCorreo);
        fecha = (EditText) findViewById(R.id.txtFecha);


        contactos.setOnClickListener(View -> {
            Intent intent = new Intent(getApplicationContext(), ActivityLista.class);
            startActivity(intent);
        });

        tomarFoto.setOnClickListener(View -> {
            PermisosCamara();
        });

        salvarContacto.setOnClickListener(View -> {
            String nombre = this.nombre.getText().toString().trim();
            String apellido = this.apellido.getText().toString().trim();
            String correo = this.correo.getText().toString().trim();
            String fechaNacimiento = fecha.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || fechaNacimiento.isEmpty()) {
                Toast.makeText(this, "Llenar todos los campos.", Toast.LENGTH_LONG).show();
            } else {
                Personas nuevaPersona = new Personas(nombre, apellido, correo, fechaNacimiento);
                crear(nuevaPersona);
            }
        });
    }

    private void crear(Personas nuevaPersona) {
        // Agregar el objeto nuevaPersona a la colección "personas" en Firestore
        firebaseFirestore.collection("personas")
                .add(nuevaPersona)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // El documento ha sido creado exitosamente
                        Toast.makeText(getApplicationContext(), "Persona creada exitosamente", Toast.LENGTH_SHORT).show();
                        // Puedes agregar aquí cualquier otra lógica que necesites después de crear la persona
                        limpiarCampos();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al intentar crear el documento
                        Toast.makeText(getApplicationContext(), "Error al crear persona: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        // Puedes manejar el error de acuerdo a tus necesidades
                    }
                });
    }

    private void limpiarCampos() {
        nombre.setText("");
        apellido.setText("");
        correo.setText("");
        fecha.setText("");
        nombre.setFocusableInTouchMode(true);
        nombre.requestFocus();
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
                File foto = new File(currentPhotoPath);
                imageView.setImageURI(Uri.fromFile(foto));
            } catch (Exception ex) {
                ex.toString();
            }
        }
    }
}