package co.edu.uniquindio.ecoruta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Entrega {
    private final String codigo;
    private final String fecha;
    private final Aportante aportante;
    private EstadoEntrega estado;
    private Comprobante comprobante;
    private final List<Lote> lotes;

    public Entrega(String codigo, String fecha, Aportante aportante) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de entrega no puede estar vacío.");
        }
        if (aportante == null) {
            throw new IllegalArgumentException("La entrega debe tener un aportante válido asignado.");
        }
        this.codigo = codigo;
        this.fecha = fecha;
        this.aportante = aportante;
        this.estado = EstadoEntrega.ABIERTA;
        this.comprobante = null;
        this.lotes = new ArrayList<>();
    }

    public boolean agregarLote(Lote lote) {
        if (this.estado != EstadoEntrega.ABIERTA) {
            return false;
        }
        if (lote == null || existeLote(lote.getCodigo()) || lote.getPesoKg() <= 0) {
            return false;
        }
        return lotes.add(lote);
    }

    public boolean existeLote(String codLote) {
        for (Lote l : lotes) {
            if (l.getCodigo().equalsIgnoreCase(codLote)) {
                return true;
            }
        }
        return false;
    }

    public boolean aceptar() {
        if (this.estado == EstadoEntrega.ACEPTADA || this.lotes.isEmpty()) {
            return false;
        }
        int pesoTotal = calcularPesoTotal();
        double valorTotal = calcularValorTotal();

        this.comprobante = new Comprobante(this.codigo, this.aportante.identificacion(), pesoTotal, valorTotal);
        this.estado = EstadoEntrega.ACEPTADA;
        return true;
    }

    public int calcularPesoTotal() {
        int total = 0;
        for (Lote l : lotes) {
            total += l.getPesoKg();
        }
        return total;
    }

    public double calcularValorTotal() {
        double total = 0.0;
        for (Lote l : lotes) {
            total += l.calcularValor(); // Polimorfismo
        }
        return total;
    }

    public List<String> obtenerEtiquetas() {
        List<String> etiquetas = new ArrayList<>();
        for (Lote l : lotes) {
            if (l instanceof GeneradorEtiqueta ge) {
                etiquetas.add(ge.generarEtiqueta());
            }
        }
        return etiquetas;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getFecha() {
        return fecha;
    }

    public Aportante getAportante() {
        return aportante;
    }

    public EstadoEntrega getEstado() {
        return estado;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public List<Lote> getLotes() {
        return Collections.unmodifiableList(lotes);
    }

    @Override
    public String toString() {
        return String.format("Entrega [%s] | Fecha: %s | Estado: %s | Aportante: %s | Total Lotes: %d",
                codigo, fecha, estado, aportante.nombre(), lotes.size());
    }
}