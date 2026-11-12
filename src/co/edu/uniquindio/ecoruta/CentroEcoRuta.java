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


    public boolean agregarLote(String cod, Lote l) {
        Entrega entrega = buscarEntrega(cod);
        if (entrega == null) {
            return false;
        }
        return entrega.agregarLote(l);
    }


    public boolean aceptarEntrega(String cod) {
        Entrega entrega = buscarEntrega(cod);
        if (entrega == null) {
            return false;
        }
        return entrega.aceptar();
    }


    public Comprobante consultarComprobante(String cod) {
        Entrega entrega = buscarEntrega(cod);
        if (entrega == null) {
            return null;
        }
        return entrega.getComprobante();
    }


    public List<String> consultarEtiquetas(String cod) {
        Entrega entrega = buscarEntrega(cod);
        if (entrega == null) {
            return null;
        }
        if (entrega.estaAbierta()) {
            return null;
        }
        return entrega.getEtiquetas();
    }


    public double calcularValorAcumulado(String id) {
        double acumulado = 0;
        for (Entrega entrega : entregas) {
            Comprobante comprobante = entrega.getComprobante();
            if (comprobante != null && comprobante.idAportante().equals(id)) {
                acumulado = acumulado + comprobante.valorTotal();
            }
        }
        return acumulado;
    }

    public Entrega buscarEntrega(String cod) {
        Entrega encontrada = null;
        for (Entrega entrega : entregas) {
            if (entrega.getCodigo().equalsIgnoreCase(cod)) {
                encontrada = entrega;
            }
        }
        return encontrada;
    }

    public Aportante buscarAportante(String id) {
        Aportante encontrado = null;
        for (Aportante aportante : aportantes) {
            if (aportante.identificacion().equals(id)) {
                encontrado = aportante;
            }
        }
        return encontrado;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Aportante> getAportantes() {
        return Collections.unmodifiableList(aportantes);
    }

    public List<Entrega> getEntregas() {
        return Collections.unmodifiableList(entregas);
    }
}
