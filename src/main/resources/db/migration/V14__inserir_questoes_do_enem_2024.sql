-- Questões objetivas do ENEM 2024 (1º Dia e 2º Dia) que não dependem de imagem.
--
-- Fonte: cadernos impressos do Inep (1º Dia: Caderno 1 Azul; 2º Dia: Caderno 7
-- Azul) e os gabaritos oficiais desses mesmos cadernos. A numeração nos
-- comentários é a do Caderno Azul.
--
-- Ficam de fora:
--   * questões que dependem de imagem (figura, charge, mapa, gráfico, esquema,
--     fórmula estrutural) ou de tabela/quadro;
--   * questões cujas alternativas ou fórmulas não puderam ser transcritas em
--     texto com segurança (2º Dia: 149, 166, 169, 170 e 180);
--   * a questão anulada pelo Inep (2º Dia: 129).
--
-- As questões de 01 a 05 do 1º Dia entram uma vez por língua estrangeira
-- (inglês e espanhol), porque são questões diferentes com o mesmo número. O
-- texto-base comum a mais de uma questão é repetido em cada uma delas, já que
-- a questão é sorteada sozinha em um simulado.
--
-- Cada questão é classificada por tópico (topic_id), que já define a matéria e
-- a área de conhecimento, e traz a resolução comentada em explanation. O Inep
-- não divulga a dificuldade das questões; todas entram como 2 (média) até haver
-- um critério próprio. Fórmulas foram transcritas em texto, com índices e
-- expoentes em Unicode e frações na forma a/b.
--
-- Os ids são gerados pelo banco: cada questão é inserida e suas alternativas
-- usam LAST_INSERT_ID(), então a migration não colide com questões já
-- cadastradas pela API.


-- O vestibular de cada dia é buscado pela chave natural (INEP, ENEM, 2024,
-- etapa), e não pelo id: bancos criados antes do Flyway podem ter ids
-- diferentes ou não ter alguma das etapas, que então é criada aqui.
INSERT INTO entrance_exam (institution_id, name, year, stage)
SELECT i.id, 'ENEM', 2024, s.stage
FROM institution i
    CROSS JOIN (SELECT '1º Dia' AS stage UNION ALL SELECT '2º Dia') s
WHERE i.acronym = 'INEP'
  AND NOT EXISTS (SELECT 1
                  FROM entrance_exam e
                  WHERE e.institution_id = i.id
                    AND e.name = 'ENEM'
                    AND e.year = 2024
                    AND e.stage = s.stage);

SET @enem_2024_dia_1 = (SELECT e.id
                        FROM entrance_exam e
                            JOIN institution i ON i.id = e.institution_id
                        WHERE i.acronym = 'INEP' AND e.name = 'ENEM' AND e.year = 2024 AND e.stage = '1º Dia');
SET @enem_2024_dia_2 = (SELECT e.id
                        FROM entrance_exam e
                            JOIN institution i ON i.id = e.institution_id
                        WHERE i.acronym = 'INEP' AND e.name = 'ENEM' AND e.year = 2024 AND e.stage = '2º Dia');


-- ENEM 2024 — 1º Dia ----------------------------------------

-- 1º Dia, questão 01, espanhol (Linguagens e Códigos › Língua Espanhola › Interpretação de Texto em Espanhol). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 88, NULL, 'El vírus de la cancelación

¿De qué se trata este fenómeno? Se entiende por cultura de la cancelación a una práctica popular que consiste en “quitarle apoyo” especialmente a figuras públicas y compañías multinacionales después de que hayan hecho o dicho algo considerado objetable u ofensivo. Cuando alguien o algo está cancelado se descarta, se deja de ver, se deja de escuchar, se desclasifica, se aísla, se abandona, se niega, se deja de consumir hasta que eventualmente puede o no desaparecer. Es una estrategia muy extendida en la historia de las luchas anticoloniales, antiespecistas, sexodisidentes, feministas y, especialmente en nuestro país, también llevadas adelante por el movimiento de derechos humanos. Entonces, ¿dónde radica el problema? Se puede observar positivamente que hay un proceso cada vez más agudo de socialización de herramientas críticas para desmantelar formas de desigualdad incrustadas en los lazos sociales. Pero la popularización irrestricta y el uso amplificado de esta herramienta por fuera de sus contextos colectivos de emergencia han despertado efectos adversos en una sociedad atravesada por las pantallas como formas de encierro-consumo, la representación online como única esfera pública y un imperativo felicista cuya moral nos obliga a trabajar ansiosamente por una vida sin desacuerdos, sin errores y sin dolor, a como dé lugar.

Disponível em: www.revistaanfibia.com. Acesso em: 7 out. 2021 (adaptado).

Na argumentação apresentada sobre cultura do cancelamento, esse texto objetiva', 'O texto apresenta a cultura do cancelamento, reconhece um lado positivo, mas afirma que sua popularização irrestrita e seu uso fora dos contextos coletivos de origem geram efeitos adversos numa sociedade em que a representação on-line é a única esfera pública. O objetivo é, portanto, criticar o impacto dessa cultura sobre a vida representada nas redes (C). A definição (A) e a menção histórica (E) são apenas pontos de partida, e o texto não trata da postura das mídias (B) nem elogia a democratização da prática (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'apresentar o conceito desse tipo de prática.', 0),
    (@question_id, 'B', 'mostrar a contrariedade das mídias com relação a essa atitude.', 0),
    (@question_id, 'C', 'criticar o impacto dessa cultura sobre a vida representada nas redes.', 1),
    (@question_id, 'D', 'evidenciar a democratização dessa prática na sociedade virtual.', 0),
    (@question_id, 'E', 'discorrer historicamente sobre a origem e as causas dessa cultura.', 0);

-- 1º Dia, questão 02, inglês (Linguagens e Códigos › Língua Inglesa › Conectivos e Coesão). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 87, NULL, 'Holy War

Oh, so we can hate each other and fear each other
We can build these walls between each other
Baby, blow by blow and brick by brick
Keep yourself locked in, yourself locked in
[…]
Oh, maybe we should love somebody
Oh, maybe we could care a little more
So maybe we should love somebody
Instead of polishing the bombs of holy war

KEYS, A. Here. Estados Unidos: RCA Records, 2016.

Nessa letra de canção, que aborda um contexto de ódio e intolerância, o marcador “instead of” introduz a ideia de', 'Na letra, o eu lírico propõe que, em vez de odiar, temer e “polir as bombas da guerra santa”, as pessoas amem alguém e se importem um pouco mais. O marcador “instead of” (em vez de) contrapõe essas duas atitudes e introduz, portanto, a ideia de mudança de comportamento (A). Conflitos (B), isolamento (C), guerra (D) e religião (E) aparecem no texto, mas como aquilo que deve ser abandonado, não como a ideia que o marcador introduz.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'mudança de comportamento.', 1),
    (@question_id, 'B', 'panorama de conflitos.', 0),
    (@question_id, 'C', 'rotina de isolamento.', 0),
    (@question_id, 'D', 'perspectiva bélica.', 0),
    (@question_id, 'E', 'cenário religioso.', 0);

-- 1º Dia, questão 02, espanhol (Linguagens e Códigos › Língua Espanhola › Interpretação de Texto em Espanhol). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 88, NULL, '“Y si llueve, que llueva” es un refrán gallego. Para mí cobró sentido una noche de febrero, cuando vivía en el barrio de la Macarena de Sevilla con dos buenos amigos, gallegos también. Mi compañero y yo nos decidimos a salir ese sábado de noche, pese a que había estado lloviendo algunas horas a lo largo del día. La idea era una locura, al parecer. Le propusimos salir “de parranda” con nosotros a una amiga andaluza, ésta respondió que no, nos dijo que no iba a salir un día de lluvia. Flipamos. Comentamos entre nosotros que si los gallegos no saliésemos de casa cuando llueve, en invierno saldríamos poco. Habríamos inventado el confinamiento hace mucho.

Los gallegos no dejamos de salir por la lluvia.

Disponível em: https://politicahora.es. Acesso em: 26 out. 2021.

O comportamento dos personagens narrado no texto destaca o(a)', 'O narrador conta que, numa noite chuvosa em Sevilha, ele e o amigo galego quiseram sair, enquanto a amiga andaluza recusou, e conclui que “los gallegos no dejamos de salir por la lluvia”. O comportamento narrado destaca, assim, a diferença cultural entre galegos e andaluzes (D). Não há abandono da identidade (A), medo da noite (B), influência do grupo na decisão (C) nem comparação meteorológica entre Sevilha e a Galícia (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'abandono da própria identidade.', 0),
    (@question_id, 'B', 'medo dos perigos durante a noite.', 0),
    (@question_id, 'C', 'influência do grupo na tomada de decisão.', 0),
    (@question_id, 'D', 'diferença cultural entre galegos e andaluzes.', 1),
    (@question_id, 'E', 'variação meteorológica entre Sevilha e Galiza.', 0);

-- 1º Dia, questão 03, espanhol (Linguagens e Códigos › Língua Espanhola › Interpretação de Texto em Espanhol). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 88, NULL, 'Celerina Patricia Sánchez Santiago comentó que su acercamiento a la poesía fue por la escuela, con libros de texto en español. Descubrió el gusto por las letras pero notó que no había textos en la suya, así que, contra muchos comentarios negativos que recibió en ese momento, decidió emprender el camino de la escritura pero en mixteco. “Fue un proceso de años para notar que en mi lengua podía escribir poesía, porque me decían que mi lengua era tan pobre que no podía tener conceptos abstractos, era un reto pero yo sabía que sí era posible”.

Este proceso no sólo le ayudó a vivir la poesía en mixteco, sino a “ir descubriendo mi propia historia y la de mi pueblo”. Comentó que las lenguas habladas en el territorio nacional se encuentran en gran desventaja con el español. “El reconocimiento a la diversidad no se ha hecho y ha sido tratada de borrar. Este es un país con 68 lenguas y somos monolingües del español. Antes había nulo de reconocimiento de ser bilingüe, negabas que hablabas tu lengua, hasta le decían dialecto. Era parte del proceso fatal que nos llevó a no reconocernos en un país multilingüe. ¿Si tenemos varias lenguas por qué no aprender?”.

Disponível em: www.fapcom.edu.br. Acesso em: 20 nov. 2021.

Em sua escrita poética, a poetisa mexicana Celerina Patricia Sánchez Santiago assume o desafio de', 'Celerina Sánchez Santiago decidiu escrever poesia em mixteco, apesar das críticas de que sua língua seria pobre demais para conceitos abstratos, e lembra que o México tem 68 línguas, embora seja “monolíngue em espanhol”. Ao escrever em mixteco, ela assume o desafio de representar a pluralidade linguística de seu país (D). A importância da literatura na escola (A) e o autoconhecimento (E) são consequências mencionadas, mas não o desafio assumido; a hegemonia da literatura em espanhol (B) é o contexto que ela enfrenta.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'destacar a importância da literatura na formação escolar.', 0),
    (@question_id, 'B', 'discutir a hegemonia da literatura escrita em espanhol.', 0),
    (@question_id, 'C', 'promover reflexões acerca de conceitos abstratos.', 0),
    (@question_id, 'D', 'representar a pluralidade linguística de seu país.', 1),
    (@question_id, 'E', 'narrar uma trajetória de autoconhecimento.', 0);

-- 1º Dia, questão 04, inglês (Linguagens e Códigos › Língua Inglesa › Interpretação de Texto em Inglês). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 83, NULL, 'I remember being caught speaking Spanish at recess [...] I remember being sent to the corner of the classroom for “talking back” to the Anglo teacher when all I was trying to do was tell her how to pronounce my name. “If you want to be American, speak ‘American’. If you don’t like it, go back to Mexico where you belong”.

“I want you to speak English […]”, my mother would say, mortified that I spoke English like a Mexican. At Pan American University, I and all Chicano students were required to take two speech classes. Their purpose: to get rid of our accents.

ANZALDÚA, G. Borderlands/La Frontera: The New Mestiza. San Francisco: Aunt Lute Books, 1987.

O problema abordado nesse texto sobre imigrantes residentes nos Estados Unidos diz respeito aos prejuízos gerados pelo(a)', 'A autora relata ter sido repreendida por falar espanhol no recreio, ouvir “se você quer ser americano, fale americano” e ser obrigada, na universidade, a fazer aulas para eliminar o sotaque. O problema abordado é o repúdio ao sotaque espanhol no uso do inglês (A). O texto não fala em resignação (B), em falta de oportunidades de aprender espanhol (C), em conflito entre gerações (D) nem em disputa entre variações das duas línguas (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'repúdio ao sotaque espanhol no uso do inglês.', 1),
    (@question_id, 'B', 'resignação diante do apagamento da língua materna.', 0),
    (@question_id, 'C', 'escassez de oportunidades de aprendizado do espanhol.', 0),
    (@question_id, 'D', 'choque entre falantes de línguas distintas de diferentes gerações.', 0),
    (@question_id, 'E', 'concorrência entre as variações linguísticas do inglês e as do espanhol.', 0);

-- 1º Dia, questão 04, espanhol (Linguagens e Códigos › Língua Espanhola › Interpretação de Texto em Espanhol). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 88, NULL, 'Morir muy vivos

No todo es perder, es cierto. Si te esfuerzas mucho y bien, porque no viene de fábrica, ganas conocimiento del mundo y de ti mismo, empatía, sosiego y, en suma, algo que podríamos denominar sabiduría. Pero creo que para ello hay que mantenerse alerta y no darse nunca por vencido. Pero también es un tiempo para saldar cuentas. No creo que haya que dejarse llevar por el peso de los días como un leño podrido al que las olas arrojan finalmente a la playa. Uno siempre puede intentar sacarse alguna de las piedras que lleva a la espalda, decir las cosas que nunca se atrevió a decir, cumplir en la medida de lo posible los deseos arrumbados, rescatar algún sueño que quedó en la cuneta. No rendirse, esa es la clave. Y sobre todo decirse: ¿y por qué no? Porque la vejez no está reñida con la audacia. Debemos aspirar a morir muy vivos.

MONTERO, R. Disponível em: https://elpais.com. Acesso em: 4 dez. 2017.

Nesse texto, ao utilizar a expressão “morir muy vivos”, a escritora Rosa Montero evidencia a importância de se', 'Rosa Montero defende que a velhice não está “reñida con la audacia” e que se deve aspirar a “morir muy vivos”: não se render, realizar desejos adiados e seguir sonhando. A expressão evidencia a importância de desfrutar de todas as fases da vida (D). Acumular sabedoria (A) e rever erros e acertos (C) são tarefas citadas, mas a expressão aponta para viver plenamente, não para o corpo (B) nem para libertar-se de amarras sociais (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'acumular sabedoria com o passar do tempo.', 0),
    (@question_id, 'B', 'observar o impacto dos anos sobre o corpo.', 0),
    (@question_id, 'C', 'rever os erros e os acertos de sua trajetória.', 0),
    (@question_id, 'D', 'desfrutar de todas as fases da vida.', 1),
    (@question_id, 'E', 'libertar das amarras sociais.', 0);

-- 1º Dia, questão 05, espanhol (Linguagens e Códigos › Língua Espanhola › Interpretação de Texto em Espanhol). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 88, NULL, 'Los últimos días del sitio de Tenochtitlán

Y todo esto pasó con nosotros.
Nosotros lo vimos,
nosotros lo admiramos.
Con esta lamentosa y triste suerte
nos vimos angustiados.
En los caminos yacen dardos rotos,
los cabellos están esparcidos.
Destechadas están las casas,
enrojecidos tienen sus muros.
Gusanos pululan por calles y plazas,
y en las paredes están salpicados los sesos.
Rojas están las aguas, están como teñidas,
y cuando la bebimos,
es como si bebiéramos agua de salitre.

Manuscrito anónimo de Tlatelolco, 1528. Disponível em: www.biblioweb.tic.unam.mx. Acesso em: 13 out. 2021 (fragmento).

Nesse poema, o eu lírico representa a voz de um sobrevivente asteca que testemunha a', 'O poema, atribuído a um manuscrito anônimo de Tlatelolco (1528), descreve dardos quebrados nos caminhos, casas destelhadas, vermes nas ruas e águas tingidas de sangue durante o cerco de Tenochtitlán. O eu lírico é um sobrevivente asteca que testemunha a destruição da capital do Império Asteca pelos colonizadores espanhóis (A). Não se trata de degradação ambiental (B), de exílio (C), de recepção dos espanhóis (D) nem de resistência a mudanças administrativas (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'destruição da capital do Império Asteca pelos colonizadores espanhóis.', 1),
    (@question_id, 'B', 'degradação do meio ambiente no entorno da capital do Império Asteca.', 0),
    (@question_id, 'C', 'tristeza dos refugiados astecas ao deixarem a capital do Império rumo ao exílio.', 0),
    (@question_id, 'D', 'aflição dos astecas ao receberem os colonizadores espanhóis na capital do Império.', 0),
    (@question_id, 'E', 'resistência dos astecas às mudanças feitas pelos colonizadores espanhóis na capital do Império.', 0);

-- 1º Dia, questão 06 (Linguagens e Códigos › Língua Portuguesa › Variação Linguística). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 73, NULL, 'Expressões e termos utilizados no Amazonas são retratados em livro e em camisetas

“Na linguagem, podemos nos ver da forma mais verdadeira: nossas crenças, nossos valores, nosso lugar no mundo”, afirmou o doutor em linguística e professor da Ufam em seu livro Amazonês: expressões e termos usados no Amazonas. Portanto, o amazonense, com todas as suas “cunhantãs” e “curumins”, acaba por encontrar um lugar no mundo e formar uma unidade linguística, informalmente denominada de português “caboco”, que muito se diferencia do português “mineiro”, “gaúcho”, “carioca” e de tantos outros espalhados pelo Brasil. O livro, que conta com cerca de 1 100 expressões e termos típicos do falar amazonense, levou dez anos para ser construído. Para o autor, o principal objetivo da obra é registrar a linguagem.

Um designer amazonense também acha o amazonês “xibata”, tanto é que criou uma série de camisetas estampadas com o nome de Caboquês Ilustrado, que mistura o bom humor com as expressões típicas da região. A coleção conta com sete modelos já lançados, entre eles: Leseira Baré, Xibata no Balde e Até o Tucupi, e 43 ainda na fila de espera. Para o criador, as camisas têm como objetivo “resgatar o orgulho do povo manauara, do povo do Norte”.

Disponível em: https://g1.globo.com. Acesso em: 15 jan. 2024 (adaptado).

A reportagem apresenta duas iniciativas: o livro Amazonês e as camisetas do Caboquês Ilustrado. Com temática em comum, essas iniciativas', 'A reportagem apresenta um livro que registra 1 100 expressões do falar amazonense e camisetas que usam essas expressões para “resgatar o orgulho do povo do Norte”. O ponto em comum é a valorização do repertório linguístico do povo do Amazonas (E). Não há recomendação de produtos (A), nem foco nas diferenças em relação a outros falares (B) ou no trabalho de pesquisadores (C); o humor das camisetas (D) é apenas um recurso, não a temática comum.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'recomendam produtos feitos por empreendedores da região Norte.', 0),
    (@question_id, 'B', 'ressaltam diferenças entre o falar manauara e outros falares.', 0),
    (@question_id, 'C', 'reverenciam o trabalho feito por pesquisadores brasileiros.', 0),
    (@question_id, 'D', 'destacam a descontração no jeito de ser do amazonense.', 0),
    (@question_id, 'E', 'valorizam o repertório linguístico do povo do Amazonas.', 1);

-- 1º Dia, questão 07 (Linguagens e Códigos › Língua Portuguesa › Gêneros Textuais). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 74, NULL, 'Conheça histórias de atletas paralímpicas que trocaram de modalidade durante a carreira esportiva

Jane Karla: a goiana de 45 anos teve poliomielite aos três anos, o que prejudicou seus movimentos das pernas. Em 2003, iniciou no tênis de mesa e conseguiu conquistar títulos nacionais e internacionais. Mas conheceu o tiro com arco e, em 2015, optou por se dedicar somente à nova modalidade. Em seu ano de estreia no tiro, já faturou a medalha de ouro nos Jogos Parapan-Americanos de Toronto 2015.

Elizabeth Gomes: a santista de 55 anos era jogadora de vôlei quando foi diagnosticada com esclerose múltipla em 1993. Ingressou no Movimento Paralímpico pelo basquete em cadeira de rodas até experimentar o atletismo.

Chegou a praticar as duas modalidades simultaneamente até optar pelas provas de campo em 2010. No Campeonato Mundial de Atletismo, realizado em Dubai, em 2019, Beth se sagrou campeã do lançamento de disco e estabeleceu um novo recorde mundial da classe F52.

Silvana Fernandes: a paraibana de 21 anos é natural de São Bento e nasceu com malformação no braço direito.

Aos 15 anos, começou a praticar atletismo no lançamento de dardo. Em 2018, enquanto competia na regional Norte-Nordeste, foi convidada para conhecer o paratae kwon do. No ano seguinte, migrou para a modalidade e já faturou o ouro na categoria até 58 kg nos Jogos Parapan-Americanos de Lima 2019.

Disponível em: https://cpb.org.br. Acesso em: 15 jan. 2024 (adaptado).

Esse conjunto de minibiografias tem como propósito', 'O texto reúne as histórias de Jane Karla, Elizabeth Gomes e Silvana Fernandes, que mudaram de modalidade ao longo da carreira e conquistaram títulos. O propósito é destacar a trajetória profissional de atletas paralímpicas brasileiras (C). Não há descrição de rotinas de treino (A), comparação de desempenhos (B), indicação de categorias adequadas (D) nem apelo para que mais mulheres participem de campeonatos (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'descrever as rotinas de treinamento das atletas.', 0),
    (@question_id, 'B', 'comparar os desempenhos de atletas de alto rendimento.', 0),
    (@question_id, 'C', 'destacar a trajetória profissional de atletas paralímpicas brasileiras.', 1),
    (@question_id, 'D', 'indicar as categorias mais adequadas a adaptações paralímpicas.', 0),
    (@question_id, 'E', 'estimular a participação de mulheres em campeonatos internacionais.', 0);

-- 1º Dia, questão 08 (Linguagens e Códigos › Artes › Música e Movimentos Musicais). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 95, NULL, 'É fundamentalmente no Minho, norte de Portugal, que o cavaquinho aparece como instrumento tipicamente popular, ligado às formas essenciais da música característica dessa província. O cavaquinho minhoto tem escala rasa com o tampo, o que facilita a prática do “rasqueado”. O cavaquinho chega ao Brasil diretamente de Portugal, e o modelo brasileiro é maior do que a sua versão portuguesa, com uma caixa de ressonância mais funda. Semelhante ao cavaquinho minhoto, o machete, ou machetinho madeirense, é um pequeno cordófono de corda dedilhada, que faz parte da grande e diversificada família das violas de mão portuguesas. O ukulele tem a sua origem no século XIX, tendo como ancestrais o braguinha (ou machete) e o rajão, instrumentos levados pelos madeirenses quando eles emigraram para o Havaí.

OLIVEIRA, E. V. Cavaquinhos e família. Disponível em: https://casadaguitarra.pt. Acesso em: 18 nov. 2021 (adaptado).

O conjunto dessas práticas musicais demonstra que os instrumentos mencionados no texto', 'O texto mostra o cavaquinho português chegando ao Brasil em versão maior e com caixa de ressonância mais funda, e o ukulele originado do braguinha levado pelos madeirenses ao Havaí. Os instrumentos, portanto, adaptam suas características a cada cultura, assumindo nova identidade (B). O texto não fala de dependência de matéria-prima europeia (A), não afirma hegemonia portuguesa (C), não trata de dominação cultural (D) e mostra mudança de nome e forma, não manutenção da nomenclatura original (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'refletem a dependência da utilização de matéria-prima europeia.', 0),
    (@question_id, 'B', 'adaptam suas características a cada cultura, assumindo nova identidade.', 1),
    (@question_id, 'C', 'comprovam a hegemonia portuguesa na invenção de cordófonos dedilhados.', 0),
    (@question_id, 'D', 'ilustram processos de dominação cultural, evidenciando situações de choque cultural.', 0),
    (@question_id, 'E', 'mantêm nomenclatura própria para garantir a fidelidade às formas originais de confecção.', 0);

-- 1º Dia, questão 09 (Linguagens e Códigos › Educação Física › Corpo e Saúde). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 98, NULL, 'Pressão, depressão, estresse e crise de ansiedade.

Os males da sociedade contemporânea também estão no esporte. A tenista Naomi Osaka, do Japão, jogadora mais bem paga do mundo e que já ocupou o número 2 do ranking, retirou-se do torneio de Roland Garros de 2021 porque não estava conseguindo administrar as crises de ansiedade provocadas pelos grandes eventos, por ser uma estrela aos 23 anos, e pelo peso de parte da imprensa. O tenista australiano Nick Kyrgios, de 25 anos, revelou sua “situação triste e solitária” enquanto lutava contra a depressão causada pelo ritmo avassalador do Circuito Mundial de Tênis. O jogador de basquete americano Kevin Love também tornou público seu quadro de ansiedade e depressão. O mundo do atleta é solitário e distante da família. O que vemos numa partida não reflete a rotina desgastante. A imprensa denomina atletas como heróis, como se aquele corpo fosse indestrutível, mas a mente é o ponto fraco da história.

Disponível em: www.uol.com.br. Acesso em: 31 out. 2021 (adaptado).

As causas do desequilíbrio na saúde mental apontadas no texto estão relacionadas às', 'O texto cita Naomi Osaka, Nick Kyrgios e Kevin Love para mostrar que a ansiedade e a depressão dos atletas decorrem do “ritmo avassalador” de competições, da pressão dos grandes eventos e do peso da imprensa, que os trata como heróis. As causas estão, portanto, nas pressões constantes dos eventos e da mídia (E). Nacionalidade, modalidade e idade (A, B, C) são apenas características dos exemplos, e a representação heroica (D) é um efeito da mídia, não a causa citada isoladamente.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'nacionalidades diversificadas dos praticantes.', 0),
    (@question_id, 'B', 'modalidades esportivas distintas.', 0),
    (@question_id, 'C', 'faixas etárias aproximadas.', 0),
    (@question_id, 'D', 'representações heroicas dos atletas.', 0),
    (@question_id, 'E', 'pressões constantes dos eventos e da mídia.', 1);

-- 1º Dia, questão 10 (Linguagens e Códigos › Língua Portuguesa › Interpretação de Texto). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 72, NULL, 'Já ouvi gente falando que o podcast é o renascimento do rádio. O rádio é genial, uma mídia imorredoura, mas podcast não tem nada a ver com ele. O formato está mais próximo do ensaio literário do que de um programa de ondas curtas, médias ou longas.

Podcasts são antípodas das redes sociais. Enquanto elas são dispersivas, levam à evasão e à desinformação, os podcasts são uma possibilidade de imersão, concentração, aprendizado. Depois que eles surgiram, lavar a louça e me locomover pela cidade viraram um programaço. Um pós-almoço de domingo e aprendo tudo sobre bonobos e gorilas. Um táxi pro aeroporto e chego ao embarque PhD em reforma tributária.

PRATA, A. Disponível em: www1.folha.uol.com.br. Acesso em: 7 jan. 2024 (adaptado).

Segundo a argumentação construída nesse texto, o podcast', 'O autor afirma que o podcast não é o renascimento do rádio, aproxima-o do ensaio literário e diz que, ao contrário das redes sociais, ele é “uma possibilidade de imersão, concentração, aprendizado”. Segundo a argumentação, o podcast propicia divulgação de conhecimento aos usuários (C). A dispersão (A) e a interação semelhante à das redes (D) são atribuídas às redes sociais, e o texto nega que o podcast seja ondas de rádio (B) ou evolução do rádio (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'provoca dispersão da atenção em seu público.', 0),
    (@question_id, 'B', 'funciona por meio de uma frequência de ondas curtas.', 0),
    (@question_id, 'C', 'propicia divulgação de conhecimento para seus usuários.', 1),
    (@question_id, 'D', 'tem um formato de interação semelhante ao das redes sociais.', 0),
    (@question_id, 'E', 'constitui uma evolução na transmissão de informações via rádio.', 0);

-- 1º Dia, questão 11 (Linguagens e Códigos › Língua Portuguesa › Variação Linguística). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 73, NULL, 'Evanildo Bechara prepara a sua aposentadoria de pouco em pouco, como se a adiasse ao máximo. Aos 95 anos, o imortal da Academia Brasileira de Letras (ABL) alcançou um status de astro pop no mundo da filologia e da gramática. Quando ainda tinha saúde para viagens mais longas, o filólogo lotava plateias em suas palestras na Europa e no Brasil, que não raro terminavam com filas para selfies.

A idade acentuou o lado “cientista” e professoral de Bechara, que adota um tom técnico na conversa até mesmo diante das perguntas mais pessoais. — “Qual o seu tipo preferido de leitura?”. — “A minha leitura está dividida em duas partes, a científica e a literária, estabelecendo uma relação de causa e efeito entre elas.” — responde. Ainda adolescente, Bechara descobriu a lexicologia. Um “novo mundo” se abriu para o pernambucano, que se mantém atento às metamorfoses do nosso idioma. Seu colega de ABL, o filólogo Ricardo Cavaliere, se lembra de quando deu carona para o mestre e este encucou com os estrangeirismos do aplicativo de navegação instalado no veículo. — “A vozinha do aplicativo avisou que havia um radar de velocidade ‘reportado’ à frente”, lembra Cavaliere. — “Esse ‘reportado’ é uma importação, né?”, notou Bechara.

Disponível em: https://oglobo.globo.com. Acesso em: 3 jan. 2024 (adaptado).

Nesse texto, as falas atribuídas a Evanildo Bechara são representativas da variedade linguística', 'As falas de Bechara, filólogo e membro da ABL, têm tom técnico e vocabulário especializado (“lexicologia”, “estrangeirismos”, “importação”), próprio de sua área de atuação. Trata-se, portanto, de variedade ocupacional (E). Não é situacional, pois o texto não mostra mudança de registro por contexto (A); não há marcas regionais (B), de formação rebuscada (C) nem de faixa etária (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'situacional, pois o contexto exige o uso da linguagem formal.', 0),
    (@question_id, 'B', 'regional, pois ele traz marcas do falar de seu local de nascimento.', 0),
    (@question_id, 'C', 'sociocultural, pois sua formação pressupõe o uso de linguagem rebuscada.', 0),
    (@question_id, 'D', 'geracional, pois ele emprega termos característicos de sua faixa etária.', 0),
    (@question_id, 'E', 'ocupacional, pois ele faz uso de termos específicos de sua área de atuação.', 1);

-- 1º Dia, questão 12 (Linguagens e Códigos › Língua Portuguesa › Variação Linguística). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 73, NULL, 'A Língua da Tabatinga, falada na cidade de Bom Despacho, Minas Gerais, foi por muito tempo estigmatizada devido à sua origem e à própria classe social de seus falantes, pois, segundo uma pesquisadora, era falada por “meninos pobres vindos da Tabatinga ou de Cruz de Monte — ruas da periferia da cidade cujos habitantes sempre foram tidos por marginais”. Conhecida por antigos como a “língua dos engraxates”, pois muitos trabalhadores desse ofício conversavam nessa língua enquanto lustravam sapatos na praça da matriz, a Língua da Tabatinga era utilizada por negros escravizados como uma espécie de “língua secreta”, um código para trocarem informações de como conseguir alimentos, ou para planejar fugas de seus senhores sem risco de serem descobertos por eles. De acordo com um documento do Iphan (2011), os falantes da língua apresentam uma forte consciência de sua relação com a descendência africana e da importância de preservar a “fala que os identifica na região”. Essa mudança de compreensão tangencia aspectos de pertencimento, pois, à medida que o falante da Língua da Tabatinga se identifica com a origem afro-brasileira, ele passa a ver essa língua como um legado recebido e tem o cuidado de transmiti-la para outras gerações. A concentração de falantes dessa língua está na faixa entre 21 e 60 anos de idade.

Disponível em: www.historiaeparcerias2019.rj.anpuh.org. Acesso em: 3 fev. 2024 (adaptado).

A Língua da Tabatinga tem sido preservada porque o(a)', 'O texto conta que a Língua da Tabatinga era estigmatizada, mas que seus falantes hoje têm forte consciência da relação com a descendência africana, veem a língua como legado e cuidam de transmiti-la. Ela é preservada porque o sentimento de identidade linguística tem se consolidado (D). A língua não passou à escrita (A), seus falantes continuam sem prestígio social garantido (B), a função de “língua secreta” mudou (C) e a faixa etária é ampla, de 21 a 60 anos, não homogênea (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'seu registro passou da forma oral para a escrita.', 0),
    (@question_id, 'B', 'classe social de seus usuários ganhou prestígio.', 0),
    (@question_id, 'C', 'sua função inicial se manteve ao longo dos anos.', 0),
    (@question_id, 'D', 'sentimento de identidade linguística tem se consolidado.', 1),
    (@question_id, 'E', 'perfil etário de seus falantes tem se tornado homogêneo.', 0);

-- 1º Dia, questão 13 (Linguagens e Códigos › Língua Portuguesa › Interpretação de Texto). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 72, NULL, 'Diante do pouco dinheiro para produtos básicos de sobrevivência, são as adolescentes o alvo mais vulnerável à precariedade menstrual. Sofrem com dois fatores: o desconhecimento da importância da higiene menstrual para sua saúde e a dependência dos pais ou familiares para a compra do absorvente, que acaba entrando na lista de artigos supérfluos da casa.

A falta do absorvente afeta diretamente o desempenho escolar dessas estudantes e, como consequência, restringe o desenvolvimento de seu potencial na vida adulta. Dados da Pesquisa Nacional de Saúde (PNS), do IBGE, revelaram que, das meninas entre 10 e 19 anos que deixaram de fazer alguma atividade (estudar, realizar afazeres domésticos, trabalhar ou, até mesmo, brincar) por problemas de saúde nos 14 dias anteriores à data da pesquisa, 2,88% deixaram de fazê-la por problemas menstruais. Para efeitos de comparação, o índice de meninas que relataram não ter conseguido realizar alguma de suas atividades por gravidez e parto foi menor: 2,55%. Dados da ONU apontam que, no mundo, uma em cada dez meninas falta às aulas durante o período menstrual. No Brasil, esse número é ainda maior: uma entre quatro estudantes já deixou de ir à escola por não ter absorventes. Com isso, perdem, em média, até 45 dias de aula, por ano letivo, como revela o levantamento Impacto da Pobreza Menstrual no Brasil. O ato biológico de menstruar acaba por virar mais um fator de desigualdade de oportunidades entre os gêneros.

Disponível em: www12.senado.leg.br. Acesso em: 21 jan. 2024 (adaptado).

Esse texto é marcado pela função referencial da linguagem, uma vez que cumpre o propósito de', 'A função referencial centra-se no assunto e busca informar com dados objetivos. O texto traz números da PNS/IBGE e da ONU sobre meninas que deixam de estudar por falta de absorventes, com o propósito de informar o leitor sobre o impacto da pobreza menstrual na vida das mulheres (D). O texto não sugere soluções (A), não compara menstruação e gravidez como tema central (B), não compara homens e mulheres (C) nem orienta rotinas de autocuidado (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'sugerir soluções para um problema de ordem social.', 0),
    (@question_id, 'B', 'estabelecer uma relação entre menstruação e gravidez.', 0),
    (@question_id, 'C', 'comparar o desempenho acadêmico de mulheres e homens.', 0),
    (@question_id, 'D', 'informar o leitor sobre o impacto da pobreza menstrual na vida das mulheres.', 1),
    (@question_id, 'E', 'orientar o público sobre a necessidade de rotinas de autocuidado na adolescência.', 0);

-- 1º Dia, questão 14 (Linguagens e Códigos › Língua Portuguesa › Variação Linguística). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 73, NULL, 'Maranhenses que moram longe matam a saudade da terra natal usando expressões próprias do estado. Se o maranhês impressiona e desperta a curiosidade de quem mora no próprio Maranhão, imagine de quem vem de outros estados e países? A variedade linguística local é enorme e o modo de falar tão próprio e característico dos maranhenses vem conquistando muita gente e inspirando títulos e muito conteúdo digital com a criação de podcasts, blogs, perfis na internet, além de estampar diversos tipos de produtos e serviços de empresas locais.

Com saudades do Maranhão, morando há 16 anos no Rio de Janeiro, um fotógrafo maranhense criou um perfil na internet no qual compartilha a culinária, brincadeiras e o ‘dicionário’ maranhês. “A primeira vez que fui a uma padaria no Rio, na inocência, pedi 3 reais de ‘pães misturados’. Quando falei isso, as pessoas pararam e me olharam de uma forma bem engraçada, aí já fiquei ‘encabulado, ó’ e o atendente sorriu e explicou que lá não existia pão misturado e, sim, pão francês e suíço. Depois foi a minha vez de explicar sobre os pães ‘massa grossa e massa fina’”, contou o fotógrafo, com humor.

Disponível em: https://oimparcial.com.br. Acesso em: 1 nov. 2021 (adaptado).

A vivência relatada no texto evidencia que as variedades linguísticas', 'O fotógrafo maranhense conta que, no Rio, o pedido de “pães misturados” causou estranheza, pois o nome local é pão francês ou suíço. A vivência mostra que as variedades linguísticas são constitutivas do português brasileiro (C): cada região tem seu modo de falar. Elas não impedem o entendimento, já que a explicação resolve o mal-entendido (A), não enaltecem uma variedade (B), não exigem dicionarização (D) e não se restringem a situações coloquiais (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'impedem o entendimento mútuo.', 0),
    (@question_id, 'B', 'enaltecem o português do Maranhão.', 0),
    (@question_id, 'C', 'são constitutivas do português brasileiro.', 1),
    (@question_id, 'D', 'exigem a dicionarização dos termos usados.', 0),
    (@question_id, 'E', 'são restritas a situações coloquiais de comunicação.', 0);

-- 1º Dia, questão 15 (Linguagens e Códigos › Língua Portuguesa › Interpretação de Texto). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 72, NULL, 'Telemedicina é para todos, mas nem todos estão preparados

A telemedicina, nos últimos anos, tem se destacado como uma ferramenta valiosa, proporcionando uma gama de benefícios que vão desde a ampliação do acesso à assistência médica até a otimização dos recursos de todo o ecossistema de saúde.

O governo federal propõe a Estratégia de Saúde Digital, um programa destinado à transformação digital da saúde no Brasil. Seu principal objetivo é facilitar a troca de informações entre os diversos pontos da Rede de Atenção à Saúde, promovendo a interoperabilidade e, assim, possibilitando a transição e a continuidade do cuidado nos setores público e privado. Também está em discussão um projeto de lei que dispõe sobre o prontuário eletrônico unificado do cidadão, o que indica o quanto o tema está em evidência tanto para os gestores públicos quanto para os privados.

Contudo, é importante reconhecer que nem todas as pessoas estão igualmente preparadas para aproveitar plenamente os cuidados ofertados pela telemedicina. Um dos principais benefícios do atendimento de saúde a distância é a capacidade de superar barreiras geográficas, proporcionando acesso a serviços médicos, especialmente para pacientes que residem em áreas remotas e/ou carentes de certas especialidades médicas, os chamados “vazios assistenciais”. A equidade no acesso é uma questão crítica, uma vez que nem todos têm ao seu alcance dispositivos tecnológicos ou uma conexão à internet que seja confiável, entre outros problemas de infraestrutura. É um desafio tanto para os pacientes quanto para os profissionais de saúde, que, em muitos casos, não contam com estrutura para o trabalho remoto nem com letramento digital para desenvolver as funções.

OLIVEIRA, D. Disponível em: www.correiobraziliense.com.br. Acesso em: 21 jan. 2024 (adaptado).

Ao tratar da telemedicina, esse texto ressalta que um dos benefícios dessa tecnologia para a sociedade é o fato de ela', 'O texto afirma que um dos principais benefícios da telemedicina é superar barreiras geográficas, dando acesso a serviços médicos a pacientes de áreas remotas ou carentes de especialidades. Isso corresponde a oportunizar atendimento médico a pacientes de áreas periféricas (B). O prontuário unificado (A) é um projeto em discussão, e o texto aponta a falta de dispositivos e de infraestrutura como desafios, não como benefícios (C, E); a interação entre especialidades (D) não é mencionada.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'disponibilizar prontuário único do cidadão tanto na rede pública quanto na privada.', 0),
    (@question_id, 'B', 'oportunizar o acesso a atendimento médico a pacientes de áreas periféricas.', 1),
    (@question_id, 'C', 'fornecer dispositivos tecnológicos para a realização de exames.', 0),
    (@question_id, 'D', 'promover a interação entre diferentes especialidades médicas.', 0),
    (@question_id, 'E', 'garantir infraestrutura para o trabalho remoto de médicos.', 0);

-- 1º Dia, questão 16 (Linguagens e Códigos › Artes › Arte Brasileira). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 94, NULL, 'Uma definição possível para o conceito de arte afro-brasileira pode ser: produção plástica que é feita por negros, mestiços ou brancos a partir de suas experiências sociais com a cultura negra nacional. Exemplos clássicos dessa abordagem são Carybé (1911-1997), Mestre Didi (1917-2013) e Djanira da Motta e Silva (1914-1979), cujas obras emergem e ganham forma em razão do ambiente social no qual habitaram e viveram. Se Didi era um célebre representante da cultura religiosa nagô baiana e brasileira, iniciado desde o ventre no candomblé, Carybé era argentino e, naturalizado brasileiro, envolveu-se de tal modo com essa religião que alguns dos orixás dos quais conhecemos a imagem visual são produções suas.

Disponível em: www.premiopipa.com. Acesso em: 13 nov. 2021 (adaptado).

Sob a perspectiva da multiculturalidade e de acordo com o texto, a produção artística afro-brasileira caracteriza-se pelo(a)', 'O texto define a arte afro-brasileira como a produzida por negros, mestiços ou brancos a partir de suas experiências com a cultura negra, e cita Mestre Didi, iniciado no candomblé, e Carybé, argentino naturalizado brasileiro que se envolveu com a religião. A produção caracteriza-se, assim, pela relação complexa entre vivências pessoais dos artistas e referenciais estéticos de matriz africana (D). Não há estranhamento (A), distanciamento (B), uniformização (C) nem padronização das obras (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'estranhamento no modo de apropriação da cultura religiosa de matriz africana.', 0),
    (@question_id, 'B', 'distanciamento entre as raízes de matriz africana e a estética de outras culturas.', 0),
    (@question_id, 'C', 'visão uniformizadora das religiões de matriz africana expressada nas diferentes produções.', 0),
    (@question_id, 'D', 'relação complexa entre as vivências pessoais dos artistas e os referenciais estéticos de matriz africana.', 1),
    (@question_id, 'E', 'padronização da forma de produção e da temática da matriz africana presente nas obras dos artistas citados.', 0);

-- 1º Dia, questão 17 (Linguagens e Códigos › Língua Portuguesa › Interpretação de Texto). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 72, NULL, 'Influenciadores negros têm recorrentemente chamado a atenção para o fato de terem muito menos repercussão em suas postagens e nas entregas do seu conteúdo quando comparados com os influenciadores brancos, mesmo se fotos, contextos e anúncios forem extremamente semelhantes. Segundo o site Negrê, a digital influencer e youtuber criadora do projeto digital Preta Pariu iniciou um experimento em uma plataforma. Após perceber a crescente queda nos índices de alcance digital, a paulista publicou fotografias de modelos brancas em seu perfil e analisou as métricas de engajamento. Surpreendentemente, a ferramenta de estatísticas aferiu um aumento de 6 000% em seu alcance.

Disponível em: https://diplomatique.org.br. Acesso em: 21 jan. 2024 (adaptado).

A apresentação do dado estatístico ao final desse texto revela a intenção de', 'O dado de aumento de 6 000% no alcance após a influenciadora publicar fotos de modelos brancas serve de prova de um experimento: mesmo conteúdo, mais alcance quando muda a cor das pessoas. A estatística revela a intenção de comprovar a relação entre o alcance de conteúdos digitais e o viés racial (E). Ela não busca mostrar a repercussão do projeto (A), quantificar postagens (B), ampliar o alcance de sites (C) nem exaltar a qualidade das publicações (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'demonstrar a repercussão de projetos como o Preta Pariu.', 0),
    (@question_id, 'B', 'informar o quantitativo de postagens da comunidade negra.', 0),
    (@question_id, 'C', 'potencializar o alcance de textos e imagens em sites como o Negrê.', 0),
    (@question_id, 'D', 'exaltar a qualidade das publicações sobre negritude em redes sociais.', 0),
    (@question_id, 'E', 'comprovar a relação entre o alcance de conteúdos digitais e o viés racial.', 1);

-- 1º Dia, questão 18 (Linguagens e Códigos › Educação Física › Corpo e Saúde). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 98, NULL, 'Um estudo norte-americano analisou os efeitos da pandemia da covid-19 sobre a saúde mental e a manutenção da atividade física, revelando que um fator está diretamente ligado ao outro. De acordo com os dados, famílias de baixa renda foram mais impactadas pelo ciclo vicioso de falta de motivação e pelo sedentarismo. Diante da necessidade de distanciamento social e do início da quarentena, as opções de espaços seguros para exercícios físicos diminuíram, o que dificultou que as pessoas mantivessem seus níveis de atividade. Os dados evidenciaram que as pessoas mais ativas tinham melhor estado de saúde mental. As pessoas com menor renda tiveram mais dificuldade para manter os níveis de atividade física durante a pandemia, sendo aproximadamente duas vezes menos propensas a continuarem no mesmo ritmo de exercícios de antes da pandemia. Habitantes de áreas urbanas mostraram maior probabilidade de não conseguirem manter os níveis de atividade física semelhantes aos de pessoas que vivem em zonas rurais, onde há mais oportunidades de sair para espaços abertos.

Disponível em: https://revistagalileu.globo.com. Acesso em: 6 dez. 2021 (adaptado).

O texto evidencia a perspectiva ampliada de saúde ao abordar criticamente a pandemia da covid-19 a partir do(a)', 'O texto mostra que famílias de baixa renda e moradores de áreas urbanas tiveram mais dificuldade de manter a atividade física na pandemia, com impacto na saúde mental. A perspectiva ampliada de saúde considera os determinantes sociais, ou seja, a relação entre condições socioeconômicas e a prática de exercícios (D). As demais alternativas tratam só de espaços (A), de equilíbrio emocional (B), de distanciamento (C) ou de morar no campo (E), sem o recorte socioeconômico que caracteriza a visão ampliada.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'busca por espaços para a prática de exercícios físicos.', 0),
    (@question_id, 'B', 'necessidade de se manter ativo para ter equilíbrio emocional.', 0),
    (@question_id, 'C', 'distanciamento social e sua vinculação com a prática de atividades físicas.', 0),
    (@question_id, 'D', 'relação entre os determinantes socioeconômicos e a prática de exercícios.', 1),
    (@question_id, 'E', 'benefício de morar em áreas rurais para preservar a estabilidade psicológica.', 0);

-- 1º Dia, questão 19 (Linguagens e Códigos › Literatura › Realismo e Naturalismo). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 78, NULL, 'Até ali que sabia das misérias do mundo? Nada.

Aquela noite do Castelo, tão simples, tão monótona, fora uma revelação! Era bem certo que a lágrima existia, que irrompiam soluços de peitos oprimidos, que para alguém os dias não tinham cor nem a noite tinha estrelas! Ela, criada entre beijos, no aroma dos seus jardins, com as vontades satisfeitas, o leito fofo, a mesa delicada, sentira sempre no coração um desejo sem nome, um desejo ou uma saudade absurda, a saudade do céu, como dizia o dr. Gervásio, e que não era mais que a doida aspiração da artista incipiente, que germinava no seu peito fraco. E aquela mesma mágoa parecia-lhe agora doce e embaladora, comparando-se à outra, a Sancha, da sua idade, negra, feia, suja, levada a pontapés, dormindo sem lençóis em uma esteira, comendo em pé, apressada, os restos parcos e frios de duas velhas, vestida de algodões rotos, curvada para um trabalho sem descanso nem paga! Por quê? Que direito teriam uns a todas as primícias e regalos da vida, se havia outros que nem por uma nesga viam a felicidade?

ALMEIDA, J. L. A falência. Disponível em: www.dominiopublico.gov.br. Acesso em: 28 dez. 2023.

Nesse fragmento do romance de Júlia Lopes de Almeida, escrito no cenário brasileiro pós-abolição, a narradora exprime um olhar crítico sobre a', 'No romance de Júlia Lopes de Almeida, escrito após a abolição, a protagonista compara sua vida confortável à de Sancha, negra, descalça, que dorme numa esteira e trabalha sem descanso “nem paga”. A narradora critica a exploração da força de trabalho da população negra (D). O fragmento não trata da arte feita por mulheres (A), de moradia (B), de emancipação feminina (C) nem de disputa de poder entre brancos e negros (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'desvalorização da arte produzida por mulheres.', 0),
    (@question_id, 'B', 'mudança das condições de moradia do povo negro.', 0),
    (@question_id, 'C', 'ruptura do projeto político de emancipação feminina.', 0),
    (@question_id, 'D', 'exploração da força de trabalho da população negra.', 1),
    (@question_id, 'E', 'disputa de poder entre brancos e negros no século XIX.', 0);

-- 1º Dia, questão 20 (Linguagens e Códigos › Língua Portuguesa › Interpretação de Texto). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 72, NULL, 'Sempre passo nervoso quando leio minha crônica neste jornal e percebo que escapuliu a palavra “coisa” em alguma frase. Acontece que “coisa” está entre as coisas mais deliciosas do mundo.

O primeiro banho da minha filha foi embalado pela minha voz dizendo, ao fundo, “cuidado, ela ainda é uma coisinha tão pequena”. “Viu só que amor? Nunca vi coisa assim”. O amor que não dá conta de explicação é “a coisa” em seu esplendor e excelência. “Alguma coisa acontece no meu coração” é a frase mais bonita que alguém já disse sobre São Paulo. E quando Caetano, citado aqui pela terceira vez pra defender a dimensão poética da coisa, diz “coisa linda”, nós sabemos que nenhuma palavra definiria de forma mais profunda e literária o quão bela e amada uma coisa pode ser.

“Coisar” é verbo de quem está com pressa ou tem lapsos de memória. É pra quando “mexe qualquer coisa dentro doida”. E que coisa magnífica poder se expressar tal qual Caetano Veloso. Agora chega, porque “esse papo já tá qualquer coisa” e eu já tô “pra lá de Marrakech”. TATI BERNARDI. Disponível em: www1.folha.uol.com.br. Acesso em: 3 jan. 2024 (adaptado).

O recurso utilizado na progressão textual para garantir a unidade temática dessa crônica é a', 'A cronista repete a palavra “coisa” e seus derivados (“coisinha”, “coisar”) ao longo do texto, e é essa repetição que dá unidade ao tema. O recurso é a reiteração, marcada pela repetição de uma palavra e de seus cognatos (C). As citações de canções (A) e a referência à escrita de crônicas (B) existem, mas não garantem a unidade temática; os conectores “quando” e “porque” (D) e a retomada pronominal (E) são recursos pontuais.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'intertextualidade, marcada pela citação de versos de letras de canções.', 0),
    (@question_id, 'B', 'metalinguagem, marcada pela referência à escrita de crônicas pela autora.', 0),
    (@question_id, 'C', 'reiteração, marcada pela repetição de uma determinada palavra e de seus cognatos.', 1),
    (@question_id, 'D', 'conexão, marcada pela presença dos conectores lógicos “quando” e “porque” entre orações.', 0),
    (@question_id, 'E', 'pronominalização, marcada pela retomada de “minha filha” e “um namorado ruim” pelos pronomes “ela” e “lo”.', 0);

-- 1º Dia, questão 22 (Linguagens e Códigos › Literatura › Literatura Contemporânea). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 82, NULL, 'TEXTO I

Capítulo 4, versículo 3

Minha palavra vale um tiro, eu tenho muita munição
Na queda ou na ascensão, minha atitude vai além
E tem disposição pro mal e pro bem
Talvez eu seja um sádico ou um anjo, um mágico
Ou juiz, ou réu, o bandido do céu
Malandro ou otário, quase sanguinário
Franco atirador se for necessário
Revolucionário, insano, ou marginal
Antigo e moderno, imortal
Fronteira do céu com o inferno
Astral imprevisível, como um ataque cardíaco do verso.

RACIONAIS MCs. Sobrevivendo ao inferno. São Paulo: Cosa Nostra, 1997 (fragmento).

TEXTO II

Pode-se dizer que as várias experiências narradas nos discos do Racionais tratam no fundo de um só tema: a violência que estrutura a nossa sociedade. O grupo canta a violência que estrutura as relações entre os familiares, os amigos, o homem e a mulher, o traficante e o viciado. Canta a violência do crime. A violência causada por inveja ou por vaidade. Também canta que a relação entre as classes sociais é sempre violenta: o racismo, a miséria, os baixos salários, a concentração de renda, a esmola, a publicidade, o alcoolismo, o jornalismo, o poder policial, a justiça, o sistema penitenciário, o governo existem por meio da violência.

GARCIA, W. Ouvindo Racionais MCs. Teresa: revista de literatura brasileira, n. 5, 2004 (adaptado).

Na letra da canção, a tematização da violência mencionada no Texto II manifesta-se', 'O Texto II diz que a violência estrutura as relações sociais. Na letra, o eu lírico afirma “minha palavra vale um tiro, eu tenho muita munição” e se define como “franco atirador”: a palavra é vista como arma. Há, portanto, procedimento metalinguístico, que concebe a palavra como forma de combate e insubordinação (C). Os termos bélicos não apontam crítica direta à opressão das periferias (B), e as definições ambíguas (D) e a imortalidade (E) não manifestam a violência descrita no Texto II.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'como metáfora da desigualdade, que associa a ideia de justiça a valores históricos negativos.', 0),
    (@question_id, 'B', 'na referência a termos bélicos, que sinaliza uma crítica social à opressão da população das periferias.', 0),
    (@question_id, 'C', 'como procedimento metalinguístico, que concebe a palavra como uma forma de combate e insubordinação.', 1),
    (@question_id, 'D', 'nas definições ambíguas do enunciador, que inverte e relativiza as representações da maldade e da bondade.', 0),
    (@question_id, 'E', 'na menção à imortalidade, que sugere a possibilidade de resistência para além da dicotomia entre vida e morte.', 0);

-- 1º Dia, questão 23 (Linguagens e Códigos › Língua Portuguesa › Gêneros Textuais). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 74, NULL, 'As reações à sétima temporada foram o ápice do último estágio em Game of Thrones. De forma alguma, este que vos fala seria capaz de argumentar que a série é perfeita, mas os defeitos que existem aqui sempre existiram, de uma forma ou de outra, durante os sete anos em que ela esteve no ar. Os dois roteiristas foram brilhantes em traduzir os personagens intrincados e conflituosos da obra de George R. R. Martin, mas nunca souberam exatamente como fazer jus a eles (e especialmente a elas, as mulheres da trama). A verdade é que, com tudo isso e mais Ramin Djawadi evocando sentimentos e ambientes improváveis com sua trilha sonora magistral, a série não conseguiria ser ruim nem se tentasse, mas continua sendo uma pena que, ao buscar o seu final com tanta sede e tanta celeridade, Benioff e Weiss tenham tirado sua qualidade mais preciosa: o fôlego, a paciência e o detalhismo que faziam suas palavras se levantarem do papel e ganharem vida.

Disponível em: https://observatoriodocinema.uol.com.br. Acesso em: 29 nov. 2017 (adaptado).

Ainda que faça uma avaliação positiva da série, nessa resenha, o autor aponta aspectos negativos da obra ao utilizar', 'O autor avalia positivamente a série, mas faz ressalvas com construções adversativas: “De forma alguma [...] a série é perfeita, mas os defeitos...”; “nunca souberam exatamente como fazer jus a eles”; “mas continua sendo uma pena”. Os aspectos negativos aparecem por meio de expressões adversativas que ressalvam os elogios (B). O texto é pessoal (“este que vos fala”), não impessoal (A), e não usa eufemismos (D), interlocução (C) ou antíteses entre roteiro e trilha (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'marcas de impessoalidade que disfarçam a opinião do especialista.', 0),
    (@question_id, 'B', 'expressões adversativas para fazer ressalvas às afirmações elogiosas.', 1),
    (@question_id, 'C', 'interlocução com o leitor para corroborar opiniões contrárias à adaptação.', 0),
    (@question_id, 'D', 'eufemismos que minimizam as críticas feitas à construção das personagens.', 0),
    (@question_id, 'E', 'antíteses que opõem a fragilidade do roteiro à beleza da trilha sonora da série.', 0);

-- 1º Dia, questão 24 (Linguagens e Códigos › Língua Portuguesa › Variação Linguística). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 73, NULL, '— Vá para o inferno, Gondim. Você acanalhou o troço. Está pernóstico, está safado, está idiota. Há lá ninguém que fale dessa forma!

Azevedo Gondim apagou o sorriso, engoliu em seco, apanhou os cacos da sua pequenina vaidade e replicou amuado que um artista não pode escrever como fala. — Não pode? — perguntei com assombro. E por quê? Azevedo Gondim respondeu que não pode porque não pode.

— Foi assim que sempre se fez. A literatura é a literatura, seu Paulo. A gente discute, briga, trata de negócios naturalmente, mas arranjar palavras com tinta é outra coisa. Se eu fosse escrever como falo, ninguém me lia.

RAMOS, G. São Bernardo. Rio de Janeiro: Record, 2009.

Nesse fragmento, a discussão dos personagens traz à cena um debate acerca da escrita que', 'Paulo Honório reprova a escrita de Gondim (“Está pernóstico [...] Há lá ninguém que fale dessa forma!”), e Gondim responde que “um artista não pode escrever como fala”. A discussão contrapõe o preciosismo linguístico à coloquialidade (D). Quem defende a aproximação entre fala e escrita é Paulo Honório, não o texto como um todo (C), e a cena não trata de dialetos de pouco prestígio (B), da diferença entre arte e norma padrão (A) nem de norma culta e desentendimento (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'diferencia a produção artística do registro padrão da língua.', 0),
    (@question_id, 'B', 'aproxima a literatura de dialetos sociais de pouco prestígio.', 0),
    (@question_id, 'C', 'defende a relação entre a fala e o estilo literário de um autor.', 0),
    (@question_id, 'D', 'contrapõe o preciosismo linguístico a situações de coloquialidade.', 1),
    (@question_id, 'E', 'associa o uso da norma culta à ocorrência de desentendimentos pessoais.', 0);

-- 1º Dia, questão 26 (Linguagens e Códigos › Educação Física › Lutas, Ginástica e Dança). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 99, NULL, 'Por trás do universo “masculino” das lutas, é cada vez mais notório o aumento da participação de mulheres nessa prática corporal. Algumas situações reforçam esse fenômeno de ocupação em ambientes de lutas: a inclusão de mulheres em combates de artes marciais mistas, ou MMA, a transmissão televisiva de lutas de mulheres e a criação de horários específicos para elas em academias que ensinam lutas. Uma pesquisa científica mostrou menor participação e mobilização das meninas em comparação com os meninos nas aulas de Educação Física. Entre as justificativas discentes para essa situação está o fato de que eles relacionam a luta como uma expressão corporal masculina e, por consequência, não adequada aos interesses femininos. Dessa forma, o ensino de lutas nas aulas de Educação Física é atravessado por tensões relacionadas às questões de gênero e sexualidade, o que, por sua vez, pode favorecer a sua exclusão do conteúdo próprio da disciplina.

SO, M. R.; MARTINS, M. Z.; BETTI, M. As relações das meninas com os saberes das lutas nas aulas de Educação Física. Motrivivência, n. 56, dez. 2018 (adaptado).

Segundo o texto, apesar do aumento da participação de mulheres em lutas, a realidade na escola ainda é diferente em razão do(a)', 'O texto diz que, mesmo com mais mulheres nas lutas, as alunas participam menos das aulas de Educação Física porque as lutas são vistas como expressão corporal masculina, “não adequada aos interesses femininos”. A realidade escolar difere por causa da masculinização dessa modalidade (B). Esportivização (A), enfoque da mídia (C), trato pedagógico (D) e marginalização pela Educação Física (E) não são apontados como razão da diferença.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'esportivização desse conteúdo.', 0),
    (@question_id, 'B', 'masculinização dessa modalidade.', 1),
    (@question_id, 'C', 'enfoque desses eventos pela mídia.', 0),
    (@question_id, 'D', 'trato pedagógico dessa manifestação.', 0),
    (@question_id, 'E', 'marginalização desse tema pela Educação Física.', 0);

-- 1º Dia, questão 27 (Linguagens e Códigos › Língua Portuguesa › Interpretação de Texto). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 72, NULL, 'Volta e meia recebo cartinhas de fãs, e alguns são bem jovens, contando como meu trabalho com a música mudou a vida deles. Fico no céu lendo essas coisas e me emociono quando escrevem que não são aceitos pelos pais por serem diferentes, e como minhas músicas são uma companhia e os libertam nessas horas de solidão.

Sinto que é mais complicado ser jovem hoje, já que nunca tivemos essa superpopulação no planeta: haja competitividade, culto à beleza, ter filho ou não, estudar, ralar para arranjar trabalho, ser mal remunerado, ser bombardeado com trocentas informações, lavagens cerebrais...

Queria dar beijinhos e carinhos sem ter fim nessa moçada e dizer a ela que a barra é pesada mesmo, mas que a juventude está a seu favor e, de repente, a maré de tempestade muda. Diria também um monte de clichê: que vale a pena estudar mais, pesquisar mais, ler mais. Diria que não é sinal de saúde estar bem-adaptado a uma sociedade doente, que o que é normal para uma aranha é o caos para uma mosca.

Meninada, sintam-se beijados pela vovó Rita.

RITA LEE. Outra autobiografia. São Paulo: Globo Livros, 2023.

Como estratégia para se aproximar de seu leitor, a autora usa uma postura de empatia explicitada em', 'Empatia é colocar-se no lugar do outro. Ao escrever “Sinto que é mais complicado ser jovem hoje”, Rita Lee reconhece as dificuldades dos jovens (competitividade, culto à beleza, trabalho mal pago) e se solidariza com eles (C). As demais passagens apenas relatam o recebimento de cartas (A), a emoção da autora (B), o desejo de dar carinho (D) ou um conselho (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '“Volta e meia recebo cartinhas de fãs, e alguns são bem jovens”.', 0),
    (@question_id, 'B', '“Fico no céu lendo essas coisas”.', 0),
    (@question_id, 'C', '“Sinto que é mais complicado ser jovem hoje”.', 1),
    (@question_id, 'D', '“Queria dar beijinhos e carinhos sem ter fim nessa moçada”.', 0),
    (@question_id, 'E', '“Diria que não é sinal de saúde estar bem-adaptado a uma sociedade doente”.', 0);

-- 1º Dia, questão 28 (Linguagens e Códigos › Literatura › Literatura Contemporânea). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 82, NULL, 'Data venia

Conheci Bentinho e Capitu nos meus curiosos e antigos quinze anos. E os olhos de água da jovem de Matacavalos atraíram-me, seduziram-me ao primeiro contato. Aliados ao seu jeito de ser, flor e mistério. Mas tomou-me também a indignação diante do narrador e seu texto, feito de acusação e vilipêndio. Sem qualquer direito de defesa. Sem acesso ao discurso, usurpado, sutilmente, pela palavra autoritária do marido, algoz, em pele de cordeiro vitimado. Crudelíssimo e desumano: não bastasse o que faz com a mulher, chega a desejar a morte do próprio filho e a festejá-la com um jantar, sem qualquer remorso. No fundo, uma pobre consciência dilacerada, um homem dividido, que busca encontrar-se na memória, e acaba faltando-se a si mesmo. Retomei inúmeras vezes a triste história daquele amor em desencanto. Familiarizei-me, ao longo do tempo, com a crítica do texto; poucos, muito poucos, escapam das bem traçadas linhas do libelo condenatório; no mínimo concedem à ré o beneplácito da dúvida: convertem-na num enigma indecifrável, seu atributo consagrador.

Eis que, diante de mais um retorno ao romance, veio a iluminação: por que não dar voz plena àquela mulher, brasileira do século XIX, que, apesar de todas as artimanhas e do maquiavelismo do companheiro, se converte numa das mais fascinantes criaturas do gênio que foi Machado de Assis? A empresa era temerária, mas escrever é sempre um risco. Apoiado no espaço de liberdade em que habita a Literatura, arrisquei-me.

O resultado: este livro em que, além-túmulo, como Brás Cubas, a dona dos olhos de ressaca assume, à luz do mistério da arte literária e do próprio texto do Dr. Bento Santiago, seu discurso e sua verdade.

PROENÇA FILHO, D. Capitu: memórias póstumas. Rio de Janeiro: Atrium, 1998.

Para apresentar a apropriação literária que faz da obra de Machado de Assis, o autor desse texto', 'O autor começa contextualizando, de forma subjetiva e pessoal, seu encontro com Bentinho e Capitu aos quinze anos, a indignação com o narrador e a crítica do romance. Na conclusão, explicita o foco narrativo que adotou: dar voz à mulher, que assume “seu discurso e sua verdade”. A estrutura é, portanto, contextualização subjetiva do enredo e, ao final, explicitação do foco a ser assumido (E). Ele não reafirma o ponto de vista original (A), não redimensiona críticos (B), não refuta (C) nem reconsidera a escolha (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'relaciona aspectos centrais da obra original e, então, reafirma o ponto de vista adotado.', 0),
    (@question_id, 'B', 'explica os pontos de vista de críticos da literatura e, por fim, os redimensiona na discussão.', 0),
    (@question_id, 'C', 'introduz elementos relevantes da história e, na sequência, apresenta motivos para refutá-los.', 0),
    (@question_id, 'D', 'justifica as razões pelas quais adotou certa abordagem e, em seguida, reconsidera tal escolha.', 0),
    (@question_id, 'E', 'contextualiza o enredo de forma subjetiva e, na conclusão, explicita o foco narrativo a ser assumido.', 1);

-- 1º Dia, questão 29 (Linguagens e Códigos › Literatura › Literatura Contemporânea). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 82, NULL, 'Meu irmão é filho adotivo. Há uma tecnicidade no termo, filho adotivo, que contribui para sua aceitação social. Há uma novidade que por um átimo o absolve das mazelas do passado, que parece limpá-lo de seus sentidos indesejáveis. Digo que meu irmão é filho adotivo e as pessoas tendem a assentir com solenidade, disfarçando qualquer pesar, baixando os olhos como se não sentissem nenhuma ânsia de perguntar mais nada. Talvez compartilhem da minha inquietude, talvez de fato se esqueçam do assunto no próximo gole ou na próxima garfada. Se a inquietude continua a reverberar em mim, é porque ouço a frase também de maneira parcial — meu irmão é filho — e é difícil aceitar que ela não termine com a verdade tautológica habitual: meu irmão é filho dos meus pais. Estou entoando que meu irmão é filho e uma interrogação sempre me salta aos lábios: filho de quem?

FUCKS, J. A resistência. São Paulo: Cia. das Letras, 2015.

Das reflexões do narrador, apreende-se uma perspectiva que associa a adoção', 'O narrador observa que o termo técnico “filho adotivo” é aceito socialmente, mas o silêncio e o pesar das pessoas revelam desconforto, e ele fica com a pergunta “filho de quem?”. Isso associa a adoção a representações sociais estigmatizadas da parentalidade (A). O texto não trata de aprovação de desconhecidos (B), julgamento da família (C), conflito entre termo técnico e afeto (D) nem de questões entre irmãos (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'a representações sociais estigmatizadas da parentalidade.', 1),
    (@question_id, 'B', 'à necessidade de aprovação por parte de desconhecidos.', 0),
    (@question_id, 'C', 'ao julgamento velado de membros do núcleo familiar.', 0),
    (@question_id, 'D', 'ao conflito entre o termo técnico e o vínculo afetivo.', 0),
    (@question_id, 'E', 'a inquietações próprias das relações entre irmãos.', 0);

-- 1º Dia, questão 30 (Linguagens e Códigos › Língua Portuguesa › Interpretação de Texto). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 72, NULL, 'TEXTO I

A 13 de fevereiro de 1946, Graciliano Ramos escreve uma carta a Cândido Portinari relembrando uma visita que lhe fizera quando tivera a ocasião de apreciar algumas telas da série Retirantes. Diz o escritor alagoano: Caríssimo Portinari:

A sua carta chegou muito atrasada, e receio que esta resposta já não o ache fixando na tela a nossa pobre gente da roça. Não há trabalho mais digno, penso eu. Dizem que somos pessimistas e exibimos deformações; contudo, as deformações e essa miséria existem fora da arte e são cultivadas pelos que nos censuram. [...] Dos quadros que você me mostrou quando almocei no Cosme Velho pela última vez, o que mais me comoveu foi aquela mãe com a criança morta. Saí de sua casa com um pensamento horrível: numa sociedade sem classes e sem miséria, seria possível fazer-se aquilo? Numa vida tranquila e feliz, que espécie de arte surgiria? Chego a pensar que teríamos cromos, anjinhos cor-de-rosa, e isto me horroriza. Graciliano

Disponível em: https://graciliano.com.br. Acesso em: 6 fev. 2024 (adaptado).

TEXTO II

Histórias de ninar (adultos)

Houve um tempo — tão perto, e, ó, tão longe — em que a arte era um holofote na unha encravada, não um campeonato de melhores esmaltes.

Raskolnikov matava velhinhas, a família de Gregor Samsa o assassinava a “maçãzadas”, Memórias póstumas de Brás Cubas (Machado de Assis) é o retrato mais perfeito de tudo o que tem de pior na sociedade brasileira, uma sequência tristemente hilária de ações moralmente condenáveis, atitudes pusilânimes, cálculos mesquinhos e maus passos cretinos.

A literatura, o cinema e o teatro vêm se transformando num exercício de lacração: o mal está sempre no outro, os protagonistas são ironmen/women da virtude. A pessoa sai da leitura ou da sessão não com a guarda abaixada, as certezas abaladas, mais próxima da verdade (ou, à falta de uma palavra melhor, da sinceridade): sai com suas certezas reforçadas.

A realidade é confusa. Contraditória. Muitas vezes incompreensível. A arte é onde tentamos nos mostrar nus, com todos os nossos defeitos.

PRATA, A. Disponível em: www1.folha.uol.com.br. Acesso em: 12 jan. 2024 (adaptado).

No que diz respeito à arte, o posicionamento de Antônio Prata, no Texto II, aproxima-se da tese de Graciliano Ramos, no Texto I, uma vez que ambos', 'Graciliano defende que a arte deve mostrar as deformações e a miséria que existem na sociedade, em vez de “cromos” e “anjinhos cor-de-rosa”. Prata critica uma arte que reforça certezas e defende que ela sirva para tentarmos nos mostrar “com todos os nossos defeitos”, abalando certezas. Ambos afirmam o necessário caráter desestabilizador da arte (D). Só Graciliano trata da pobreza (C), só Prata fala de mudanças recentes (E), e Prata diz que a arte hoje reforça crenças, o que ele critica (B).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'defendem a dignidade do ofício dos artistas.', 0),
    (@question_id, 'B', 'concluem que a arte reforça crenças pessoais.', 0),
    (@question_id, 'C', 'apresentam a pobreza como inspiração para a arte.', 0),
    (@question_id, 'D', 'afirmam o necessário caráter desestabilizador da arte.', 1),
    (@question_id, 'E', 'atestam que há mudanças significativas na produção artística.', 0);

-- 1º Dia, questão 31 (Linguagens e Códigos › Literatura › Realismo e Naturalismo). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 78, NULL, 'Cap. XLVIII / Terpsícore

Ao contrário do que ficou dito atrás, Flora não se aborreceu na ilha. Conjeturei mal, emendo-me a tempo. Podia aborrecer-se pelas razões que lá ficam, e ainda outras que poupei ao leitor apressado; mas, em verdade, passou bem a noite. A novidade da festa, a vizinhança do mar, os navios perdidos na sombra, a cidade defronte com os seus lampiões de gás, embaixo e em cima, na praia e nos outeiros, eis aí aspectos novos que a encantaram durante aquelas horas rápidas.

Não lhe faltavam pares, nem conversação, nem alegria alheia e própria. Toda ela compartia da felicidade dos outros. Via, ouvia, sorria, esquecia-se do resto para se meter consigo. Também invejava a princesa imperial, que viria a ser imperatriz um dia, com o absoluto poder de despedir ministros e damas, visitas e requerentes, e ficar só, no mais recôndito do paço, fartando-se de contemplação ou de música. Era assim que Flora definia o ofício de governar. Tais ideias passavam e tornavam. De uma vez alguém lhe disse, como para lhe dar força: “Toda alma livre é imperatriz!”.

ASSIS, M. Esaú e Jacó. Rio de Janeiro: Nova Aguilar, 1974.

Convidada para o último baile do Império, na Ilha Fiscal, localizada no Rio de Janeiro, Flora devaneia sobre aspectos daquele contexto, no qual o narrador ironiza a', 'O narrador diz que Flora “não se aborreceu” no baile da Ilha Fiscal, o último do Império, e sonha com ser imperatriz, sem perceber que a monarquia estava prestes a acabar. A ironia recai sobre a alienação da elite em relação ao fim da monarquia (B). Não há promessa de esperança com a República (A), contemplação distanciada da cidade como foco (C), animosidade entre povo e nobreza (D) nem fantasia de casamento (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'promessa de esperança com o futuro regime.', 0),
    (@question_id, 'B', 'alienação da elite em relação ao fim da monarquia.', 1),
    (@question_id, 'C', 'perspectiva da contemplação distanciada da capital.', 0),
    (@question_id, 'D', 'animosidade entre população e membros da nobreza.', 0),
    (@question_id, 'E', 'fantasia de amor e de casamento da mulher burguesa.', 0);

-- 1º Dia, questão 32 (Linguagens e Códigos › Literatura › Literatura Contemporânea). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 82, NULL, 'Marília acorda

Tomo café em golinhos para não queimar meus lábios ressequidos. Como pão em pedacinhos para não engasgar com um farelo mais duro. Marília come também, mas olha o tempo todo para baixo. Parece que tem um acanhamento novo entre a gente. Termino. Olho mais uma vez pela janela. O dia está bom. Quero caminhar pelo pátio. Marília levanta, pega o andador e põe ao lado da cama. Ela sabe que eu quero levantar sozinha, e levanto. O lance de escadas, apesar de pequeno, ainda me causa problemas, mas não quero um elevador na casa e não vou tolerar descer uma rampa de cadeira de rodas. Marília abre a porta e saímos para a manhã. O dia está mais fresco do que eu imaginava. Ela pega uma manta de tricô que temos desde não sei quando e põe sobre as minhas costas. Ela aperta meus ombros com muita força, porque mesmo depois de todos esses anos, não descobriu a medida certa do carinho. Eu gosto. Porque entendo que naquele ato, naquela força está o nosso carinho.

POLESSO, N. B. Amora. Porto Alegre: Não Editora, 2015.

Nesse trecho, o drama do declínio físico da narradora transmite uma sensibilidade lírica centrada na', 'No trecho, a velhice e as limitações físicas aparecem em gestos simples: Marília apoia a narradora, põe o andador, coloca a manta e aperta seus ombros, e a narradora conclui que ali está “o nosso carinho”. O lirismo está na atmosfera de afeto fortalecido pelo convívio (B). Adaptações na casa (A), dependência (C), rotina (D) e aceitação das restrições (E) aparecem como detalhes, não como centro da sensibilidade lírica.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'necessidade de fazer adaptações na casa.', 0),
    (@question_id, 'B', 'atmosfera de afeto fortalecido pelo convívio.', 1),
    (@question_id, 'C', 'condição de dependência de outras pessoas.', 0),
    (@question_id, 'D', 'determinação de manter a regularidade da rotina.', 0),
    (@question_id, 'E', 'aceitação das restrições de mobilidade da personagem.', 0);

-- 1º Dia, questão 34 (Linguagens e Códigos › Língua Portuguesa › Variação Linguística). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 73, NULL, 'Teclado amazônico

Em novembro de 2023, uma professora indígena recebeu uma missão: verter as regras de um jogo de tabuleiro infantil do português para o tukano, sua língua nativa. Com vinte anos de experiência como professora de línguas em Taracuá, no Amazonas, ela já se dedicava à tradução havia tempos. O trabalho ficou mais fácil graças a um aplicativo lançado no ano anterior: com o Linklado em seu computador, ela traduziu as sete páginas das instruções do jogo em dois dias. Sem esse recurso, a tarefa seria bem mais trabalhosa. Antes dele, diz a professora, as transcrições de línguas indígenas exigiam o esforço quase manual de produzir diacríticos (acentos gráficos) e letras que não constam no teclado de aplicativos de mensagens ou programas de texto.

Para a pesquisadora do Instituto Nacional de Pesquisas da Amazônia (Inpa), idealizadora do aplicativo, o Linklado representa uma revolução. O programa não restringe combinações de acentos, e isso poderá facilitar a criação de representações gráficas para fonemas que ainda não têm forma escrita. “Eu mirei em uma dor e atingimos várias outras”, diz.

“O Linklado possibilita que o Brasil reconheça a sua diversidade linguística”, afirma uma antropóloga que é colega da pesquisadora no Inpa e faz parte da equipe do aplicativo. Ela defende que escrever na língua materna é uma das principais formas de preservá-la.

Disponível em: https://piaui.folha.uol.com.br. Acesso em: 3 fev. 2024 (adaptado).

De acordo com esse texto, o aplicativo Linklado contribuiu para a', 'O Linklado permite digitar acentos e letras que não existem nos teclados comuns, e a pesquisadora afirma que o app possibilita ao Brasil reconhecer sua diversidade linguística. Isso contribui para a democratização do registro escrito de línguas dos povos originários (B). O aplicativo não cria fonemas (A), apenas facilita sua representação; a tradução do jogo é um exemplo, não uma adaptação de jogos indígenas (C); e o texto não trata de divulgar técnicas (D) nem de ensinar português (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'criação de fonemas representativos de línguas indígenas no meio digital.', 0),
    (@question_id, 'B', 'democratização do registro escrito de línguas dos povos originários.', 1),
    (@question_id, 'C', 'adaptação de regras de jogos de tabuleiro de origem indígena.', 0),
    (@question_id, 'D', 'divulgação das técnicas de tradução de línguas indígenas.', 0),
    (@question_id, 'E', 'aprendizagem da língua portuguesa pelos indígenas.', 0);

-- 1º Dia, questão 35 (Linguagens e Códigos › Literatura › Romantismo). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 77, NULL, '— Eu lhe juro, Aurélia. Estes lábios nunca tocaram a face de outra mulher, que não fosse minha mãe. Meu primeiro beijo de amor, guardei-o para minha esposa, para ti...

[...] — Ou de outra mais rica! — disse ela, retraindo-se para fugir ao beijo do marido, e afastando-o com a ponta dos dedos. A voz da moça tomara o timbre cristalino, eco da rispidez e aspereza do sentimento que lhe sublevava o seio, e que parecia ringir-lhe nos lábios como aço.

— Aurélia! Que significa isto?

— Representamos uma comédia, na qual ambos desempenhamos o nosso papel com perícia consumada. Podemos ter este orgulho, que os melhores atores não nos excederiam. Mas é tempo de pôr termo a esta cruel mistificação, com que nos estamos escarnecendo mutuamente, senhor. Entremos na realidade por mais triste que ela seja; e resigne-se cada um ao que é, eu, uma mulher traída; o senhor, um homem vendido.

— Vendido! — exclamou Seixas ferido dentro d’alma.

— Vendido, sim: não tem outro nome. Sou rica, muito rica; sou milionária; precisava de um marido, traste indispensável às mulheres honestas. O senhor estava no mercado; comprei-o. Custou-me cem contos de réis, foi barato; não se fez valer. Eu daria o dobro, o triplo, toda a minha riqueza por este momento.

Ao tematizar o casamento, esse fragmento reproduz uma concepção de literatura romântica evidenciada na', 'No romance Senhora, de José de Alencar, Aurélia revela que “comprou” Seixas como marido por cem contos de réis, mostrando que o dinheiro corrompeu o amor. A concepção romântica evidenciada é a interferência da riqueza sobre o amor (D). O trecho não defende igualdade de gêneros (A), castidade (B) nem critica injustiças sociais em geral (C), e as relações interpessoais não são valorizadas, mas deturpadas (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'defesa da igualdade de gêneros.', 0),
    (@question_id, 'B', 'importância atribuída à castidade.', 0),
    (@question_id, 'C', 'indignação com as injustiças sociais.', 0),
    (@question_id, 'D', 'interferência da riqueza sobre o amor.', 1),
    (@question_id, 'E', 'valorização das relações interpessoais.', 0);

-- 1º Dia, questão 37 (Linguagens e Códigos › Língua Portuguesa › Semântica e Figuras de Linguagem). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 71, NULL, 'Se você é feito de música, este texto é pra você

Às vezes, no silêncio da noite, eu fico imaginando: que graça teria a vida sem música? Sem ela não há paz, não há beleza. Nos dias de festa e nas madrugadas de pranto, nas trilhas dos filmes e nas corridas no parque, o que seria de nós sem as canções que enfeitam o cotidiano com ritmo e verso? Quem nunca curou uma dor de cotovelo dançando lambada ou terminou de se afundar ouvindo sertanejo sofrência? Quantos já criticaram funk e fecharam a noite descendo até o chão? Tudo bem... Raul nos ensinou que é preferível ser essa metamorfose ambulante do que ter aquela velha opinião formada sobre tudo.

Já somos castigados com o peso das tragédias, o barulho das buzinas, os ruídos dos conflitos. É pau, é pedra, é o fim do caminho. Há uma nuvem de lágrimas sobre os olhos, você está na lanterna dos afogados, o coração despedaçado. Mas, como um sopro, da janela do vizinho, entra o samba que reanima a mente. Floresce do fundo do nosso quintal a batida que ressuscita o ânimo, sintoniza a alegria e equaliza o fôlego. Levanta, sacode a poeira, dá a volta por cima.

BITTAR, L. Disponível em: www.revistabula.com. Acesso em: 21 nov. 2021 (adaptado).

Defendendo a importância da música para o bem-estar e o equilíbrio emocional das pessoas, a autora usa, como recurso persuasivo, a', 'A autora recorre a versos e títulos conhecidos para defender a importância da música: “metamorfose ambulante” (Raul Seixas), “é pau, é pedra, é o fim do caminho” (Tom Jobim), “levanta, sacode a poeira, dá a volta por cima” (Paulo Vanzolini). O recurso persuasivo é a intertextualidade, ao resgatar versos de letras de canções (C). A metáfora (B) e a enumeração (D) aparecem pontualmente, e não há contradição (A) nem hipérbole como recurso central (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'contradição, ao associar o coração despedaçado à alegria.', 0),
    (@question_id, 'B', 'metáfora, ao citar a imagem da metamorfose ambulante.', 0),
    (@question_id, 'C', 'intertextualidade, ao resgatar versos de letras de canções.', 1),
    (@question_id, 'D', 'enumeração, ao mencionar diferentes ritmos musicais.', 0),
    (@question_id, 'E', 'hipérbole, ao falar em “sofrência”, “tragédias” e “afogados”.', 0);

-- 1º Dia, questão 38 (Linguagens e Códigos › Literatura › Literatura Contemporânea). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 82, NULL, 'pessoas com suas malas
mochilas e valises
chegam e se vão
se encontram
se despedem
e se despem
de seus pertences
como se pudessem chegar
a algum lugar
onde elas mesmas
não estivessem

RUIZ, A. In: SANT’ANNA, A. Rua Aribau: coletânea de poemas. Porto Alegre: TAG, 2018.

Esse poema, por meio da ideia de deslocamento, metaforiza a tentativa de pessoas', 'O poema fala de pessoas que “chegam e se vão”, “se despedem e se despem de seus pertences” como se pudessem chegar a um lugar onde elas mesmas não estivessem. O deslocamento metaforiza a tentativa de fugir da própria identidade (B), pois não se foge de si ao mudar de lugar. Os versos não falam de novos encontros (A), de lugares inexplorados (C), de experiências inusitadas (D) nem de desaparecer da vida social (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'buscarem novos encontros.', 0),
    (@question_id, 'B', 'fugirem da própria identidade.', 1),
    (@question_id, 'C', 'procurarem lugares inexplorados.', 0),
    (@question_id, 'D', 'partirem em experiências inusitadas.', 0),
    (@question_id, 'E', 'desaparecerem da vida em sociedade.', 0);

-- 1º Dia, questão 39 (Linguagens e Códigos › Língua Portuguesa › Variação Linguística). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 73, NULL, 'Falar errado é uma arte, Arnesto!

No dia 6 de agosto de 1910, Emma Riccini Rubinato pariu um garoto sapeca em Valinhos e deu a ele o nome de João Rubinato. Na escola, João não passou do terceiro ano. Não era a área dele, tinha de escolher outra. Fez o que apareceu. Foi ser garçom, metalúrgico, até virar radialista, comediante, ator de cinema e TV, cantor e compositor. De samba.

Como tinha sobrenome italiano, João resolveu mudar para emplacar seu samba. E como ia mudar o sobrenome, mudou o nome. Virou Adoniran Barbosa. O cara falava errado, voz rouca, pinta de malandro da roça. Virou ícone da música brasileira, o mais paulista de todos, falando errado e irritando Vinicius de Moraes, que ficou de bico fechado depois de ouvir a música que Adoniran fez para a letra Bom dia, tristeza, de autoria do Poetinha. Coisa de arrepiar. Para toda essa gente que implicava, Adoniran tinha uma resposta neoerudita: “Gosto de samba e não foi fácil, pra mim, ser aceito como compositor, porque ninguém queria nada com as minhas letras que falavam ‘nóis vai’, ‘nóis fumo’, ‘nóis fizemo’, ‘nóis peguemo’. Acontece que é preciso saber falar errado. Falar errado é uma arte, senão vira deboche”.

Ele sabia o que fazia. Por isso dizia que falar errado era uma arte. A sua arte. Escolhida a dedo porque casava com seu tipo. O Samba do Arnesto é um monumento à fala errada, assim como Tiro ao Álvaro. O erudito podia resmungar, mas o povo se identificava.

PEREIRA, E. Disponível em: www.tribunapr.com.br. Acesso em: 8 jul. 2024 (adaptado).

O “falar errado” a que o texto se refere constitui um preconceito em relação ao uso que Adoniran Barbosa fazia da língua em suas composições, pois esse uso', 'O texto diz que Adoniran falava “nóis vai”, “nóis fumo”, e que ouviu críticas por isso, embora soubesse o que fazia e dissesse que era preciso saber falar errado. O preconceito decorre de seu uso transgredir a norma-padrão vigente na época (E). O texto não relaciona seu falar à linguagem dos comediantes (A), não diz que prejudicava a compreensão (B), que mostrava ausência de estilo (C) ou que limitava a criação poética (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'marcava a linguagem dos comediantes no mesmo período.', 0),
    (@question_id, 'B', 'prejudicava a compreensão das canções pelo público.', 0),
    (@question_id, 'C', 'denunciava a ausência de estilo nas letras de canção.', 0),
    (@question_id, 'D', 'restringia a criação poética nas letras do compositor.', 0),
    (@question_id, 'E', 'transgredia a norma-padrão vigente à época.', 1);

-- 1º Dia, questão 40 (Linguagens e Códigos › Educação Física › Esporte e Sociedade). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 100, NULL, 'O festival folclórico de Parintins, no Amazonas, anunciou que o Boi Caprichoso levou, em 2018, seu 23º título — contra 31 do adversário Boi Garantido. Desde o fim do evento que não paro de cantar duas músicas que aprendi no Bumbódromo (arena onde ocorre o espetáculo). Revezo entre “meu amor, eu sou feliz, ééé azul o meu país”, obviamente do boi azul, o Caprichoso; e “vermelhou o curral, a ideologia avermelhou”, do boi vermelho, o Garantido. Esse revezamento seria proibido em Parintins, cidade tão dividida entre as torcidas dos bois. Em Parintins, você tem de ter um lado. Há aqueles que tentam fugir e dizem que são “garanchoso”, com os quais me identifiquei, mas esses são vistos com certo desdém.

DYNIEWICZ, L. Disponível em: https://viagem.estadao.com.br. Acesso em: 22 nov. 2018 (adaptado).

A apropriação de elementos como rivalidade, competitividade, torcida e gritos de guerra pelo festival de Parintins evidencia a', 'Em Parintins, a disputa entre Caprichoso e Garantido tem rivalidade, competitividade, torcida e gritos de guerra, elementos próprios do esporte, e o autor diz que ali “você tem de ter um lado”. A apropriação evidencia a aproximação da manifestação folclórica com o esporte (D). Não se trata da escolha do local (A), do interesse dos turistas (B), da interação após o evento (C) ou de enredos criados pelos “garanchosos” (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'escolha de um local específico para a festa.', 0),
    (@question_id, 'B', 'importância atribuída pelos turistas aos bois.', 0),
    (@question_id, 'C', 'interação social estabelecida após o evento.', 0),
    (@question_id, 'D', 'aproximação da manifestação folclórica com o esporte.', 1),
    (@question_id, 'E', 'composição de enredos musicais pelos “garanchosos”.', 0);

-- 1º Dia, questão 41 (Linguagens e Códigos › Língua Portuguesa › Interpretação de Texto). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 72, NULL, 'O Brasil somou cerca de 60 mil novos casos de câncer de mama até o final de 2019, número que corresponde a 25% de todos os diagnósticos da condição registrados no país, segundo dados do Instituto Nacional do Câncer (Inca). Apesar de o Outubro Rosa ser o mês de conscientização sobre a questão voltada para as mulheres, é muito importante lembrar que um dos grandes mitos da medicina é o de que o câncer de mama não afeta o sexo masculino.

Fatores importantes para detectar o câncer de mama masculino:

1. Genética: se houver casos na família, as chances são um pouco mais elevadas.

2. Hormônios: homens podem desenvolver tecido real das glândulas mamárias por tomarem certos medicamentos ou apresentarem níveis hormonais anormais.

3. Caroços: é necessário que os médicos se atentem a alguns sintomas suspeitos, como um caroço na área do tórax.

4. Retração na pele: em situações mais graves do câncer de mama masculino, é possível também ocorrer uma retração do mamilo.

Disponível em: https://pebmed.com.br. Acesso em: 24 nov. 2021 (adaptado).

As informações dessa reportagem auxiliam no combate ao câncer de mama masculino por apresentarem um alerta sobre o(s)', 'A reportagem desmente o mito de que câncer de mama não afeta homens e lista fatores e sintomas: histórico familiar, hormônios, caroços na região do tórax e retração da pele ou do mamilo. Ela ajuda no combate à doença ao alertar sobre os sinais indicadores (A). Não apresenta índice de crescimento de casos (B), exames diagnósticos (C), mitos sobre herança genética (D) nem o período de campanhas (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'sinais indicadores da doença.', 1),
    (@question_id, 'B', 'índice de crescimento de casos.', 0),
    (@question_id, 'C', 'exames para diagnóstico do tumor.', 0),
    (@question_id, 'D', 'mitos a respeito da herança genética.', 0),
    (@question_id, 'E', 'período de campanhas de conscientização.', 0);

-- 1º Dia, questão 42 (Linguagens e Códigos › Língua Portuguesa › Gêneros Textuais). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 74, NULL, 'Feijoada à minha moda

Amiga Helena Sangirardi
Conforme um dia prometi
Onde, confesso que esqueci
E embora — perdoe — tão tarde
(Melhor do que nunca!) este poeta
Segundo manda a boa ética
Envia-lhe a receita (poética)
De sua feijoada completa.

Em atenção ao adiantado
Da hora em que abrimos o olho
O feijão deve, já catado
Nos esperar, feliz, de molho.

Uma vez cozido o feijão
(Umas quatro horas, fogo médio)
Nós, bocejando o nosso tédio
Nos chegaremos ao fogão

[...]

De carne-seca suculenta
Gordos paios, nédio toucinho
(Nunca orelhas de bacorinho
Que a tornam em excesso opulenta!)

[...]

Enquanto ao lado, em fogo brando
Desmilinguindo-se de gozo
Deve também se estar fritando
O torresminho delicioso
Em cuja gordura, de resto
(Melhor gordura nunca houve!)
Deve depois frigir a couve
Picada, em fogo alegre e presto.

[...]

Dever cumprido. Nunca é vã
A palavra de um poeta... — jamais!
Abraça-a, em Brillat-Savarin,
O seu Vinicius de Moraes.

MORAES, V. In: CÍCERO, A.; QUEIROZ, E. (Org.). Vinicius de Moraes: nova antologia poética. São Paulo: Cia. das Letras, 2005 (fragmento).

Apesar de haver marcas formais de carta e receita, a característica que define esse texto como poema é o(a)', 'O poema usa marcas formais de carta (vocativo, destinatário, assinatura) e de receita (etapas de preparo da feijoada), mas o que o define como poema é o uso de linguagem expressiva: versos, rimas, ritmo, ironia e imagens (“desmilinguindo-se de gozo”). A característica definidora é a utilização de uma linguagem expressiva (D). Nomear interlocutor (A) e descrever procedimentos (C) são marcas de carta e de receita, e intimidade (B) e ingredientes (E) não bastam para caracterizar um poema.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'nomeação de um interlocutor.', 0),
    (@question_id, 'B', 'manifestação de intimidade.', 0),
    (@question_id, 'C', 'descrição de procedimentos.', 0),
    (@question_id, 'D', 'utilização de uma linguagem expressiva.', 1),
    (@question_id, 'E', 'apresentação de ingredientes culinários.', 0);

-- 1º Dia, questão 44 (Linguagens e Códigos › Educação Física › Esporte e Sociedade). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 100, NULL, 'Os Jogos Olímpicos já não são mais os mesmos.

E isso não é nem uma crítica, nem um elogio. É uma constatação. Esse movimento começou com o vôlei de praia tornando-se esporte olímpico em 1996, passou pela chegada do BMX Racing como primeiro “radical” a entrar no programa em 2008, e agora atinge seu momento mais insólito com a inclusão do break dance como modalidade dos Jogos de Paris, em 2024. Para os mais tradicionalistas, o cruzamento da linha que delimitava o que é esporte e o que é cultura e arte é uma afronta ao espírito dos Jogos Olímpicos. Skate e surfe, que há anos têm competições na televisão, pareciam estar na divisa entre esses dois mundos, o limite do aceitável pelos puristas. O break dance estaria do lado de “lá” dessa fronteira. Para o Comitê Olímpico Internacional, a decisão faz parte de uma estratégia de se comunicar com jovens urbanos que se exercitam e se entretêm de uma maneira muito diferente da dos seus avós.

Disponível em: www.uol.com.br. Acesso em: 19 nov. 2021 (adaptado).

A mudança no programa olímpico mencionada no texto mostra que o esporte está se', 'O texto mostra que os Jogos Olímpicos incorporaram vôlei de praia, BMX e break dance, numa estratégia do COI de dialogar com jovens urbanos que se exercitam e se entretêm de forma diferente da dos avós. A mudança mostra que o esporte está se adaptando às demandas do seu tempo (E). Não se trata de aproximar-se da aventura (A), manter-se na forma padrão (B), tornar-se dança (C) ou afastar-se da cultura (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'aproximando da aventura.', 0),
    (@question_id, 'B', 'mantendo em sua forma padrão.', 0),
    (@question_id, 'C', 'tornando uma forma de dança.', 0),
    (@question_id, 'D', 'afastando de elementos culturais.', 0),
    (@question_id, 'E', 'adaptando às demandas do seu tempo.', 1);

-- 1º Dia, questão 46 (Ciências Humanas › Geografia › Geologia e Relevo). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 11, NULL, 'TEXTO I

Terremoto de magnitude 5,9 atinge Valparaíso, no Chile

Um terremoto de magnitude 5,9 atingiu a cidade de Valparaíso, na costa chilena. O terremoto ocorreu a uma profundidade de 112 quilômetros.

Disponível em: www.cnnbrasil.com.br. Acesso em: 6 nov. 2021 (adaptado).

TEXTO II

Tremor de terra de magnitude 4,8 é registrado no interior do Amazonas

Um tremor de terra de magnitude 4,8 foi registrado no município de Atalaia do Norte, no interior do estado do Amazonas. O abalo é de magnitude considerada mediana para os níveis do Brasil. Os eventos dessa região costumam ser resultado das atividades da placa de Nazca.

Disponível em: https://g1.globo.com. Acesso em: 6 nov. 2021 (adaptado).

TEXTO III

Moradores do interior de SP relatam tremores de terra

Moradores usaram as redes sociais para relatar tremores de terra no interior de São Paulo. As atividades foram registradas nas cidades de Júlio Mesquita e Guaimbê e tiveram magnitude 3,0 na escala Richter, o que é considerado pequeno e sem previsão de danos.

Disponível em: https://noticias.r7.com. Acesso em: 6 nov. 2021 (adaptado).

As diferenças entre os eventos geológicos relatados decorrem de distintas posições geográficas das cidades em relação a:', 'O terremoto no Chile ocorre na costa oeste da América do Sul, onde a placa de Nazca mergulha sob a Sul-Americana (subducção), gerando abalos fortes. No Amazonas há reflexo mais fraco dessas atividades, e no interior de São Paulo, bem longe das bordas de placa, os tremores são pequenos. As diferenças decorrem da posição das cidades em relação às zonas de subducção (C). Planícies costeiras, bacias continentais, áreas de denudação e vertentes escarpadas (A, B, D, E) são feições de relevo que não explicam a magnitude dos abalos.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Planícies costeiras.', 0),
    (@question_id, 'B', 'Bacias continentais.', 0),
    (@question_id, 'C', 'Zonas de subducção.', 1),
    (@question_id, 'D', 'Áreas de denudação.', 0),
    (@question_id, 'E', 'Vertentes escarpadas.', 0);

-- 1º Dia, questão 47 (Ciências Humanas › Sociologia › Trabalho e Sociedade). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 27, NULL, 'Tal qual num exército, não se compreende um efetivo composto apenas de oficiais. Também na saúde pública, os funcionários técnicos graduados necessitam ser assistidos por auxiliares em número suficiente e com preparo adequado, constituído pelas enfermeiras de saúde pública, educadoras ou visitadoras sanitárias, técnicos de laboratório, inspetores ou guardas etc., para não falarmos no pessoal burocrático, não especializado.

PAULA SOUZA, G. H.; VIEIRA, F. B. Centro de saúde “eixo” de organização sanitária. Boletim do Instituto de Higiene de São Paulo, n. 59 (adaptado).

O texto dos sanitaristas atuantes nas décadas de 1920 e 1930 veicula uma mensagem caracterizada pela', 'Os sanitaristas comparam a saúde pública a um exército, em que não bastam oficiais: os técnicos graduados precisam de auxiliares em número suficiente, como enfermeiras, visitadoras, técnicos de laboratório, inspetores e pessoal burocrático. A mensagem se caracteriza pela hierarquização profissional (E). Não há higienização moral (A), eugenia (B), assimilação cultural (C) ou hegemonização identitária (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'higienização moral.', 0),
    (@question_id, 'B', 'imposição eugênica.', 0),
    (@question_id, 'C', 'assimilação cultural.', 0),
    (@question_id, 'D', 'hegemonização identitária.', 0),
    (@question_id, 'E', 'hierarquização profissional.', 1);

-- 1º Dia, questão 48 (Ciências Humanas › Geografia › Geografia Urbana). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 15, NULL, 'O rompimento da barragem de Fundão levou muito consigo. A lama soterrou sonhos e modificou de forma permanente centenas de vidas nascidas e criadas em Bento Rodrigues e Paracatu, em Mariana (MG). Mas não somente. Ao se estender ao longo do rio, outras famílias e histórias foram atingidas de formas diferentes. Ao fugirem dos rejeitos que rapidamente tomaram as localidades, deixaram para trás os resquícios da vida que tiveram até o 5 de novembro de 2015. Nada jamais seria igual.

SANTOS, P. Histórias soterradas. Curinga, n. 19, nov. 2016 (adaptado).

Conforme o texto, o evento gerou o seguinte impacto na relação entre as pessoas e o seu espaço vivido:', 'O texto afirma que a lama soterrou sonhos e modificou vidas em Bento Rodrigues e Paracatu e que os moradores deixaram para trás “os resquícios da vida que tiveram” até 5 de novembro de 2015: casas, memórias e laços com o lugar foram destruídos. O impacto foi a fragilização de vínculos afetivos com o espaço vivido (C). Não houve flexibilização ambiental (A), consolidação de identidades regionais (B), fim das práticas exploratórias (D) ou recuperação de tradições (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Flexibilização de parâmetros ambientais.', 0),
    (@question_id, 'B', 'Consolidação de identidades regionais.', 0),
    (@question_id, 'C', 'Fragilização de vínculos afetivos.', 1),
    (@question_id, 'D', 'Supressão de práticas exploratórias.', 0),
    (@question_id, 'E', 'Recuperação de tradições ancestrais.', 0);

-- 1º Dia, questão 49 (Ciências Humanas › História › República Velha). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 3, NULL, 'Com a proximidade do final do século XIX, amplificam-se as expectativas com relação ao século seguinte. Se muitas eram as utopias, talvez uma das mais evidentes tenha se concentrado nas potencialidades da nova ciência, com suas invenções e projetos. Não é por mera coincidência que a agenda do país tenha sido tomada pela introdução de uma série de inventos. De forma acelerada, entraram no Brasil a luz elétrica e, com ela, o telégrafo, o telefone, o cinematógrafo. Na área dos transportes, o trem a vapor é substituído pelo elétrico, que assiste à entrada do automóvel e até do aeroplano.

COSTA, A. M.; SCHWARCZ, L. M. 1890-1914, no tempo das certezas. São Paulo: Cia. das Letras, 2000 (adaptado).

No Brasil, os eventos descritos ganharam conotação política ao serem vinculados à', 'No fim do século XIX, as inovações (luz elétrica, telégrafo, telefone, cinematógrafo, automóvel) eram vistas como sinal de progresso e modernidade. No Brasil, recém-republicano, esses eventos ganharam conotação política ao serem vinculados à visão republicana de nação, associada à ordem, ao progresso e à ciência (E). Não se vinculavam ao imperialismo estratégico (A), ao mercantilismo (B), à laicidade da educação (C) ou ao absolutismo (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'expansão estratégica do imperialismo.', 0),
    (@question_id, 'B', 'ascensão gradual do mercantilismo.', 0),
    (@question_id, 'C', 'laicidade da educação.', 0),
    (@question_id, 'D', 'retomada do absolutismo.', 0),
    (@question_id, 'E', 'visão republicana de nação.', 1);

-- 1º Dia, questão 50 (Ciências Humanas › História › Brasil Colônia). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 1, NULL, 'O bispo Bartolomeu de Las Casas é o homem mais odiado da América, o anti-Cristo dos senhores, o açoite destas terras. Por sua culpa, o imperador promulgou novas leis que despojam de escravos índios os filhos dos conquistadores. O que será deles sem os braços que os sustentam nas minas e nas lavouras? As novas leis estão arrancando a comida de suas bocas. Las Casas é o homem mais amado da América. Voz dos mudos, teimoso defensor dos que recebem pior tratamento que o esterco das praças, denunciador de quem por cobiça converte Jesus Cristo no mais cruel dos deuses e o rei em lobo faminto de carne humana.

GALEANO, E. Os nascimentos. Porto Alegre: L&PM, 2011 (adaptado).

Os diferentes pontos de vista presentes no texto expressam que o bispo era, ao mesmo tempo,', 'O texto mostra duas visões de Las Casas: “o homem mais odiado da América” para os senhores que perderam escravos indígenas com as novas leis e “o homem mais amado” como defensor dos que recebiam tratamento pior que o esterco. Ele era detestado pelos colonizadores e respeitado pelos povos do lugar (B). Não foi execrado pelos reis (A), nem idolatrado pelos governantes (C), nem adorado pelos invasores (D), nem desacatado pelos excluídos (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'execrado pelos reis e reverenciado pelos religiosos do local.', 0),
    (@question_id, 'B', 'detestado pelos colonizadores e respeitado pelos povos do lugar.', 1),
    (@question_id, 'C', 'menosprezado pela colônia e idolatrado pelos governantes da região.', 0),
    (@question_id, 'D', 'desrespeitado pela metrópole e adorado pelos invasores da Espanha.', 0),
    (@question_id, 'E', 'desacatado pelos excluídos e valorizado pelos negociantes de negros.', 0);

-- 1º Dia, questão 51 (Ciências Humanas › História › Idade Média). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 6, NULL, 'A valsa vienense é a mais antiga das danças de salão tradicional. É dançada desde a Idade Média, quando os pares davam voltas pelo salão realizando giros em torno de si mesmos em postura fechada. Pelo fato de ser dançada aos pares em contato íntimo, a valsa encantava a sociedade medieval, como também sofria proibições por infringir os “bons costumes”. Originária das danças campestres e folclóricas, no século XVI, a aristocracia francesa abandonou a valsa por sua estreita relação com a cultura plebeia, retomando-a posteriormente.

FRANCO, N.; FERREIRA, N. Evolução da dança no contexto histórico: aproximações iniciais com o tema. Repertório, n. 26, 2016 (adaptado).

A expressão cultural descrita no texto foi rejeitada no início da Idade Moderna por congregar', 'O texto diz que a valsa, originária de danças campestres e folclóricas, foi abandonada pela aristocracia francesa no século XVI “por sua estreita relação com a cultura plebeia”. Ela foi rejeitada no início da Idade Moderna por congregar elementos provenientes de segmentos populares (E). Não há menção a feitiçaria nórdica (A), rituais pagãos (B), regras renascentistas (C) ou compassos de territórios colonizados (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'traços advindos da feitiçaria nórdica.', 0),
    (@question_id, 'B', 'práticas inspiradas em rituais pagãos.', 0),
    (@question_id, 'C', 'regras decorrentes do período renascentista.', 0),
    (@question_id, 'D', 'compassos produzidos em territórios colonizados.', 0),
    (@question_id, 'E', 'elementos provenientes de segmentos populares.', 1);

-- 1º Dia, questão 52 (Ciências Humanas › Sociologia › Movimentos Sociais). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 29, NULL, 'O Black Lives Matter vai para além do nacionalismo estreito que pode prevalecer no seio das comunidades negras, que se limita a apelar aos negros a amar os negros, viver como os negros e comprar produtos dos negros, e mantém à frente do movimento homens negros heterossexuais. Black Lives Matter estima as vidas dos negros e negras homossexuais e transexuais, pessoas incapacitadas, negros sem documentos ou com antecedentes criminais, mulheres e as vidas de todos os negros de todo o espectro de gêneros.

LA BOTZ, D. O movimento Black Lives Matter organiza-se e procura definir-se politicamente. Disponível em: www.ufes.br. Acesso em: 4 out. 2021 (adaptado).

A reivindicação do movimento norte-americano apresentada no texto consiste na necessidade de', 'O texto diz que o Black Lives Matter vai além do nacionalismo estreito e estima as vidas de negros e negras homossexuais, transexuais, pessoas com deficiência, sem documentos ou com antecedentes criminais. A reivindicação é integrar a diversidade do grupo identitário (B). O movimento não se limita a manter direitos já conquistados (A), não prioriza culturas africanas (C) e rejeita supremacismo (D) e o modelo androcêntrico (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'manter a conquista de direitos sociais.', 0),
    (@question_id, 'B', 'integrar a diversidade do grupo identitário.', 1),
    (@question_id, 'C', 'priorizar a preservação de culturas africanas.', 0),
    (@question_id, 'D', 'defender a adoção de valores supremacistas.', 0),
    (@question_id, 'E', 'permitir a permanência do modelo androcêntrico.', 0);

-- 1º Dia, questão 54 (Ciências Humanas › Filosofia › Teoria do Conhecimento). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 25, NULL, 'Os grupos dominantes são beneficiados em termos de credibilidade e podem, com isso, controlar falas de membros de outros grupos, descredibilizando seus testemunhos com base em concepções compartilhadas de preconceito de identidade (gênero e raça). Algumas formas de preconceito tornam as declarações das pessoas menos importantes devido ao seu pertencimento a determinado grupo social. Assim, um falante recebe menos credibilidade devido ao preconceito do ouvinte.

KUHNEN, T. Resenha de The Power and Ethics of Knowing, de Miranda Fricker. Revista Princípios, n. 33, 2013.

Com base na reflexão suscitada no texto, o preconceito de identidade é responsável por um tipo de injustiça', 'O texto, sobre a obra de Miranda Fricker, mostra que o preconceito de identidade faz com que o ouvinte dê menos credibilidade ao falante. Trata-se de injustiça epistêmica, que prejudica o testemunho e as trocas informacionais (D). Não é injustiça estética (A), sensorial (B), afetiva (C) ou econômica (E), pois o prejuízo recai sobre a credibilidade do que se diz.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'estética, que normatiza os padrões corporais.', 0),
    (@question_id, 'B', 'sensorial, que privilegia as habilidades visuais.', 0),
    (@question_id, 'C', 'afetiva, que impede as expressões emocionais.', 0),
    (@question_id, 'D', 'epistêmica, que prejudica as trocas informacionais.', 1),
    (@question_id, 'E', 'econômica, que perpetua as desigualdades materiais.', 0);

-- 1º Dia, questão 55 (Ciências Humanas › Filosofia › Filosofia Antiga). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 19, NULL, 'A alma funciona no meu corpo de maneira maravilhosa. Nele se aloja, certamente, mas sabe bem dele escapar: escapa para ver as coisas através da janela dos meus olhos, escapa para sonhar quando durmo, para sobreviver quando morro. Minha alma durará muito tempo e mais que muito tempo, quando meu corpo vier a apodrecer. Viva minha alma! É meu corpo luminoso, purificado, virtuoso, ágil, móvel, tépido, viçoso; é meu corpo liso, castrado, arredondado como uma bolha de sabão.

FOUCAULT, M. O corpo utópico, as heterotopias. São Paulo: Edições N-1, 2013.

Esse texto reforça uma concepção metafísica clássica que remete a um(a)', 'Foucault descreve a alma como algo que se aloja no corpo, mas escapa dele para sonhar, ver e sobreviver à morte, separando alma e corpo. A concepção metafísica clássica, desde o dualismo platônico, é o pensamento dicotômico (B). Não se trata de um pressuposto lógico (A), de contemplação da natureza (C), de raciocínio argumentativo (D) nem de crítica à individualidade (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'pressuposto lógico.', 0),
    (@question_id, 'B', 'pensamento dicotômico.', 1),
    (@question_id, 'C', 'contemplação da natureza.', 0),
    (@question_id, 'D', 'raciocínio argumentativo.', 0),
    (@question_id, 'E', 'crítica à individualidade.', 0);

-- 1º Dia, questão 56 (Ciências Humanas › Sociologia › Cultura e Indústria Cultural). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 28, NULL, 'No primeiro dia, foi colocada uma panela de barro no centro do barracão, a qual representava o espírito do morto presente na sala. Aqueles que dançavam depositavam moedas ao passarem junto dela e, ao seu redor, milho branco, mel, água, acaçás, cachaça. No segundo dia, os ogãs, antes de iniciar a cerimônia, caminharam pelo corredor formado pelas casas, batendo com longas varas de bambus nos seus beirais, até alcançarem o portão de entrada. No terceiro dia, quatro pessoas, as mais influentes do culto, carregaram um lençol, que aparentemente continha um corpo em seu interior. No entanto, esse corpo era formado por folhas verdes de plantas, que foram derramadas sobre uma pessoa.

MANZOCHI, H. M. Axexe, um rito de passagem. Revista do Museu de Arqueologia e Etnologia, n. 5, 1995 (adaptado).

O ritual brasileiro apresentado no texto representa, para seus adeptos, a', 'O axexê é um rito funerário de matriz africana em que os adeptos reverenciam o morto com oferendas e gestos rituais coletivos ao longo de vários dias. Para os adeptos, representa a manutenção de uma memória coletiva (A). Não há contestação de identidade étnica (B), sacrifício de divindade (C), legitimação de prática “pagã” (D) ou revolta social (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'manutenção de uma memória coletiva.', 1),
    (@question_id, 'B', 'contestação de uma identidade étnica.', 0),
    (@question_id, 'C', 'imolação de uma divindade africana.', 0),
    (@question_id, 'D', 'legitimação de uma prática pagã.', 0),
    (@question_id, 'E', 'promissão de uma revolta social.', 0);

-- 1º Dia, questão 57 (Ciências Humanas › Filosofia › Filosofia Política). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 24, NULL, 'Espaços públicos não são produtos dados e acabados, uma instituição que, uma vez estabelecida, traria a paz da consensualidade e a perfeita igualdade. São os lugares em que os problemas aparecem e se transformam em debates, em diálogo e em possibilidade de ajuste e compromissos. Por isso, não anulam os conflitos, ao contrário, são canais de comunicação e de visibilidade de oposições.

GOMES, P. C. C. Espaço público, espaços públicos. Geographia, n. 44, set.-dez. 2018 (adaptado).

As características descritas no texto exibem a importância dos espaços públicos para a', 'O texto diz que os espaços públicos são lugares em que os problemas aparecem e viram debate, diálogo e ajuste, canais de comunicação e de visibilidade das oposições. Isso mostra sua importância para a construção da democracia (D). Eles não anulam conflitos nem erradicam a violência (C), e lazer (A), vigilância (B) e diversificação do trabalho (E) não são tratados.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'prática do lazer.', 0),
    (@question_id, 'B', 'vigilância da sociedade.', 0),
    (@question_id, 'C', 'erradicação da violência.', 0),
    (@question_id, 'D', 'construção da democracia.', 1),
    (@question_id, 'E', 'diversificação do trabalho.', 0);

-- 1º Dia, questão 58 (Ciências Humanas › Filosofia › Ética e Moral). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 23, NULL, 'TEXTO I

Aristóteles entendia que a felicidade era diretamente ligada ao respeito pela própria natureza e, de certa maneira, a uma vida que tivesse na natureza de si mesma uma referência inabalável. Isso lhe permitiu formular o conceito de excelência. O que seria excelência? Seria, justamente, ao longo da vida, tirar de si mesmo, em forma de performance, de conduta, de comportamento, de disposição, o que a natureza permitiria de melhor.

COEN, M.; BARROS FILHO, C. A monja e o professor: reflexões sobre ética, preceitos e valores. Rio de Janeiro: Best Seller, 2018.

TEXTO II

A noção de eudaimonia é central para a ética aristotélica. A eudaimonia é uma atividade e não um estado psicológico, pois é definida na Ética a Nicômaco como uma atividade da alma com base na virtude moral. A virtude moral é definida em termos de uma disposição diretamente ligada à deliberação, o que o leva a estudar a virtude intelectual que opera em seu interior, isto é, a prudência. A estrutura conceitual da ética aristotélica responde a uma tentativa de elucidar conceitualmente em que consiste isto, agir bem, ou, na linguagem aristotélica, o que significa ser feliz.

ZINGANO, M. Eudaimonia, razão e contemplação na ética aristotélica. Analytica, n. 1, 2017 (adaptado).

Os textos indicam que a prática de ações virtuosas, sempre efetivada na pólis, ocorre por meio do(a)', 'Os textos tratam da ética aristotélica: a excelência é tirar de si o melhor de sua natureza, e a eudaimonia é uma atividade da alma baseada na virtude moral, que depende da deliberação e da prudência. A ação virtuosa na pólis se dá, portanto, pelo aperfeiçoamento das condutas humanas (D). A teoria das formas (A) é de Platão, e os textos não tratam de técnicas retóricas (C), de episteme (E) nem apenas de princípios racionais (B).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'teoria das formas essenciais.', 0),
    (@question_id, 'B', 'identificação dos princípios racionais.', 0),
    (@question_id, 'C', 'desenvolvimento das técnicas retóricas.', 0),
    (@question_id, 'D', 'aperfeiçoamento das condutas humanas.', 1),
    (@question_id, 'E', 'conhecimento das epistemes verdadeiras.', 0);

-- 1º Dia, questão 59 (Ciências Humanas › História › Idade Média). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 6, NULL, 'As instituições que a Idade Média nos legou são de um valor maior e mais imperecível do que suas catedrais. E a universidade é nitidamente uma instituição medieval — tanto quanto a monarquia constitucional, ou os parlamentos, ou o julgamento por meio do júri. As universidades e os produtos imediatos das suas atividades constituem a grande realização da Idade Média na esfera intelectual.

RASHDALL, H. apud OLIVEIRA, T. Origem e memória das universidades medievais. Varia História, n. 37, jan.-jun. 2007.

De acordo com o texto, o legado das universidades medievais torna-se um relevante patrimônio histórico-cultural por', 'O texto afirma que as universidades são uma realização medieval de valor imperecível, produtos das atividades intelectuais da Idade Média. Seu legado é patrimônio histórico-cultural por agregar tradição e conhecimento científico (E). O texto não menciona currículo padronizado (A), rejeição de costumes orientais (B), função mercantil (C) nem valores ecumênicos (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'adotar currículo e método padronizado.', 0),
    (@question_id, 'B', 'rejeitar ideologias e costumes orientais.', 0),
    (@question_id, 'C', 'possuir organização e função mercantil.', 0),
    (@question_id, 'D', 'transmitir técnicas e valores ecumênicos.', 0),
    (@question_id, 'E', 'agregar tradição e conhecimento científico.', 1);

-- 1º Dia, questão 60 (Ciências Humanas › Geografia › Geografia Agrária). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 16, NULL, 'Há experiências de lutas sociais de reapropriação cultural da natureza que são movimentos emblemáticos, como a dos seringueiros no Brasil, que da luta sindical para a comercialização da borracha chegaram a inventar o conceito de reserva extrativista e estão avançando para um novo modo de produção, uma nova racionalidade produtiva, mostrando que é possível viver bem, e não apenas sobreviver, em harmonia com a natureza que habitam.

O novo planeta que podemos imaginar é feito desses territórios produtivos que não são apenas economias de autossubsistência mas economias que potencializam a produtividade ecológica de seus territórios.

LEFF, E. Discursos sustentáveis. São Paulo: Cortez, 2010 (adaptado).

O texto expõe a possibilidade de uma nova racionalidade produtiva por meio de uma gestão territorial que se baseia na', 'Leff cita os seringueiros, que criaram a reserva extrativista e mostram ser possível viver bem em harmonia com a natureza, e fala de territórios produtivos que potencializam a produtividade ecológica. A gestão territorial se baseia na conexão de valores fundamentados por decisões locais (B). Mercados regionais (A), preços nacionais (C), normas de instituições mundiais (D) e determinações globais (E) vão no sentido oposto da autonomia local defendida.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'integração de mercados regidos por pressões regionais.', 0),
    (@question_id, 'B', 'conexão de valores fundamentados por decisões locais.', 1),
    (@question_id, 'C', 'unificação de preços delimitados por demandas nacionais.', 0),
    (@question_id, 'D', 'normatização de regras construídas por instituições mundiais.', 0),
    (@question_id, 'E', 'valorização de tradições orientadas por determinações globais.', 0);

-- 1º Dia, questão 61 (Ciências Humanas › Filosofia › Filosofia Contemporânea). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 22, NULL, 'Eu sentia falta do futuro. É claro que eu sabia, muito mesmo antes da recorrência dele, que nunca envelheceria. Era muito provável que eu nunca mais fosse ver o oceano de uma altura de trinta mil pés de novo, uma distância tão grande que não dá nem para distinguir as ondas, nem nenhum barco, de um jeito que faz o oceano parecer um enorme e infinito monólito.

Eu poderia imaginá-lo. Eu poderia me lembrar dele.

Mas não poderia vê-lo de novo, e me ocorreu que a ambição voraz dos seres humanos nunca é saciada quando os sonhos são realizados, porque há sempre a sensação de que tudo poderia ter sido feito melhor e ser feito outra vez.

GREEN, J. A culpa é das estrelas. Rio de Janeiro: Intrínseca, 2012.

O texto apresenta uma reflexão da personagem acerca de um problema característico da filosofia contemporânea, que trata da(s)', 'A personagem, doente, sabe que nunca envelhecerá e não verá mais o oceano do alto, refletindo sobre a limitação da vida e a insaciável ambição humana. O problema filosófico contemporâneo tratado é o da finitude humana (B), tema do existencialismo. O trecho não discute implicações éticas (A), limites da linguagem (C), pressuposição existencial (D) nem objetividade do conhecimento (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'implicações éticas.', 0),
    (@question_id, 'B', 'finitude humana.', 1),
    (@question_id, 'C', 'limitações da linguagem.', 0),
    (@question_id, 'D', 'pressuposição existencial.', 0),
    (@question_id, 'E', 'objetividade do conhecimento.', 0);

-- 1º Dia, questão 62 (Ciências Humanas › Filosofia › Teoria do Conhecimento). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 25, NULL, 'TEXTO I

O empirismo moderno foi, em grande parte, condicionado por dois dogmas. Um deles é a crença em certa divisão fundamental entre verdades analíticas, ou fundadas em significados independentemente de questões de fato, e verdades sintéticas, ou fundadas em fatos. O outro dogma é o reducionismo: a crença de que todo enunciado significativo é equivalente a algum construto lógico sobre termos que se referem à experiência imediata.

QUINE, W. V. O. Dois dogmas do empirismo. In: RYLE, G. et al. Ensaios. São Paulo: Abril Cultural, 1975.

TEXTO II

Teses: 1. Somente os enunciados que possuem conteúdo factual são teoricamente significativos; enunciados que não podem, em princípio, estar fundamentados pela experiência são carentes de significado. 2. As ciências empíricas usam somente o conteúdo empírico da realidade. 3. A filosofia usa um conceito não empírico da realidade.

CARNAP, R. Pseudoproblemas na filosofia. In: SCHLICK, M.; CARNAP, R.; POPPER, K. Coletânea de textos. São Paulo: Abril Cultural, 1975.

Ao comparar os textos, conclui-se que eles apresentam posicionamentos filosóficos divergentes com relação ao', 'O Texto I, de Quine, critica dois dogmas do empirismo: a divisão entre verdades analíticas e sintéticas e o reducionismo, segundo o qual todo enunciado significativo equivale a termos da experiência imediata. O Texto II, de Carnap, sustenta que só são significativos os enunciados com conteúdo factual. A divergência é sobre o estatuto epistemológico da linguagem, ou seja, sobre o que torna um enunciado significativo (A). Moral, metafísica, ontologia e estética (B, C, D, E) não são o foco da comparação.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'estatuto epistemológico da linguagem.', 1),
    (@question_id, 'B', 'alicerce estruturante da moralidade.', 0),
    (@question_id, 'C', 'conteúdo essencial da metafísica.', 0),
    (@question_id, 'D', 'princípio constitutivo da ontologia.', 0),
    (@question_id, 'E', 'domínio reflexivo da estética.', 0);

-- 1º Dia, questão 63 (Ciências Humanas › Sociologia › Cultura e Indústria Cultural). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 28, NULL, 'Uma das principais atividades provocadas pela arte, a reflexão, é abandonada pela indústria cultural. A indústria cultural seria como uma isca que ilude os indivíduos, com o sonho de que eles são livres, originais, únicos e especiais quando, na verdade, os trata como servos e partes de uma massa homogênea.

FONTES, B.; MAGALHÃES, R. O que é indústria cultural? In: BODART, C. N. (Org.). Conceitos e categorias do ensino de sociologia. Maceió: Café com Sociologia, 2021 (adaptado).

Ao analisar as consequências da dinâmica apresentada no texto, as autoras destacam a importância do conceito como:', 'O texto diz que a indústria cultural ilude os indivíduos com o sonho de que são livres e únicos, quando os trata como massa homogênea e os afasta da reflexão. Ao analisar essas consequências, o conceito se mostra um mecanismo de controle social (B). Não é uma ferramenta de luta coletiva (A), instituição pública (C), organização da iniciativa privada (D) nem instrumento de manipulação estatal (E), pois o texto aponta o mercado cultural, não o Estado.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Ferramenta de luta coletiva.', 0),
    (@question_id, 'B', 'Mecanismo de controle social.', 1),
    (@question_id, 'C', 'Instituição de interesse público.', 0),
    (@question_id, 'D', 'Organização da iniciativa privada.', 0),
    (@question_id, 'E', 'Instrumento de manipulação estatal.', 0);

-- 1º Dia, questão 64 (Ciências Humanas › História › Brasil Império). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 2, NULL, 'TEXTO I

Neta de Tomásia, uma escravizada alforriada, Chiquinha Gonzaga foi uma das primeiras mulheres a se destacar na música popular brasileira. Entre suas obras mais famosas estão a marcha Ó abre alas, sucesso nos blocos de Carnaval até hoje, e o tango Gaúcho, além de inúmeras peças musicais para teatro e óperas. Chiquinha também teve uma atuação em defesa dos direitos das mulheres e pelo fim da escravatura.

Disponível em: www.uol.com.br. Acesso em: 7 out. 2023 (adaptado).

TEXTO II

Depois da estreia de sua peça Gonzaga, em 1867, que tem como um de seus núcleos dramáticos dois escravizados, pai e filha, separados há muitos anos, Castro Alves escreve talvez o maior conjunto de poemas antiescravistas do Romantismo brasileiro, publicados em 1883 no livro Os escravos, doze anos após a sua morte. Os textos Vozes d’África e Navio negreiro, por exemplo, publicados em folhetos, já em 1878, tiveram enorme repercussão desde a sua circulação, a ponto de Afrânio Coutinho (um importante crítico brasileiro) afirmar ter sido Castro Alves, no que diz respeito à poesia antiescravista, um dos primeiros que o Brasil ouviu.

Disponível em: www.uol.com.br. Acesso em: 8 out. 2023 (adaptado).

Os textos indicam a participação de artistas e intelectuais brasileiros em defesa do(a)', 'Chiquinha Gonzaga atuou “pelo fim da escravatura” e Castro Alves escreveu o maior conjunto de poemas antiescravistas do Romantismo brasileiro (Vozes d’África, Navio negreiro). Os textos indicam a participação de artistas e intelectuais em defesa do movimento abolicionista (C). Eles não se relacionam à ação integralista (A), à literatura realista (B), à política indigenista (D) nem ao nacionalismo desenvolvimentista (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'ação integralista.', 0),
    (@question_id, 'B', 'literatura realista.', 0),
    (@question_id, 'C', 'movimento abolicionista.', 1),
    (@question_id, 'D', 'política indigenista.', 0),
    (@question_id, 'E', 'nacionalismo desenvolvimentista.', 0);

-- 1º Dia, questão 65 (Ciências Humanas › Geografia › Globalização e Geopolítica). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 18, NULL, 'Como conclusão provisória, parece então que a globalização tem, sim, o efeito de contestar e deslocar as identidades centradas e “fechadas” de uma cultura nacional. Ela tem um efeito pluralizante sobre as identidades, produzindo uma variedade de possibilidades e novas posições de identificação, e tornando as identidades mais posicionais, mais políticas, mais plurais e diversas; menos fixas, unificadas ou trans-históricas.

HALL, S. A identidade cultural na pós-modernidade. Rio de Janeiro: DP&A, 2011.

De acordo com o texto, o processo apresentado contribuiu para', 'Stuart Hall afirma que a globalização desloca as identidades nacionais “fechadas” e as torna mais plurais, posicionais e políticas, com novas posições de identificação. O processo contribuiu para fortalecer as pautas das minorias (D). Não há menção a aumento de renda (A), abandono de valores morais (B), igualdade racial (C) ou inversão de fluxos migratórios (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'elevar a renda da população.', 0),
    (@question_id, 'B', 'abandonar os valores morais.', 0),
    (@question_id, 'C', 'estabelecer a igualdade racial.', 0),
    (@question_id, 'D', 'fortalecer as pautas das minorias.', 1),
    (@question_id, 'E', 'inverter os fluxos das migrações.', 0);

-- 1º Dia, questão 66 (Ciências Humanas › Geografia › Demografia). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 17, NULL, 'Em 1960, a primeira pílula anticoncepcional foi comercializada nos EUA, e, em poucos anos, o método contraceptivo se difundiu pelo mundo, inclusive no Brasil. Em nosso país, a chegada das pílulas anticoncepcionais foi simultânea às discussões neomalthusianas sobre a crise demográfica, à aceleração dos processos de modernização e ao boom da indústria farmacêutica multinacional.

DIAS, T. M. et al. A pílula da oportunidade: discursos sobre as pílulas anticoncepcionais em A Gazeta da Farmácia, 1960-1981. História, Ciências, Saúde — Manguinhos, n. 3, jul.-set. 2018 (adaptado).

Qual foi o efeito social resultante do avanço tecnológico mencionado no texto?', 'O texto situa a chegada da pílula no Brasil junto às discussões neomalthusianas sobre crise demográfica, à modernização e ao crescimento da indústria farmacêutica. O efeito social foi a ampliação do debate público sobre o planejamento familiar (C). O texto não aponta afastamento da autoridade médica (A), superação da moralidade (B), centralização da pesquisa no setor privado (D) nem enrijecimento religioso (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'O afastamento da autoridade médica na regulação da fecundidade.', 0),
    (@question_id, 'B', 'A superação do discurso da moralidade pela ação da mídia estatal.', 0),
    (@question_id, 'C', 'A ampliação do debate público sobre o planejamento familiar.', 1),
    (@question_id, 'D', 'A centralização da pesquisa científica pelo sistema privado de saúde.', 0),
    (@question_id, 'E', 'O enrijecimento das doutrinas religiosas sobre a organização da vida doméstica.', 0);

-- 1º Dia, questão 67 (Ciências Humanas › Geografia › Globalização e Geopolítica). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 18, NULL, 'A internet fortalece o engajamento e a visibilidade de populações tradicionais e indígenas por meio de redes sociais, rádios e veículos de mídia digital. Ela também promove a criação e o fortalecimento de mercados e a inovação nos sistemas de financiamento, integrando pequenos agricultores e produtores da comunidade florestal a cadeias de abastecimento maiores.

GROTTERA, C.; CASTRO, L. M.; BRITO, M. C. Como a tecnologia pode ser uma aliada na conservação ambiental. Nexo Jornal, 14 ago. 2021.

A adoção da tecnologia mencionada amplia a rentabilidade das comunidades citadas, ao possibilitar o(a):', 'O texto diz que a internet fortalece a visibilidade de populações tradicionais e indígenas e cria mercados, integrando pequenos produtores a cadeias de abastecimento maiores. A tecnologia amplia a rentabilidade ao possibilitar a valorização da economia local (A). Projetos culturais (B), regras comerciais (C), infraestrutura educacional (D) e homogeneização da qualificação (E) não são citados.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Valorização da economia local.', 1),
    (@question_id, 'B', 'Elaboração de projetos culturais.', 0),
    (@question_id, 'C', 'Estabelecimento de regras comerciais.', 0),
    (@question_id, 'D', 'Incremento da infraestrutura educacional.', 0),
    (@question_id, 'E', 'Homogeneização da qualificação profissional.', 0);

-- 1º Dia, questão 68 (Ciências Humanas › Geografia › Climatologia). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 12, NULL, 'TEXTO I

Uma única árvore joga entre 300 e 1 000 litros de água por dia para a atmosfera. Considerando a demanda mínima que uma pessoa consome de água, ou seja, 120 litros por dia, uma única árvore pode ser capaz de produzir água para até oito pessoas.

MAGNO, C. Estudiosos explicam o motivo de chover tanto em Belém. Disponível em: www.diarioonline.com.br. Acesso em: 6 nov. 2021.

TEXTO II

A Amazônia perdeu diariamente uma área de floresta maior do que 4 mil campos de futebol apenas em setembro de 2021. Em todo o mês, foram devastados 1 224 km², o que corresponde ao tamanho da cidade do Rio de Janeiro e é a pior marca para setembro em 10 anos. Os dados são do Sistema de Alerta de Desmatamento (SAD) do Instituto do Homem e Meio Ambiente da Amazônia (Imazon), que monitora a floresta por meio de imagens de satélites.

FREITAS, A. Desmatamento na Amazônia em setembro foi o maior para o período em 10 anos. Disponível em: www.cnnbrasil.com.br. Acesso em: 6 nov. 2021.

Para a região amazônica, a relação entre as informações dos textos indica uma redução do(a)', 'O Texto I mostra que uma árvore devolve de 300 a 1 000 litros de água por dia à atmosfera (evapotranspiração), o que alimenta a formação de nuvens e chuvas. O Texto II mostra o desmatamento em grande escala. Menos árvores significa menos umidade e redução da intensidade das chuvas convectivas (E). O desmatamento tende a aumentar, e não reduzir, o aquecimento dos solos (B) e a refletividade da superfície (D), pois o solo exposto reflete mais que a floresta; ventos alísios (A) e temperatura oceânica (C) não dependem da cobertura florestal local.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'circulação de ventos alísios.', 0),
    (@question_id, 'B', 'aquecimento dos solos locais.', 0),
    (@question_id, 'C', 'média de temperatura oceânica.', 0),
    (@question_id, 'D', 'índice de refletividade superficial.', 0),
    (@question_id, 'E', 'intensidade de chuvas convectivas.', 1);

-- 1º Dia, questão 69 (Ciências Humanas › Sociologia › Movimentos Sociais). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 29, NULL, 'Atribuo a causa desse florescimento estéril a um sistema de educação falso, extraído de livros sobre o assunto escrito por homens que, ao considerar as mulheres mais como fêmeas do que como criaturas humanas, estão mais ansiosos em torná-las damas sedutoras do que esposas afetuosas e mães racionais. O entendimento do sexo feminino tem sido tão distorcido por essa homenagem ilusória de que as mulheres civilizadas de nosso século, com raras exceções, anseiam apenas inspirar amor, quando deveriam nutrir uma ambição mais nobre e exigir respeito por suas capacidades e virtudes.

WOLLSTONECRAFT, M. Reivindicação dos direitos da mulher. São Paulo: Boitempo, 2016.

Nesse texto, escrito no século XVIII, a autora reivindica para as mulheres a', 'Wollstonecraft critica a educação que forma mulheres como “damas sedutoras” e defende que elas deveriam exigir “respeito por suas capacidades e virtudes”. A autora reivindica a valorização de seu papel social (C). Não menciona participação política (A), igualdade salarial (B), distinção biológica (D) nem cargos públicos (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'recuperação de sua participação política.', 0),
    (@question_id, 'B', 'equiparação de ganhos salariais.', 0),
    (@question_id, 'C', 'valorização de seu papel social.', 1),
    (@question_id, 'D', 'distinção de padrões biológicos.', 0),
    (@question_id, 'E', 'ocupação de cargos públicos.', 0);

-- 1º Dia, questão 70 (Ciências Humanas › Geografia › Globalização e Geopolítica). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 18, NULL, 'Cada objeto é, em si mesmo, um sistema funcionando sistemicamente. Um grande supermercado ou shopping center seria incapaz de existir se não fossem servidos por vias rápidas, estacionamentos adequados e acessíveis, sistemas de transportes públicos com horários regulares e conhecidos e se, no seu próprio interior, as atividades não estivessem subordinadas a uma coordenação. Esse é o caso dos armazéns, dos silos etc. Os portos, a rede rodoviária de um país e, sobretudo, a rede ferroviária são exemplos de objetos complexos e sistêmicos.

SANTOS, M. A natureza do espaço: técnica, tempo, razão e emoção. São Paulo: Hucitec, 1996 (adaptado).

De acordo com o texto, o território torna-se cada vez mais dotado de objetos com a finalidade de intensificar a', 'Milton Santos descreve objetos técnicos sistêmicos, como shoppings, portos, rodovias e ferrovias, que só funcionam se integrados a vias, transportes e coordenação. Eles servem para intensificar a dinamização dos fluxos (E) de mercadorias, pessoas e informações. O texto não trata de desindustrialização (A), desregulamentação (B), concentração da produção (C) nem de distribuição de renda (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'desindustrialização da economia.', 0),
    (@question_id, 'B', 'desregulamentação do mercado.', 0),
    (@question_id, 'C', 'concentração da produção.', 0),
    (@question_id, 'D', 'distribuição de renda.', 0),
    (@question_id, 'E', 'dinamização dos fluxos.', 1);

-- 1º Dia, questão 71 (Ciências Humanas › Geografia › Globalização e Geopolítica). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 18, NULL, 'A 26ª Conferência do Clima das Nações Unidas foi realizada com a perspectiva de que os países tornassem ainda mais ambiciosos os compromissos assumidos no enfrentamento das mudanças climáticas, de modo a evitar que a temperatura global se eleve acima de 1,5 °C, marca que já vinha sendo discutida desde o Acordo de Paris, de 2015.

A pressão do setor empresarial, que se posicionou de maneira contundente nesta COP-26, gerou impacto positivo. O Conselho Empresarial Brasileiro para o Desenvolvimento Sustentável (CEBDS) capitaneou, por meio do movimento Empresários pelo Clima, a adesão de 119 CEOs de importantes empresas e 14 instituições do setor privado, de setores tão diversos como agronegócio, alimentício, aviação, elétrico, farmacêutico, finanças, infraestrutura, logística, papel e celulose, petroquímico, saúde, tecnologia, telefonia e varejo.

O Brasil deixa a conferência com o compromisso de fazer valer sua meta e reduzir 50% dos gases de efeito estufa até 2030 em relação aos níveis de 2005 — anteriormente, a meta de redução era de 43%.

GROSSI, M. A mais plural das COPs e a lição de casa para o Brasil. Disponível em: www.nexojornal.com.br. Acesso em: 24 nov. 2021 (adaptado).

Conforme o texto, o compromisso assumido pelo Brasil foi resultado dos tensionamentos promovidos por', 'O texto afirma que o compromisso brasileiro de reduzir 50% das emissões até 2030 resultou da pressão do setor empresarial e dos acordos da COP-26, conduzida pela ONU, com participação de CEOs e instituições. Os tensionamentos vieram de organismos supranacionais e da sociedade civil (D). Povos ribeirinhos, blocos econômicos, instituições militares, grupos científicos e turismo (A, B, C, E) não são citados como agentes da pressão.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'povos ribeirinhos e segmentos culturais.', 0),
    (@question_id, 'B', 'blocos econômicos e instituições militares.', 0),
    (@question_id, 'C', 'grupos científicos e universidades públicas.', 0),
    (@question_id, 'D', 'organismos supranacionais e sociedade civil.', 1),
    (@question_id, 'E', 'agentes governamentais e demanda turística.', 0);

-- 1º Dia, questão 72 (Ciências Humanas › Filosofia › Ética e Moral). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 23, NULL, 'A pessoa com deficiência de qualquer modalidade — seja visual, auditiva, física ou mental — encontra-se em uma posição de grande vulnerabilidade em relação às pessoas sem deficiência, sendo frequentemente marcante a assimetria das relações de poder na interação entre ambas. Tal assimetria de relação hierárquica é multiplicada conforme a severidade de cada caso, sendo ampliada se a pessoa com necessidades especiais pertencer a um outro grupo de risco, por exemplo, se for mulher ou criança.

PASIAN, M. S. A negligência parental e a relação com a deficiência: o que mostra a pesquisa nacional. Revista Educação Especial, n. 53, set.-dez. 2015 (adaptado).

A realidade abordada no texto indica a necessidade de se promover uma ética interpessoal centrada no', 'O texto mostra a vulnerabilidade da pessoa com deficiência e a assimetria de poder nas relações, que se agrava em casos de maior risco. A realidade indica a necessidade de uma ética interpessoal centrada no cuidado, na proteção e na valorização dos indivíduos (A). Controle, regramento e punição (C, D), perdão e tolerância dos responsáveis (B) e indulgência (E) não protegem quem está em posição vulnerável.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'cuidado, proteção e valorização dos indivíduos.', 1),
    (@question_id, 'B', 'entendimento, perdão e tolerância dos responsáveis.', 0),
    (@question_id, 'C', 'cerceamento, arregimentação e controle de entidades.', 0),
    (@question_id, 'D', 'regramento, legislação e responsabilização de culpados.', 0),
    (@question_id, 'E', 'ensimesmamento, interiorização e indulgência dos agentes.', 0);

-- 1º Dia, questão 73 (Ciências Humanas › Sociologia › Cultura e Indústria Cultural). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 28, NULL, 'As capas dos folhetos de cordel, já então ilustradas por postais fotográficos, desenhos ou fotogramas de filmes, demoravam mais de uma semana para serem transformadas em clichês em Recife ou Fortaleza, o que levou a que santeiros e artesãos locais fossem requisitados para cortar na umburana — madeira preferida para o taco xilográfico pela facilidade do talhe e abundância — princesas, dragões, cangaceiros.

CARVALHO, G. Xilogravura: os percursos da criação popular. Revista do Instituto de Estudos Brasileiros, n. 39, 1986 (adaptado).

No início do século XX, a incorporação da técnica de produção descrita no texto promoveu uma renovação da', 'O texto conta que, para as capas dos folhetos de cordel, santeiros e artesãos passaram a entalhar na umburana imagens como princesas, dragões e cangaceiros, usando a xilogravura. A técnica renovou a estética editorial (D) do cordel. Não se trata de manifestação jornalística (A), narrativa literária (B), indústria regional (C) nem de cultura erudita (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'manifestação jornalística.', 0),
    (@question_id, 'B', 'narrativa literária.', 0),
    (@question_id, 'C', 'indústria regional.', 0),
    (@question_id, 'D', 'estética editorial.', 1),
    (@question_id, 'E', 'cultura erudita.', 0);

-- 1º Dia, questão 74 (Ciências Humanas › Sociologia › Cultura e Indústria Cultural). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 28, NULL, 'O Círio de Nazaré é uma festa que ocorre, anualmente, na cidade de Belém do Pará, no segundo domingo do mês de outubro. Sua estrutura ritualística tem origem no catolicismo devocional que surge em Portugal por volta do século XV. Até 1789, a festa em louvor a Nossa Senhora de Nazaré, em Belém, era marcada pelas ladainhas e novenas.

Em 1790, a Igreja Católica autorizou a realização de festa em homenagem à Virgem. A primeira procissão ocorreu em 1793. Existindo há mais de duzentos anos, a Festa congrega um extenso mosaico de elementos integrados em diferentes planos e graus de intensidade.

ALMEIDA, I. M. A. Revisitando o Círio de Nazaré a partir da lente sociológica de Eidorfe Moreira. Boletim do Museu Paraense Emílio Goeldi, n. 3, set.-dez. 2015 (adaptado).

O reconhecimento da festa descrita no texto, como patrimônio histórico, encontra sustentação no(a)', 'O Círio de Nazaré é uma festa religiosa, um conjunto de rituais, ladainhas e procissões transmitidos ao longo de mais de 200 anos, ou seja, um bem cultural imaterial. Seu reconhecimento como patrimônio sustenta-se no registro de bens culturais de natureza imaterial (B), instrumento do Iphan. Tombamento é para bens materiais (C), e não se trata de política pública local (A), salvaguarda de objetos sacros (D) ou de categorização de manifestações cristãs oficiais (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'instituição de políticas públicas de âmbito local.', 0),
    (@question_id, 'B', 'registro de bens culturais de natureza imaterial.', 1),
    (@question_id, 'C', 'tombamento de sítios arqueológicos de propriedade privada.', 0),
    (@question_id, 'D', 'salvaguarda de elementos sacros de expressão regional.', 0),
    (@question_id, 'E', 'categorização de manifestações cristãs de caráter oficial.', 0);

-- 1º Dia, questão 75 (Ciências Humanas › Filosofia › Filosofia Moderna). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 21, NULL, 'Os salões permitiam aos escritores da época do Iluminismo adentrar no universo dos poderosos. Figuras como as de Voltaire e Duclos exortavam seus “irmãos” a aproveitarem da mobilidade que era oferecida pela ordem social do Antigo Regime, juntando-se à elite. Nos últimos decênios do Ancien Régime, ele foi se tornando cada vez mais o reduto dos filósofos do Alto Iluminismo, que deixavam os cafés para os tipos inferiores de literário. Com efeito, os cafés se constituíram na antítese lógica dos salões. Eles eram abertos a todos, a um passo da rua. Como é possível constatar, salões e cafés constituem interessantes instituições do espaço público literário através das quais é possível vislumbrar as bases sociais nas quais se assentavam o Alto e o Baixo Iluminismo.

HABERMAS, J. Mudança estrutural da esfera pública: investigações quanto a uma categoria da sociedade burguesa. Rio de Janeiro: Tempo Brasileiro, 1984 (adaptado).

No período iluminista, os espaços sociais mencionados contribuíram para', 'O texto mostra que os salões, frequentados pela elite, e os cafés, abertos a todos, constituíam o espaço público literário do Alto e do Baixo Iluminismo. Eles contribuíram para segregar pensadores por classe social e, ao mesmo tempo, aumentar a circulação de ideias (A). Não apoiaram revolucionários contra a nobreza (B), nem proibiram mulheres (C), nem censuraram cronistas (D), nem integraram artistas para ampliar o comércio (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'segregar pensadores e aumentar a circulação de ideias.', 1),
    (@question_id, 'B', 'apoiar revolucionários e perseguir a nobreza local.', 0),
    (@question_id, 'C', 'rejeitar poetisas e proibir a entrada de mulheres.', 0),
    (@question_id, 'D', 'censurar cronistas e coibir o patrocínio editorial.', 0),
    (@question_id, 'E', 'integrar artistas e ampliar o comércio urbano.', 0);

-- 1º Dia, questão 76 (Ciências Humanas › Filosofia › Filosofia Contemporânea). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 22, NULL, 'Resumamos os principais caracteres de um rizoma: diferentemente das árvores ou de suas raízes, o rizoma conecta um ponto qualquer com outro ponto qualquer e cada um de seus traços não remete necessariamente a traços de mesma natureza. Contra os sistemas centrados (e mesmo policentrados), de comunicação hierárquica e ligações preestabelecidas, o rizoma é um sistema a-centrado não hierárquico e não significante, sem general, sem memória organizadora ou autômato central, unicamente definido por uma circulação de estados.

DELEUZE, G.; GUATTARI, F. Mil platôs. São Paulo: Editora 34, 1995 (adaptado).

Qual elemento da cultura contemporânea se relaciona às características do conceito de rizoma, conforme descrito no texto?', 'Deleuze e Guattari descrevem o rizoma como sistema sem centro, não hierárquico, que conecta qualquer ponto a qualquer outro. Essa lógica se relaciona à cultura contemporânea da internet e das redes, marcada pela pluralidade de interações (E). Estrutura fixa (A), lógica binária (B), controle homogêneo (C) e uniformidade de opiniões (D) são características dos sistemas centrados e hierárquicos que o conceito contesta.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Estrutura fixa.', 0),
    (@question_id, 'B', 'Lógica binária.', 0),
    (@question_id, 'C', 'Controle homogêneo.', 0),
    (@question_id, 'D', 'Uniformidade de opiniões.', 0),
    (@question_id, 'E', 'Pluralidade de interações.', 1);

-- 1º Dia, questão 78 (Ciências Humanas › Filosofia › Ética e Moral). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 23, NULL, 'A regra de ouro, popularmente conhecida pelo provérbio “Trate os outros como gostaria de ser tratado”, é um dos princípios morais mais onipresentes. A noção subjacente, que apela para o senso ético mais básico, se expressa de uma forma ou de outra em praticamente todas as tradições religiosas, e poucos filósofos morais deixaram de invocar a regra ou pelo menos de tecer comentários a respeito da relação com seus próprios princípios.

DUPRÉ, B. 50 grandes ideias da humanidade. São Paulo: Planeta do Brasil, 2016.

O princípio ético apresentado no texto, como elemento estruturante da vida em sociedade, se traduz pela seguinte formulação teórica:', '“Trate os outros como gostaria de ser tratado” é a regra de ouro, princípio moral universal. Na filosofia, sua formulação teórica é o imperativo categórico de Kant: agir segundo uma máxima que se possa querer como lei universal (B). Doutrina teleológica (A), pensamento utilitarista (C) e raciocínio consequencialista (E) avaliam a ação pelos resultados, e secularização inautêntica (D) não é formulação ética.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Doutrina teleológica.', 0),
    (@question_id, 'B', 'Imperativo categórico.', 1),
    (@question_id, 'C', 'Pensamento utilitarista.', 0),
    (@question_id, 'D', 'Secularização inautêntica.', 0),
    (@question_id, 'E', 'Raciocínio consequencialista.', 0);

-- 1º Dia, questão 79 (Ciências Humanas › Sociologia › Cidadania e Direitos Humanos). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 30, NULL, 'O que alicerça, portanto, o acolhimento de refugiados pelos Estados gira em torno da fronteira erguida entre inclusão e exclusão, admissão e rejeição, desejáveis e indesejáveis; ao mesmo tempo, enseja vulnerabilidade, indefinição e incerteza a esses migrantes internacionais forçados.

MOREIRA, J. B. Refugiados no Brasil. REMHU, n. 43, jul.-dez. 2014.

A eliminação, para os refugiados, do tipo de fronteira descrita no texto necessita de políticas públicas de', 'O texto diz que o acolhimento de refugiados cria uma fronteira entre incluídos e excluídos, que gera vulnerabilidade e incerteza. Para eliminar essa fronteira são necessárias políticas públicas de integração socioeconômica (E). Planejamento familiar (A), segregação (B), homogeneização cultural (C) e diferenciação étnico-racial (D) mantêm ou ampliam a exclusão.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'planejamento familiar.', 0),
    (@question_id, 'B', 'segregação territorial.', 0),
    (@question_id, 'C', 'homogeneização cultural.', 0),
    (@question_id, 'D', 'diferenciação étnico-racial.', 0),
    (@question_id, 'E', 'integração socioeconômica.', 1);

-- 1º Dia, questão 80 (Ciências Humanas › Sociologia › Cultura e Indústria Cultural). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 28, NULL, 'TEXTO I

“As novelas brasileiras foram as primeiras a aparecer na TV russa ainda na época de Gorbachov, nos anos 1980. Elas causaram sensação. Houve casos anedóticos, como quando o Parlamento da Federação Russa terminou a sessão mais cedo para ver o último capítulo de Escrava Isaura”, conta o russo Anatoli Sostanov, consultor na área de TV internacional e ex-diretor da emissora estatal russa Canal.

Disponível em: www.bbc.com. Acesso em: 16 out. 2021.

TEXTO II

As novelas de maior impacto em Angola foram as produções brasileiras. Fizeram tanto sucesso que um mercado popular, na época era o maior a céu aberto na África, chegou a receber o nome de Roque Santeiro, novela brasileira exibida naquele país na década de 1980. A influência das produções brasileiras fez com que os angolanos conhecessem o Brasil por meio das novelas. Além de entreter, os folhetins brasileiros até hoje ditam a moda em Angola. Figurinos e acessórios de personagens se transformam em uma verdadeira febre e influenciam o comportamento.

Disponível em: https://tvbrasil.ebc.com.br. Acesso em: 16 out. 2021 (adaptado).

Embora situados em continentes diferentes com práticas sociais distintas, Rússia e Angola se aproximam, conforme os textos I e II, no aspecto', 'Os textos mostram que as novelas brasileiras foram sucesso na Rússia (o Parlamento encerrou a sessão para ver Escrava Isaura) e em Angola (um mercado ganhou o nome de Roque Santeiro e a moda dos personagens virou febre). Os dois países se aproximam no aspecto cultural, ao absorver costumes da teledramaturgia estrangeira (A). Não há importação de confecções (B), encurtamento de distâncias geográficas (C), adaptação de valores tradicionais (D) nem adoção de governos democráticos (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'cultural, ao subjetivarem costumes da teledramaturgia estrangeira.', 1),
    (@question_id, 'B', 'econômico, ao importarem confecções de estilistas famosos.', 0),
    (@question_id, 'C', 'geográfico, ao encurtarem distâncias de caráter ideológico.', 0),
    (@question_id, 'D', 'antropológico, ao adaptarem valores de povos tradicionais.', 0),
    (@question_id, 'E', 'político, ao adotarem sistemas de governos democráticos.', 0);

-- 1º Dia, questão 81 (Ciências Humanas › Sociologia › Cidadania e Direitos Humanos). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 30, NULL, 'No Brasil, os remanescentes de antigos quilombos, “mocambos”, “comunidades negras rurais”, “quilombos contemporâneos”, “comunidades quilombolas” ou “terras de preto” referem-se a um mesmo patrimônio territorial e cultural inestimável, que só recentemente passaram a ter atenção do Estado e ser do interesse de algumas autoridades e organismos oficiais.

ANJOS, R. S. A. Cartografia e quilombos: territórios étnicos africanos no Brasil. Africana Studia, n. 9, 2007.

Na esfera de ação do Estado, com a Constituição de 1988, os espaços mencionados tornaram-se objeto de', 'O texto trata das comunidades remanescentes de quilombos, que só recentemente receberam atenção do Estado. Com a Constituição de 1988, que reconheceu a propriedade das terras quilombolas, esses espaços passaram a ser objeto de políticas de inserção social (D). Não são alvo de planejamento familiar (A), reorientação religiosa (B), moradias sustentáveis (C) nem de homogeneização educacional (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'iniciativas de planejamento familiar.', 0),
    (@question_id, 'B', 'projetos de reorientação religiosa.', 0),
    (@question_id, 'C', 'programas de moradias sustentáveis.', 0),
    (@question_id, 'D', 'políticas de inserção social.', 1),
    (@question_id, 'E', 'medidas de homogeneização educacional.', 0);

-- 1º Dia, questão 82 (Ciências Humanas › Filosofia › Teoria do Conhecimento). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 25, NULL, 'Bertrand Russell conta a história de um peru que descobrira, em sua primeira manhã na fazenda de perus, que fora alimentado às 9 da manhã. Contudo, ele não tirou conclusões apressadas. Esperou até recolher um grande número de observações do fato de que era alimentado às 9 da manhã e fez essas observações sob uma ampla variedade de circunstâncias, às quartas e quintas-feiras, em dias quentes e dias frios, em dias chuvosos e dias secos. A cada dia acrescentava uma outra proposição de observação à sua lista. Finalmente, sua consciência ficou satisfeita e ele concluiu. “Eu sou alimentado sempre às 9 da manhã”.

CHALMERS, A. O que é ciência, afinal? São Paulo: Brasiliense, 1993 (adaptado).

Qual tipo de raciocínio corresponde ao padrão de pensamento exibido pelo personagem do texto?', 'O peru de Russell observa muitas vezes que é alimentado às 9 horas, em condições variadas, e conclui que será sempre alimentado nesse horário. Esse é o raciocínio indutivo: parte de casos particulares observados e infere uma lei geral (C). A limitação, que o próprio exemplo ilustra, é que nenhuma quantidade de observações garante a conclusão universal. Não é dedutivo/demonstrativo (D), analógico (E) nem prático (A) ou absoluto (B).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Prático, porque recolhe evidências e recomenda ações.', 0),
    (@question_id, 'B', 'Absoluto, porque busca confirmações e bloqueia refutações.', 0),
    (@question_id, 'C', 'Indutivo, porque observa eventos particulares e infere leis universais.', 1),
    (@question_id, 'D', 'Demonstrativo, porque encadeia premissas e extrai conclusões indubitáveis.', 0),
    (@question_id, 'E', 'Analógico, porque compara diferentes situações e detecta elementos semelhantes.', 0);

-- 1º Dia, questão 83 (Ciências Humanas › Geografia › Cartografia). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 10, NULL, 'A finalidade mais marcante em toda a história dos mapas, desde o seu início, teria sido a de estarem sempre voltados à prática, principalmente a serviço da dominação, do poder. Sempre registraram o que mais interessava a uma minoria, fato este que acabou estimulando o incessante aperfeiçoamento deles.

MARTINELLI, M. Mapas da geografia e cartografia temática. São Paulo: Contexto, 2011 (adaptado).

No texto, a cartografia é apresentada como um instrumento usado essencialmente para a', 'O texto afirma que os mapas sempre estiveram voltados à prática, a serviço da dominação e do poder, registrando o que interessava a uma minoria. A cartografia é apresentada como instrumento de sustentação de hegemonias territoriais (E). Não é usada essencialmente para preservar espaços naturais (A), emancipar marginais (B), revalorizar culturas reprimidas (C) nem inverter hierarquias (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'preservação de espaços naturais.', 0),
    (@question_id, 'B', 'emancipação de sujeitos marginais.', 0),
    (@question_id, 'C', 'revalorização de culturas reprimidas.', 0),
    (@question_id, 'D', 'inversão de hierarquias estabelecidas.', 0),
    (@question_id, 'E', 'sustentação de hegemonias territoriais.', 1);

-- 1º Dia, questão 84 (Ciências Humanas › Sociologia › Cultura e Indústria Cultural). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 28, NULL, 'TEXTO I

O bufarinheiro, conhecido nas cidades por teque-teque, chama-se, nos recônditos da Amazônia, “regatão”. Em lugar de transportar nas costas o mundo de miudezas, transporta-o no bojo de uma gaiola que desloca duas, três, quatro toneladas, divididas em seções de secos e molhados e é movido por remo de faia. Cortando comunidades e matas da Amazônia por rios, dentro dessas gaiolas, riscadas de prateleiras, encontram-se os artigos mais díspares, que vão da agulha à espingarda, do lenço ao cobertor, da chita à escova de dentes.

MORAES, R. Na Planície Amazônica. São Paulo: Editora Nacional, 1936 (adaptado).

TEXTO II

No século XIX, o comércio dos regatões era feito, então, com base em relações tecidas com quilombolas, pequenos produtores, comerciantes locais e indígenas, constituindo relação comercial alternativa ao abastecimento da população.

HENRIQUE, M. C.; MORAIS, L. T. Estradas líquidas, comércio sólido: índios e regatões na Amazônia (século XIX). Rev. Hist., n. 171, jul.-dez. 2014 (adaptado).

Como parte do patrimônio cultural da Amazônia, o regatão foi fundamental, no século XIX, para a', 'Os textos descrevem o regatão, comerciante fluvial que, no século XIX, levava mercadorias a comunidades da Amazônia e se relacionava com quilombolas, pequenos produtores e indígenas. Ele foi fundamental para a construção de redes de sociabilidade no interior brasileiro (D). Não se trata de rotas de fuga (A), de postos de trabalho nos seringais (B), de receitas de fármacos (C) ou de ambientes de lazer (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'organização de rotas de fuga na floresta tropical.', 0),
    (@question_id, 'B', 'criação de postos de trabalho nos seringais nortistas.', 0),
    (@question_id, 'C', 'divulgação de receitas de fármacos nas zonas ribeirinhas.', 0),
    (@question_id, 'D', 'construção de redes de sociabilidade no interior brasileiro.', 1),
    (@question_id, 'E', 'ampliação de ambientes de lazer nos territórios autóctones.', 0);

-- 1º Dia, questão 85 (Ciências Humanas › Sociologia › Cidadania e Direitos Humanos). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 30, NULL, 'Era uma vez um país, uma cidade, uma praça, algumas mães... Las Madres de Plaza de Mayo! Silenciosas, com lenços brancos na cabeça, rondavam a Praça de Maio.

Incansáveis, caminharam por dias, meses, anos. Foram chamadas de loucas. Em silêncio, criaram um fato político, escancararam as entranhas da repressão, desafiaram o aparato militar e suas dores ecoaram pelo mundo. Como observou Oliveira, “à luz do dia, sob as janelas do ditador, sob chuva, sob sol, no silêncio entrecortado de gritos, faziam ouvir como que a alucinação de uma litania, que ecoou no país, na América Latina e além-mar”. Era impossível ignorá-las, estavam lá, sempre em silêncio, mas estavam lá.

GONÇALVES, R. De antigas e novas loucas: Madres e Mães de Maio contra a violência de Estado. Lutas Sociais, n. 29, jul.-dez. 2012.

Qual problema de âmbito nacional argentino o movimento social mencionado expôs ao mundo?', 'As Madres de Plaza de Mayo caminharam em silêncio na praça, em plena ditadura militar argentina, para cobrar notícias de filhos desaparecidos, expondo a repressão do Estado. O problema nacional exposto ao mundo foi a violação dos direitos humanos (A). Não se trata de insegurança da juventude periférica (B), violência doméstica (C), desigualdades sociais (D) ou relativização de princípios democráticos como tema central (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Violação dos direitos humanos.', 1),
    (@question_id, 'B', 'Insegurança da juventude periférica.', 0),
    (@question_id, 'C', 'Naturalização da violência doméstica.', 0),
    (@question_id, 'D', 'Ampliação das desigualdades sociais.', 0),
    (@question_id, 'E', 'Relativização dos princípios democráticos.', 0);

-- 1º Dia, questão 87 (Ciências Humanas › Filosofia › Filosofia Política). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 24, NULL, 'A democracia responde a esta pergunta: quem deve exercer o poder público? A resposta é: o exercício do poder público corresponde à coletividade dos cidadãos. Contudo, nessa pergunta não se fala sobre qual extensão deva ter o poder público. Trata-se somente de determinar o sujeito a quem o mando compete. A democracia propõe que mandemos todos; quer dizer, que todos intervenham nos fatos sociais.

ORTEGA Y GASSET, J. apud MAIA, E. C. Mario Vargas Llosa e o indivíduo para além da tribo. Disponível em: www.estadodaarte.estadao.com.br. Acesso em: 10 out. 2021 (adaptado).

O que sustenta o exercício do poder, conforme a configuração apresentada no texto escrito na década de 1920?', 'Ortega y Gasset diz que a democracia responde à pergunta de quem deve exercer o poder público: a coletividade dos cidadãos, pois “mandemos todos”. O que sustenta o exercício do poder é a soberania popular (A). Divisão de classes (B), acúmulo de capital (C), defesa da propriedade (D) e centralização administrativa (E) não aparecem como fundamento.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Soberania popular.', 1),
    (@question_id, 'B', 'Divisão de classes.', 0),
    (@question_id, 'C', 'Acúmulo de capital.', 0),
    (@question_id, 'D', 'Defesa da propriedade.', 0),
    (@question_id, 'E', 'Centralização administrativa.', 0);

-- 1º Dia, questão 88 (Ciências Humanas › Geografia › Geografia Urbana). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 15, NULL, 'A mudança do clima nas cidades brasileiras é um desafio de adaptação e equidade. Inundações, alagamentos e ondas de calor são cada vez mais frequentes e intensas. Cidades precisam se adaptar com urgência, a começar pelas áreas e populações mais vulneráveis. Implementar soluções baseadas na natureza de forma sistêmica pode contribuir para a redução de desastres relacionados às mudanças do clima e ainda gerar múltiplos benefícios para a economia, o ambiente e as pessoas.

EVERS, H. et al. Soluções baseadas na natureza para adaptação em cidades. Disponível em: www.wribrasil.org.br. Acesso em: 19 out. 2023 (adaptado).

Qual medida atenua os problemas abordados no texto?', 'O texto propõe soluções baseadas na natureza para reduzir inundações, alagamentos e ondas de calor nas cidades. A implantação de parques públicos aumenta áreas permeáveis e verdes, ajuda na drenagem e reduz ilhas de calor (C). Faixas sinalizadoras (A), incineração de resíduos (B), verticalização de áreas centrais (D) e estacionamentos privados (E) não são soluções baseadas na natureza e tendem a agravar a impermeabilização e o calor.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Criação de faixas sinalizadoras.', 0),
    (@question_id, 'B', 'Incineração de resíduos sólidos.', 0),
    (@question_id, 'C', 'Implantação de parques públicos.', 1),
    (@question_id, 'D', 'Verticalização de espaços centrais.', 0),
    (@question_id, 'E', 'Construção de estacionamentos privados.', 0);

-- 1º Dia, questão 89 (Ciências Humanas › Geografia › Globalização e Geopolítica). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 18, NULL, 'O Conselho de Segurança da Organização das Nações Unidas (ONU) é, junto com a Assembleia-Geral, um dos principais órgãos de tomada de decisão dentro da entidade. O Conselho lida com questões de segurança e paz internacionais, além de recomendar a admissão de novos membros à Assembleia-Geral e aprovar mudanças na Carta das Nações Unidas. Cinco dos quinze membros são permanentes e podem vetar resoluções, o que ocorreu 261 vezes até 2020.

GOMES, L.; PRETTO, N. O funcionamento do Conselho de Segurança das Nações Unidas. Disponível em: www.nexojornal.com.br. Acesso em: 10 nov. 2021 (adaptado).

A composição e o funcionamento do organismo internacional apresentados revelam a seguinte característica das relações internacionais entre os países-membros:', 'O Conselho de Segurança tem 15 membros, mas só cinco permanentes podem vetar resoluções, o que ocorreu 261 vezes até 2020. A composição e o funcionamento revelam assimetria política entre os países-membros (B). Não há igualdade militar (A), consenso multipolar (C), equilíbrio estratégico (D) nem soberania compartilhada em pé de igualdade (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Igualdade militar.', 0),
    (@question_id, 'B', 'Assimetria política.', 1),
    (@question_id, 'C', 'Consenso multipolar.', 0),
    (@question_id, 'D', 'Equilíbrio estratégico.', 0),
    (@question_id, 'E', 'Soberania compartilhada.', 0);

-- 1º Dia, questão 90 (Ciências Humanas › Sociologia › Trabalho e Sociedade). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_1, 27, NULL, 'Uma fábrica na qual os operários fossem, efetiva e integralmente, simples peças de máquinas executando cegamente as ordens da direção pararia em quinze minutos. O capitalismo só pode funcionar com a contribuição constante da atividade propriamente humana de seus subjugados que, ao mesmo tempo, tenta reduzir e desumanizar o mais possível.

CASTORIADIS, C. A instituição imaginária da sociedade. Rio de Janeiro: Paz e Terra, 1982.

O texto apresenta uma contradição interna do capitalismo caracterizada pela', 'Castoriadis afirma que uma fábrica de operários que fossem simples peças de máquinas pararia em quinze minutos, pois o capitalismo depende da atividade humana de seus subjugados, que ao mesmo tempo tenta reduzir e desumanizar. A contradição é a alienação decorrente da organização do trabalho (C). O texto não trata de obsolescência tecnológica (A), administração de conflitos (B), anomia da geração de riquezas (D) nem de produtividade ligada à autonomia (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'obsolescência associada ao uso da tecnologia.', 0),
    (@question_id, 'B', 'orientação voltada à administração de conflitos.', 0),
    (@question_id, 'C', 'alienação decorrente da organização do trabalho.', 1),
    (@question_id, 'D', 'isonomia remanescente da geração de riquezas.', 0),
    (@question_id, 'E', 'produtividade vinculada ao fortalecimento da autonomia.', 0);


-- ENEM 2024 — 2º Dia ----------------------------------------

-- 2º Dia, questão 93 (Ciências da Natureza › Química › Química Orgânica). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 64, NULL, 'No senso comum, considera-se, ainda hoje, que compostos orgânicos são substâncias presentes nos seres vivos. Na Química, a expressão “compostos orgânicos” tem um uso histórico de mais de 200 anos, adquirindo diferentes conotações ao longo do desenvolvimento dessa ciência. Atualmente, atribui-se a essa expressão outro significado. A concepção científica atual define esses compostos como substâncias', 'Na Química atual, compostos orgânicos são aqueles que contêm carbono como elemento principal (em geral ligado a hidrogênio, oxigênio, nitrogênio e outros), independentemente de serem produzidos por seres vivos (E). A ideia antiga de que seriam só substâncias dos seres vivos é o senso comum que o texto supera. Benefícios à saúde (A), biodegradabilidade (B), origem no gás carbônico (C) e produção sem agrotóxicos (D) não definem a classe.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'benéficas à saúde humana.', 0),
    (@question_id, 'B', 'capazes de serem biodegradadas.', 0),
    (@question_id, 'C', 'formadas a partir de gás carbônico.', 0),
    (@question_id, 'D', 'produzidas sem o uso de agrotóxicos.', 0),
    (@question_id, 'E', 'contendo carbono como elemento principal.', 1);

-- 2º Dia, questão 95 (Ciências da Natureza › Biologia › Genética). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 34, NULL, 'O desenvolvimento da biotecnologia e da clonagem gênica em procariotos fez com que a produção de proteínas se tornasse mais intensa, rápida e econômica. Para a produção de hormônios, enzimas e proteínas de resistência a drogas, uma variação da técnica de reação em cadeia pela polimerase (PCR, na sigla em inglês) utiliza a enzima transcriptase reversa (RT-PCR), que sintetiza moléculas de DNA complementares a partir de fitas de RNA. Nesse contexto, essa técnica é importante para detectar genes', 'A RT-PCR usa a transcriptase reversa para sintetizar DNA complementar (cDNA) a partir de RNA. Como o RNA mensageiro só existe para os genes que estão sendo transcritos na célula, a técnica detecta genes expressos (A). Ela não distingue se o gene é plasmidial (B), bacteriano (C), dominante (D) ou autossômico (E), pois essas características não dependem da expressão.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'expressos.', 1),
    (@question_id, 'B', 'plasmidiais.', 0),
    (@question_id, 'C', 'bacterianos.', 0),
    (@question_id, 'D', 'dominantes.', 0),
    (@question_id, 'E', 'autossômicos.', 0);

-- 2º Dia, questão 97 (Ciências da Natureza › Química › Soluções). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 59, NULL, 'O soro caseiro serve para combater a desidratação por meio da reposição da água e sais minerais perdidos, por exemplo, por diarreia. Uma receita simples para a sua preparação consiste em utilizar duas colheres grandes (de sopa) de açúcar e duas colheres pequenas (de café) de sal de cozinha, dissolvidos em 2 L de água fervida, obtendo-se uma solução com concentração de íon sódio de 1,4 mg/mL.

Considere as massas molares: NaCl = 58,5 g/mol; Na = 23 g/mol.

Qual é o valor mais próximo da massa, em grama, de cloreto de sódio presente em uma única colher pequena?', 'A concentração de 1,4 mg/mL de Na⁺ em 2 L (2 000 mL) corresponde a 2 800 mg, ou 2,8 g de sódio. Em mols: 2,8 / 23 ≈ 0,122 mol de Na⁺. Como todo o sódio vem do sal e a receita usa duas colheres pequenas de sal, n(NaCl) ≈ 0,122 mol, e m = 0,122 × 58,5 ≈ 7,1 g de NaCl no total. Dividindo por duas colheres pequenas, resultam cerca de 3,6 g em cada uma (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '0,7 g', 0),
    (@question_id, 'B', '1,8 g', 0),
    (@question_id, 'C', '2,8 g', 0),
    (@question_id, 'D', '3,6 g', 1),
    (@question_id, 'E', '7,0 g', 0);

-- 2º Dia, questão 99 (Ciências da Natureza › Física › Ondulatória). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 48, NULL, 'Uma ambulância em alta velocidade com a sirene ligada desloca-se em direção a um radar operado por uma pessoa. O radar emite ondas de rádio com frequência f₀ que são refletidas pela dianteira da ambulância, retornando para o detector com frequência fᵣ. A percepção do operador do radar, em relação ao som emitido pela sirene, é de que este se altera à medida que a ambulância se aproxima ou se afasta.

Durante a aproximação, como o operador percebe o som da sirene e qual é a relação entre as frequências fᵣ e f₀ medidas pelo radar?', 'Pelo efeito Doppler, quando a fonte se aproxima do observador, a frequência percebida aumenta, e o som da sirene é percebido como mais agudo. Para a onda de rádio refletida pela ambulância que se aproxima, a frequência de retorno também é maior do que a emitida, ou seja, fᵣ > f₀ (D). Som mais grave e fᵣ menor ou igual a f₀ só ocorreriam com a ambulância se afastando ou parada.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Mais grave do que o som emitido e fᵣ < f₀.', 0),
    (@question_id, 'B', 'Mais agudo do que o som emitido e fᵣ < f₀.', 0),
    (@question_id, 'C', 'Mais agudo do que o som emitido e fᵣ = f₀.', 0),
    (@question_id, 'D', 'Mais agudo do que o som emitido e fᵣ > f₀.', 1),
    (@question_id, 'E', 'Mais grave do que o som emitido e fᵣ > f₀.', 0);

-- 2º Dia, questão 102 (Ciências da Natureza › Biologia › Citologia). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 32, NULL, 'Uma agricultora, com a intenção de comercializar o milho recém-colhido, testou uma forma de preservar o sabor adocicado do seu produto. O melhor resultado foi obtido quando ela imergiu as espigas em água fervente durante alguns minutos e, em seguida, em água gelada. Com esse procedimento, parte da glicose do milho não foi transformada em amido, mantendo o seu sabor adocicado. Utilizando esse procedimento, o sabor foi conservado porque houve', 'Mergulhar as espigas em água fervente desnatura as enzimas que transformam a glicose em amido, e a água gelada interrompe o cozimento; assim, a glicose não é convertida e o sabor adocicado se mantém. O processo é a desnaturação enzimática pela alta temperatura (A). Não há conversão de nutrientes pelo frio (B), degradação das reservas (C), impedimento da entrada de oxigênio (D) nem desidratação dos grãos (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'desnaturação enzimática pela alta temperatura.', 1),
    (@question_id, 'B', 'conversão de nutrientes pela redução de temperatura.', 0),
    (@question_id, 'C', 'degradação das reservas nutritivas pelo choque térmico.', 0),
    (@question_id, 'D', 'impedimento da entrada de oxigênio pela fervura da água.', 0),
    (@question_id, 'E', 'desidratação dos grãos por causa da alteração da temperatura.', 0);

-- 2º Dia, questão 105 (Ciências da Natureza › Biologia › Histologia). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 33, NULL, 'Impactos do microplástico

A ação de fatores abióticos aliada à biodeterioração contribuem para a formação de microplásticos, os quais se aderem a outros poluentes orgânicos apolares persistentes, como os derivados de pesticidas lipossolúveis. Há uma proporcionalidade direta entre a solubilidade desses tipos de poluentes e sua concentração nos tecidos dos organismos expostos a eles.

Disponível em: www.ecycle.com.br. Acesso em: 9 dez. 2021 (adaptado).

Em animais vertebrados, essa associação de poluentes será preferencialmente acumulada no tecido', 'Os poluentes aderidos aos microplásticos são orgânicos, apolares e lipossolúveis, e sua concentração nos tecidos é diretamente proporcional à solubilidade. Substâncias lipossolúveis acumulam-se onde há gordura, ou seja, no tecido adiposo (D). Os tecidos ósseo (A), nervoso (B), epitelial (C) e sanguíneo (E) têm menos lipídios de reserva, portanto acumulam menos esses poluentes.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'ósseo.', 0),
    (@question_id, 'B', 'nervoso.', 0),
    (@question_id, 'C', 'epitelial.', 0),
    (@question_id, 'D', 'adiposo.', 1),
    (@question_id, 'E', 'sanguíneo.', 0);

-- 2º Dia, questão 106 (Ciências da Natureza › Física › Ondulatória). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 48, NULL, 'Em aeroportos, por razões de segurança, os passageiros devem ter suas bagagens de mão examinadas antes do embarque, passando-as em esteiras para sua inspeção por aparelhos de raios X. Nessas inspeções, os passageiros são orientados a retirar seus computadores portáteis (notebooks ou laptops) de malas, mochilas ou bolsas para passá-los isoladamente pela esteira. Que explicação física justifica esse procedimento?', 'Os raios X atravessam materiais pouco densos, como roupas e plástico, e são absorvidos por materiais densos, como os metais. Os componentes densos do notebook bloqueiam a radiação e impedem ver o que há à frente ou atrás deles, daí a necessidade de passá-lo isoladamente (D). Os raios X interagem, sim, com metais (A); não desmagnetizam discos por refração (B), nem aquecem, nem ionizam metais pelos mecanismos descritos (C, E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Os raios X não interagem com os componentes metálicos do computador, o que impede a formação de imagens.', 0),
    (@question_id, 'B', 'Os raios X desmagnetizam o disco rígido do computador, quando refratados pelos componentes metálicos das bagagens de mão.', 0),
    (@question_id, 'C', 'Os raios X aquecem os materiais metálicos encontrados em bagagens de mão, quando refletidos pelos componentes do computador.', 0),
    (@question_id, 'D', 'Os raios X não atravessam os componentes densos do computador, o que impede a visualização de objetos que estão à frente ou atrás deles.', 1),
    (@question_id, 'E', 'Os raios X ionizam os materiais metálicos normalmente encontrados em bagagens de mão, quando difratados pelos componentes do computador.', 0);

-- 2º Dia, questão 107 (Ciências da Natureza › Biologia › Fisiologia Humana). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 37, NULL, 'Com base em testes realizados em ratos, concluiu-se que os compostos nitrosos são substâncias mutagênicas, ou seja, produzem mutações nas células e possivelmente câncer. Esses compostos podem ser obtidos pela reação entre o nitrito de sódio, que é um conservante adicionado às carnes, e o ácido clorídrico. O ácido nitroso produzido irá reagir com compostos nitrogenados, como as aminas, dando origem aos compostos nitrosos.

Em qual órgão esse processo será iniciado?', 'O nitrito de sódio dos conservantes reage com o ácido clorídrico para formar ácido nitroso, que reage com aminas e gera compostos nitrosos. O ácido clorídrico está presente no suco gástrico, portanto o processo se inicia no estômago (E). Rim, fígado, intestino e pâncreas (A, B, C, D) não secretam HCl que permita a reação.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Rim.', 0),
    (@question_id, 'B', 'Fígado.', 0),
    (@question_id, 'C', 'Intestino.', 0),
    (@question_id, 'D', 'Pâncreas.', 0),
    (@question_id, 'E', 'Estômago.', 1);

-- 2º Dia, questão 108 (Ciências da Natureza › Biologia › Ecologia). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 36, NULL, 'Sistemas agroflorestais (SAFs)

Os sistemas agroflorestais alinham os interesses econômicos aos ecológicos. Esses sistemas podem ser usados na recuperação ambiental de áreas degradadas que se tornaram pouco produtivas, como as utilizadas por muito tempo para pastagem. Para isso, num primeiro momento, as árvores nativas são plantadas em conjunto com culturas agrícolas anuais, como feijão, mandioca, milho e abóbora, que geram renda para os proprietários da terra e incentivam a manutenção do sistema. Em um segundo momento, são introduzidas outras espécies, como trepadeiras e arbustos, de acordo com um arranjo espacial e temporal preestabelecido. Nesse processo, ocorrerão mudanças graduais na estrutura e composição das comunidades vegetais ao longo do tempo, que culminarão no aumento da diversidade do ambiente.

Disponível em: www.embrapa.br. Acesso em: 9 dez. 2021 (adaptado).

O conjunto dessas mudanças graduais é análogo ao processo natural denominado', 'Nos sistemas agroflorestais, culturas anuais dão lugar a arbustos, trepadeiras e árvores, com mudança gradual da composição e aumento da diversidade. Esse processo é análogo à sucessão ecológica (B), em que comunidades se substituem até a mais estável. Rotação de culturas (A) é manejo agrícola, e coevolução (C), seleção adaptativa (D) e convergência adaptativa (E) são processos evolutivos de longo prazo, não de substituição de comunidades.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'rotação de culturas.', 0),
    (@question_id, 'B', 'sucessão ecológica.', 1),
    (@question_id, 'C', 'coevolução específica.', 0),
    (@question_id, 'D', 'adaptação por seleção.', 0),
    (@question_id, 'E', 'convergência adaptativa.', 0);

-- 2º Dia, questão 109 (Ciências da Natureza › Biologia › Fisiologia Humana). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 37, NULL, 'As fibras musculares esqueléticas não são todas iguais. As fibras lentas, também conhecidas como fibras vermelhas, apresentam muitas mitocôndrias e são bem supridas por vasos sanguíneos. Já as fibras rápidas, ou fibras brancas, apresentam poucas mitocôndrias e recebem menor suprimento sanguíneo. Dessa forma, a distribuição das fibras nos músculos esqueléticos do corpo auxilia de forma diferenciada no desempenho físico de um atleta.

SADAVA, D. et al. Vida: a ciência da biologia —volume III: plantas e animais. Porto Alegre: Artmed, 2009 (adaptado).

Um atleta que sonha em disputar os Jogos Olímpicos e tem uma maior proporção de fibras brancas que fibras vermelhas teria mais vantagens na realização da prova de:', 'As fibras brancas, ou rápidas, têm poucas mitocôndrias e pouco suprimento sanguíneo, de modo que produzem energia sobretudo sem oxigênio, com contração rápida e potente, mas pouco resistente à fadiga. Elas favorecem provas explosivas e curtas, como o salto em altura (B). Triatlo, marcha atlética, maratona aquática e ciclismo em estrada (A, C, D, E) são provas de resistência, que exigem fibras vermelhas.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Triatlo.', 0),
    (@question_id, 'B', 'Salto em altura.', 1),
    (@question_id, 'C', 'Marcha atlética.', 0),
    (@question_id, 'D', 'Maratona aquática.', 0),
    (@question_id, 'E', 'Ciclismo em estrada.', 0);

-- 2º Dia, questão 111 (Ciências da Natureza › Biologia › Zoologia). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 39, NULL, 'O exoesqueleto dos crustáceos é formado por quitina e impregnações de sais calcários e, por isso, é mais duro quando comparado com o exoesqueleto de outros artrópodes. Esse revestimento externo confere proteção, mas, por ser duro, limita o crescimento desses animais. Para superar essa limitação, o exoesqueleto deve ser', 'O exoesqueleto de quitina e sais calcários é rígido e não cresce junto com o animal. Para crescer, o crustáceo precisa trocar o exoesqueleto por meio de mudas (ecdises) que ocorrem periodicamente (D). O exoesqueleto não é formado só na fase adulta (A), não é fragmentado nas articulações para expandir (B), não é remodelado continuamente (C) nem tem menos sais para se distender (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'formado somente na fase adulta do animal.', 0),
    (@question_id, 'B', 'fragmentado para expansão nas áreas de articulação.', 0),
    (@question_id, 'C', 'modelado continuamente para ajuste ao tamanho do corpo.', 0),
    (@question_id, 'D', 'substituído por meio de mudas que ocorrem periodicamente.', 1),
    (@question_id, 'E', 'impregnado por pequena quantidade de sais para sua distensão.', 0);

-- 2º Dia, questão 116 (Ciências da Natureza › Química › Soluções). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 59, NULL, 'O magnésio metálico utilizado em ligas leves é produzido em um processo que envolve várias etapas e utiliza água do mar como matéria-prima. A primeira etapa desse processo consiste na reação entre o íon Mg²⁺ e hidróxido de cálcio, Ca(OH)₂, obtendo uma mistura que contém hidróxido de magnésio, pouco solúvel, e íons Ca²⁺, de acordo com a equação química:

Mg²⁺ (aq) + Ca(OH)₂ (aq) → Mg(OH)₂ (s) + Ca²⁺ (aq)

O método adequado para separar o Mg(OH)₂ dessa mistura é a', 'A mistura contém hidróxido de magnésio, sólido pouco solúvel, e íons Ca²⁺ dissolvidos, ou seja, é uma mistura heterogênea sólido-líquido. O método adequado para separar o sólido do líquido é a filtração (A). Catação serve para sólidos grandes (B), destilação e evaporação separam solvente de soluto (C, E), e dissolução (D) não separa componentes.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'filtração.', 1),
    (@question_id, 'B', 'catação.', 0),
    (@question_id, 'C', 'destilação.', 0),
    (@question_id, 'D', 'dissolução.', 0),
    (@question_id, 'E', 'evaporação.', 0);

-- 2º Dia, questão 117 (Ciências da Natureza › Biologia › Zoologia). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 39, NULL, 'Moradores do Brooklin, bairro de São Paulo, perdem o sono com um som alto, constante e estridente. O barulho é causado por anfíbios anuros trazidos do Caribe, da espécie Eleutherodactylus jahnstonei, que têm tamanho um pouco maior que o de um grão de feijão e que encontraram na capital um ambiente favorável. Cientistas foram até o local e encontraram esses animais nos jardins das casas. Pererecas-assobiadoras tiram o sono de moradores do Brooklin. Disponível em: http://g1.globo.com. Acesso em: 26 mar. 2015 (adaptado).

Ao emitirem o som estridente, esses anfíbios', 'As pererecas-assobiadoras são anfíbios anuros, e os machos emitem som de alta intensidade para atrair as fêmeas para o acasalamento (E). O som não indica por si só que estão fora do hábitat (A), nem funciona como bioindicador de poluição urbana (B), de superpopulação (C) ou como forma de direcionar insetos (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'indicam que estão fora de seu hábitat natural.', 0),
    (@question_id, 'B', 'alertam para a presença de poluição urbana.', 0),
    (@question_id, 'C', 'sinalizam a existência de superpopulação.', 0),
    (@question_id, 'D', 'direcionam insetos para sua alimentação.', 0),
    (@question_id, 'E', 'atraem fêmeas para o acasalamento.', 1);

-- 2º Dia, questão 120 (Ciências da Natureza › Biologia › Microbiologia e Doenças). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 40, NULL, 'O vírus linfotrópico de células T humanas tipo 1 (HTLV-1) é um retrovírus do mesmo grupo do vírus da imunodeficiência humana (HIV). Ambos são transmitidos da mesma forma e infectam as mesmas células de defesa do organismo, os linfócitos T. A diferença entre eles é que o HTLV-1 estimula o aumento da produção desses linfócitos, enquanto o HIV causa destruição dessas células.

ROMANELLI, L. C. F.; CARAMELLI, P.; PROIETTI, A. B. F. C. O vírus linfotrópico de células T humanas tipo 1 (HTLV-1): quando suspeitar da infecção? Revista da Associação Médica Brasileira, v. 56, 2010 (adaptado).

Uma possível consequência da infecção por HTLV-1 é o desenvolvimento de', 'O HTLV-1 é retrovírus que estimula a proliferação dos linfócitos T, enquanto o HIV os destrói. O aumento descontrolado dessas células pode levar a leucemia ou linfoma, ou seja, ao desenvolvimento de câncer (B). A aids (A) decorre da destruição dos linfócitos pelo HIV, e diabetes (C), hepatite B (D) e hemorragia (E) não são consequências citadas da infecção.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'aids.', 0),
    (@question_id, 'B', 'câncer.', 1),
    (@question_id, 'C', 'diabetes.', 0),
    (@question_id, 'D', 'hepatite B.', 0),
    (@question_id, 'E', 'hemorragia.', 0);

-- 2º Dia, questão 126 (Ciências da Natureza › Biologia › Microbiologia e Doenças). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 40, NULL, 'Indústrias farmacêuticas e instituições científicas têm trabalhado no desenvolvimento de diferentes vacinas contra a covid-19. Em algumas dessas vacinas, a principal estrutura antigênica é uma proteína de superfície viral chamada espícula (spike, em inglês). Essa proteína só existe em coronavírus, incluindo o SARS-CoV-2. Ela se liga a receptores de membrana específicos das células humanas por um mecanismo do tipo “chave-fechadura”. Dessa forma, os vírus entram nas células, podendo se multiplicar e acarretar a doença.

O que são vacinas? Disponível em: www.cienciaviva.pt. Acesso em: 30 nov. 2021 (adaptado).

Nessas vacinas, essa proteína viral induz a', 'Nas vacinas descritas, a proteína spike é o antígeno: ao ser reconhecida pelo sistema imune, induz a produção de anticorpos específicos contra o vírus (A) e a formação de células de memória. A imunidade assim obtida é ativa, não passiva (B); a vacina não altera o genoma humano (C); a neutralização é feita pelos anticorpos, não diretamente pela proteína (D); e os receptores celulares não são modificados (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'produção de anticorpos específicos contra os vírus.', 1),
    (@question_id, 'B', 'imunidade passiva contra o desenvolvimento da doença.', 0),
    (@question_id, 'C', 'alteração genômica para formação da memória imunológica.', 0),
    (@question_id, 'D', 'neutralização direta dos vírus presentes na circulação sanguínea.', 0),
    (@question_id, 'E', 'modificação dos receptores de membrana específicos para o vírus.', 0);

-- 2º Dia, questão 133 (Ciências da Natureza › Química › Estequiometria). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 58, NULL, 'O biogás é uma alternativa energética muito importante, pois, além de reduzir a dependência por combustíveis fósseis, sua obtenção pode ser realizada a partir de resíduos da produção agroindustrial. Considere que o biogás produzido em um empreendimento de suinocultura contém 70% em volume de metano (massa molar 16 g/mol; volume molar 22 L/mol). Ele será utilizado para geração de energia em substituição ao etanol (massa molar 46 g/mol) em um gerador no qual 1 m³ de biogás de origem suína substitui 0,59 L de etanol anidro (densidade 0,78 g/mL). Nessas condições, a massa de metano necessária para substituir 10 mol de etanol na produção de energia é mais próxima de', 'Dez mols de etanol correspondem a 10 × 46 = 460 g, ou 460 / 0,78 ≈ 590 mL = 0,59 L. Esse volume é substituído por 1 m³ (1 000 L) de biogás. O biogás tem 70% de metano, ou seja, 700 L. Com volume molar de 22 L/mol, são 700 / 22 ≈ 31,8 mol de CH₄, e m = 31,8 × 16 ≈ 509 g, valor mais próximo de 510 g (C).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '300 g.', 0),
    (@question_id, 'B', '400 g.', 0),
    (@question_id, 'C', '510 g.', 1),
    (@question_id, 'D', '590 g.', 0),
    (@question_id, 'E', '720 g.', 0);

-- 2º Dia, questão 135 (Ciências da Natureza › Química › Estequiometria). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 58, NULL, 'Brasil fecha 2020 entre os maiores recicladores de latas de alumínio

A bauxita contém alumina (Al₂O₃), que é a matéria-prima para produção do alumínio (Al). De forma geral, são necessários 50 kg de bauxita para produzir 10 kg de alumínio. O Brasil fechou 2020 como um dos principais líderes mundiais em reciclagem de latas de alumínio. De acordo com levantamento da Associação Brasileira dos Fabricantes de Latas de Alumínio (Abralatas), o país obteve um índice de reciclagem de 97,4%, de um total de 4,0 × 10⁵ toneladas de latas vendidas. Considere que a lata é constituída de alumínio puro.

Disponível em: https://agenciabrasil.ebc.com.br. Acesso em: 30 nov. 2021 (adaptado).

Levando em conta apenas a reciclagem de latas, qual é o valor mais próximo da massa de bauxita, em tonelada, que deixou de ser extraída da natureza em 2020 no Brasil?', 'Foram vendidas 4,0 × 10⁵ t de latas e recicladas 97,4%, ou seja, 0,974 × 4,0 × 10⁵ ≈ 3,9 × 10⁵ t de alumínio. Como 50 kg de bauxita produzem 10 kg de alumínio, a razão é 5 : 1, e a massa de bauxita que deixou de ser extraída é 5 × 3,9 × 10⁵ ≈ 1,9 × 10⁶ t (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '1,0 × 10⁴ ton', 0),
    (@question_id, 'B', '3,9 × 10⁵ ton', 0),
    (@question_id, 'C', '5,0 × 10⁵ ton', 0),
    (@question_id, 'D', '1,9 × 10⁶ ton', 1),
    (@question_id, 'E', '2,0 × 10⁷ ton', 0);

-- 2º Dia, questão 137 (Matemática › Matemática › Estatística). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 108, NULL, 'Ao calcular a média de suas notas em 4 provas, um estudante dividiu, por engano, a soma das notas por 5. Com isso, a média obtida foi 1 unidade menor do que deveria ser, caso fosse calculada corretamente. O valor correto da média das notas desse estudante é', 'Seja m a média correta, de modo que a soma das notas é S = 4m. O estudante calculou S / 5 = 4m / 5, que é 1 unidade menor que m: m − 4m / 5 = 1, logo m / 5 = 1 e m = 5 (B).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '4.', 0),
    (@question_id, 'B', '5.', 1),
    (@question_id, 'C', '6.', 0),
    (@question_id, 'D', '19.', 0),
    (@question_id, 'E', '21.', 0);

-- 2º Dia, questão 140 (Matemática › Matemática › Razão, Proporção e Porcentagem). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 102, NULL, 'João e Felipe participaram, na escola, de uma maratona de matemática na qual, durante uma semana, resolveram 200 questões cada. Nessa maratona, a porcentagem P de acertos de cada participante é convertida em um conceito:
• insatisfatório: se 0 ≤ P < 50;
• regular: se 50 ≤ P < 60;
• bom: se 60 ≤ P < 75;
• muito bom: se 75 ≤ P < 90;
• excelente: se 90 ≤ P ≤ 100.

João acertou 75% das questões da maratona e Felipe acertou 30% a menos que a quantidade de questões que João acertou.

Os conceitos de João e Felipe foram, respectivamente,', 'João acertou 75% de 200 questões, ou seja, 150, e seu conceito é muito bom (75 ≤ P < 90). Felipe acertou 30% a menos: 0,7 × 150 = 105 questões, que correspondem a 105 / 200 = 52,5% e conceito regular (50 ≤ P < 60). Os conceitos foram muito bom e regular (B).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'muito bom e bom.', 0),
    (@question_id, 'B', 'muito bom e regular.', 1),
    (@question_id, 'C', 'muito bom e insatisfatório.', 0),
    (@question_id, 'D', 'bom e regular.', 0),
    (@question_id, 'E', 'bom e insatisfatório.', 0);

-- 2º Dia, questão 143 (Matemática › Matemática › Razão, Proporção e Porcentagem). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 102, NULL, 'Uma casa de shows terá um evento cujo custo total de produção é de R$ 34 350,00, sendo que comporta 500 pessoas. O preço do ingresso será de R$ 130,00 e, normalmente, 60% das pessoas adquirem meia-entrada, pagando R$ 65,00 pelo ingresso. Além do faturamento proveniente da venda de ingressos, a casa de shows vende, com 60% de lucro, bebidas e petiscos ao público no dia do evento.

Após ter vendido todos os 500 ingressos, constatou-se que a quantidade de meias-entradas vendidas superou em 50% o que estava previsto, impactando o faturamento estimado com a venda de ingressos.

No dia do evento, decidiu-se manter o percentual de 60% de lucro sobre as bebidas e petiscos, pois todo o público que comprou ingresso compareceu ao show. Com isso, espera-se ter lucro de R$ 17 000,00 nesse evento.

Para que se alcance o lucro esperado, o gasto médio por pessoa com bebidas e petiscos, em real, deverá ser de', 'Com 500 ingressos e 60% de meia previstos (300), a venda real de meias foi 50% maior: 450 meias a R$ 65,00 e 50 inteiras a R$ 130,00, o que dá 450 × 65 + 50 × 130 = 29 250 + 6 500 = R$ 35 750,00. Descontado o custo de R$ 34 350,00, sobram R$ 1 400,00. Se o gasto médio for x, a venda de bebidas e petiscos é 500x e o lucro de 60% é 300x. Então 1 400 + 300x = 17 000, e x = 52,00 (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '19,50.', 0),
    (@question_id, 'B', '28,80.', 0),
    (@question_id, 'C', '34,00.', 0),
    (@question_id, 'D', '52,00.', 1),
    (@question_id, 'E', '68,70.', 0);

-- 2º Dia, questão 145 (Matemática › Matemática › Geometria Plana). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 109, NULL, 'O estádio do Maracanã passou por algumas modificações estruturais para a realização da Copa do Mundo de 2014, como, por exemplo, as dimensões do campo retangular. Para se adaptar aos padrões da Fifa, as dimensões do campo foram reduzidas de 110 m × 75 m para 105 m × 68 m.

Disponível em: http://virgula.uol.com.br. Acesso em: 14 ago. 2013 (adaptado).

Em quantos metros quadrados a área do campo do Maracanã foi reduzida?', 'A área antiga era 110 × 75 = 8 250 m², e a nova é 105 × 68 = 7 140 m². A redução foi 8 250 − 7 140 = 1 110 m² (D). O erro comum é subtrair as dimensões (110 − 105 = 5 e 75 − 68 = 7) em vez de comparar as áreas.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '24', 0),
    (@question_id, 'B', '35', 0),
    (@question_id, 'C', '555', 0),
    (@question_id, 'D', '1110', 1),
    (@question_id, 'E', '1145', 0);

-- 2º Dia, questão 146 (Matemática › Matemática › Funções). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 103, NULL, 'Um fazendeiro pretende construir um galinheiro ocupando uma região plana de formato retangular, com lados de comprimentos L metro e C metro. Os lados serão cercados por telas de tipos diferentes. Nos lados de comprimento L metro, será utilizada uma tela cujo metro linear custa R$ 20,00, enquanto, nos outros dois lados, uma que custa R$ 15,00. O fazendeiro quer gastar, no máximo, R$ 6 000,00 na compra de toda a tela necessária para o galinheiro, e deseja que o galinheiro tenha a maior área possível.

Qual será a medida, em metro, do maior lado do galinheiro?', 'O custo da tela é 2 · L · 20 + 2 · C · 15 = 6 000, ou seja, 4L + 3C = 600, e C = (600 − 4L) / 3. A área A = L · C = (600L − 4L²) / 3 é uma parábola de concavidade para baixo, com máximo em L = 600 / 8 = 75 m. Então C = (600 − 300) / 3 = 100 m, que é o maior lado (B).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '85', 0),
    (@question_id, 'B', '100', 1),
    (@question_id, 'C', '175', 0),
    (@question_id, 'D', '200', 0),
    (@question_id, 'E', '350', 0);

-- 2º Dia, questão 151 (Matemática › Matemática › Razão, Proporção e Porcentagem). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 102, NULL, 'O arquiteto Renzo Piano exibiu a maquete da nova sede do Museu Whitney de Arte Americana, um prédio assimétrico que tem um vão aberto para a galeria principal, cuja medida da área é 1 672 m².

Considere que a escala da maquete exibida é 1 : 200.

Época, n. 682, jun. 2011 (adaptado).

A medida da área do vão aberto nessa maquete, em centímetro quadrado, é', 'A escala 1 : 200 vale para comprimentos; para áreas a razão é o quadrado, 1 : 40 000. A área real é 1 672 m², portanto na maquete é 1 672 / 40 000 = 0,0418 m². Como 1 m² = 10 000 cm², isso dá 418 cm² (E).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '4,18.', 0),
    (@question_id, 'B', '8,36.', 0),
    (@question_id, 'C', '41,80.', 0),
    (@question_id, 'D', '83,60.', 0),
    (@question_id, 'E', '418,00.', 1);

-- 2º Dia, questão 153 (Matemática › Matemática › Razão, Proporção e Porcentagem). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 102, NULL, 'Um instituto de pesquisa constatou que, nos últimos dez anos, o crescimento populacional de uma cidade foi de 135,25%.

Qual é a representação decimal da taxa percentual desse crescimento populacional?', 'Uma taxa percentual se converte em decimal dividindo por 100: 135,25% = 135,25 / 100 = 1,3525 (D). Valores como 13,525 ou 0,13525 resultam de erro de deslocamento da vírgula.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '13525,0', 0),
    (@question_id, 'B', '135,25', 0),
    (@question_id, 'C', '13,525', 0),
    (@question_id, 'D', '1,3525', 1),
    (@question_id, 'E', '0,13525', 0);

-- 2º Dia, questão 156 (Matemática › Matemática › Funções). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 103, NULL, 'O uso de aplicativos de transporte tem sido uma alternativa à população que busca preços mais competitivos para se locomover, principalmente nas grandes cidades. As formas usadas para determinar o valor cobrado por cada viagem variam de um aplicativo para outro, mas, em geral, o valor V a ser pago, em real, varia em função de:
• tarifa base F: valor fixo, em real, cobrado no início da viagem;
• tempo T: tempo, em minuto, de duração da viagem;
• distância D: distância percorrida, em quilômetro.

Um desses aplicativos cobra R$ 2,00 de valor fixo, acrescido de R$ 0,26 por minuto de viagem e de R$ 1,40 por quilômetro rodado.

Nessas condições, a expressão que fornece o valor V a ser pago por uma viagem desse aplicativo é', 'O valor da viagem é a soma do valor fixo de R$ 2,00 (a tarifa base, que não é multiplicada por nenhuma variável), de R$ 0,26 por minuto (0,26T) e de R$ 1,40 por quilômetro (1,40D). Logo, V = 2,00 + 0,26T + 1,40D (B). A alternativa A multiplicaria a tarifa base pela variável F, e as demais omitem parcelas ou usam T e D sem os preços unitários.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '2,00F + 0,26T + 1,40D', 0),
    (@question_id, 'B', '2,00 + 0,26T + 1,40D', 1),
    (@question_id, 'C', '2,00 + 0,26T + D', 0),
    (@question_id, 'D', '0,26T + 1,40D', 0),
    (@question_id, 'E', 'F + T + D', 0);

-- 2º Dia, questão 157 (Matemática › Matemática › Razão, Proporção e Porcentagem). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 102, NULL, 'Uma doceira vende e entrega, em seu bairro, porções de 100 g de docinhos de aniversário. Atualmente, a taxa única de entrega é R$ 10,00, e o valor cobrado por uma porção é R$ 25,00. Por uma estratégia de vendas, a partir da próxima semana, a taxa única de entrega será R$ 15,00, e um novo valor será cobrado por uma porção, de maneira que o valor total a ser pago por um cliente na compra de 5 porções permaneça o mesmo.

A partir da próxima semana, qual será o novo valor cobrado, em real, por uma porção?', 'Hoje, o cliente que compra 5 porções paga 5 × 25 + 10 = R$ 135,00. Com a taxa de R$ 15,00 e novo valor p por porção, o total deve continuar igual: 15 + 5p = 135, então p = 120 / 5 = R$ 24,00 (C).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '12,50', 0),
    (@question_id, 'B', '20,00', 0),
    (@question_id, 'C', '24,00', 1),
    (@question_id, 'D', '30,00', 0),
    (@question_id, 'E', '37,50', 0);

-- 2º Dia, questão 159 (Matemática › Matemática › Razão, Proporção e Porcentagem). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 102, NULL, 'Uma tubulação despeja sempre o mesmo volume de água por unidade de tempo em uma caixa-d’água, o que significa dizer que a vazão de água nessa tubulação é constante. Na junção dessa tubulação com a caixa-d’água, está instalada uma membrana de filtragem cujo objetivo é filtrar eventuais impurezas presentes na água, combinado a um bom fluxo de água. O fluxo (φ) de água através da superfície da membrana é diretamente proporcional à vazão de água na tubulação, medida em mililitro por segundo, e inversamente proporcional à área da superfície da membrana, medida em centímetro quadrado.

A unidade de medida adequada para descrever o fluxo (φ) de água que atravessa a superfície da membrana é', 'O fluxo é diretamente proporcional à vazão e inversamente proporcional à área: φ = vazão / área. A vazão é medida em mL/s e a área em cm², logo φ é medido em (mL/s) / cm² = mL / (cm² · s) (C). As demais alternativas invertem a posição da área ou do tempo, ou multiplicam grandezas que deveriam estar dividindo.', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'mL · s · cm²', 0),
    (@question_id, 'B', '(mL · cm²)/s', 0),
    (@question_id, 'C', 'mL/(cm² · s)', 1),
    (@question_id, 'D', '(cm² · s)/mL', 0),
    (@question_id, 'E', 'cm²/(mL · s)', 0);

-- 2º Dia, questão 160 (Matemática › Matemática › Razão, Proporção e Porcentagem). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 102, NULL, 'Uma piscina tem capacidade de 2 500 000 litros. Seu sistema de abastecimento foi regulado para ter uma vazão constante de 6 000 litros de água por minuto. O mesmo sistema foi instalado em uma segunda piscina, com capacidade de 2 750 000 litros, e regulado para ter uma vazão, também constante, capaz de enchê-la em um tempo 20% maior que o gasto para encher a primeira piscina.

A vazão do sistema de abastecimento da segunda piscina, em litro por minuto, é', 'A primeira piscina enche em 2 500 000 / 6 000 ≈ 416,7 min. A segunda deve levar 20% mais, ou seja, 1,2 × 416,7 = 500 min. A vazão necessária é 2 750 000 / 500 = 5 500 L/min (D).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '8 250.', 0),
    (@question_id, 'B', '7 920.', 0),
    (@question_id, 'C', '6 545.', 0),
    (@question_id, 'D', '5 500.', 1),
    (@question_id, 'E', '5 280.', 0);

-- 2º Dia, questão 162 (Matemática › Matemática › Progressões). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 104, NULL, 'Uma imobiliária iniciou uma campanha de divulgação para promover a venda de apartamentos que podem ser pagos em 100 parcelas mensais. O valor da primeira delas é fixado no momento da compra, com o pagamento dessa primeira parcela. A partir da segunda parcela, o valor é determinado pela aplicação de um acréscimo percentual fixo ao valor da parcela anterior. Como atrativo, a imobiliária fará o pagamento de todas as parcelas correspondentes ao mês de aniversário do comprador.

Um cliente, que faz aniversário no mês de maio, decidiu comprar um desses apartamentos por meio do financiamento oferecido pela imobiliária, e pretende escolher o mês mais adequado para realizar essa compra, de modo que o valor total dos pagamentos seja o menor possível. Qual é o mês que esse cliente deverá escolher para realizar a compra do apartamento?', 'As parcelas formam uma progressão geométrica crescente, e o cliente quer que as parcelas gratuitas, as dos meses de maio, sejam as maiores possíveis. A 100ª parcela ocorre 99 meses depois da primeira, e 99 = 8 × 12 + 3, ou seja, três meses depois do mês da compra. Para que a última parcela, a maior de todas, caia em maio (e, a cada 12 meses para trás, as outras parcelas de maio), a compra deve ser feita três meses antes: fevereiro (A).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Fevereiro.', 1),
    (@question_id, 'B', 'Abril.', 0),
    (@question_id, 'C', 'Maio.', 0),
    (@question_id, 'D', 'Junho.', 0),
    (@question_id, 'E', 'Agosto.', 0);

-- 2º Dia, questão 165 (Matemática › Matemática › Probabilidade). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 107, NULL, 'Para melhorar o fluxo de ônibus em uma avenida que tem dois semáforos, a prefeitura reduzirá o tempo em que cada sinal ficará vermelho, que atualmente é de 15 segundos a cada 60 segundos. Admita que o instante de chegada de um ônibus a cada semáforo é aleatório.

O engenheiro de tráfego da prefeitura calculou a probabilidade de um ônibus encontrar cada um deles vermelho, obtendo 15/60. A partir daí, estabeleceu uma mesma redução na quantidade do tempo, em segundo, em que cada sinal ficará vermelho, de maneira que a probabilidade de um ônibus encontrar ambos os sinais vermelhos numa mesma viagem seja igual a 4/100, considerando os eventos independentes.

Para isso, a redução do tempo em que o sinal ficará vermelho, em segundo, estabelecida pelo engenheiro foi de', 'A probabilidade de encontrar cada sinal vermelho é 15 / 60 = 1/4, e a de encontrar os dois é 1/16. A nova meta é 4 / 100 para os dois, logo, para cada um, p² = 4/100 e p = 2/10 = 1/5. O novo tempo de vermelho é 60 · 1/5 = 12 s, e a redução é 15 − 12 = 3 s (B).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '1,35.', 0),
    (@question_id, 'B', '3,00.', 1),
    (@question_id, 'C', '9,00.', 0),
    (@question_id, 'D', '12,60.', 0),
    (@question_id, 'E', '13,80.', 0);

-- 2º Dia, questão 171 (Matemática › Matemática › Geometria Plana). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 109, NULL, 'Um jardineiro dispõe de k metros lineares de cerca baixa para fazer um jardim ornamental. O jardim, delimitado por essa cerca, deve ter a forma de um triângulo equilátero, um quadrado ou um hexágono regular. A escolha será pela forma que resulte na maior área.

O jardineiro escolherá a forma de', 'Com perímetro k, o triângulo equilátero tem lado k/3 e área (√3/4) · (k/3)² = k²√3 / 36 ≈ 0,048k². O quadrado tem lado k/4 e área k² / 16 ≈ 0,0625k². O hexágono regular tem lado k/6 e área 6 · (√3/4) · (k/6)² = k²√3 / 24 ≈ 0,072k². A maior área é a do hexágono regular, k²√3 / 24 (A).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'hexágono regular, pois a área do jardim, em metro quadrado, será k²√3/24.', 1),
    (@question_id, 'B', 'hexágono regular, pois a área do jardim, em metro quadrado, será 3k²√3/2.', 0),
    (@question_id, 'C', 'quadrado, pois a área do jardim, em metro quadrado, será k²/16.', 0),
    (@question_id, 'D', 'triângulo equilátero, pois a área do jardim, em metro quadrado, será k²√3/36.', 0),
    (@question_id, 'E', 'triângulo equilátero, pois a área do jardim, em metro quadrado, será k²√3/4.', 0);

-- 2º Dia, questão 179 (Matemática › Matemática › Razão, Proporção e Porcentagem). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, explanation, difficulty) VALUES
    (@enem_2024_dia_2, 102, NULL, 'Uma empresa de engenharia foi contratada para realizar um serviço no valor de R$ 71 250,00. Os sócios da empresa decidiram que 40% desse valor seria destinado ao pagamento de três engenheiros que gerenciaram o serviço. O pagamento para cada um deles será feito de forma diretamente proporcional ao total de horas trabalhadas. O número de dias e o número de horas diárias trabalhadas pelos engenheiros foram, respectivamente:
• engenheiro I: 4 dias, numa jornada de 5 horas e meia por dia;
• engenheiro II: 5 dias, numa jornada de 4 horas por dia;
• engenheiro III: 6 dias, numa jornada de 2 horas e meia por dia.

Qual a maior diferença, em real, entre os valores recebidos por esse serviço entre dois desses engenheiros?', 'O valor destinado aos engenheiros é 40% de R$ 71 250,00 = R$ 28 500,00. As horas trabalhadas foram: engenheiro I, 4 × 5,5 = 22 h; engenheiro II, 5 × 4 = 20 h; engenheiro III, 6 × 2,5 = 15 h, num total de 57 h, e cada hora vale 28 500 / 57 = R$ 500,00. Os valores são R$ 11 000,00, R$ 10 000,00 e R$ 7 500,00, e a maior diferença é 11 000 − 7 500 = R$ 3 500,00 (C).', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '1 000', 0),
    (@question_id, 'B', '1 500', 0),
    (@question_id, 'C', '3 500', 1),
    (@question_id, 'D', '3 800', 0),
    (@question_id, 'E', '5 250', 0);
