package co.edu.uniquindio.ecoruta;

public class ResiduoElectronico extends Lote implements Etiquetable {

    private int equiposCompletos;

    public ResiduoElectronico(String codigo, String descripcion, int pesoKg, int equiposCompletos) {
        super(codigo, descripcion, pesoKg);
        this.equiposCompletos = equiposCompletos;
    }



    @Override
    public double calcularValor() {
        return pesoKg * 1400 + equiposCompletos * 800;
    }


    @Override
    public String generarEtiqueta() {
        return "Lote: " + codigo + " | Equipos completos: " + equiposCompletos;
    }

    public int getEquiposCompletos() {
        return equiposCompletos;
    }
}
