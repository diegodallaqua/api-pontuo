/**
 * Endpoints REST da aplicação.
 *
 * <p>Cada controller é a <b>Facade</b> do seu recurso: ele expõe uma interface
 * simples (a rota HTTP) e esconde de quem consome a API os subsistemas
 * envolvidos na operação — service de regras de negócio, repositórios JPA,
 * conversão entre DTO e entidade e, nas rotas de autenticação, a emissão e a
 * revogação de tokens. O cliente chama {@code POST /api/addresses} e não
 * precisa saber que existe um {@code AddressService}, um
 * {@code AddressRepository} e um {@code CityRepository} atrás disso.</p>
 *
 * <p>Os controllers são componentes Spring ({@code @RestController}), portanto
 * instanciados uma única vez pelo contêiner — ver
 * {@link com.pontuo.api_pontuo.service} para a nota sobre <b>Singleton</b>.</p>
 */
package com.pontuo.api_pontuo.controller;
