package co.edu.uniquindio.ecoruta;

import javax.swing.JOptionPane;
import java.util.List;

public class Main {
    private static final EcoRuta ecoRuta = new EcoRuta();

    public static void main(String[] args) {
        precargarDatos();
        int opcion = 0;

        String menu = """
                ===================================================
                   CENTRO DE APROVECHAMIENTO ECORUTA - GESTIÓN
                ===================================================
                1. Registrar nuevo aportante
                2. Crear nueva entrega (Estado: ABIERTA)
                3. Agregar lote a entrega abierta
                4. Aceptar entrega (Generar Comprobante)
                5. Consultar comprobante por código de entrega
                6. Consultar etiquetas de control interno por entrega
                7. Consultar valor acumulado por aportante
                8. Listar todas las entregas
                9. EJECUTAR BATERÍA DE PRUEBAS FUNCIONALES (Consola)
                10. Salir
                ---------------------------------------------------
                Seleccione una opción:""";

        do {
            String entrada = JOptionPane.showInputDialog(null, menu, "EcoRuta - Menú Principal", JOptionPane.QUESTION_MESSAGE);

            // Si el usuario cierra la ventana o presiona Cancelar
            if (entrada == null) {
                opcion = 10;
                break;
            }

            try {
                opcion = Integer.parseInt(entrada.trim());
                switch (opcion) {
                    case 1 -> registrarAportante();
                    case 2 -> crearNuevaEntrega();
                    case 3 -> agregarLoteAEntrega();
                    case 4 -> aceptarEntrega();
                    case 5 -> consultarComprobante();
                    case 6 -> consultarEtiquetas();
                    case 7 -> consultarAcumuladoAportante();
                    case 8 -> listarEntregas();
                    case 9 -> {
                        PruebasEcoRuta.ejecutarTodas();
                        JOptionPane.showMessageDialog(null, "Las 5 pruebas se ejecutaron.\nRevisa el resultado detallado en la consola de abajo.", "Pruebas Ejecutadas", JOptionPane.INFORMATION_MESSAGE);
                    }
                    case 10 -> JOptionPane.showMessageDialog(null, "Saliendo del sistema EcoRuta. ¡Hasta pronto!", "Salida", JOptionPane.INFORMATION_MESSAGE);
                    default -> JOptionPane.showMessageDialog(null, "Opción inválida. Ingrese un número del 1 al 10.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            }
        } while (opcion != 10);
    }

    private static void registrarAportante() {
        String id = JOptionPane.showInputDialog("Ingrese la identificación del aportante:");
        if (id == null) return;
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre completo:");
        if (nombre == null) return;
        String correo = JOptionPane.showInputDialog("Ingrese el correo electrónico:");
        if (correo == null) return;

        try {
            Aportante a = new Aportante(id.trim(), nombre.trim(), correo.trim());
            if (ecoRuta.registrarAportante(a)) {
                JOptionPane.showMessageDialog(null, "Aportante registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error: Ya existe un aportante con esa identificación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Datos no válidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void crearNuevaEntrega() {
        String codigo = JOptionPane.showInputDialog("Código único de entrega (ej: E01):");
        if (codigo == null) return;
        String fecha = JOptionPane.showInputDialog("Fecha (DD/MM/AAAA):");
        if (fecha == null) return;
        String id = JOptionPane.showInputDialog("Identificación del aportante (Precargados: 109491, 109492, 109493):");
        if (id == null) return;

        if (ecoRuta.crearEntrega(codigo.trim(), fecha.trim(), id.trim())) {
            JOptionPane.showMessageDialog(null, "Entrega creada con éxito en estado ABIERTA.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error: Código de entrega repetido o aportante no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void agregarLoteAEntrega() {
        String codEntrega = JOptionPane.showInputDialog("Código de la entrega:");
        if (codEntrega == null) return;

        Entrega ent = ecoRuta.buscarEntrega(codEntrega.trim());
        if (ent == null) {
            JOptionPane.showMessageDialog(null, "Error: La entrega no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (ent.getEstado() != EstadoEntrega.ABIERTA) {
            JOptionPane.showMessageDialog(null, "Error: La entrega está ACEPTADA y no admite nuevos lotes.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codLote = JOptionPane.showInputDialog("Código único del lote (ej: L01):");
        if (codLote == null) return;
        String desc = JOptionPane.showInputDialog("Descripción del lote:");
        if (desc == null) return;

        int peso;
        try {
            String pesoStr = JOptionPane.showInputDialog("Peso en kilogramos enteros (> 0):");
            if (pesoStr == null) return;
            peso = Integer.parseInt(pesoStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El peso debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] opcionesMaterial = {"Papel y Cartón", "Metales (Aluminio / Acero)", "Residuos Electrónicos (RAEE)"};
        int tipo = JOptionPane.showOptionDialog(null, "Seleccione el tipo de material:", "Tipo de Lote",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcionesMaterial, opcionesMaterial[0]);

        if (tipo == -1) return; // Cerró ventana

        Lote lote = null;
        try {
            switch (tipo) {
                case 0 -> { // Papel y Cartón
                    int resp = JOptionPane.showConfirmDialog(null, "¿El material fue clasificado antes de entregarse?", "Clasificación", JOptionPane.YES_NO_OPTION);
                    boolean clasif = (resp == JOptionPane.YES_OPTION);
                    lote = new LotePapelCarton(codLote.trim(), desc.trim(), peso, clasif);
                }
                case 1 -> { // Metales
                    TipoMetal[] metales = {TipoMetal.ALUMINIO, TipoMetal.ACERO};
                    TipoMetal metalSeleccionado = (TipoMetal) JOptionPane.showInputDialog(null, "Seleccione el tipo de metal:",
                            "Tipo de Metal", JOptionPane.QUESTION_MESSAGE, null, metales, metales[0]);
                    if (metalSeleccionado == null) return;
                    lote = new LoteMetal(codLote.trim(), desc.trim(), peso, metalSeleccionado);
                }
                case 2 -> { // Electrónicos
                    String eqStr = JOptionPane.showInputDialog("Cantidad de equipos completos (>= 0):");
                    if (eqStr == null) return;
                    int equipos = Integer.parseInt(eqStr.trim());
                    lote = new LoteElectronico(codLote.trim(), desc.trim(), peso, equipos);
                }
            }

            if (lote != null) {
                if (ecoRuta.agregarLoteAEntrega(codEntrega.trim(), lote)) {
                    JOptionPane.showMessageDialog(null, "Lote agregado con éxito a la entrega.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Código de lote duplicado o peso inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: Debe ingresar un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Error de validación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void aceptarEntrega() {
        String cod = JOptionPane.showInputDialog("Código de la entrega a aceptar:");
        if (cod == null) return;

        if (ecoRuta.aceptarEntrega(cod.trim())) {
            Comprobante comp = ecoRuta.consultarComprobante(cod.trim());
            JOptionPane.showMessageDialog(null, "¡Entrega ACEPTADA exitosamente!\n\n" + comp, "Entrega Aceptada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error: La entrega no existe, ya fue aceptada o se encuentra VACÍA sin lotes.", "Rechazo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void consultarComprobante() {
        String cod = JOptionPane.showInputDialog("Código de la entrega:");
        if (cod == null) return;

        Comprobante c = ecoRuta.consultarComprobante(cod.trim());
        if (c != null) {
            JOptionPane.showMessageDialog(null, c.toString(), "Comprobante", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No existe comprobante (entrega inexistente o aún en estado ABIERTA).", "Sin Comprobante", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void consultarEtiquetas() {
        String cod = JOptionPane.showInputDialog("Código de la entrega:");
        if (cod == null) return;

        List<String> etiqs = ecoRuta.consultarEtiquetasEntrega(cod.trim());
        if (etiqs.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay etiquetas disponibles (entrega abierta o sin lotes que apliquen).", "Etiquetas", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("Etiquetas generadas (" + etiqs.size() + "):\n\n");
            for (String et : etiqs) {
                sb.append("• ").append(et).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString(), "Etiquetas de Control Interno", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void consultarAcumuladoAportante() {
        String id = JOptionPane.showInputDialog("Identificación del aportante:");
        if (id == null) return;

        Aportante a = ecoRuta.buscarAportante(id.trim());
        if (a == null) {
            JOptionPane.showMessageDialog(null, "Aportante no encontrado en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double total = ecoRuta.consultarValorAcumuladoAportante(id.trim());
        String msg = String.format("El aportante %s (%s)\ntiene un total acumulado reconocido de:\n\n$%,.2f COP",
                a.nombre(), a.identificacion(), total);
        JOptionPane.showMessageDialog(null, msg, "Valor Acumulado", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void listarEntregas() {
        List<Entrega> lista = ecoRuta.getEntregas();
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay entregas registradas en el sistema.", "Listado de Entregas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("--- LISTADO DE ENTREGAS ---\n\n");
        for (Entrega e : lista) {
            sb.append(e).append("\n");
            for (Lote l : e.getLotes()) {
                sb.append("    * ").append(l).append("\n");
            }
            sb.append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Entregas Registradas", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void precargarDatos() {
        ecoRuta.registrarAportante(new Aportante("109491", "Samuel Ocampo", "samuel@unicauca.edu.co"));
        ecoRuta.registrarAportante(new Aportante("109492", "Matias Guapacha", "guapacha@uniquindio.edu.co"));
        ecoRuta.registrarAportante(new Aportante("109493", "Comercializadora Armenia", "contacto@comercio.co"));
    }
}