package co.edu.uniquindio.ecoruta;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

// Las 5 pruebas funcionales del enunciado.
// Cada verificación imprime: qué se prueba, el resultado esperado y el obtenido.
public class PruebasFuncionales {

    private static int totalVerificaciones = 0;
    private static int verificacionesCorrectas = 0;

    public static void main(String[] args) {

        // Escenario inicial: el centro arranca con un aportante registrado
        CentroEcoRuta centro = new CentroEcoRuta("EcoRuta");
        centro.registrarAportante(new Aportante("1001", "Ana Ruiz", "ana@mail.com"));
        LocalDate fecha = LocalDate.of(2026, 11, 12);

        prueba1RegistroEntrega(centro, fecha);
        prueba2ValidacionLotes(centro, fecha);
        prueba3RechazoEntregaVacia(centro, fecha);
        prueba4AceptacionYCalculo(centro, fecha);
        prueba5ConsultaEtiquetas(centro);
        pruebasAdicionales(centro, fecha);

        System.out.println();
        System.out.println("RESULTADO FINAL: " + verificacionesCorrectas + " de "
                + totalVerificaciones + " verificaciones correctas");
    }

    // ---------------------------------------------------------------
    // PRUEBA 1: Registro de una entrega
    // ---------------------------------------------------------------
    private static void prueba1RegistroEntrega(CentroEcoRuta centro, LocalDate fecha) {
        titulo("PRUEBA 1: Registro de una entrega");
        entrada("Crear E01 para el aportante 1001; luego otra E01; luego E99 con aportante 9999");

        boolean creada = centro.crearEntrega("E01", fecha, "1001");
        Entrega e01 = centro.buscarEntrega("E01");
        verificar("Crear E01", true, creada);
        verificar("Estado de E01", EstadoEntrega.ABIERTA, e01.getEstado());
        verificar("Cantidad de lotes de E01", 0, e01.getLotes().size());
        verificar("Comprobante de E01", null, centro.consultarComprobante("E01"));

        verificar("Crear otra entrega con código E01 (repetido)", false,
                centro.crearEntrega("E01", fecha, "1001"));
        verificar("Crear E99 con aportante desconocido 9999", false,
                centro.crearEntrega("E99", fecha, "9999"));
        verificar("E01 se conserva (total de entregas)", 1, centro.getEntregas().size());
        verificar("E99 no fue creada", null, centro.buscarEntrega("E99"));
    }

    // ---------------------------------------------------------------
    // PRUEBA 2: Validación de lotes
    // ---------------------------------------------------------------
    private static void prueba2ValidacionLotes(CentroEcoRuta centro, LocalDate fecha) {
        titulo("PRUEBA 2: Validación de lotes");
        entrada("En E02 (abierta): agregar L01 con 5 kg; otro L01; y L02 con 0 kg");

        centro.crearEntrega("E02", fecha, "1001");
        Entrega e02 = centro.buscarEntrega("E02");

        verificar("Agregar L01 con 5 kg", true,
                centro.agregarLote("E02", new PapelCarton("L01", "Cajas de cartón", 5, false)));
        verificar("Agregar otro lote con código L01 (repetido)", false,
                centro.agregarLote("E02", new PapelCarton("L01", "Otro lote", 3, false)));
        verificar("Agregar L02 con 0 kg (peso inválido)", false,
                centro.agregarLote("E02", new PapelCarton("L02", "Sin peso", 0, false)));
        verificar("Cantidad de lotes en E02", 1, e02.getLotes().size());
        verificar("El único lote es L01", "L01", e02.getLotes().get(0).getCodigo());
    }

    // ---------------------------------------------------------------
    // PRUEBA 3: Rechazo de una entrega vacía
    // ---------------------------------------------------------------
    private static void prueba3RechazoEntregaVacia(CentroEcoRuta centro, LocalDate fecha) {
        titulo("PRUEBA 3: Rechazo de una entrega vacía");
        entrada("Crear E03 sin lotes e intentar aceptarla");

        centro.crearEntrega("E03", fecha, "1001");
        Entrega e03 = centro.buscarEntrega("E03");

        verificar("Aceptar E03 (vacía)", false, centro.aceptarEntrega("E03"));
        verificar("Estado de E03", EstadoEntrega.ABIERTA, e03.getEstado());
        verificar("Comprobante de E03", null, centro.consultarComprobante("E03"));
    }

    // ---------------------------------------------------------------
    // PRUEBA 4: Aceptación y cálculo con los tres tipos de lote
    // ---------------------------------------------------------------
    private static void prueba4AceptacionYCalculo(CentroEcoRuta centro, LocalDate fecha) {
        titulo("PRUEBA 4: Aceptación y cálculo con los tres tipos de lote");
        entrada("E04: 10 kg papel/cartón clasificado, 5 kg aluminio, 4 kg electrónicos con 2 equipos");

        centro.crearEntrega("E04", fecha, "1001");
        Entrega e04 = centro.buscarEntrega("E04");

        PapelCarton papel = new PapelCarton("L01", "Cartón clasificado", 10, true);
        Metal aluminio = new Metal("L02", "Latas de aluminio", 5, TipoMetal.ALUMINIO);
        ResiduoElectronico residuo = new ResiduoElectronico("L03", "Computadores", 4, 2);

        centro.agregarLote("E04", papel);
        centro.agregarLote("E04", aluminio);
        centro.agregarLote("E04", residuo);

        verificar("Valor del lote de papel y cartón", 6000.0, papel.calcularValor());
        verificar("Valor del lote de aluminio", 11000.0, aluminio.calcularValor());
        verificar("Valor del lote electrónico", 7200.0, residuo.calcularValor());

        verificar("PapelCarton no es Etiquetable", false, papel instanceof Etiquetable);
        verificar("Metal es Etiquetable", true, aluminio instanceof Etiquetable);
        verificar("ResiduoElectronico es Etiquetable", true, residuo instanceof Etiquetable);

        verificar("Aceptar E04", true, centro.aceptarEntrega("E04"));
        verificar("Estado de E04", EstadoEntrega.ACEPTADA, e04.getEstado());

        Comprobante comprobante = centro.consultarComprobante("E04");
        verificar("Comprobante: código de entrega", "E04", comprobante.codigoEntrega());
        verificar("Comprobante: identificación del aportante", "1001", comprobante.idAportante());
        verificar("Comprobante: peso total (kg)", 19, comprobante.pesoTotal());
        verificar("Comprobante: valor total ($)", 24200.0, comprobante.valorTotal());
    }

    // ---------------------------------------------------------------
    // PRUEBA 5: Consulta de etiquetas
    // ---------------------------------------------------------------
    private static void prueba5ConsultaEtiquetas(CentroEcoRuta centro) {
        titulo("PRUEBA 5: Consulta de etiquetas");
        entrada("Consultar las etiquetas de E04 (la entrega aceptada de la prueba 4)");

        List<String> etiquetas = centro.consultarEtiquetas("E04");
        System.out.println("     Etiquetas obtenidas: " + etiquetas);

        verificar("Cantidad de etiquetas", 2, etiquetas.size());
        // Las etiquetas salen en el orden en que se agregaron los lotes: metal y luego electrónico
        verificar("Etiqueta de metal incluye el código L02", true, etiquetas.get(0).contains("L02"));
        verificar("Etiqueta de metal incluye el tipo ALUMINIO", true, etiquetas.get(0).contains("ALUMINIO"));
        verificar("Etiqueta electrónica incluye el código L03", true, etiquetas.get(1).contains("L03"));
        verificar("Etiqueta electrónica incluye 2 equipos completos", true,
                etiquetas.get(1).contains("Equipos completos: 2"));
    }

    // ---------------------------------------------------------------
    // PRUEBAS ADICIONALES: otras reglas del enunciado
    // ---------------------------------------------------------------
    private static void pruebasAdicionales(CentroEcoRuta centro, LocalDate fecha) {
        titulo("PRUEBAS ADICIONALES");
        entrada("Reglas del enunciado que las 5 pruebas no cubren");

        verificar("Aceptar E04 por segunda vez", false, centro.aceptarEntrega("E04"));
        verificar("Agregar lote a E04 (ya aceptada, cerrada)", false,
                centro.agregarLote("E04", new PapelCarton("L09", "Tarde", 2, false)));
        verificar("Aceptar una entrega que no existe (E77)", false, centro.aceptarEntrega("E77"));
        verificar("Etiquetas de E02 (aún abierta)", null, centro.consultarEtiquetas("E02"));
        verificar("Valor acumulado del aportante 1001 (solo E04 aceptada)", 24200.0,
                centro.calcularValorAcumulado("1001"));

        centro.crearEntrega("E05", fecha, "1001");
        verificar("Agregar residuo electrónico con -3 equipos (inválido)", false,
                centro.agregarLote("E05", new ResiduoElectronico("L01", "Inválido", 4, -3)));
        verificar("Agregar residuo electrónico con 0 equipos (válido)", true,
                centro.agregarLote("E05", new ResiduoElectronico("L02", "Sin equipos", 4, 0)));

        centro.registrarAportante(new Aportante("1002", "Carlos Pérez", "carlos@mail.com"));
        verificar("Valor acumulado de un aportante sin comprobantes", 0.0,
                centro.calcularValorAcumulado("1002"));
    }

    // ---------------------------------------------------------------
    // Métodos auxiliares de presentación
    // ---------------------------------------------------------------
    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    private static void entrada(String texto) {
        System.out.println("   Entrada: " + texto);
    }

    private static void verificar(String que, Object esperado, Object obtenido) {
        totalVerificaciones++;
        boolean correcto = Objects.equals(esperado, obtenido);
        if (correcto) {
            verificacionesCorrectas++;
        }
        String marca = correcto ? "[OK]   " : "[FALLA]";
        System.out.println("   " + marca + " " + que
                + " | esperado: " + esperado + " | obtenido: " + obtenido);
    }
}
