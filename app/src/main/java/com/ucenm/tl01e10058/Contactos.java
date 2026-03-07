package com.ucenm.tl01e10058;

/**
 * Clase modelo que representa un Contacto.
 * Se utiliza para transportar los datos del contacto entre la base de datos y la interfaz de usuario.
 */
public class Contactos {
    // Atributos del contacto
    private Integer id;
    private String pais;
    private String nombre;
    private String telefono;
    private String nota;
    private byte[] imagen; // La imagen se almacena como un arreglo de bytes (BLOB en la BD)

    // Constructor vacío requerido por algunos frameworks o para inicialización manual
    public Contactos() {}

    /**
     * Constructor con parámetros para inicializar todos los campos del contacto.
     */
    public Contactos(Integer id, String nombre, String telefono, String nota, String pais, byte[] imagen) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.nota = nota;
        this.pais = pais;
        this.imagen = imagen;
    }

    // Métodos Getter y Setter para acceder y modificar los atributos de forma segura

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }

    public byte[] getImagen() { return imagen; }
    public void setImagen(byte[] imagen) { this.imagen = imagen; }
}
