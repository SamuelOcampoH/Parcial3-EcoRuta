
package co.edu.uniquindio.ecoruta;

public class LotePapelCarton extends Lote {
    private final boolean clasificado;

    public LotePapelCarton(String codigo, String descripcion, int pesoKg, boolean clasificado) {
        super(codigo, descripcion, pesoKg);
        this.clasificado = clasificado;
    }

    @Override
    public double calcularValor() {
        double tarifa = 500.0 + (clasificado ? 100.0 : 0.0);
        return getPesoKg() * tarifa;
    }

    public boolean isClasificado() {
        return clasificado;
    }

    @Override
    public String toString() {
        return super.toString() + " | Papel/Cartón (Clasificado: " + (clasificado ? "SÍ" : "NO") + ")";
    }
}