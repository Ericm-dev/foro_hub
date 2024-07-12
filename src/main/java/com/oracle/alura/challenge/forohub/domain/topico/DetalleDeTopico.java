package com.oracle.alura.challenge.forohub.domain.topico;

import java.time.LocalDateTime;

public record DetalleDeTopico(
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        boolean status,
        String autor,
        String curso
) {
    public DetalleDeTopico(Topico topico) {
        this(topico.getTitulo(), topico.getMensaje(), topico.getFechaCreacion(),
                topico.isStatus(), topico.getAutor().getNombre(), topico.getCurso().getNombre());
    }
}
