package co.edu.uniquindio.ecoruta;

import java.util.List;

public class  PruebasEcoRuta {

    public static void ejecutarTodas() {
        System.out.println("======================================================================");
        System.out.println("        EJECUCIÓN DE PRUEBAS FUNCIONALES EXIGIDAS EN EL PARCIAL");
        System.out.println("======================================================================");

        prueba1_RegistroEntrega();
        prueba2_ValidacionLotes();
        prueba3_RechazoEntregaVacia();
        prueba4_AceptacionYCalculoLotes();
        prueba5_ConsultaEtiquetas();

        System.out.println("======================================================================");
        System.out.println("                FIN DE LAS PRUEBAS FUNCIONALES");
        System.out.println("======================================================================\n");
    }

    private static void prueba1_RegistroEntrega() {
        System.out.println("\n--- PRUEBA 1: Registro de una entrega ---");
        EcoRuta centro = new EcoRuta();
        centro.registrarAportante(new Aportante("1094", "Samuel Ocampo", "samuel@gmail.com"));

        // 1. Crear E01
        boolean creada = centro.crearEntrega("E01", "10/11/2026", "1094");
        Entrega e01 = centro.buscarEntrega("E01");
        boolean estadoCorrecto = e01 != null && e01.getEstado() == EstadoEntrega.ABIERTA
                && e01.getLotes().isEmpty() && e01.getComprobante() == null;

        // 2. Intentar duplicar E01
        boolean duplicadaRechazada = !centro.crearEntrega("E01", "11/11/2026", "1094");

        // 3. Intentar con aportante desconocido
        boolean desconocidoRechazado = !centro.crearEntrega("E02", "10/11/2026", "9999");

        System.out.println("Datos de entrada : Crear 'E01' (id: 1094), repetir 'E01', crear 'E02' (id: 9999 desconocido)");
        System.out.println("Resultado esperado: E01 ABIERTA, sin lotes, sin comprobante. Duplicada rechazada. Desconocido rechazado.");
        System.out.println("Resultado obtenido: E01 creada: " + creada + " | Estado ok: " + estadoCorrecto
                + " | Rechazo duplicada: " + duplicadaRechazada + " | Rechazo desconocido: " + desconocidoRechazado);
        System.out.println("Veredicto: " + (creada && estadoCorrecto && duplicadaRechazada && desconocidoRechazado ? "PASÓ [✓]" : "FALLÓ [X]"));
    }

    private static void prueba2_ValidacionLotes() {
        System.out.println("\n--- PRUEBA 2: Validación de lotes ---");
        EcoRuta centro = new EcoRuta();
        centro.registrarAportante(new Aportante("1094", "Samuel Ocampo", "samuel@gmail.com"));
        centro.crearEntrega("E01", "10/11/2026", "1094");

        // 1. Agregar L01 con 5 kg
        Lote l1 = new LotePapelCarton("L01", "Cartón grueso", 5, false);
        boolean l1Agregado = centro.agregarLoteAEntrega("E01", l1);

        // 2. Intentar agregar L01 repetido
        Lote l1Repetido = new LoteMetal("L01", "Metal vario", 8, TipoMetal.ACERO);
        boolean l1RepRechazado = !centro.agregarLoteAEntrega("E01", l1Repetido);

        // 3. Intentar agregar lote con 0 kg
        boolean lote0kgRechazado = false;
        try {
            Lote lInvalido = new LoteMetal("L02", "Latas aluminio", 0, TipoMetal.ALUMINIO);
            centro.agregarLoteAEntrega("E01", lInvalido);
        } catch (IllegalArgumentException e) {
            lote0kgRechazado = true; // El constructor del lote protegió la integridad
        }

        Entrega e01 = centro.buscarEntrega("E01");
        boolean soloUnLote = e01.getLotes().size() == 1 && e01.getLotes().get(0).getCodigo().equals("L01");

        System.out.println("Datos de entrada : L01 (5kg), L01 repetido, L02 (0kg)");
        System.out.println("Resultado esperado: Primer L01 aceptado, repetido rechazado, 0kg rechazado, tamaño final: 1 lote.");
        System.out.println("Resultado obtenido: L01 añadido: " + l1Agregado + " | Repetido rechazado: " + l1RepRechazado
                + " | 0kg rechazado: " + lote0kgRechazado + " | Cantidad lotes conservados: " + e01.getLotes().size());
        System.out.println("Veredicto: " + (l1Agregado && l1RepRechazado && lote0kgRechazado && soloUnLote ? "PASÓ [✓]" : "FALLÓ [X]"));
    }

    private static void prueba3_RechazoEntregaVacia() {
        System.out.println("\n--- PRUEBA 3: Rechazo de una entrega vacía ---");
        EcoRuta centro = new EcoRuta();
        centro.registrarAportante(new Aportante("1094", "Samuel Ocampo", "samuel@gmail.com"));
        centro.crearEntrega("E01", "10/11/2026", "1094");

        // Intentar aceptar sin haber agregado lotes
        boolean aceptacionRechazada = !centro.aceptarEntrega("E01");
        Entrega e01 = centro.buscarEntrega("E01");
        boolean seConserva = e01 != null && e01.getEstado() == EstadoEntrega.ABIERTA && e01.getComprobante() == null;

        System.out.println("Datos de entrada : Aceptar 'E01' con 0 lotes");
        System.out.println("Resultado esperado: Operación rechazada, entrega permanece ABIERTA y sin comprobante.");
        System.out.println("Resultado obtenido: Rechazo exitoso: " + aceptacionRechazada + " | Estado: " + e01.getEstado() + " | Comprobante: " + e01.getComprobante());
        System.out.println("Veredicto: " + (aceptacionRechazada && seConserva ? "PASÓ [✓]" : "FALLÓ [X]"));
    }

    private static void prueba4_AceptacionYCalculoLotes() {
        System.out.println("\n--- PRUEBA 4: Aceptación y cálculo con los tres tipos de lote ---");
        EcoRuta centro = new EcoRuta();
        centro.registrarAportante(new Aportante("1094", "Samuel Ocampo", "samuel@gmail.com"));
        centro.crearEntrega("E01", "10/11/2026", "1094");

        // Lote 1: Papel clasificado 10 kg -> 10 * 600 = $6.000
        Lote papel = new LotePapelCarton("LP01", "Papel archivo", 10, true);
        // Lote 2: Aluminio 5 kg -> 5 * 2.200 = $11.000
        Lote metal = new LoteMetal("LM01", "Perfiles aluminio", 5, TipoMetal.ALUMINIO);
        // Lote 3: Electrónico 4 kg con 2 equipos -> 4 * 1.400 + 2 * 800 = 5.600 + 1.600 = $7.200
        Lote raee = new LoteElectronico("LE01", "Portátiles viejos", 4, 2);

        centro.agregarLoteAEntrega("E01", papel);
        centro.agregarLoteAEntrega("E01", metal);
        centro.agregarLoteAEntrega("E01", raee);

        boolean aceptada = centro.aceptarEntrega("E01");
        Entrega e01 = centro.buscarEntrega("E01");
        Comprobante comp = e01.getComprobante();

        double valorPapel = papel.calcularValor();
        double valorMetal = metal.calcularValor();
        double valorRaee = raee.calcularValor();

        boolean valoresCorrectos = (valorPapel == 6000.0) && (valorMetal == 11000.0) && (valorRaee == 7200.0);
        boolean compCorrecto = comp != null && comp.pesoTotal() == 19 && comp.valorTotal() == 24200.0;
        boolean estadoAceptado = e01.getEstado() == EstadoEntrega.ACEPTADA;

        System.out.println("Datos de entrada : 10kg papel clasificado, 5kg aluminio, 4kg RAEE con 2 equipos.");
        System.out.println("Resultado esperado: Subtotales: $6.000, $11.000, $7.200. Total Comprobante: 19 kg y $24.200. Estado: ACEPTADA.");
        System.out.printf("Resultado obtenido: Subtotales: $%,.2f, $%,.2f, $%,.2f | Comp: %d kg, $%,.2f | Estado: %s%n",
                valorPapel, valorMetal, valorRaee, comp.pesoTotal(), comp.valorTotal(), e01.getEstado());
        System.out.println("Veredicto: " + (aceptada && valoresCorrectos && compCorrecto && estadoAceptado ? "PASÓ [✓]" : "FALLÓ [X]"));
    }

    private static void prueba5_ConsultaEtiquetas() {
        System.out.println("\n--- PRUEBA 5: Consulta de etiquetas ---");
        EcoRuta centro = new EcoRuta();
        centro.registrarAportante(new Aportante("1094", "Samuel Ocampo", "samuel@gmail.com"));
        centro.crearEntrega("E01", "10/11/2026", "1094");

        centro.agregarLoteAEntrega("E01", new LotePapelCarton("LP01", "Cartón", 10, true));
        centro.agregarLoteAEntrega("E01", new LoteMetal("LM01", "Perfiles", 5, TipoMetal.ALUMINIO));
        centro.agregarLoteAEntrega("E01", new LoteElectronico("LE01", "CPUs", 4, 2));
        centro.aceptarEntrega("E01");

        List<String> etiquetas = centro.consultarEtiquetasEntrega("E01");

        boolean exactamenteDos = etiquetas.size() == 2;
        boolean etiquetaMetalValida = false;
        boolean etiquetaRaeeValida = false;

        for (String et : etiquetas) {
            if (et.contains("LM01") && et.contains("ALUMINIO")) {
                etiquetaMetalValida = true;
            }
            if (et.contains("LE01") && et.contains("2")) {
                etiquetaRaeeValida = true;
            }
        }

        System.out.println("Datos de entrada : Consulta de etiquetas de la entrega aceptada con 3 lotes.");
        System.out.println("Resultado esperado: Exactamente 2 etiquetas (Metal con LM01 y ALUMINIO; RAEE con LE01 y 2). Papel sin etiqueta.");
        System.out.println("Resultado obtenido: Cantidad etiquetas: " + etiquetas.size());
        for (String e : etiquetas) {
            System.out.println("  -> " + e);
        }
        System.out.println("Veredicto: " + (exactamenteDos && etiquetaMetalValida && etiquetaRaeeValida ? "PASÓ [✓]" : "FALLÓ [X]"));
    }
}