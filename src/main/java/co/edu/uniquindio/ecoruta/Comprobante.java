package co.edu.uniquindio.ecoruta;

public record Comprobante(String codigoEntrega, String identificacionAportante, int pesoTotal, double valorTotal) {
    @Override
    public String toString() {
        return String.format("COMPROBANTE | Entrega: %s | Aportante ID: %s | Peso Total: %d kg | Valor Total: $%,.2f",
                codigoEntrega, identificacionAportante, pesoTotal, valorTotal);
    }
}