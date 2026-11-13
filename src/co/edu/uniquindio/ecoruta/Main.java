package co.edu.uniquindio.ecoruta;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.util.List;

public class Main {

    // Valor especial que devuelven los métodos de entrada cuando el usuario presiona "Cancelar"
    private static final int CANCELADO = Integer.MIN_VALUE;

    private static final String MENU = "MENÚ ECORUTA\n\n"
            + "1. Crear entrega\n"
            + "2. Agregar lote a una entrega\n"
            + "3. Aceptar entrega\n"
            + "4. Consultar comprobante\n"
            + "5. Consultar etiquetas\n"
            + "6. Consultar valor acumulado por aportante\n"
            + "0. Salir\n\n"
            + "Digite una opción:";

    public static void main(String[] args) {

        CentroEcoRuta centro = new CentroEcoRuta("EcoRuta");
        cargarAportantesIniciales(centro);

        int opcion = -1;
        while (opcion != 0) {
            opcion = ingresarOpcionMenu();

            switch (opcion) {
                case 1:
                    crearEntrega(centro);
                    break;
                case 2:
                    agregarLote(centro);
                    break;
                case 3:
                    aceptarEntrega(centro);
                    break;
                case 4:
                    consultarComprobante(centro);
                    break;
                case 5:
                    consultarEtiquetas(centro);
                    break;
                case 6:
                    consultarValorAcumulado(centro);
                    break;
                case 0:
                    mostrarMensaje("Hasta luego.");
                    break;
                default:
                    mostrarMensaje("Opción inválida. Intente de nuevo.");
            }
        }
    }

    // ===================== DATOS INICIALES =====================

    private static void cargarAportantesIniciales(CentroEcoRuta centro) {
        centro.registrarAportante(new Aportante("1001", "Ana Ruiz", "ana@mail.com"));
        centro.registrarAportante(new Aportante("1002", "Carlos Pérez", "carlos@mail.com"));
        centro.registrarAportante(new Aportante("1003", "Laura Gómez", "laura@mail.com"));
    }

    // ===================== OPCIONES DEL MENÚ =====================

    private static void crearEntrega(CentroEcoRuta centro) {
        String codigo = ingresarTexto("Código de la entrega:");
        if (codigo == null) {
            return;
        }
        String id = ingresarTexto("Identificación del aportante.\n\n" + listarAportantes(centro));
        if (id == null) {
            return;
        }

        boolean creada = centro.crearEntrega(codigo, LocalDate.now(), id);
        if (creada) {
            mostrarMensaje("Entrega " + codigo + " creada en estado ABIERTA.");
        } else {
            mostrarMensaje("No se pudo crear la entrega: el código ya existe "
                    + "o el aportante no está registrado.");
        }
    }

    private static void agregarLote(CentroEcoRuta centro) {
        String codigoEntrega = ingresarTexto("Código de la entrega:");
        if (codigoEntrega == null) {
            return;
        }
        int tipo = ingresarEnteroEntre("Tipo de lote:\n1. Papel y cartón\n2. Metal\n3. Residuo electrónico", 1, 3);
        if (tipo == CANCELADO) {
            return;
        }
        String codigoLote = ingresarTexto("Código del lote:");
        if (codigoLote == null) {
            return;
        }
        String descripcion = ingresarTexto("Descripción del lote:");
        if (descripcion == null) {
            return;
        }
        int peso = ingresarEnteroMayorQue("Peso en kg (entero positivo):", 0);
        if (peso == CANCELADO) {
            return;
        }

        Lote lote = null;
        if (tipo == 1) {
            boolean clasificado = preguntarSiNo("¿El material fue clasificado antes de entregarse?");
            lote = new PapelCarton(codigoLote, descripcion, peso, clasificado);
        } else if (tipo == 2) {
            int opcionMetal = ingresarEnteroEntre("Tipo de metal:\n1. ALUMINIO\n2. ACERO", 1, 2);
            if (opcionMetal == CANCELADO) {
                return;
            }
            TipoMetal tipoMetal = TipoMetal.ACERO;
            if (opcionMetal == 1) {
                tipoMetal = TipoMetal.ALUMINIO;
            }
            lote = new Metal(codigoLote, descripcion, peso, tipoMetal);
        } else {
            int equipos = ingresarEnteroMayorQue("Equipos completos (0 o más):", -1);
            if (equipos == CANCELADO) {
                return;
            }
            lote = new ResiduoElectronico(codigoLote, descripcion, peso, equipos);
        }

        boolean agregado = centro.agregarLote(codigoEntrega, lote);
        if (agregado) {
            mostrarMensaje("Lote " + codigoLote + " agregado a la entrega " + codigoEntrega + ".");
        } else {
            mostrarMensaje("No se pudo agregar el lote: la entrega no existe o ya fue aceptada, "
                    + "o el código del lote ya está repetido en esa entrega.");
        }
    }

    private static void aceptarEntrega(CentroEcoRuta centro) {
        String codigo = ingresarTexto("Código de la entrega a aceptar:");
        if (codigo == null) {
            return;
        }

        boolean aceptada = centro.aceptarEntrega(codigo);
        if (aceptada) {
            mostrarMensaje("Entrega aceptada.\n\n" + textoComprobante(centro.consultarComprobante(codigo)));
        } else {
            mostrarMensaje("No se pudo aceptar: la entrega no existe, está vacía o ya fue aceptada.");
        }
    }

    private static void consultarComprobante(CentroEcoRuta centro) {
        String codigo = ingresarTexto("Código de la entrega:");
        if (codigo == null) {
            return;
        }

        Comprobante comprobante = centro.consultarComprobante(codigo);
        if (comprobante == null) {
            mostrarMensaje("No hay comprobante: la entrega no existe o aún no ha sido aceptada.");
        } else {
            mostrarMensaje(textoComprobante(comprobante));
        }
    }

    private static void consultarEtiquetas(CentroEcoRuta centro) {
        String codigo = ingresarTexto("Código de la entrega:");
        if (codigo == null) {
            return;
        }

        List<String> etiquetas = centro.consultarEtiquetas(codigo);
        if (etiquetas == null) {
            mostrarMensaje("No hay etiquetas: la entrega no existe o aún no ha sido aceptada.");
        } else if (etiquetas.isEmpty()) {
            mostrarMensaje("La entrega no tiene lotes que generen etiqueta.");
        } else {
            String texto = "Etiquetas de la entrega " + codigo + ":\n";
            for (String etiqueta : etiquetas) {
                texto = texto + "\n" + etiqueta;
            }
            mostrarMensaje(texto);
        }
    }

    private static void consultarValorAcumulado(CentroEcoRuta centro) {
        String id = ingresarTexto("Identificación del aportante:");
        if (id == null) {
            return;
        }
        double acumulado = centro.calcularValorAcumulado(id);
        mostrarMensaje("Valor acumulado del aportante " + id + ": " + formatearDinero(acumulado));
    }

    // ===================== MÉTODOS DE ENTRADA (con validación) =====================

    private static int ingresarOpcionMenu() {
        String texto = JOptionPane.showInputDialog(null, MENU, "EcoRuta", JOptionPane.QUESTION_MESSAGE);
        int opcion = -1;
        if (texto == null) {
            opcion = 0; // Cancelar = salir
        } else {
            try {
                opcion = Integer.parseInt(texto.trim());
            } catch (NumberFormatException e) {
                opcion = -1; // no era un número: cae en "Opción inválida"
            }
        }
        return opcion;
    }

    // Devuelve el texto ingresado, o null si el usuario cancela.
    private static String ingresarTexto(String mensaje) {
        String resultado = null;
        boolean valido = false;
        while (!valido) {
            String texto = JOptionPane.showInputDialog(mensaje);
            if (texto == null) {
                valido = true; // canceló: se devuelve null
            } else if (texto.trim().isEmpty()) {
                mostrarMensaje("El dato no puede estar vacío.");
            } else {
                resultado = texto.trim();
                valido = true;
            }
        }
        return resultado;
    }

    // Pide un entero estrictamente mayor que "minimo". Devuelve CANCELADO si el usuario cancela.
    private static int ingresarEnteroMayorQue(String mensaje, int minimo) {
        int resultado = 0;
        boolean valido = false;
        while (!valido) {
            String texto = JOptionPane.showInputDialog(mensaje);
            if (texto == null) {
                resultado = CANCELADO;
                valido = true;
            } else {
                try {
                    int numero = Integer.parseInt(texto.trim());
                    if (numero > minimo) {
                        resultado = numero;
                        valido = true;
                    } else {
                        mostrarMensaje("El valor debe ser mayor que " + minimo + ".");
                    }
                } catch (NumberFormatException e) {
                    mostrarMensaje("Debe ingresar un número entero.");
                }
            }
        }
        return resultado;
    }

    // Pide un entero entre "minimo" y "maximo" (incluidos). Devuelve CANCELADO si el usuario cancela.
    private static int ingresarEnteroEntre(String mensaje, int minimo, int maximo) {
        int resultado = 0;
        boolean valido = false;
        while (!valido) {
            String texto = JOptionPane.showInputDialog(mensaje);
            if (texto == null) {
                resultado = CANCELADO;
                valido = true;
            } else {
                try {
                    int numero = Integer.parseInt(texto.trim());
                    if (numero >= minimo && numero <= maximo) {
                        resultado = numero;
                        valido = true;
                    } else {
                        mostrarMensaje("Debe elegir un número entre " + minimo + " y " + maximo + ".");
                    }
                } catch (NumberFormatException e) {
                    mostrarMensaje("Debe ingresar un número entero.");
                }
            }
        }
        return resultado;
    }

    private static boolean preguntarSiNo(String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(null, mensaje, "EcoRuta", JOptionPane.YES_NO_OPTION);
        return respuesta == JOptionPane.YES_OPTION;
    }

    // ===================== MÉTODOS DE SALIDA =====================

    private static void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "EcoRuta", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String textoComprobante(Comprobante comprobante) {
        return "COMPROBANTE\n"
                + "Código de entrega: " + comprobante.codigoEntrega() + "\n"
                + "Aportante: " + comprobante.idAportante() + "\n"
                + "Peso total: " + comprobante.pesoTotal() + " kg\n"
                + "Valor total: " + formatearDinero(comprobante.valorTotal());
    }

    private static String listarAportantes(CentroEcoRuta centro) {
        String texto = "Aportantes registrados:";
        for (Aportante aportante : centro.getAportantes()) {
            texto = texto + "\n" + aportante.identificacion() + " - " + aportante.nombre();
        }
        return texto;
    }

    private static String formatearDinero(double valor) {
        return String.format("$%,.0f", valor);
    }
}
