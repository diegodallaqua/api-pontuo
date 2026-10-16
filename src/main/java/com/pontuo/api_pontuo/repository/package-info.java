/**
 * Acesso a dados.
 *
 * <p>Implementação do padrão <b>Repository</b>: cada interface daqui estende
 * {@code JpaRepository} e representa a coleção de uma entidade, com uma API de
 * consulta em termos de domínio ({@code findByCityId}, {@code findByUsername})
 * no lugar de SQL espalhado pelos services. Nenhuma dessas interfaces é
 * implementada à mão — o Spring Data gera a implementação em tempo de
 * execução, a partir do nome dos métodos, e registra o proxy como bean
 * ({@code @Repository}). Onde o nome do método não dá conta, como na busca de
 * questões com filtros que se combinam, a consulta é declarada em JPQL com
 * {@code @Query} — segue sendo o Spring Data que a executa.</p>
 *
 * <p>Como o contrato é uma interface, a fonte de dados é trocável sem mudar os
 * services, e nos testes unitários o repositório é substituído por um mock.</p>
 */
package com.pontuo.api_pontuo.repository;
