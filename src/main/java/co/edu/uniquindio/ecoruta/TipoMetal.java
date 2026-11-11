package co.edu.uniquindio.ecoruta;

public enum TipoMetal {
    ALUMINIO(2200),
    ACERO(900);

    private final int precioPorKg;

    TipoMetal(int precioPorKg) {
        this.precioPorKg = precioPorKg;
    }

    public int getPrecioPorKg() {
        return precioPorKg;
    }
}