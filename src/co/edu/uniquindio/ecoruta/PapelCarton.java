package co.edu.uniquindio.ecoruta;

public class PapelCarton extends Lote {

    private boolean clasificado;

    public PapelCarton(String codigo, String descripcion, int pesoKg, boolean clasificado) {
        super(codigo, descripcion, pesoKg); // llama al constructor de Lote
        this.clasificado = clasificado;
    }


    @Override
    public double calcularValor() {
        double valor = pesoKg * 500;
        if (clasificado) {
            valor = valor + pesoKg * 100;
        }
        return valor;
    }

    public boolean isClasificado() {
        return clasificado;
    }
}
