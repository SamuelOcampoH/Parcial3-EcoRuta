package co.edu.uniquindio.ecoruta;

public abstract class Lote {
    private final String codigo;
    private final String descripcion;
    private final int pesoKg;

    public Lote(String codigo, String descripcion, int pesoKg) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del lote no puede estar vacío.");
        }
        if (pesoKg <= 0) {
            throw new IllegalArgumentException("El peso en kilogramos debe ser un entero positivo (> 0).");
        }
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.pesoKg = pesoKg;
    }

    public abstract double calcularValor();

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPesoKg() {
        return pesoKg;
    }

    @Override
    public String toString() {
        return String.format("Lote [%s] - %s | Peso: %d kg | Valor: $%,.2f", codigo, descripcion, pesoKg, calcularValor());
    }
}