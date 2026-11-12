
package co.edu.uniquindio.ecoruta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EcoRuta {
    private final List<Aportante> aportantes;
    private final List<Entrega> entregas;

    public EcoRuta() {
        this.aportantes = new ArrayList<>();
        this.entregas = new ArrayList<>();
    }

    public boolean registrarAportante(Aportante aportante) {
        if (aportante == null || buscarAportante(aportante.identificacion()) != null) {
            return false;
        }
        return aportantes.add(aportante);
    }

    public Aportante buscarAportante(String identificacion) {
        if (identificacion == null) return null;
        for (Aportante a : aportantes) {
            if (a.identificacion().equalsIgnoreCase(identificacion)) {
                return a;
            }
        }
        return null;
    }

    public boolean crearEntrega(String codigo, String fecha, String idAportante) {
        if (codigo == null || codigo.isBlank() || buscarEntrega(codigo) != null) {
            return false;
        }
        Aportante aportante = buscarAportante(idAportante);
        if (aportante == null) {
            return false;
        }
        Entrega entrega = new Entrega(codigo, fecha, aportante);
        return entregas.add(entrega);
    }

    public Entrega buscarEntrega(String codigo) {
        if (codigo == null) return null;
        for (Entrega e : entregas) {
            if (e.getCodigo().equalsIgnoreCase(codigo)) {
                return e;
            }
        }
        return null;
    }

    public boolean agregarLoteAEntrega(String codEntrega, Lote lote) {
        Entrega e = buscarEntrega(codEntrega);
        if (e == null) {
            return false;
        }
        return e.agregarLote(lote);
    }

    public boolean aceptarEntrega(String codEntrega) {
        Entrega e = buscarEntrega(codEntrega);
        if (e == null) {
            return false;
        }
        return e.aceptar();
    }

    public Comprobante consultarComprobante(String codEntrega) {
        Entrega e = buscarEntrega(codEntrega);
        if (e != null && e.getEstado() == EstadoEntrega.ACEPTADA) {
            return e.getComprobante();
        }
        return null;
    }

    public List<String> consultarEtiquetasEntrega(String codEntrega) {
        Entrega e = buscarEntrega(codEntrega);
        if (e != null && e.getEstado() == EstadoEntrega.ACEPTADA) {
            return e.obtenerEtiquetas();
        }
        return Collections.emptyList();
    }

    public double consultarValorAcumuladoAportante(String idAportante) {
        Aportante a = buscarAportante(idAportante);
        if (a == null) {
            return 0.0;
        }
        double total = 0.0;
        for (Entrega e : entregas) {
            if (e.getAportante().identificacion().equalsIgnoreCase(idAportante)
                    && e.getEstado() == EstadoEntrega.ACEPTADA
                    && e.getComprobante() != null) {
                total += e.getComprobante().valorTotal();
            }
        }
        return total;
    }

    public List<Entrega> getEntregas() {
        return Collections.unmodifiableList(entregas);
    }

    public List<Aportante> getAportantes() {
        return Collections.unmodifiableList(aportantes);
    }
}