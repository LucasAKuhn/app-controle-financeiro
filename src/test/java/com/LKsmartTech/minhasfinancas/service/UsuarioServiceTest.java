package com.LKsmartTech.minhasfinancas.service;

import com.LKsmartTech.minhasfinancas.exception.ErroAutenticacao;
import com.LKsmartTech.minhasfinancas.exception.RegraNegocioException;
import com.LKsmartTech.minhasfinancas.model.Repository.UsuarioRepository;
import com.LKsmartTech.minhasfinancas.model.entity.Usuario;
import com.LKsmartTech.minhasfinancas.service.imp.UsuarioServiceImp;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImp service;
    @MockBean
    UsuarioRepository repository;

    @Test(expected = Test.None.class)
    public void deveSalvarUmUsuario() {
        //cenario
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .id(1l)
                .nome("nome")
                .email("email@email.com")
                .senha("senha")
                .build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //acao
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        //verificaçao
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }

    @Test(expected = RegraNegocioException.class)
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
        //cenario
        String email = "email@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //acao
        service.salvarUsuario(usuario);

        //verificacao
        Mockito.verify( repository, Mockito.never()).save(usuario); //para dar certo, espero que nunca chegue a salvar usuario
    }


    @Test(expected = Test.None.class)   //espera que  não lance nenhuma Exceptio
    public void deveValidarEmail() {
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //UsuarioRepository usuarioRepositoryMock = Mockito.mock(UsuarioRepository.class);

        //acao
        service.validarEmail("email@email.com");
    }

    @Test(expected = Test.None.class)
    public void deveAutenticarUmUsuarioComSucesso() {
        //cenario
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email("email").senha("senha").id(1l).build();
        Mockito.when( repository.findByEmail(email)).thenReturn(Optional.of(usuario));
        //acao
        Usuario result = service.autenticar(email, senha);

        //verificacao
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado(){
        //cenario
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        //acao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));

        //verificação
        Assertions.assertThat(exception)
                .isInstanceOf(ErroAutenticacao.class)
                .hasMessage("Usuario   não localizado");
    }

//    @Test
//    public void deveLancarErroQuandoSenhaNaoBater() {
//        //cenario
//        Usuario usuario = Usuario.builder().email("email@email.com").senha("senha").build();
//        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
//
//        //acao
//        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));
//        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("a");
//    }

    @Test(expected = RegraNegocioException.class)   //espera que lance uma exception
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //acao
        service.validarEmail("email@email.com");
    }

}
