package co.edu.uniquindio.ecoruta;

import co.edu.uniquindio.ecoruta.enums.TipoMetal;


public class Metal extends Lote implements Etiquetable {

    private TipoMetal tipo;

    public Metal(String codigo, String descripcion, int pesoKg, TipoMetal tipo) {
        super(codigo, descripcion, pesoKg);
        this.tipo = tipo;
    }


    @Override
    public double calcularValor() {
        double valor;
        if (tipo == TipoMetal.ALUMINIO) {
            valor = pesoKg * 2200;
        } else {
            valor = pesoKg * 900;
        }
        return valor;
    }


    @Override
    public String generarEtiqueta() {
        return "Lote: " + codigo + " | Metal: " + tipo;
    }

    public TipoMetal getTipo() {
        return tipo;
    }
}
