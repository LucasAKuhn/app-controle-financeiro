package com.LKsmartTech.minhasfinancas.service.imp;

import com.LKsmartTech.minhasfinancas.exception.ErroAutenticacao;
import com.LKsmartTech.minhasfinancas.exception.RegraNegocioException;
import com.LKsmartTech.minhasfinancas.model.Repository.UsuarioRepository;
import com.LKsmartTech.minhasfinancas.model.entity.Usuario;
import com.LKsmartTech.minhasfinancas.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImp implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImp(UsuarioRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);
        if(!usuario.isPresent()) {
            throw new ErroAutenticacao("Usuario   não localizado");
        }
        if(!usuario.get().getSenha().equals(senha)) {
            throw new ErroAutenticacao("Senha inválida");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if(existe) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return repository.findById(id);
    }
}