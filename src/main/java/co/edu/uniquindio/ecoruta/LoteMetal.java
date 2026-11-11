package co.edu.uniquindio.ecoruta;

public class LoteMetal extends Lote implements GeneradorEtiqueta {
    private final TipoMetal tipoMetal;

    public LoteMetal(String codigo, String descripcion, int pesoKg, TipoMetal tipoMetal) {
        super(codigo, descripcion, pesoKg);
        if (tipoMetal == null) {
            throw new IllegalArgumentException("El tipo de metal es obligatorio.");
        }
        this.tipoMetal = tipoMetal;
    }

    @Override
    public double calcularValor() {
        return getPesoKg() * (double) tipoMetal.getPrecioPorKg();
    }

    @Override
    public String generarEtiqueta() {
        return "ETIQUETA-METAL | Código: " + getCodigo() + " | Tipo: " + tipoMetal.name();
    }

    public TipoMetal getTipoMetal() {
        return tipoMetal;
    }

    @Override
    public String toString() {
        return super.toString() + " | Metal (" + tipoMetal + ")";
    }
}
