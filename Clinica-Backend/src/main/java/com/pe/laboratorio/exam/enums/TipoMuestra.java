package com.pe.laboratorio.exam.enums;

public enum TipoMuestra {
    SANGRE("Sangre"),
    SUERO("Suero"),
    PLASMA("Plasma"),
    ORINA("Orina"),
    HECES("Heces"),
    ESPUTO("Esputo"),
    LIQUIDO_CEFALORRAQUIDEO("Líquido Cefalorraquídeo"),
    OTROS("Otros");

    private final String descripcion;

    TipoMuestra(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}