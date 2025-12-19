package com.pe.laboratorio.exam.enums;

public enum TipoResultado {
    NUMERICO("Numérico"),
    TEXTO("Texto"),
    IMAGEN("Imagen"),
    PANEL("Panel"),
    CUANTITATIVO("Cuantitativo"),
    CUALITATIVO("Cualitativo");

    private final String descripcion;

    TipoResultado(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}