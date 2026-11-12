package co.edu.uniquindio.ecoruta;

public abstract class Lote {

    protected String codigo;
    protected String descripcion;
    protected int pesoKg;

    public Lote(String codigo, String descripcion, int pesoKg) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.pesoKg = pesoKg;
    }


    public abstract double calcularValor();

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPesoKg() {
        return pesoKg;
    }
}
