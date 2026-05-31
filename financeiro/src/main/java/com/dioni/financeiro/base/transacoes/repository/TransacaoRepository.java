package com.dioni.financeiro.base.transacoes.repository;

import com.dioni.financeiro.base.auth.model.Usuario;
import com.dioni.financeiro.base.transacoes.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.dioni.financeiro.base.transacoes.repository.TransacaoQueries.FILTRAR_TRANSACAO_POR_MES_PAGINADO;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query(value = FILTRAR_TRANSACAO_POR_MES_PAGINADO)
    Page<Transacao> findByMesAndAno(@Param("mes") int mes,
                                    @Param("ano") int ano,
                                    @Param("usuario") Usuario usuario,
                                    Pageable pageable);

    List<Transacao> findByUsuario(Usuario usuario);

    Page<Transacao> findByUsuario(Usuario usuario, Pageable pageable);
}
