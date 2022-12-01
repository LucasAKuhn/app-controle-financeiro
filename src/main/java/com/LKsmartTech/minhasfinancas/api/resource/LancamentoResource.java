package com.LKsmartTech.minhasfinancas.api.resource;

import com.LKsmartTech.minhasfinancas.api.dto.AtualizarStatusDTO;
import com.LKsmartTech.minhasfinancas.api.dto.LancamentoDTO;
import com.LKsmartTech.minhasfinancas.exception.RegraNegocioException;
import com.LKsmartTech.minhasfinancas.model.entity.Lancamento;
import com.LKsmartTech.minhasfinancas.model.entity.Usuario;
import com.LKsmartTech.minhasfinancas.model.enums.StatusLancamento;
import com.LKsmartTech.minhasfinancas.model.enums.TipoLancamento;
import com.LKsmartTech.minhasfinancas.service.LancamentoService;
import com.LKsmartTech.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

    private final LancamentoService service;

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario
    ) {
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if(!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possivel realizar a consulta. Usuario não encontrado");
        } else {
            lancamentoFiltro.setUsuario(usuario.get());
        }

        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
        try {
            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);
            return ResponseEntity.ok(entidade);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar (@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return service.obterPorId(id).map( entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            }catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () ->
                new ResponseEntity<>("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualizar-status")
    public ResponseEntity atualizarStatus( @PathVariable("id") Long id, @RequestBody AtualizarStatusDTO dto){
        return service.obterPorId(id).map( entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if(statusSelecionado == null) {
                return ResponseEntity.badRequest().body(("Não foi possivel atualizar o status de lançamento."));
            }

            try {
                entity.setStatus(statusSelecionado);
                service.atualizar(entity);
                return ResponseEntity.ok(entity);
            }catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () ->
                new ResponseEntity("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST) );
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar( @PathVariable("id") Long id) {
        return service.obterPorId(id).map( entidade -> {
            service.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet( () ->
                new ResponseEntity<>("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    private Lancamento converter(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();

        lancamento.setDescricao(dto.getDescricao());
        lancamento.setId(dto.getId());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService
                .obterPorId(dto.getUsuario())
                .orElseThrow( () -> new RegraNegocioException("Usuário não " +
                        "encontrado para o Id informado."));

        lancamento.setUsuario(usuario);

        if(dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }
        if(dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }

        return lancamento;
    }

}
