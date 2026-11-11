package co.edu.uniquindio.ecoruta;

public class LoteElectronico extends Lote implements GeneradorEtiqueta {
    private final int equiposCompletos;

    public LoteElectronico(String codigo, String descripcion, int pesoKg, int equiposCompletos) {
        super(codigo, descripcion, pesoKg);
        if (equiposCompletos < 0) {
            throw new IllegalArgumentException("Los equipos completos deben ser un entero mayor o igual a cero.");
        }
        this.equiposCompletos = equiposCompletos;
    }

    @Override
    public double calcularValor() {
        return (getPesoKg() * 1400.0) + (equiposCompletos * 800.0);
    }

    @Override
    public String generarEtiqueta() {
        return "ETIQUETA-RAEE | Código: " + getCodigo() + " | Equipos Completos: " + equiposCompletos;
    }

    public int getEquiposCompletos() {
        return equiposCompletos;
    }

    @Override
    public String toString() {
        return super.toString() + " | RAEE (Equipos Completos: " + equiposCompletos + ")";
    }
}