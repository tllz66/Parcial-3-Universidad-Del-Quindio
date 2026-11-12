package co.edu.uniquindio.ecoruta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CentroEcoRuta {

    private String nombre;
    private List<Aportante> aportantes;
    private List<Entrega> entregas;

    public CentroEcoRuta(String nombre) {
        this.nombre = nombre;
        this.aportantes = new ArrayList<>();
        this.entregas = new ArrayList<>();
    }


    public boolean registrarAportante(Aportante a) {
        if (a == null) {
            return false;
        }
        if (buscarAportante(a.identificacion()) != null) {
            return false;
        }
        aportantes.add(a);
        return true;
    }


    public boolean crearEntrega(String cod, LocalDate fecha, String idAportante) {
        if (cod == null || cod.isBlank()) {
            return false;
        }
        if (buscarEntrega(cod) != null) {
            return false;
        }
        Aportante aportante = buscarAportante(idAportante);
        if (aportante == null) {
            return false;
        }
        entregas.add(new Entrega(cod, fecha, aportante));
        return true;
    }



}
