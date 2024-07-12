package com.oracle.alura.challenge.forohub.domain.topico;

import com.oracle.alura.challenge.forohub.domain.autor.Autor;
import com.oracle.alura.challenge.forohub.domain.autor.AutorRepository;
import com.oracle.alura.challenge.forohub.domain.curso.Curso;
import com.oracle.alura.challenge.forohub.domain.curso.CursoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Transactional
    public DatosRespuestaTopico registrarTopico(DatosRegistroTopico datosRegistroTopico) {
        // Validar duplicados
        if (topicoRepository.existsByTituloAndMensaje(datosRegistroTopico.titulo(), datosRegistroTopico.mensaje())) {
            return new DatosRespuestaTopico("Título y mensaje duplicados");
        }

        // Obtener o crear el autor
        Autor autor = autorRepository.findByNombre(datosRegistroTopico.autor())
                .orElseGet(() -> {
                    Autor nuevoAutor = new Autor();
                    nuevoAutor.setNombre(datosRegistroTopico.autor());
                    autorRepository.save(nuevoAutor);
                    return nuevoAutor;
                });

        // Obtener o crear el curso
        Curso curso = cursoRepository.findByNombre(datosRegistroTopico.curso())
                .orElseGet(() -> {
                    Curso nuevoCurso = new Curso();
                    nuevoCurso.setNombre(datosRegistroTopico.curso());
                    cursoRepository.save(nuevoCurso);
                    return nuevoCurso;
                });

        // Crear una nueva instancia de Topico con los datos recibidos
        Topico topico = new Topico();
        topico.setTitulo(datosRegistroTopico.titulo());
        topico.setMensaje(datosRegistroTopico.mensaje());
        topico.setAutor(autor);
        topico.setCurso(curso);

        // Guardar el nuevo topico en la base de datos
        topicoRepository.save(topico);

        // Crear los datos de respuesta
        return new DatosRespuestaTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getAutor().getNombre(),
                topico.getCurso().getNombre()
        );
    }
}
