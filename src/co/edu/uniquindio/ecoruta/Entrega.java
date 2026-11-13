package co.edu.uniquindio.ecoruta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Entrega {

    private String codigo;
    private LocalDate fecha;
    private Aportante aportante;
    private EstadoEntrega estado;
    private List<Lote> lotes;
    private Comprobante comprobante;

    public Entrega(String codigo, LocalDate fecha, Aportante aportante) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.aportante = aportante;
        this.estado = EstadoEntrega.ABIERTA; // toda entrega nace ABIERTA
        this.lotes = new ArrayList<>();      // y sin lotes
        this.comprobante = null;             // el comprobante nace al aceptarla
    }

    /**
     * Agrega un lote a la entrega. Se rechaza (false) si:
     * - el lote es null,
     * - la entrega ya fue aceptada (está cerrada),
     * - el peso no es un entero positivo,
     * - es un residuo electrónico con equipos completos negativos,
     * - ya existe un lote con ese código DENTRO de esta entrega.
     */
    public boolean agregarLote(Lote l) {
        if (l == null) {
            return false;
        }
        if (!estaAbierta()) {
            return false;
        }
        if (l.getPesoKg() <= 0) {
            return false;
        }
        if (l instanceof ResiduoElectronico) {
            ResiduoElectronico residuo = (ResiduoElectronico) l;
            if (residuo.getEquiposCompletos() < 0) {
                return false;
            }
        }
        if (existeLote(l.getCodigo())) {
            return false;
        }
        lotes.add(l);
        return true;
    }

    private boolean existeLote(String codigoLote) {
        boolean existe = false;
        for (Lote lote : lotes) {
            if (lote.getCodigo().equalsIgnoreCase(codigoLote)) {
                existe = true;
            }
        }
        return existe;
    }

    /**
     * Acepta la entrega. Se rechaza (false) si ya fue aceptada o si está vacía.
     * Si es válida: calcula el total, genera el comprobante y pasa a ACEPTADA.
     * Si se rechaza, no se modifica nada.
     */
    public boolean aceptar() {
        if (!estaAbierta()) {
            return false;
        }
        if (lotes.isEmpty()) {
            return false;
        }
        int pesoTotal = calcularPesoTotal();
        double valorTotal = calcularValorTotal();
        comprobante = new Comprobante(codigo, aportante.identificacion(), pesoTotal, valorTotal);
        estado = EstadoEntrega.ACEPTADA;
        return true;
    }

    public int calcularPesoTotal() {
        int total = 0;
        for (Lote lote : lotes) {
            total = total + lote.getPesoKg();
        }
        return total;
    }

    /**
     * POLIMORFISMO: se llama calcularValor() en cada lote sin preguntar de
     * qué tipo es; Java ejecuta la fórmula de la clase hija correspondiente.
     */
    public double calcularValorTotal() {
        double total = 0;
        for (Lote lote : lotes) {
            total = total + lote.calcularValor();
        }
        return total;
    }

    /**
     * Devuelve las etiquetas de los lotes que pueden generarla
     * (Metal y ResiduoElectronico). PapelCarton no es Etiquetable.
     */
    public List<String> getEtiquetas() {
        List<String> etiquetas = new ArrayList<>();
        for (Lote lote : lotes) {
            if (lote instanceof Etiquetable) {
                Etiquetable etiquetable = (Etiquetable) lote;
                etiquetas.add(etiquetable.generarEtiqueta());
            }
        }
        return etiquetas;
    }

    public boolean estaAbierta() {
        return estado == EstadoEntrega.ABIERTA;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Aportante getAportante() {
        return aportante;
    }

    public EstadoEntrega getEstado() {
        return estado;
    }

    // Lista de solo lectura: los lotes solo se agregan con agregarLote()
    public List<Lote> getLotes() {
        return Collections.unmodifiableList(lotes);
    }

    public Comprobante getComprobante() {
        return comprobante;
    }
}
