/**
 * Regras de negócio da aplicação.
 *
 * <p>Toda classe daqui é anotada com {@code @Service}, ou seja, é um bean do
 * Spring com escopo padrão {@code singleton}: o contêiner cria uma única
 * instância e a injeta em todos os controllers que a declaram no construtor.
 * Esse é o <b>Singleton</b> na prática com Spring — sem campo estático e sem
 * {@code getInstance()}, o ciclo de vida fica com o contêiner, o que mantém as
 * classes testáveis (nos testes, as dependências entram como mocks).</p>
 *
 * <p>Os services também são o ponto em que o padrão <b>Strategy</b> entraria:
 * hoje cada domínio tem uma implementação só, e por isso a injeção é feita
 * pela classe concreta. Se um domínio passar a ter mais de uma variação de
 * regra, o caminho é extrair uma interface (ex.: {@code QuestionService}) e
 * mover a implementação atual para {@code service.impl}; os controllers
 * passariam a depender da interface, e a escolha da implementação ficaria com
 * o Spring. Strategy já aplicado no projeto está em
 * {@link com.pontuo.api_pontuo.security}.</p>
 */
package com.pontuo.api_pontuo.service;
