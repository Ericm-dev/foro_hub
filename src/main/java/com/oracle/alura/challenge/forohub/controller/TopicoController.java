package com.oracle.alura.challenge.forohub.controller;

import com.oracle.alura.challenge.forohub.domain.topico.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("topicos")
public class TopicoController {

    @Autowired
    private TopicoService topicoService;

    @Autowired
    private TopicoRepository topicoRepository;

    // Registrar un topico
    @PostMapping
    public ResponseEntity<DatosRespuestaTopico> registrarTopico(
            @RequestBody @Valid DatosRegistroTopico datosRegistroTopico,
            UriComponentsBuilder uriComponentsBuilder) {
        DatosRespuestaTopico respuesta = topicoService.registrarTopico(datosRegistroTopico);

        if (respuesta.id() == null) {
            return ResponseEntity.status(409).body(respuesta);
        }

        // Construir la URL del nuevo recurso creado
        URI url = uriComponentsBuilder.path("/topicos/{id}")
                .buildAndExpand(respuesta.id()).toUri();

        // Retornar la respuesta con código 201 Created y el cuerpo de la respuesta
        return ResponseEntity.created(url).body(respuesta);
    }

    // Listar todos los topico
    @GetMapping
    public List<DatosListadoTopico> listarTopicosActivos() {
        return topicoRepository.findAllActive().stream()
                .map(topico -> new DatosListadoTopico(
                        topico.getId(),
                        topico.getTitulo(),
                        topico.getMensaje(),
                        topico.getFechaCreacion(),
                        topico.isStatus(),
                        topico.getAutor().getNombre(),
                        topico.getCurso().getNombre()))
                .collect(Collectors.toList());
    }

    // Detalle de topico
    @GetMapping("/{id}")
    public ResponseEntity<DetalleDeTopico> obtenerTópicoPorId(@PathVariable Long id) {
        Optional<Topico> topicoEncontrado = topicoRepository.findById(id);
        if (topicoEncontrado.isPresent()) {
            DetalleDeTopico detalle = new DetalleDeTopico(
                    topicoEncontrado.get().getTitulo(),
                    topicoEncontrado.get().getMensaje(),
                    topicoEncontrado.get().getFechaCreacion(),
                    topicoEncontrado.get().isStatus(),
                    topicoEncontrado.get().getAutor().getNombre(),
                    topicoEncontrado.get().getCurso().getNombre()
            );
            return ResponseEntity.ok(detalle);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar topico
    @PutMapping("/{id}")
    public ResponseEntity<DatosRespuestaTopico> actualizarTopico(
            @PathVariable Long id,
            @RequestBody ActualizarTopico actualizarTopico) {
        Optional<Topico> topicoEncontrado = topicoRepository.findById(id);

        if (topicoEncontrado.isPresent()) {
            Topico topicoExistente = topicoEncontrado.get();
            topicoExistente.setTitulo(actualizarTopico.titulo());
            topicoExistente.setMensaje(actualizarTopico.mensaje());
            topicoExistente.getAutor().setNombre(actualizarTopico.autor());
            topicoExistente.getCurso().setNombre(actualizarTopico.curso());

            // Guarda los cambios en la base de datos
            topicoRepository.save(topicoExistente);

            // Devuelve una respuesta exitosa
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Borrar topico
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarLogicamenteTopico(@PathVariable Long id) {
        topicoRepository.findById(id).ifPresent(topico -> {
            topico.setStatus(false);
            topicoRepository.save(topico);
        });
        return ResponseEntity.noContent().build();
    }
}
