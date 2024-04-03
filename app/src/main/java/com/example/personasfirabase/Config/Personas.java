package com.example.personasfirabase.Config;

public class Personas {
    private String id;
    private String nombre;
    private String apellido;
    private String correo;
    private String fechanac;
    private String foto;

    public Personas(){

    }

//    public Personas(String id, String nombres, String apellidos, String telefono, String fechanac, String foto) {
//        this.id = id;
//        this.nombre = nombres;
//        this.apellido = apellidos;
//        this.correo = telefono;
//        this.fechanac = fechanac;
//        this.foto = foto;
//    }


    public Personas(String nombre, String apellido, String correo, String fechanac, String foto) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.fechanac = fechanac;
        this.foto = foto;
    }

    public Personas(String nombres, String apellidos, String correo, String fechanac) {
        this.nombre = nombres;
        this.apellido = apellidos;
        this.correo = correo;
        this.fechanac = fechanac;
    }

    public Personas(String nombres){
        this.nombre =nombres;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFechanac() {
        return fechanac;
    }

    public void setFechanac(String fechanac) {
        this.fechanac = fechanac;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
