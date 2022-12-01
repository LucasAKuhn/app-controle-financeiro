package com.LKsmartTech.minhasfinancas.api.resource;

import com.LKsmartTech.minhasfinancas.api.dto.UsuarioDTO;
import com.LKsmartTech.minhasfinancas.exception.ErroAutenticacao;
import com.LKsmartTech.minhasfinancas.exception.RegraNegocioException;
import com.LKsmartTech.minhasfinancas.model.Repository.UsuarioRepository;
import com.LKsmartTech.minhasfinancas.model.entity.Usuario;
import com.LKsmartTech.minhasfinancas.service.LancamentoService;
import com.LKsmartTech.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {

    private final UsuarioService service;
    private final LancamentoService lancamentoService;

    private final UsuarioRepository repository;

    @PostMapping("/autenticar")
    public ResponseEntity autenticar ( @RequestBody UsuarioDTO dto) {
        try {
            Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
            return ResponseEntity.ok(usuarioAutenticado);
        }catch (ErroAutenticacao e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {

        Usuario usuario = Usuario.builder()    //transformando DTO em entidade Usuario
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();

        try {
            Usuario usuarioSalvo = service.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/saldo/{id}")
    public ResponseEntity obterSaldo( @PathVariable("id") Long id ) {
        Optional<Usuario> usuario = service.obterPorId(id);

        if(!usuario.isPresent()) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }

    @GetMapping("/findAll")
    public List<Usuario> findAll() {

        List<Usuario> usuarios = repository.findAll();
        return usuarios;

    }

}
