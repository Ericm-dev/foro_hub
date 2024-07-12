package com.oracle.alura.challenge.forohub.domain.topico;

public record DatosRespuestaTopico(
        Long id,
        String titulo,
        String mensaje,
        String autor,
        String curso) {

    public DatosRespuestaTopico(String error) {
        this(null, error, null, null, null);
    }

}
