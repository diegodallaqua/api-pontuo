-- Questões objetivas do ENEM 2025 (1º Dia e 2º Dia).
--
-- Fonte: cadernos impressos do Inep na raiz do projeto (1º Dia: Caderno 3
-- Branco; 2º Dia: Caderno 6 Cinza) e os gabaritos oficiais desses mesmos
-- cadernos. A numeração nos comentários é a do caderno; entre parênteses, a do
-- Caderno Azul (1 e 7), usada em questoes_pendentes.md.
--
-- Ficam de fora:
--   * questões que dependem de imagem (figura, charge, gráfico, fórmula
--     estrutural) ou de tabela, listadas em questoes_pendentes.md até que as
--     imagens sejam publicadas e registradas em question_image;
--   * as questões anuladas pelo Inep (2º Dia: 115, 118 e 172).
--
-- As questões de 01 a 05 do 1º Dia entram duas vezes, uma por língua
-- estrangeira (inglês e espanhol), porque são questões diferentes com o mesmo
-- número. O texto-base comum às questões 06 a 10 é repetido em cada uma delas,
-- já que a questão é sorteada sozinha em um simulado.
--
-- O Inep não divulga a dificuldade das questões; todas entram como 2 (média)
-- até haver um critério próprio. Fórmulas foram transcritas em texto, com
-- índices e expoentes em Unicode e frações na forma a/b.
--
-- Os ids são gerados pelo banco: cada questão é inserida e suas alternativas
-- usam LAST_INSERT_ID(), então a migration não colide com questões já
-- cadastradas pela API.


-- O vestibular de cada dia é buscado pela chave natural (INEP, ENEM, 2025,
-- etapa), e não pelo id: bancos criados antes do Flyway (baseline na V2) podem
-- ter ids diferentes ou não ter alguma das etapas, que então é criada aqui.
INSERT INTO entrance_exam (institution_id, name, year, stage)
SELECT i.id, 'ENEM', 2025, s.stage
FROM institution i
    CROSS JOIN (SELECT '1º Dia' AS stage UNION ALL SELECT '2º Dia') s
WHERE i.acronym = 'INEP'
  AND NOT EXISTS (SELECT 1
                  FROM entrance_exam e
                  WHERE e.institution_id = i.id
                    AND e.name = 'ENEM'
                    AND e.year = 2025
                    AND e.stage = s.stage);

SET @enem_2025_dia_1 = (SELECT e.id
                        FROM entrance_exam e
                            JOIN institution i ON i.id = e.institution_id
                        WHERE i.acronym = 'INEP' AND e.name = 'ENEM' AND e.year = 2025 AND e.stage = '1º Dia');
SET @enem_2025_dia_2 = (SELECT e.id
                        FROM entrance_exam e
                            JOIN institution i ON i.id = e.institution_id
                        WHERE i.acronym = 'INEP' AND e.name = 'ENEM' AND e.year = 2025 AND e.stage = '2º Dia');


-- ENEM 2025 — 1º Dia ------------------------------------------------------

-- 1º Dia, questão 01, inglês (04 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 83, NULL, 'It is true that all children are special, simply because they are children. But most adults are not special, and children end up as adults pretty quickly. Life then can be difficult and even disappointing. The shock of this may account for the emergence of the “snowflake generation” of university students, who are so delicate they can’t handle controversial ideas being put forward in their lectures. The roots of this fragility run deep in modern culture. So, an approach of the world that states: “Life is wonderful, you’re special and, if you are a good boy/girl, life will be amazing forever” is not a message designed to aid bouncing back from failure or confronting catastrophe. Resilience is not about feeding ego — telling your children how wonderful they are — but strengthening it.

LOTT, T. Disponível em: www.theguardian.com. Acesso em: 10 dez. 2017 (adaptado).

Nesse texto, a expressão “snowflake generation” é usada para', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'abordar obstáculos impostos a universitários.', 0),
    (@question_id, 'B', 'destacar mensagens de incentivo a estudantes.', 0),
    (@question_id, 'C', 'estimular ações proativas em situações de emergência.', 0),
    (@question_id, 'D', 'retratar relações conflituosas em ambiente universitário.', 0),
    (@question_id, 'E', 'apontar posturas de uma juventude avessa a contrariedades.', 1);

-- 1º Dia, questão 03, inglês (01 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 83, NULL, 'Glory Ames, from the White Earth reservation, is frustrated that despite the presence of several indigenous reservations near Moorhead, local Halloween stores still feature a western section with costumes such as “pow wow princess”.

Even worse, despite a long-running debate about racism and cultural appropriation, often prompted by backlash against celebrities and politicians for donning offensive costumes, people continue to wear such costumes.

Last Halloween, Ames spotted a photo on Instagram of a girl dressed as a Native American with a bullet in her forehead. She immediately reported it to the social media platform and had it removed.

“They blatantly take certain aspects of our culture, race, religion, and use it for their advantage and ignore the people living it”, said Ames.

LIU, M. C. M. Disponível em: www.washingtonpost.com. Acesso em: 12 maio 2024 (adaptado).

Ao abordar um aspecto da celebração do Halloween, esse texto tem por objetivo', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'denunciar a violência contra crianças indígenas.', 0),
    (@question_id, 'B', 'descrever costumes tradicionais em celebrações indígenas.', 0),
    (@question_id, 'C', 'valorizar as vestimentas características dos povos originários.', 0),
    (@question_id, 'D', 'criticar a exploração indevida de elementos da identidade indígena.', 1),
    (@question_id, 'E', 'sugerir ações de combate ao preconceito contra os povos originários.', 0);

-- 1º Dia, questão 04, inglês (02 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 83, NULL, 'My idea of philosophy is that if it is not relevant to human problems, if it does not tell us how we can go about eradicating some of the misery in this world, then it is not worth the name of philosophy. I think Socrates made a very profound statement when he asserted that philosophy is to teach us proper living. In this day and age “proper living” means liberation from the urgent problems of poverty, economic necessity and indoctrination, mental oppression.

DAVIS, A. Lectures on Liberation. Washington: Smithsonian Libraries, 1971 (adaptado).

Nesse texto, ao discorrer sobre a relevância da filosofia, a escritora Angela Davis tem por objetivo', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'criticá-la pela restrição temática.', 0),
    (@question_id, 'B', 'vinculá-la ao universo acadêmico.', 0),
    (@question_id, 'C', 'afastá-la da abordagem socrática.', 0),
    (@question_id, 'D', 'aproximá-la dos problemas sociais.', 1),
    (@question_id, 'E', 'responsabilizá-la pela pobreza humana.', 0);

-- 1º Dia, questão 05, inglês (03 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 83, NULL, 'Remember the sky that you were born under,
know each of the star’s stories.
Remember the moon, know who she is.
Remember the sun’s birth at dawn. [...]

Remember your birth, how your mother struggled
to give you form and breath [...]

Remember the earth whose skin you are:
red earth, black earth, yellow earth, white earth
brown earth, we are earth.
Remember the plants, trees, animal life who all have their
tribes, their families, their histories, too [...]

Remember you are all people and all people are you.
Remember you are this universe and this universe is you.

Remember all is in motion, is growing, is you.

HARJO, J. She Had Some Horses. Londres: W. Norton & Company, 1983 (fragmento).

Nesse poema, de uma autora de ascendência indígena, o eu lírico ressalta a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'potência dos astros celestes.', 0),
    (@question_id, 'B', 'origem das plantas e dos animais.', 0),
    (@question_id, 'C', 'importância do apego à terra natal.', 0),
    (@question_id, 'D', 'relação entre seres humanos e natureza.', 1),
    (@question_id, 'E', 'conexão entre o tempo real e o tempo imaginário.', 0);

-- 1º Dia, questão 02, espanhol (05 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 88, NULL, '¿Qué es la generación Alfa?

Hacer cortes generacionales no es una ciencia exacta. Sin embargo, según un análisis de 2018 del centro de estudios Pew Research Center, analizar las generaciones ofrece “una manera de entender cómo los acontecimientos globales y los cambios tecnológicos, económicos y sociales interactúan para definir la forma en que la gente ve el mundo”. Y está claro cómo ve el mundo la próxima generación: a través de una pantalla.

“Antes las generaciones se definían a partir de sucesos históricos o sociales importantes. Hoy se delimitan por el uso de determinada tecnología”, le explica a BBC Mundo un psicólogo, docente, escritor y experto en el uso — y abuso — de las redes sociales.

Ninguna de las generaciones anteriores será comparable a nivel digital con los Alfa, que, como contraparte, serán la primera a la que le serán ajenos muchos aspectos del mundo analógico.

Disponível em: www.bbc.com. Acesso em: 9 maio 2024 (adaptado).

Nesse texto, a expressão “a través de una pantalla” evidencia que a geração Alfa estabelece com o mundo uma relação marcada pelo(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'conflito etário.', 0),
    (@question_id, 'B', 'efemeridade da tecnologia.', 0),
    (@question_id, 'C', 'dependência de recursos digitais.', 1),
    (@question_id, 'D', 'valor dos acontecimentos sociais.', 0),
    (@question_id, 'E', 'indiferença em relação a fatos históricos.', 0);

-- 1º Dia, questão 03, espanhol (01 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 88, NULL, '¿Dónde está nuestro pan, patrón?
¿Dónde quedó todo ese dinero?
¿Lo tiene oculto bajo el colchón
o lo escondió en otro sucio agujero?

Yo tengo un Tàpies, dice Juan Luis;
yo tengo un Antonio López, dice Jaume.
¿Quién de los dos sabrá decir
cuántos muertos tiene a sus espaldas?

NACHO VEGAS. Disponível em: www.lahiguera.net. Acesso em: 12 abr. 2024 (fragmento).

Na letra da canção Polvorado, ao apresentar as reflexões do eu poético, o cantor espanhol Nacho Vegas', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'demonstra o orgulho dos trabalhadores para com artistas de referência.', 0),
    (@question_id, 'B', 'critica a postura dos patrões frente aos direitos dos trabalhadores.', 1),
    (@question_id, 'C', 'apresenta propostas para diminuir as desigualdades sociais.', 0),
    (@question_id, 'D', 'evidencia o diálogo horizontal entre patrão e trabalhadores.', 0),
    (@question_id, 'E', 'questiona a insalubridade do ambiente de trabalho.', 0);

-- 1º Dia, questão 04, espanhol (02 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 88, NULL, 'Guagua, las palabras también migran

Pocas palabras del idioma castellano despiertan tanto interés y asombro como guagua. En España su uso se reduce a las Islas Canarias, donde se registra a partir de los años 30 del pasado siglo. El origen de guagua resulta algo polémico. Todo parece indicar que la voz vino de Cuba. La trajeron de vuelta, junto con sus maletas y sus recuerdos, aquellos inmigrantes canarios que viajaron a la isla antillana en busca de fortuna. Recientemente en Lanzarote, las autoridades acuñaron “guagüismo” para potenciar el uso del transporte público y han solicitado a la Real Academia Española que introduzca dicho término en el diccionario.

El origen de guagua cambia si nos vamos al sur del continente. Autobús no es la única acepción de la palabra guagua que se usa en América. En los países del sur, tales como Colombia, Argentina o Perú, como una derivación del término quechua wáwa, también se le dice guagua a un niño de pecho o bebé. Algunos autores chilenos han defendido también que esta acepción de guagua como bebé proviene del idioma mapuche. Sea uno u otro el origen, es común en todo el cono sur oír a hombres y mujeres hablar con ternura de sus “guaguas lindas”.

Disponível em: www.escribirbienyclaro.com. Acesso em: 13 maio 2024.

Ao abordar a trajetória da palavra “guagua”, o texto destaca a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'presença de empréstimo linguístico no espanhol.', 1),
    (@question_id, 'B', 'validação de um vocábulo por uma instituição renomada.', 0),
    (@question_id, 'C', 'concorrência entre línguas indígenas e a língua espanhola.', 0),
    (@question_id, 'D', 'valorização da língua de um país em detrimento da de outro.', 0),
    (@question_id, 'E', 'disputa entre hispano-americanos e espanhóis por sua origem.', 0);

-- 1º Dia, questão 05, espanhol (03 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 90, NULL, 'Cuando yo era chiquito
todo quedaba cerca, cerquita.
Para llegar al cielo
no más bastaba una subidita.
El sueño me alcanzaba
para ir tan lejos como quería.
Cuando yo era chiquito
yo sí podía, yo sí podía.
Libertad, libertad,
libertad para mi niño.
[...]
Cuando yo era vejigo
me iba pa’l río porque era hermoso,
aunque estaba prohibido
por peligroso, por peligroso.
Como jagüey y ceiba,
como la palma y la yagruma,
cuando yo era vejigo
yo era del monte y soñaba espuma.
Libertad, libertad,
libertad para mi niño.

RODRÍGUEZ, S. Cuando yo era un enano. In: Oh Melancolía. Havana: Empresa de Grabaciones y Ediciones Musicales, 1988 (fragmento).

O recurso que caracteriza essa letra de canção como um relato das memórias do eu poético é o uso de', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'palavras no grau diminutivo.', 0),
    (@question_id, 'B', 'adjetivos na descrição da paisagem.', 0),
    (@question_id, 'C', 'vocábulos relacionados à fauna cubana.', 0),
    (@question_id, 'D', 'verbos no pretérito imperfeito do indicativo.', 1),
    (@question_id, 'E', 'marcas linguísticas de uma variedade caribenha.', 0);

-- 1º Dia, questão 06 (07 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 74, NULL, 'De próprio punho

A escrita e suas tecnologias sofrem interessantes metamorfoses, numa ciranda que vai do simples bilhete aos originais de um livro

Estranhei muito na primeira vez que escutei a expressão “de próprio punho”. Parecia que eu ia bater em alguém. Não era bem o caso. Foi numa situação bancária, dessas bem burocráticas, e eu devia escrever algo bem breve, mas com minhas mãos. Na verdade, o que importava era a autenticidade da minha caligrafia, que à época ainda era mais fluente e firme. Depois dos teclados de computador, ela rateia bastante. Minha letra, hoje, tem uma espécie de alternância: dia sim, dia não, trêmula e firme, forte e fraca, mais rotunda e mais cheia de arestas.

É claro que já escrevi muito mais de próprio punho ou, numa palavra mais bonita, manuscrevi (prefiro a mão ao punho, embora ele também seja usado na tarefa). Mas isso não é um feito individual. Em larga medida, é social. Muita gente sente o mesmo que eu, isto é, escreve bem menos usando as mãos, ou melhor, empregando algum tipo de tecnologia (lápis, caneta etc.) para escrever com grafite ou tinta ou giz ou carvão ou sangue e o que mais. É importante lembrar que ainda há gente que não sabe escrever neste país, neste planeta, mas muita gente sabe e tem um combo de tecnologias mais ou menos à disposição para isso. Sou dessas pessoas privilegiadas que têm várias possibilidades, e uma delas nunca deixou de ser o uso das minhas mãos. Ainda hoje, são elas que batucam meu teclado de computador ou que tocam suavemente duas ou três telas sensíveis. Mas não expressam mais a minha letra. No lugar, aparecem Times New Roman, Arial, Calibri e mais uma centena de “letras” à minha escolha. Eu e Deus e o mundo.

A despeito desse rol de chances e ferramentas para escrever, o manuscrito nunca deixou de pintar aqui e ali, muitas vezes como obrigação. Na escola, por exemplo, até hoje ele é soberano. No Enem também. Curioso, não? Fico pensando em que espaços e ocasiões ainda uso minha letra. Olhando ao redor, na minha casa, minha letra está em espaços muito delimitados e específicos: bilhetes. Eles estão principalmente na cozinha, em especial na porta da geladeira, a fim de manter a comunicação com meus coabitantes, sempre muito esquecidos ou relapsos. Mas também há bilhetes em post its na minha mesa do escritório, textinhos em garranchos por meio dos quais me comunico comigo mesma, a evitar um comportamento esquecido e relapso.

No escritório, costumo ser mais suave comigo mesma, mas também muito mais lacônica, a ponto de nem eu me entender, se passar o tempo. Em todos os casos vai minha letra, menos e mais redonda, a lápis e a tinta azul, em post its rosa-choque, colados precariamente, e todos com destino à lixeira, em breve. Justo porque eles funcionam como lembretes de tarefas e coisas que devem ser vencidas e, claro, substituídas por outras, num fluxo infinito, às vezes ansiogênico, com que a maioria dos adultos (e mais ainda as adultas) precisa conviver.

As formas de escrever mudam, as necessidades também, e o resultado é um elenco complexo, em que nada dispensa nada, a depender da tarefa ou da importância das coisas ou de suas funções, claro. A escrita e suas tecnologias incríveis vão se reposicionando, mudando de status, numa ciranda interessante e importante que pode ser vista à luz de certa diversidade que encontra suas oportunidades e seus efeitos, aqui e ali. Não adianta muito pensar sempre como se tudo fosse excludente. Estão aí minha farta comunicação por bilhetes, minha gaveta alegre de post its de toda cor, esperando para serem usados, e o cheque do cartório, em que quase tudo já é digital. “Do punho ao pixel” não é uma frase filosoficamente correta. O negócio é mais “o punho e o pixel”.

RIBEIRO, A. E. Disponível em: https://rascunho.com.br. Acesso em: 16 jan. 2024 (adaptado).

O elemento que caracteriza esse texto como uma crônica é a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'defesa das opiniões da autora sobre um tema de interesse coletivo.', 0),
    (@question_id, 'B', 'exposição sobre o uso de tecnologias nas práticas de escrita atuais.', 0),
    (@question_id, 'C', 'abordagem de fatos do contexto pessoal em uma perspectiva reflexiva.', 1),
    (@question_id, 'D', 'utilização de recursos linguísticos para a interlocução direta com o leitor.', 0),
    (@question_id, 'E', 'apresentação de acontecimentos segundo a ordem de sucessão no tempo.', 0);

-- 1º Dia, questão 07 (06 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 74, NULL, 'De próprio punho

A escrita e suas tecnologias sofrem interessantes metamorfoses, numa ciranda que vai do simples bilhete aos originais de um livro

Estranhei muito na primeira vez que escutei a expressão “de próprio punho”. Parecia que eu ia bater em alguém. Não era bem o caso. Foi numa situação bancária, dessas bem burocráticas, e eu devia escrever algo bem breve, mas com minhas mãos. Na verdade, o que importava era a autenticidade da minha caligrafia, que à época ainda era mais fluente e firme. Depois dos teclados de computador, ela rateia bastante. Minha letra, hoje, tem uma espécie de alternância: dia sim, dia não, trêmula e firme, forte e fraca, mais rotunda e mais cheia de arestas.

É claro que já escrevi muito mais de próprio punho ou, numa palavra mais bonita, manuscrevi (prefiro a mão ao punho, embora ele também seja usado na tarefa). Mas isso não é um feito individual. Em larga medida, é social. Muita gente sente o mesmo que eu, isto é, escreve bem menos usando as mãos, ou melhor, empregando algum tipo de tecnologia (lápis, caneta etc.) para escrever com grafite ou tinta ou giz ou carvão ou sangue e o que mais. É importante lembrar que ainda há gente que não sabe escrever neste país, neste planeta, mas muita gente sabe e tem um combo de tecnologias mais ou menos à disposição para isso. Sou dessas pessoas privilegiadas que têm várias possibilidades, e uma delas nunca deixou de ser o uso das minhas mãos. Ainda hoje, são elas que batucam meu teclado de computador ou que tocam suavemente duas ou três telas sensíveis. Mas não expressam mais a minha letra. No lugar, aparecem Times New Roman, Arial, Calibri e mais uma centena de “letras” à minha escolha. Eu e Deus e o mundo.

A despeito desse rol de chances e ferramentas para escrever, o manuscrito nunca deixou de pintar aqui e ali, muitas vezes como obrigação. Na escola, por exemplo, até hoje ele é soberano. No Enem também. Curioso, não? Fico pensando em que espaços e ocasiões ainda uso minha letra. Olhando ao redor, na minha casa, minha letra está em espaços muito delimitados e específicos: bilhetes. Eles estão principalmente na cozinha, em especial na porta da geladeira, a fim de manter a comunicação com meus coabitantes, sempre muito esquecidos ou relapsos. Mas também há bilhetes em post its na minha mesa do escritório, textinhos em garranchos por meio dos quais me comunico comigo mesma, a evitar um comportamento esquecido e relapso.

No escritório, costumo ser mais suave comigo mesma, mas também muito mais lacônica, a ponto de nem eu me entender, se passar o tempo. Em todos os casos vai minha letra, menos e mais redonda, a lápis e a tinta azul, em post its rosa-choque, colados precariamente, e todos com destino à lixeira, em breve. Justo porque eles funcionam como lembretes de tarefas e coisas que devem ser vencidas e, claro, substituídas por outras, num fluxo infinito, às vezes ansiogênico, com que a maioria dos adultos (e mais ainda as adultas) precisa conviver.

As formas de escrever mudam, as necessidades também, e o resultado é um elenco complexo, em que nada dispensa nada, a depender da tarefa ou da importância das coisas ou de suas funções, claro. A escrita e suas tecnologias incríveis vão se reposicionando, mudando de status, numa ciranda interessante e importante que pode ser vista à luz de certa diversidade que encontra suas oportunidades e seus efeitos, aqui e ali. Não adianta muito pensar sempre como se tudo fosse excludente. Estão aí minha farta comunicação por bilhetes, minha gaveta alegre de post its de toda cor, esperando para serem usados, e o cheque do cartório, em que quase tudo já é digital. “Do punho ao pixel” não é uma frase filosoficamente correta. O negócio é mais “o punho e o pixel”.

RIBEIRO, A. E. Disponível em: https://rascunho.com.br. Acesso em: 16 jan. 2024 (adaptado).

No que diz respeito ao gênero bilhete, a autora dessa crônica', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'ressalta a formalidade na comunicação com as pessoas de sua convivência.', 0),
    (@question_id, 'B', 'critica a ansiedade causada pela velocidade da comunicação.', 0),
    (@question_id, 'C', 'expressa a obrigatoriedade de concisão nas anotações.', 0),
    (@question_id, 'D', 'questiona a prática da escrita de próprio punho.', 0),
    (@question_id, 'E', 'apresenta a diversidade de usos no cotidiano.', 1);

-- 1º Dia, questão 08 (10 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'De próprio punho

A escrita e suas tecnologias sofrem interessantes metamorfoses, numa ciranda que vai do simples bilhete aos originais de um livro

Estranhei muito na primeira vez que escutei a expressão “de próprio punho”. Parecia que eu ia bater em alguém. Não era bem o caso. Foi numa situação bancária, dessas bem burocráticas, e eu devia escrever algo bem breve, mas com minhas mãos. Na verdade, o que importava era a autenticidade da minha caligrafia, que à época ainda era mais fluente e firme. Depois dos teclados de computador, ela rateia bastante. Minha letra, hoje, tem uma espécie de alternância: dia sim, dia não, trêmula e firme, forte e fraca, mais rotunda e mais cheia de arestas.

É claro que já escrevi muito mais de próprio punho ou, numa palavra mais bonita, manuscrevi (prefiro a mão ao punho, embora ele também seja usado na tarefa). Mas isso não é um feito individual. Em larga medida, é social. Muita gente sente o mesmo que eu, isto é, escreve bem menos usando as mãos, ou melhor, empregando algum tipo de tecnologia (lápis, caneta etc.) para escrever com grafite ou tinta ou giz ou carvão ou sangue e o que mais. É importante lembrar que ainda há gente que não sabe escrever neste país, neste planeta, mas muita gente sabe e tem um combo de tecnologias mais ou menos à disposição para isso. Sou dessas pessoas privilegiadas que têm várias possibilidades, e uma delas nunca deixou de ser o uso das minhas mãos. Ainda hoje, são elas que batucam meu teclado de computador ou que tocam suavemente duas ou três telas sensíveis. Mas não expressam mais a minha letra. No lugar, aparecem Times New Roman, Arial, Calibri e mais uma centena de “letras” à minha escolha. Eu e Deus e o mundo.

A despeito desse rol de chances e ferramentas para escrever, o manuscrito nunca deixou de pintar aqui e ali, muitas vezes como obrigação. Na escola, por exemplo, até hoje ele é soberano. No Enem também. Curioso, não? Fico pensando em que espaços e ocasiões ainda uso minha letra. Olhando ao redor, na minha casa, minha letra está em espaços muito delimitados e específicos: bilhetes. Eles estão principalmente na cozinha, em especial na porta da geladeira, a fim de manter a comunicação com meus coabitantes, sempre muito esquecidos ou relapsos. Mas também há bilhetes em post its na minha mesa do escritório, textinhos em garranchos por meio dos quais me comunico comigo mesma, a evitar um comportamento esquecido e relapso.

No escritório, costumo ser mais suave comigo mesma, mas também muito mais lacônica, a ponto de nem eu me entender, se passar o tempo. Em todos os casos vai minha letra, menos e mais redonda, a lápis e a tinta azul, em post its rosa-choque, colados precariamente, e todos com destino à lixeira, em breve. Justo porque eles funcionam como lembretes de tarefas e coisas que devem ser vencidas e, claro, substituídas por outras, num fluxo infinito, às vezes ansiogênico, com que a maioria dos adultos (e mais ainda as adultas) precisa conviver.

As formas de escrever mudam, as necessidades também, e o resultado é um elenco complexo, em que nada dispensa nada, a depender da tarefa ou da importância das coisas ou de suas funções, claro. A escrita e suas tecnologias incríveis vão se reposicionando, mudando de status, numa ciranda interessante e importante que pode ser vista à luz de certa diversidade que encontra suas oportunidades e seus efeitos, aqui e ali. Não adianta muito pensar sempre como se tudo fosse excludente. Estão aí minha farta comunicação por bilhetes, minha gaveta alegre de post its de toda cor, esperando para serem usados, e o cheque do cartório, em que quase tudo já é digital. “Do punho ao pixel” não é uma frase filosoficamente correta. O negócio é mais “o punho e o pixel”.

RIBEIRO, A. E. Disponível em: https://rascunho.com.br. Acesso em: 16 jan. 2024 (adaptado).

O recurso linguístico usado para marcar a síntese da opinião da autora sobre a temática desenvolvida foi o(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'emprego da primeira pessoa em “Estranhei muito na primeira vez que escutei a expressão ‘de próprio punho’”. (l. 1)', 0),
    (@question_id, 'B', 'utilização de locução adverbial em “Na verdade, o que importava era a autenticidade da minha caligrafia”. (l. 3-4)', 0),
    (@question_id, 'C', 'uso de pronome possessivo em “Minha letra, hoje, tem uma espécie de alternância”. (l. 5-6)', 0),
    (@question_id, 'D', 'adoção de termo autorreflexivo em “No escritório, costumo ser mais suave comigo mesma”. (l. 30)', 0),
    (@question_id, 'E', 'substituição da expressão “Do punho ao pixel” (l. 44) pela expressão “o punho e o pixel”. (l. 45)', 1);

-- 1º Dia, questão 09 (08 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'De próprio punho

A escrita e suas tecnologias sofrem interessantes metamorfoses, numa ciranda que vai do simples bilhete aos originais de um livro

Estranhei muito na primeira vez que escutei a expressão “de próprio punho”. Parecia que eu ia bater em alguém. Não era bem o caso. Foi numa situação bancária, dessas bem burocráticas, e eu devia escrever algo bem breve, mas com minhas mãos. Na verdade, o que importava era a autenticidade da minha caligrafia, que à época ainda era mais fluente e firme. Depois dos teclados de computador, ela rateia bastante. Minha letra, hoje, tem uma espécie de alternância: dia sim, dia não, trêmula e firme, forte e fraca, mais rotunda e mais cheia de arestas.

É claro que já escrevi muito mais de próprio punho ou, numa palavra mais bonita, manuscrevi (prefiro a mão ao punho, embora ele também seja usado na tarefa). Mas isso não é um feito individual. Em larga medida, é social. Muita gente sente o mesmo que eu, isto é, escreve bem menos usando as mãos, ou melhor, empregando algum tipo de tecnologia (lápis, caneta etc.) para escrever com grafite ou tinta ou giz ou carvão ou sangue e o que mais. É importante lembrar que ainda há gente que não sabe escrever neste país, neste planeta, mas muita gente sabe e tem um combo de tecnologias mais ou menos à disposição para isso. Sou dessas pessoas privilegiadas que têm várias possibilidades, e uma delas nunca deixou de ser o uso das minhas mãos. Ainda hoje, são elas que batucam meu teclado de computador ou que tocam suavemente duas ou três telas sensíveis. Mas não expressam mais a minha letra. No lugar, aparecem Times New Roman, Arial, Calibri e mais uma centena de “letras” à minha escolha. Eu e Deus e o mundo.

A despeito desse rol de chances e ferramentas para escrever, o manuscrito nunca deixou de pintar aqui e ali, muitas vezes como obrigação. Na escola, por exemplo, até hoje ele é soberano. No Enem também. Curioso, não? Fico pensando em que espaços e ocasiões ainda uso minha letra. Olhando ao redor, na minha casa, minha letra está em espaços muito delimitados e específicos: bilhetes. Eles estão principalmente na cozinha, em especial na porta da geladeira, a fim de manter a comunicação com meus coabitantes, sempre muito esquecidos ou relapsos. Mas também há bilhetes em post its na minha mesa do escritório, textinhos em garranchos por meio dos quais me comunico comigo mesma, a evitar um comportamento esquecido e relapso.

No escritório, costumo ser mais suave comigo mesma, mas também muito mais lacônica, a ponto de nem eu me entender, se passar o tempo. Em todos os casos vai minha letra, menos e mais redonda, a lápis e a tinta azul, em post its rosa-choque, colados precariamente, e todos com destino à lixeira, em breve. Justo porque eles funcionam como lembretes de tarefas e coisas que devem ser vencidas e, claro, substituídas por outras, num fluxo infinito, às vezes ansiogênico, com que a maioria dos adultos (e mais ainda as adultas) precisa conviver.

As formas de escrever mudam, as necessidades também, e o resultado é um elenco complexo, em que nada dispensa nada, a depender da tarefa ou da importância das coisas ou de suas funções, claro. A escrita e suas tecnologias incríveis vão se reposicionando, mudando de status, numa ciranda interessante e importante que pode ser vista à luz de certa diversidade que encontra suas oportunidades e seus efeitos, aqui e ali. Não adianta muito pensar sempre como se tudo fosse excludente. Estão aí minha farta comunicação por bilhetes, minha gaveta alegre de post its de toda cor, esperando para serem usados, e o cheque do cartório, em que quase tudo já é digital. “Do punho ao pixel” não é uma frase filosoficamente correta. O negócio é mais “o punho e o pixel”.

RIBEIRO, A. E. Disponível em: https://rascunho.com.br. Acesso em: 16 jan. 2024 (adaptado).

Nesse texto, o que caracteriza a escrita “de próprio punho” é a letra manuscrita, enquanto a escrita digital é ilustrada pelo(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'utilização de tecnologias diversificadas.', 0),
    (@question_id, 'B', 'desenvolvimento de novos recursos de escrita.', 0),
    (@question_id, 'C', 'possibilidade de interações mediadas por telas.', 0),
    (@question_id, 'D', 'diversidade de fontes tipográficas que estão disponíveis.', 1),
    (@question_id, 'E', 'delimitação dos espaços onde a produção textual ocorre.', 0);

-- 1º Dia, questão 10 (09 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'De próprio punho

A escrita e suas tecnologias sofrem interessantes metamorfoses, numa ciranda que vai do simples bilhete aos originais de um livro

Estranhei muito na primeira vez que escutei a expressão “de próprio punho”. Parecia que eu ia bater em alguém. Não era bem o caso. Foi numa situação bancária, dessas bem burocráticas, e eu devia escrever algo bem breve, mas com minhas mãos. Na verdade, o que importava era a autenticidade da minha caligrafia, que à época ainda era mais fluente e firme. Depois dos teclados de computador, ela rateia bastante. Minha letra, hoje, tem uma espécie de alternância: dia sim, dia não, trêmula e firme, forte e fraca, mais rotunda e mais cheia de arestas.

É claro que já escrevi muito mais de próprio punho ou, numa palavra mais bonita, manuscrevi (prefiro a mão ao punho, embora ele também seja usado na tarefa). Mas isso não é um feito individual. Em larga medida, é social. Muita gente sente o mesmo que eu, isto é, escreve bem menos usando as mãos, ou melhor, empregando algum tipo de tecnologia (lápis, caneta etc.) para escrever com grafite ou tinta ou giz ou carvão ou sangue e o que mais. É importante lembrar que ainda há gente que não sabe escrever neste país, neste planeta, mas muita gente sabe e tem um combo de tecnologias mais ou menos à disposição para isso. Sou dessas pessoas privilegiadas que têm várias possibilidades, e uma delas nunca deixou de ser o uso das minhas mãos. Ainda hoje, são elas que batucam meu teclado de computador ou que tocam suavemente duas ou três telas sensíveis. Mas não expressam mais a minha letra. No lugar, aparecem Times New Roman, Arial, Calibri e mais uma centena de “letras” à minha escolha. Eu e Deus e o mundo.

A despeito desse rol de chances e ferramentas para escrever, o manuscrito nunca deixou de pintar aqui e ali, muitas vezes como obrigação. Na escola, por exemplo, até hoje ele é soberano. No Enem também. Curioso, não? Fico pensando em que espaços e ocasiões ainda uso minha letra. Olhando ao redor, na minha casa, minha letra está em espaços muito delimitados e específicos: bilhetes. Eles estão principalmente na cozinha, em especial na porta da geladeira, a fim de manter a comunicação com meus coabitantes, sempre muito esquecidos ou relapsos. Mas também há bilhetes em post its na minha mesa do escritório, textinhos em garranchos por meio dos quais me comunico comigo mesma, a evitar um comportamento esquecido e relapso.

No escritório, costumo ser mais suave comigo mesma, mas também muito mais lacônica, a ponto de nem eu me entender, se passar o tempo. Em todos os casos vai minha letra, menos e mais redonda, a lápis e a tinta azul, em post its rosa-choque, colados precariamente, e todos com destino à lixeira, em breve. Justo porque eles funcionam como lembretes de tarefas e coisas que devem ser vencidas e, claro, substituídas por outras, num fluxo infinito, às vezes ansiogênico, com que a maioria dos adultos (e mais ainda as adultas) precisa conviver.

As formas de escrever mudam, as necessidades também, e o resultado é um elenco complexo, em que nada dispensa nada, a depender da tarefa ou da importância das coisas ou de suas funções, claro. A escrita e suas tecnologias incríveis vão se reposicionando, mudando de status, numa ciranda interessante e importante que pode ser vista à luz de certa diversidade que encontra suas oportunidades e seus efeitos, aqui e ali. Não adianta muito pensar sempre como se tudo fosse excludente. Estão aí minha farta comunicação por bilhetes, minha gaveta alegre de post its de toda cor, esperando para serem usados, e o cheque do cartório, em que quase tudo já é digital. “Do punho ao pixel” não é uma frase filosoficamente correta. O negócio é mais “o punho e o pixel”.

RIBEIRO, A. E. Disponível em: https://rascunho.com.br. Acesso em: 16 jan. 2024 (adaptado).

A autora conclui que as novas tecnologias de escrita', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'evoluem para facilitar a vida cotidiana.', 0),
    (@question_id, 'B', 'alcançam diferentes realidades sociais.', 0),
    (@question_id, 'C', 'coexistem com outras já estabelecidas.', 1),
    (@question_id, 'D', 'promovem maior agilidade na comunicação.', 0),
    (@question_id, 'E', 'surgem nos contextos em que são necessárias.', 0);

-- 1º Dia, questão 11 (18 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 100, NULL, 'Com 20 anos de experiência no futebol de alto rendimento, Marina, ex-jogadora da seleção brasileira de futebol, salienta que, por trás do espetáculo apresentado nas mídias, com mensagens de motivação e superação, o esporte não é tão inclusivo assim. “É esta análise que devemos fazer: aqueles atletas que estão ali estão trazendo uma alta performance a partir dos seus limites”, explica. Para a profissional, é preciso analisar com cautela “a ideia romântica que a mídia passa para os telespectadores”. A realidade é muito mais dura do que as imagens espetaculosas que principalmente a televisão busca transmitir para a audiência. “Por trás existe um ser humano, a gente não pode nunca esquecer isso. Aquela pessoa treinou insistentemente para estar ali, durante meses, semanas e temporadas. Duas vezes ao dia, de duas a quatro horas”, pondera Marina. Atualmente, as crianças e os jovens vislumbram o sucesso profissional e a boa-vida financeira de poucos atletas que se destacam e estampam os meios de comunicação. Tudo parece ser muito mais fácil do que realmente é quando apenas as conquistas são mostradas.

ROSOLEN, N. Disponível em: www.uninter.com. Acesso em: 10 maio 2024 (adaptado).

Nesse texto, a visão crítica de uma ex-atleta de futebol revela que', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'os meios de comunicação invisibilizam as dificuldades presentes no esporte.', 1),
    (@question_id, 'B', 'o treinamento atlético de alto nível é desestimulante para os indivíduos.', 0),
    (@question_id, 'C', 'o trabalho contínuo é desvalorizado no contexto esportivo profissional.', 0),
    (@question_id, 'D', 'as ações de incentivo financeiro a jovens atletas são precárias.', 0),
    (@question_id, 'E', 'as publicações da mídia esportiva rotulam atletas iniciantes.', 0);

-- 1º Dia, questão 12 (19 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 98, NULL, 'No predomínio das mulheres pretas brasileiras nos Jogos Olímpicos de 2024, uma coisa chamou a atenção no pódio: elas valorizam a parte psicológica. As duas medalhistas de ouro, a judoca Beatriz Souza e a ginasta Rebeca Andrade, ressaltam, em várias entrevistas, a importância da saúde mental. Em uma dessas entrevistas, Rebeca sinaliza: “Acho que não é só sobre vencer a Simone, é sobre vencer a mim mesma. A minha briga está na minha cabeça, não está com outras pessoas. Para conseguir fazer as minhas apresentações, preciso controlar a minha cabeça, o meu corpo, e essa é a briga”. Na mesma linha, a skatista Rayssa Leal exalta a necessidade da terapia, e a Seleção Brasileira de Futebol de Mulheres tem o suporte psicológico como reforço no treinamento.

Disponível em: https://iclnoticias.com.br. Acesso em: 18 set. 2024 (adaptado).

Nesse texto, as atletas brasileiras defendem o(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'investimento na modernização de equipamentos.', 0),
    (@question_id, 'B', 'subordinação do treinamento físico ao mental.', 0),
    (@question_id, 'C', 'estímulo à competição entre adversárias.', 0),
    (@question_id, 'D', 'aprimoramento da expressão corporal.', 0),
    (@question_id, 'E', 'importância da saúde emocional.', 1);

-- 1º Dia, questão 13 (20 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 73, NULL, 'A característica fundamental no aprendizado das práticas rituais nos candomblés é o processo iniciático e participante. Durante o período de reclusão em terreiros ou rocas, o iniciado passa por uma série de ritos esotéricos (banhos rituais, raspagem da cabeça etc.), ao mesmo tempo em que começa a adquirir um complexo código de símbolos materiais (substâncias, folhas, frutos, raízes etc.) e de gestos associados a um repertório linguístico específico das cerimônias que se desenrolam nos contextos sagrados em geral e em cada terreiro em particular.

Esse repertório linguístico, genericamente chamado de “língua de santo” na Bahia, compreende uma terminologia religiosa operacional, de caráter mágico-semântico e de aparente forma portuguesa, mas que repousa sobre sistemas lexicais de diferentes línguas africanas que provavelmente foram faladas no Brasil escravocrata, vindo a constituir uma língua ritual, que se acredita pertencer à nação do vodum, do orixá ou do inquice, e não a determinada nação africana política atual.

Disponível em: https://periodicos.ufba.br. Acesso em: 21 jan. 2024 (adaptado).

A “língua de santo” tem sua importância para o patrimônio linguístico brasileiro por', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'apresentar uma carga semântica mítica.', 0),
    (@question_id, 'B', 'conservar elementos dos falares dos escravizados.', 1),
    (@question_id, 'C', 'resgatar expressões portuguesas do período colonial.', 0),
    (@question_id, 'D', 'decodificar o ritual religioso dos nossos antepassados.', 0),
    (@question_id, 'E', 'favorecer a compreensão do léxico africano contemporâneo.', 0);

-- 1º Dia, questão 14 (21 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 82, NULL, 'O meu medo é entrar na faculdade e tirar zero eu que nunca fui bom de matemática fraco no inglês eu que nunca gostei de química geografia e português o que é que eu faço agora hein mãe não sei. [...]

O meu medo é a vida piorar e eu não conseguir arranjar emprego nem de faxineiro nem de porteiro nem de ajudante de pedreiro e o pessoal dizer que o governo já fez o que pôde já pôde o que fez já deu a sua cota de participação hein mãe não sei.

O meu medo é que mesmo com diploma debaixo do braço andando por aí desiludido e desempregado o policial me olhe de cara feia e eu acabe fazendo uma burrice sei lá uma besteira será que eu vou ter direito a uma cela especial hein mãe não sei.

FREIRE, M. Contos negreiros. Rio de Janeiro: Record, 2005.

Nesse texto, a reiteração dos medos e das angústias do narrador exprime', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'inseguranças sobre o futuro familiar.', 0),
    (@question_id, 'B', 'dilemas resultantes de seu fracasso escolar.', 0),
    (@question_id, 'C', 'incertezas centradas em sua condição social.', 1),
    (@question_id, 'D', 'hesitações em relação à sua formação profissional.', 0),
    (@question_id, 'E', 'preocupações com as políticas públicas assistenciais.', 0);

-- 1º Dia, questão 16 (16 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 79, NULL, 'Símbolos

Eu e tu, ante a noite e o amplo desdobramento
do mar, fero, a estourar de encontro à rocha nua...
Um símbolo descubro aqui, neste momento
esta rocha, este mar... a minha vida e a tua.

O mar vem, o mar vai, nele há o gesto violento
de quem maltrata e, após, se arrepende e recua.
Como compreendo bem da rocha o sentimento!
São muito iguais, por certo, a minha mágoa e a sua.

Contemplo neste quadro a nossa triste vida;
tu és dúbio mar que, na sua inconsciência,
tem carinhos de amor e fúrias de demência!

Eu sou a dor estanque, a dor empedernida,
sou rocha a emergir de um côncavo de areia,
imóvel, muda, isenta e alheia ao mar, alheia.

MACHADO, G. Poesia completa. Rio de Janeiro: Cátedra/MEC, 1978.

Nesse soneto, os traços da estética simbolista são resgatados pelo eu lírico ao', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'rejeitar as emoções de “amor” e “mágoa”.', 0),
    (@question_id, 'B', 'expressar a dubiedade do olhar sobre o outro.', 0),
    (@question_id, 'C', 'representar o “eu” e o “tu” como sujeitos volúveis.', 0),
    (@question_id, 'D', 'associar a sua inconsciência a elementos da natureza.', 0),
    (@question_id, 'E', 'metaforizar o conflito amoroso nas imagens de “mar” e “rocha”.', 1);

-- 1º Dia, questão 17 (17 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'Antes do inverno chegar.
Ela tinha olhinhos brilhantes. Os mesmos de antes. Antes da fome. Antes das 17 mudanças de cidade. Dos sete filhos e dos muitos anos de trabalho dentro e fora de casa.

Ela fazia ambrosia, bolo de fubá e pedacinhos de queijo. Antes do inverno, ela plantava flores novas e diferentes para nos esperar nas próximas férias de verão.

Ela tinha o jeito de menina. Menina sapeca, correndo na grama seca do cerrado. O mesmo jeito de antes. Antes do marido (e mesmo com o marido). Antes do cansaço dos anos. Antes da dureza do trato com a terra.

Ela tinha histórias. Compridas, curtas, divertidas e verdadeiras. Mas isso foi antes. Antes das lembranças se bagunçarem feito bolas coloridas de Natal esperando para serem montadas na árvore.

Eu era sua neta. Antes do Alzheimer chegar, eu era sua neta. Mas ela é e sempre será minha avó.

PERSON, C. R. Borboletas no estômago: porque às vezes o título precisa ser adolescente e clichê, já que a vida exige sermos tão adultos. São Paulo: Ed. das Autoras, 2021.

A narradora, ao resgatar memórias da história de vida da avó, faz uso recorrente da locução “antes de”. Esse termo colabora para a progressão temática na medida em que', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'relaciona eventos ocorridos simultaneamente.', 0),
    (@question_id, 'B', 'estabelece uma comparação entre as lembranças.', 0),
    (@question_id, 'C', 'ressalta fatos que ressignificam o momento presente.', 1),
    (@question_id, 'D', 'sinaliza uma sequência que denota ações consecutivas.', 0),
    (@question_id, 'E', 'apresenta uma explicação para as memórias resgatadas.', 0);

-- 1º Dia, questão 18 (11 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 77, NULL, '— Vejo, disse ele com algum acanhamento, que o doutor não é nenhum pé-rapado, mas nunca é bom facilitar... Minha filha Nocência fez 18 anos pelo Natal, e é rapariga que pela feição parece moça de cidade, muito ariscazinha de modos, mas bonita e boa deveras... Coitada, foi criada sem mãe, e aqui nestes fundões. [...]

— Ora muito que bem, continuou Pereira caindo aos poucos na habitual garrulice, quando vi a menina tomar corpo, tratei logo de casá-la.

— Ah! é casada? perguntou Cirino.
— Isto é, é e não é. A coisa está apalavrada. Por aqui costuma labutar no costeio do gado para São Paulo um homem de mão-cheia, que talvez o sr. conheça... o Manecão Doca...

— Não, respondeu Cirino abanando a cabeça.
— Pois isso é um homem às direitas, desempenado e trabucador como ele só... fura estes sertões todos e vem tangendo pontes de gado que metem pasmo. Também dizem que tem bichado muito e ajuntado cobre grosso, o que é possível, porque não é gastador nem dado a mulheres. Uma feita que estava aqui de pousada... olhe, mesmo neste lugar onde estava mecê inda agorinha, falei-lhe em casamento... isto é, dei-lhe uns toques... porque os pais devem tomar isso a si para bem de suas famílias; não acha?

— Boa dúvida, aprovou Cirino, dou-lhe toda a razão; era do seu dever.

TAUNAY, A. d’E. Inocência. Disponível em: www.dominiopublico.gov.br. Acesso em: 29 fev. 2024.

Nesse trecho, ao se referir à sua filha, o pai de Inocência reproduz os ideais românticos, presentes na', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'valorização do ambiente rural na formação moral da mulher.', 0),
    (@question_id, 'B', 'figura decorativa da mulher ante o protagonismo masculino.', 1),
    (@question_id, 'C', 'equivalência de origem social para a harmonia do casal.', 0),
    (@question_id, 'D', 'importância do dote como condição para o casamento.', 0),
    (@question_id, 'E', 'aura de mistério sobre a identidade da jovem.', 0);

-- 1º Dia, questão 19 (12 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 100, NULL, 'O Ministério do Esporte no Brasil lançou o programa Maré Inclusiva, em 2024, ano dos Jogos Paralímpicos de Paris. Esse programa visa ampliar as oportunidades para pessoas com deficiência que desejam praticar o surf. O parasurf é a prática do surf adaptada para permitir que pessoas com deficiência pratiquem o esporte em todas as suas categorias, modalidades e manifestações. Para a Secretaria Nacional do Paradesporto, a iniciativa é mais do que um programa de esporte, é uma iniciativa que busca transformar vidas e promover a inclusão por meio do parasurf, criando um legado de igualdade e respeito.

Disponível em: www.gov.br/esporte. Acesso em: 6 set. 2024 (adaptado).

De acordo com esse texto, o programa voltado ao estímulo da prática do parasurf evidencia a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'adesão de diferentes países a programas inclusivos.', 0),
    (@question_id, 'B', 'preocupação política em atender a demandas paralímpicas.', 0),
    (@question_id, 'C', 'importância de uma política pública esportiva para a inclusão.', 1),
    (@question_id, 'D', 'eficiência das iniciativas de inclusão em megaeventos esportivos.', 0),
    (@question_id, 'E', 'escassez de investimento em práticas corporais de aventura na natureza.', 0);

-- 1º Dia, questão 20 (13 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 74, NULL, 'Art. 26-A. Nos estabelecimentos de ensino fundamental e médio, oficiais e particulares, torna-se obrigatório o ensino sobre História e Cultura Afro-Brasileira.

§ 1º O conteúdo programático a que se refere o caput deste artigo incluirá o estudo da História da África e dos Africanos, a luta dos negros no Brasil, a cultura negra brasileira e o negro na formação da sociedade nacional, resgatando a contribuição do povo negro nas áreas social, econômica e política pertinentes à História do Brasil.

§ 2º Os conteúdos referentes à História e Cultura Afro-Brasileira serão ministrados no âmbito de todo o currículo escolar, em especial nas áreas de Educação Artística e de Literatura e História Brasileiras.

BRASIL. Lei n. 10 639/2003. Disponível em: www.gov.br/planalto. Acesso em: 5 maio 2024.

O emprego da norma-padrão é justificado nesse texto', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'pela especialização de seu público-alvo.', 0),
    (@question_id, 'B', 'pela relevância cultural de seu conteúdo.', 0),
    (@question_id, 'C', 'pelos contextos pedagógicos em que circula.', 0),
    (@question_id, 'D', 'pela importância para os grupos étnico-raciais.', 0),
    (@question_id, 'E', 'pelas características do gênero a que pertence.', 1);

-- 1º Dia, questão 23 (25 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'Passando por aqui para lembrar algumas palavras, frases e expressões que nos infernizaram em 2023. Inclusive passando por aqui. Se você for proativo, vai achar que é o novo normal. Estarão na sua zona de conforto. Mas, se for reativo como eu, vai achar que é uma narrativa que precisa ser ressignificada.

É uma questão de empatia. É sobre entregar um discurso mais robusto e empoderado. Sei bem que não tenho lugar de fala para harmonizar certos pontos fora da curva e que preciso aplicar toda a minha resiliência para fazer um realinhamento. O nível de fitness está hoje num sarrafo muito alto.

O fato é que acho cringe essas falas fora da caixinha. Aliás, falar cringe já é meio cringe. Preciso usar a superação para me reinventar e entender que resenha não tem mais a ver com futebol, é qualquer papo, desde que latente.

Pensando bem, não é tão difícil. Frases feitas são aquelas que entram por um ouvido e saem pelo outro sem um estágio intermediário no cérebro. A boca fala por conta própria, dispensando-nos de pensar. E não tem problema nisso. Ou as ditas frases se incorporam à língua ou morrem e nascem outras. A língua é assim. Simples assim.

CASTRO, R. Disponível em: www1.folha.uol.com.br. Acesso em: 3 fev. 2024 (adaptado).

Nesse texto, a estratégia empregada para criticar a constante exposição a palavras, frases e expressões automatizadas é o(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'menção feita à efemeridade de alguns usos linguísticos aleatórios.', 0),
    (@question_id, 'B', 'subjetividade marcada pela reflexão que se desenvolve em primeira pessoa.', 0),
    (@question_id, 'C', 'efeito estilístico da repetição intencional da palavra “assim” no último parágrafo.', 0),
    (@question_id, 'D', 'sedução sugerida pelo envolvimento direto do leitor marcado nos usos de “você” e “sua”.', 0),
    (@question_id, 'E', 'humor gerado pelo uso das estruturas linguísticas que são objeto da reflexão desenvolvida.', 1);

-- 1º Dia, questão 27 (27 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 99, NULL, 'A diferença entre briga e luta é a existência de juízes e medalhas? A briga desumaniza o outro e pode até matá-lo. Já na luta, as intenções do outro são consideradas sua proposta combativa e suas habilidades, enfim, sua meta de vencer. Na luta, o desenvolvimento passa pelo contato com a agressividade, a raiva, a frustração, o orgulho, a determinação e a fraqueza. Daí também a luta não ser apenas com o outro, mas consigo mesmo, num combate contra as próprias limitações, sobretudo, contra o próprio orgulho.

BARREIRA, C. A briga desumaniza. A luta, não. O Estado de S. Paulo, 22 ago. 2010 (adaptado).

Esse texto apresenta as diferenças entre briga e luta, na medida em que aponta o(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'superação pessoal na luta.', 1),
    (@question_id, 'B', 'violência evidenciada na luta.', 0),
    (@question_id, 'C', 'predomínio de regras na briga.', 0),
    (@question_id, 'D', 'desafio externo presente na luta.', 0),
    (@question_id, 'E', 'habilidade desenvolvida na briga.', 0);

-- 1º Dia, questão 28 (28 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 79, NULL, 'Pequenino morto

Tange o sino, tange, numa voz de choro,
Numa voz de choro... tão desconsolado...
No caixão dourado, como em berço de ouro,
Pequenino, levam-te dormindo... Acorda!
Olha que te levam para o mesmo lado
De onde o sino tange numa voz de choro...
Pequenino, acorda!

Que caminho triste, e que viagem! Alas
De ciprestes negros a gemer no vento;
Tanta boca aberta de famintas valas
A pedir que as fartem, a esperar que as encham...
Pequenino, acorda! Recupera o alento,
Foge da cobiça dessas fundas valas
A pedir que as encham.

CARVALHO, V. Poemas e canções. Rio de Janeiro: Saraiva, 1962 (fragmento).

Nesse fragmento do poema, o sentimento de luto adquire contornos expressivos e é intensificado pela', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'descrição da paisagem de um cemitério.', 0),
    (@question_id, 'B', 'recusa do eu lírico à irreversibilidade da morte.', 1),
    (@question_id, 'C', 'sonoridade dos versos produzida pela pontuação.', 0),
    (@question_id, 'D', 'religiosidade evocada como forma de fortalecimento.', 0),
    (@question_id, 'E', 'impressão de sonho na construção da estrutura poética.', 0);

-- 1º Dia, questão 29 (29 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 82, NULL, 'Só entende os corações desse lugar quem mergulha nesse mar a perder de vista e recoberto de cana caiana, cana fita, cana roxa, cana-de-macaco, açúcar, melado, rapadura, aguardente, fumo, mandioca, quiabos, pimentas, moendas, frutas, fruta-pão, sobrados, senzalas, tachos, casa de purgar. Um reino dentro de outro, com tudo o que se tem direito: reis, rainhas, príncipes e princesas, bobos da corte, cortesãos, conselheiros e escravos, muitos escravos. […]

A corte do massapé, como qualquer outra na história da humanidade, fazia tudo para não deixar escapar nenhum mísero grão dos seus domínios para quem estivesse de fora do seu apertado círculo. Os nomes se repetiam de pai para filho, para sobrinho, para netos e bisnetos, de forma concêntrica e repetitiva, para que não pairasse nenhuma dúvida de que são todos da mesma parentela. As farinhas todas num mesmo saco brasonado.

CRUZ, E. A. Água de barrela. Rio de Janeiro: Malê, 2018.

Nesse fragmento, o narrador enumera o resultado do trabalho com a terra, o qual, no contexto em que aparece,', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'espelha a permanência dos privilégios de classe.', 1),
    (@question_id, 'B', 'oferece um panorama da população do campo.', 0),
    (@question_id, 'C', 'mostra os benefícios da fartura na agricultura.', 0),
    (@question_id, 'D', 'defende a importância da atividade coletiva.', 0),
    (@question_id, 'E', 'valoriza o trabalho ao longo das gerações.', 0);

-- 1º Dia, questão 31 (35 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'Margot Robbie foi criticada por “não ser bonita o suficiente” para interpretar a Barbie. Recentemente, Paolla Oliveira foi chamada de gorda. Só fico pensando o que serei eu com mais de 50 (também no peso), com a minha aparência comum. O corpo da mulher vive um reality show permanente: é sempre vigiado e fiscalizado, como se fosse domínio público. A mulher que não atender aos estereótipos está sujeita a sofrer penalidades básicas, como distúrbios, obsessões, medo do próprio corpo e, é claro, dietas à base de rúcula. “A dieta é o sedativo político mais potente na história das mulheres”, escreveu Naomi Wolf, em 1991. A regra é não haver singularidade, mascarar a passagem do tempo, imobilizar a beleza (já imaginou como isso seria enfadonho?). O mandamento é obedecer às regras sociais do bom comportamento corporal, “como deve ser”, não nos atos, mas na forma.

KORICH, B. S. Disponível em: www1.folha.uol.com.br. Acesso em: 22 jan. 2024 (adaptado).

Nesse texto, para introduzir a ideia de que a fiscalização permanente sobre o corpo afeta todas as mulheres, a autora', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'faz um comentário sobre sua própria imagem.', 0),
    (@question_id, 'B', 'destaca avaliações particulares entre parênteses.', 0),
    (@question_id, 'C', 'cita um formato de programa influente no segmento da beleza.', 0),
    (@question_id, 'D', 'utiliza declaração de uma jornalista como argumento de autoridade.', 0),
    (@question_id, 'E', 'enumera críticas à aparência de mulheres consideradas padrões de beleza.', 1);

-- 1º Dia, questão 32 (36 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 73, NULL, 'I: o nome do filme é roupa suja ... eu assisti na minha casa ... com minha mãe ... tinha um ... o filme era sobre um homem que colocaram ... trocaram as bolsas ... daí o homem levou uma bolsa cheia de dinheiro sem ele saber que na mala dele ... pensando que era dele mas era errada ... quando ele chegou onde ele ia trabalhar ... tinha uma moça tentando abrir a porta pra fazer entrevista com uns cantores lá que tinham ... daí ele perguntou ... “você tá tentando abrir a porta?”... daí ele ... “não ... não” ... daí ele disse ... “ah ... tá ... sim” ... daí ela ... “é ... e quero fazer uma entrevista” ... daí ele disse ... “você quer entrar ... então pode entrar” ... daí entraram ... daí ficaram lá ... quando ela entrou e queria fazer a entrevista um homem num deixou ... daí a mulher pegou ... subiu onde o homem tava trabalhando ... rapaz né ... onde ele tava trabalhando e ficou lá e dando o show ...

CUNHA, M. A. F. Corpus discurso & gramática: a língua falada e escrita na cidade de Natal. Disponível em: https://deg.uff.br. Acesso em: 4 dez. 2024 (adaptado).

Nesse texto, a repetição da forma “daí” revela', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'a necessidade de adequação ao interlocutor.', 0),
    (@question_id, 'B', 'a origem regional do locutor.', 0),
    (@question_id, 'C', 'a escolaridade do falante.', 0),
    (@question_id, 'D', 'uma estratégia presente na linguagem oral.', 1),
    (@question_id, 'E', 'uma ênfase em determinadas partes do discurso.', 0);

-- 1º Dia, questão 33 (30 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 74, NULL, 'Uruku
Urucum
Rocou
(Bixa orellana)
Moju, dono da água, não gosta do cheiro de urucum. Mani’ojarã, dono da mandioca, e os donos das outras plantas cultivadas também não. Eles não suportam. Por isso, os Wajãpi se untam de urucum, deixam o rosto vermelho e se perfumam com seu aroma agradável. Além disso, os seres agressores, os jarã (donos) e os espíritos terrestres, gostam do cheiro dos fluidos humanos, do sangue, do suor. Então, o urucum os dissimula, protegendo as pessoas que vão caçar, caminhar pela floresta, que estão sendo perturbadas por espíritos em sonhos ou que estão em resguardo, como os doentes. O seu uso é tão cotidiano que os Wajãpi o plantam na aldeia, para ter sempre pertinho. Como o urucum não tem jarã, não tem problema nenhum em arrancar e usar para pintar.

STRAPPAZZON, A. I.; SIGOLO, R. P. Jardins da história: medicinas indígenas. Recife: ObservaPICS, 2022.

Esse verbete contribui para a preservação do patrimônio linguístico nacional, pois apresenta uma', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'explicação de um rito medicinal do povo Wajãpi.', 0),
    (@question_id, 'B', 'definição de um termo na perspectiva ancestral indígena.', 1),
    (@question_id, 'C', 'relação de equivalência entre vocábulos de diferentes línguas indígenas.', 0),
    (@question_id, 'D', 'atualização de saberes tradicionais dos povos indígenas brasileiros.', 0),
    (@question_id, 'E', 'descrição das propriedades científicas de plantas silvestres.', 0);

-- 1º Dia, questão 35 (32 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 74, NULL, 'Muitos pensam que narrativa curta é sinônimo de conto, perdendo de vista gêneros que, por tradição ruim, continuam à margem da nobreza. Acontece que o conto tem uma densidade específica, centrando-se na exemplaridade de um instante da condição humana, sem que essa exemplaridade se refira à valoração moral, já que uma grande mazela pode muito bem exemplificar uma das nossas faces. A crônica não tem essa característica. Conservou a marca do registro circunstancial feito por narrador-repórter que relata um fato para muitos leitores que formam um público determinado.

Mas que público é esse? Sendo a crônica uma soma de jornalismo e literatura (daí a imagem do narrador-repórter), dirige-se a uma classe que tem preferência pelo jornal em que ela é publicada, o que significa uma espécie de censura ou, pelo menos, de limitação: a ideologia do veículo corresponde ao interesse dos seus consumidores, direcionados pelos proprietários dos periódicos e/ou pelos editores-chefes da redação. Ocorre ainda o limite de espaço, uma vez que a página comporta várias matérias, o que impõe a cada uma delas um número restrito de laudas, obrigando o redator a explorar, da maneira mais econômica possível, o pequeno espaço de que dispõe. É dessa economia que nasce sua riqueza estrutural.

SÁ, J. A crônica. São Paulo: Ática, 1987 (adaptado).

De acordo com esse texto, o aspecto tecnológico que influencia a composição do gênero crônica advém da', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'conexão ideológica.', 0),
    (@question_id, 'B', 'densidade temática.', 0),
    (@question_id, 'C', 'ênfase no público leitor.', 0),
    (@question_id, 'D', 'apresentação de uma moral.', 0),
    (@question_id, 'E', 'restrição espacial do suporte.', 1);

-- 1º Dia, questão 37 (39 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'Do rádio ao podcast

Desde a disseminação do rádio no Brasil, entre as décadas de 1920 e 1930, principalmente no governo de Getúlio Vargas, as pessoas passaram a dedicar uma parte de seu dia para escutar notícias, novelas, músicas e eventos esportivos em aparelhos de som. O radiojornalismo, por sua vez, teve seu pontapé inicial durante a Revolução Constitucionalista (1932) e se desenvolveu durante a Segunda Guerra Mundial (1939-1945).

Quando a TV surgiu, esperava-se que o rádio fosse totalmente substituído, porém ele se manteve em alta, pois o sinal de televisão não cobria todos os lugares, diferentemente do rádio. Com o surgimento da internet, dos smartphones e de outros dispositivos móveis, o rádio foi incorporado a essas novas tecnologias até o desenvolvimento da web rádio e do podcast, mostrando-se um meio de comunicação versátil e democrático na área jornalística.

Para um pesquisador da Universidade Federal de Uberlândia (UFU), o rádio não se tornou obsoleto, visto que não deixou de ser consumido e se reinventou com o tempo. “O podcast é uma continuação, uma evolução natural do rádio”, opina.

ALVES, A.; ALVES, C. Disponível em: https://comunica.ufu.br. Acesso em: 19 abr. 2024 (adaptado).

Ao abordar a trajetória dos meios de comunicação, esse texto propõe uma reflexão sobre a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'tecnologia digital e seus desdobramentos no desenvolvimento da televisão.', 0),
    (@question_id, 'B', 'evolução da tecnologia digital com o predomínio do podcast sobre o rádio.', 0),
    (@question_id, 'C', 'permanência do rádio e sua evolução por meio da tecnologia digital.', 1),
    (@question_id, 'D', 'influência da televisão sobre os programas de radiojornalismo.', 0),
    (@question_id, 'E', 'interferência da tecnologia digital nas interações humanas.', 0);

-- 1º Dia, questão 38 (40 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 78, NULL, 'Desenvolvendo-se nesse meio, é natural que Celina, filha mais velha de D. Adozinda, tivesse seus pequenos flirts com alguns desses rapazes, muito íntimos da casa e trazendo-lhe da cidade presentes de doces, de balas de ovo, de jornais ilustrados ou de frutas.

As irmãs mais novas iam ao colégio; ela ficava, enchendo o tempo com uns crochês vagarosos, costuras leves, a leitura dos folhetins dos jornais; e o Gilberto, que raramente saía, andava sempre ao seu lado, muito caído por esse tipo um pouco mórbido de menina anêmica [...].

O Gilberto não valia nada, mas quem sabe se apareceria outro, simplório e sincero como ele? E a filha, com os seus dezessete anos, começava a embaraçá-la um pouco, nesse difícil papel de virgem numa casa de pensão, cheia de rapazes. Ora, o melhor era esperar, dar tempo ao tempo... E o Gilberto e a Celina continuaram a namorar-se, ele cândido, ela dúbia; enquanto o Coronel Juvenato, que deixara a mulher em Sobral para tratar de uma concessão rendosa com os políticos do Rio, ia agora monopolizando, como protetor mais importante, as alegres visitas matinais da viúva, que já lhe levava sempre o café — mas sem flores colhidas no jardim, ainda rociadas de orvalho, porque o cearense não dava para essas coisas de poesia. Era rápido, prático, e não admitia bobagens. Por isso, todos os sábados à noite, ele dizia a D. Adozinda com um tremor lúbrico nas banhas moles da face, os olhinhos vivos pestanejando:

— A senhora não se esqueça que amanhã é domingo... Leve-me cedo o café, hein?... que eu tenho de ir à missa...
— Pois não, pois não, Coronel! fique descansado — respondia a viúva do Ferreira, muito atenciosamente, tirando-lhe umas
caspas da gola do paletó, com a mão repolhuda.

Os outros hóspedes riam-se à socapa; e no domingo o café não faltava, bem cedinho...

DOLORES, C. A luta. Rio de Janeiro: Ímã, s.d.

Nesse trecho, ao explorar a descrição como recurso que demarca impressões e pontos de vista, o narrador cria uma ambiência sugestiva do(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'escárnio relacionado à degradação moral dos indivíduos.', 1),
    (@question_id, 'B', 'cenário urbano marcado por condições de insalubridade.', 0),
    (@question_id, 'C', 'persistência do sentimentalismo explorado pelos folhetins.', 0),
    (@question_id, 'D', 'prática do enriquecimento ilícito visto nas grandes cidades.', 0),
    (@question_id, 'E', 'desigualdade de gênero acentuada pela baixa escolarização.', 0);

-- 1º Dia, questão 40 (42 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 73, NULL, 'TRADUZINDO O JURIDIQUÊS

“Denego a liminar pleiteada na exordial, inobstante após a oitiva da parte adversa e da dilação probatória possa lograr alcançar um outro epílogo para o deslinde da quaestio sub examine.”

TRADUÇÃO: Não atendo, por ora, a liminar requerida na petição inicial, ainda que possa chegar a uma outra conclusão após ouvir a outra parte e avaliar as provas produzidas.

Proposta de emenda à Constituição 269 de 2013. Aplica-se aos Governadores e Prefeitos o Regime Geral de Previdência Social, vedada a concessão graciosa, após o término do mandato, de vantagem pecuniária, verba de representação, pensão ou subsídio.

TRADUÇÃO: Torna-se proibido pagar benefícios vitalícios para ex-prefeitos e ex-governadores.

Superinteressante, n. 322, ago. 2013 (adaptado).

Nesse texto, contribui para a construção da ironia a tradução das passagens escritas em “juridiquês” para uma variedade', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'padrão, que alcança o público em geral.', 1),
    (@question_id, 'B', 'histórica, que registra a evolução das leis.', 0),
    (@question_id, 'C', 'coloquial, que reproduz as relações sociais cotidianas.', 0),
    (@question_id, 'D', 'erudita, que resgata a origem latina da língua portuguesa.', 0),
    (@question_id, 'E', 'técnica, que facilita a circulação de informações no sistema judiciário.', 0);

-- 1º Dia, questão 41 (43 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'Em 1995, os Jenipapo-Kanindé quebraram a tradição da sucessão masculina e nomearam Maria de Lourdes da Conceição Alves como sua líder. Desde então, a Cacique Pequena guia o povo em grandes batalhas pelo direito a terra, educação, saúde e cidadania. Hoje, a anciã de 73 anos prepara duas filhas para lhe sucederem quando ela “tombar e pai Tupã a levar”.

Hoje, 129 famílias do município de Aquiraz são reconhecidas pela Fundação Nacional dos Povos Indígenas (Funai) como indígenas, principal luta de Pequena para o seu povo desde o início. “Em 1995, fui a Brasília e tive a oportunidade de conversar com o presidente da Funai. Pedi que mandasse o povo dele na aldeia para fazer o estudo da nossa mãe-terra e de nós”. Dois anos depois, vieram os antropólogos, que concluíram: “Nós era índio sim!”, diz ela.

Há cerca de oito anos, Pequena adoeceu e ficou entre a vida e a morte. Nesse momento, precisou escolher, entre os 16 filhos, quem assumiria sua missão quando partisse. Reunida, a família decidiu sobre a sucessão. “Disseram que, como eu era a primeira cacique mulher do Ceará, acharam melhor eu colocar duas filhas”.

Disponível em: www.sesc-ce.com.br. Acesso em: 15 set. 2024 (adaptado).

Ao abordar a realidade da etnia Jenipapo-Kanindé, essa reportagem cumpre uma função social quando destaca o(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'quantidade de famílias indígenas em Aquiraz.', 0),
    (@question_id, 'B', 'força da tradição nas comunidades indígenas.', 0),
    (@question_id, 'C', 'estudo sobre a demarcação das terras indígenas.', 0),
    (@question_id, 'D', 'protagonismo feminino na linha sucessória desse povo.', 1),
    (@question_id, 'E', 'reconhecimento dessa comunidade pelo governo brasileiro.', 0);

-- 1º Dia, questão 42 (44 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'Porque ler para crianças é um ato de amor

Parece que, com o avanço da tecnologia, os livros têm enfrentado cada vez mais concorrência. Por isso, é nossa função lembrar a importância da leitura em todas as fases da vida, mas principalmente na primeira infância (entre 0 e 5 anos), quando o desenvolvimento das crianças acontece de forma mais intensa.

O ato de ler com uma criança ou ler para ela vai muito além de apenas aproveitar uma história em conjunto. É um laço de amorosidade, porque oferece a ela ferramentas que vão ajudá-la a crescer forte e independente.

Se você precisa de uma motivação extra para entrar nessa rede de incentivo, fique ligado nos motivos a seguir. Adotar esse hábito em casa:

1 – cria um laço emocional com a criança;
2 – ajuda no desenvolvimento das capacidades cognitivas;
3 – ensina sobre o mundo;
4 – incentiva o processamento de informações e a imaginação.

Disponível em: www.huffpost.com.br. Acesso em: 22 maio 2018 (adaptado).

Para persuadir o interlocutor sobre a importância de ler para as crianças, esse texto recorre à estratégia de', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'propor uma condição aos pais, pelo emprego da conjunção “se”.', 0),
    (@question_id, 'B', 'relativizar a opinião apresentada pelo autor, com o uso de “Parece que”.', 0),
    (@question_id, 'C', 'empregar uma linguagem metafórica, com o uso da expressão “laço de amorosidade”.', 0),
    (@question_id, 'D', 'enumerar razões pertinentes a esse ato, como no exemplo “ensina sobre o mundo”.', 1),
    (@question_id, 'E', 'implicar o autor do texto como corresponsável pela campanha, pelo uso de “é nossa função”.', 0);

-- 1º Dia, questão 44 (37 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'TEXTO I

Os Doze Trabalhos de Hércules

Hércules é uma figura lendária da mitologia greco-romana. Ele é frequentemente retratado como um herói de força sobre-humana e coragem, filho de Zeus, o rei dos deuses, e Alcmena, uma mulher mortal. O episódio mais conhecido de Hércules é a realização dos Doze Trabalhos.

Esses trabalhos são impostos a ele como uma forma de expiação pelos crimes cometidos durante um acesso de loucura, causado pela deusa Hera, esposa de Zeus. Os Doze Trabalhos são: matar o Leão de Nemeia; matar a Hidra de Lerna; capturar a corça de Cerineia; capturar o javali de Erimanto; limpar os estábulos de Áugias; matar as aves do lago Estínfalo; matar o touro de Creta; capturar os cavalos de Diomedes; roubar o cinturão de Hipólita, a rainha das Amazonas; capturar o gado de Gerião; capturar os pomos de ouro do Jardim das Hespérides; capturar o cão de Hades, Cérbero.

HERTEL, R. Mitologia. Disponível em: https://osmelhoreslivros.com.br. Acesso em: 4 jun. 2025 (adaptado).

TEXTO II

Os Doze Trabalhos

O que lhe faltava de estudo lhe sobrava de boa vontade e inteligência. No escritório improvisado na salinha da casa, anunciava seus serviços de bombeiro hidráulico e eletricista. Nas horas vagas entregava panfletos e lavava carros. Quando a cidade fervia com alguma festa, postava-se à entrada vendendo cerveja. Se fosse algum show infantil, cocadas. Aos sábados, era pedreiro e, aos domingos, conservava um jardim de uma mansão, além de tratar da piscina e dos cachorros. Nas férias, abrigava-se na fazenda dos donos da mansão, onde trabalhava como caseiro e motorista. Seu nome: João Antonio da Silva. Mas pode chamar de Hércules.

FERREIRA, G. V. Os doze trabalhos. Disponível em: www.minicontos.com.br. Acesso em: 15 jul. 2015 (adaptado).

A comparação entre os textos I e II indica que o(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'intertextualidade com o mito apresentado no Texto I é um recurso presente no Texto II.', 1),
    (@question_id, 'B', 'narração de fatos do Texto II sintetiza os acontecimentos retratados no Texto I.', 0),
    (@question_id, 'C', 'vocabulário empregado no Texto II é ancorado em conhecimento literário.', 0),
    (@question_id, 'D', 'tema do trabalho como reparação é abordado em ambos os textos.', 0),
    (@question_id, 'E', 'marcação temporal no passado predomina em ambos os textos.', 0);

-- 1º Dia, questão 45 (38 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 72, NULL, 'O mais assustador do meteoro que cruzou o céu da Sibéria e explodiu no ar como várias bombas atômicas é que ele chegou sem ser anunciado. Com todas as atenções voltadas para o outro asteroide, o que passou de raspão, o asteroide da Sibéria entrou pela porta dos fundos sem ser detectado. A desculpa é que era pequeno demais para chamar a atenção e por isso os alarmes não funcionaram. Nossa ilusão, até agora, era que qualquer detrito espacial que se aproximasse de nós seria identificado e rotulado, e sua trajetória calculada até o último milímetro com grande antecedência, o que nos daria tempo para preparar o espírito — ou usar nossos cartões de crédito até o limite — no caso da colisão com a Terra ser inevitável.

VERISSIMO, L. F. Disponível em: www.estadao.com.br. Acesso em: 1 mar. 2013.

Com base na organização coesiva desse texto, o(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'oração “que passou de raspão” (l. 3) refere-se ao “meteoro que cruzou o céu da Sibéria” (l. 1).', 0),
    (@question_id, 'B', 'expressão “sua trajetória” (l. 6) refere-se ao elemento textual “qualquer detrito espacial” (l. 5-6).', 1),
    (@question_id, 'C', 'palavra “isso” (l. 5) remete ao segmento textual posterior “os alarmes não funcionaram” (l. 5).', 0),
    (@question_id, 'D', 'pronome “o” em “o que nos daria tempo” (l. 7) remete a “ou usar nossos cartões de crédito” (l. 8).', 0),
    (@question_id, 'E', 'fragmento “o asteroide da Sibéria” (l. 3) introduz um elemento novo no texto.', 0);

-- 1º Dia, questão 46 (57 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 17, NULL, 'Dos 10 aos 15 anos de idade, Virgínia adorava acompanhar seu pai, aos domingos, naquela sinestésica Feira de São Cristóvão (RJ), talvez por ser o maior elo que ela experimentava com o mundo exterior à sua casa e, visto assim e agora, tão íntimo e próximo de algo que ela ainda não sabia, mas que seria, no futuro, a sua própria casa: a Paraíba. Dona Didi costurava, sob medida, camisas sociais, bermudas, shorts, vestidos, saias, sempre em casa e rodeada pelos quatro filhos pequenos do casal, desdobrando-se para dar conta de toda a responsabilidade sem trégua que isso demandava.

PASSOS, M. C. P. et al. apud SCARELI, G. A máquina de costura e os fios da memória. Revista Brasileira de Pesquisa (Auto) Biográfica, n. 18, maio-ago. 2021 (adaptado).

Os itinerários afetivos e socioespaciais mencionados no texto associam-se à vida dos personagens por apresentarem', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'histórias conectadas e recordações do lugar.', 1),
    (@question_id, 'B', 'direitos trabalhistas e produção industrial.', 0),
    (@question_id, 'C', 'preconceitos linguísticos e dinâmicas territoriais.', 0),
    (@question_id, 'D', 'lembranças fabris e discriminação dos operários.', 0),
    (@question_id, 'E', 'experiências profissionais e segregação regional.', 0);

-- 1º Dia, questão 47 (58 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 2, NULL, 'Em 1872, havia mais de 1 milhão de votantes, correspondentes a 13% da população livre. Em 1886, votaram nas eleições parlamentares pouco mais de 100 mil eleitores, ou 0,8% da população total. Houve um corte de quase 90% do eleitorado. O dado é chocante, sobretudo se lembrarmos que a tendência de todos os países europeus da época era na direção de ampliar os direitos políticos. O mais grave é que esse retrocesso foi duradouro. A Proclamação da República não alterou o quadro.

CARVALHO, J. M. Cidadania no Brasil. Rio de Janeiro: Civilização Brasileira, 2002 (adaptado).

De acordo com o texto, a participação no processo eleitoral brasileiro após a Reforma de 1881 sofreu uma variação que se explica pela', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'restrição de gênero.', 0),
    (@question_id, 'B', 'exclusão de imigrantes.', 0),
    (@question_id, 'C', 'comprovação de domicílio.', 0),
    (@question_id, 'D', 'exigência da alfabetização.', 1),
    (@question_id, 'E', 'obrigatoriedade do sufrágio.', 0);

-- 1º Dia, questão 48 (59 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 12, NULL, 'Pela falta de chuvas, a geração de energia eólica, solar e térmica atingiu níveis recordes em agosto de 2021, quando as hidrelétricas ficaram com cerca de 50% do total. Para um professor da Universidade Federal da Bahia, essa queda não é uma surpresa. “Não aconteceu de uma hora para outra. Se olharmos o mapa do Brasil, um dos grandes provedores de água é a Floresta Amazônica. Se você diminui a floresta, diminui a quantidade de água que vai para a atmosfera”, explica.

BORGES, T. O fantasma do apagão. Disponível em: www.correio24horas.com.br. Acesso em: 9 out. 2021 (adaptado).

De acordo com o texto, a dificuldade na produção de energia é causada pela alteração da(s)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'variável em pesquisas meteorológicas.', 0),
    (@question_id, 'B', 'paisagem em locais estratégicos.', 1),
    (@question_id, 'C', 'demandas em regiões industriais.', 0),
    (@question_id, 'D', 'metas em acordos climáticos.', 0),
    (@question_id, 'E', 'geologia em áreas naturais.', 0);

-- 1º Dia, questão 49 (60 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 23, NULL, 'Adam Smith via o açougueiro e o padeiro não só como indivíduos buscando seus interesses financeiros, mas como pessoas moralmente motivadas dentro de uma sociedade. A base da moral era a empatia e o julgamento, instaurando uma distinção entre o que queremos fazer e o que sentimos que devemos fazer.

COLLIER, P. O futuro do capitalismo. Porto Alegre: LP&M, 2019.

O texto defende uma motivação capitalista para o campo dos negócios, na qual o lucro se mostra associado à', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'consolidação do poder político.', 0),
    (@question_id, 'B', 'procura de satisfação subjetiva.', 0),
    (@question_id, 'C', 'estruturação do monopólio comercial.', 0),
    (@question_id, 'D', 'percepção de responsabilidade ética.', 1),
    (@question_id, 'E', 'conquista do reconhecimento público.', 0);

-- 1º Dia, questão 50 (61 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 24, NULL, 'O corpo de cidadãos é o poder supremo dos Estados. A supremacia pode residir ou num homem, ou na minoria, ou em todos. Sempre que o Um, ou a Minoria, ou Todos governam, tendo em vista o bem-estar comum, essas constituições são justas; mas se procuram apenas o benefício de uma das partes, seja ela o Um, a Minoria ou Todos, estabelece-se um desvio.

ARISTÓTELES. Política. São Paulo: Nova Cultural, 2000.

No excerto encontra-se a base da teoria clássica das três formas de governo representadas pela', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'tirania, oligarquia e república.', 0),
    (@question_id, 'B', 'burocracia, autarquia e império.', 0),
    (@question_id, 'C', 'ditadura, autocracia e anarquia.', 0),
    (@question_id, 'D', 'plutocracia, tecnocracia e demagogia.', 0),
    (@question_id, 'E', 'monarquia, aristocracia e democracia.', 1);

-- 1º Dia, questão 51 (46 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 18, NULL, 'Macedônia do Norte
Acordo entra em vigor e país muda oficialmente de nome

Entrou em vigor, em 2019, o acordo que determina a mudança de nome da Macedônia para Macedônia do Norte. A troca põe fim — ao menos por enquanto — no impasse entre essa antiga república da Iugoslávia e a vizinha Grécia. O governo grego se opunha ao uso do nome Macedônia pelo novo país vizinho porque a Grécia tem uma província no norte com o mesmo nome. Por causa desse impasse, a Grécia bloqueou as negociações de adesão de seu vizinho à União Europeia. Depois de negociações, as duas partes chegaram a um acordo.

Disponível em: https://g1.globo.com. Acesso em: 7 nov. 2021 (adaptado).

Para o país originado da antiga Iugoslávia, a mudança de nome é uma estratégia política para', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'criar a moeda própria.', 0),
    (@question_id, 'B', 'proteger a cultura local.', 0),
    (@question_id, 'C', 'subjugar a minoria étnica.', 0),
    (@question_id, 'D', 'expandir o território nacional.', 0),
    (@question_id, 'E', 'intensificar a integração regional.', 1);

-- 1º Dia, questão 53 (48 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 25, NULL, 'A credulidade dos ouvintes aumenta o descaramento do narrador, e o descaramento deste conquista-lhes a credulidade. A eloquência, quando levada a seu patamar mais alto, deixa pouco lugar à razão ou à reflexão, mas, dirigindo-se inteiramente à imaginação e aos afetos, cativa os ouvintes condescendentes e subjuga-lhes o entendimento.

HUME, D. Uma investigação sobre o entendimento humano e sobre os princípios da moral. São Paulo: Edunesp, 2003.

No contexto do século XVIII, o autor propõe uma reflexão radical acerca da arte da eloquência, restringindo-a ao', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'sistema de crenças, conforme a proposta kantiana de objetividade do conhecimento.', 0),
    (@question_id, 'B', 'campo dos absolutos, semelhante ao entendimento medieval dos Universais.', 0),
    (@question_id, 'C', 'domínio da lógica, consoante a compreensão aristotélica nos Analíticos.', 0),
    (@question_id, 'D', 'paradigma da racionalidade, alinhado ao modelo cartesiano de método.', 0),
    (@question_id, 'E', 'âmbito da persuasão, análogo às críticas platônicas aos sofistas.', 1);

-- 1º Dia, questão 54 (52 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 8, NULL, 'Não há consenso em torno do nome dado à pandemia, tendo, desde o seu início, sido chamada de gripe espanhola provavelmente por causa da desinformação em torno da notícia sobre sua origem. Uma das hipóteses deriva da neutralidade espanhola na Primeira Guerra Mundial e a consequente liberdade de imprensa naquele país, maior do que nos demais países envolvidos no conflito. Hoje existe o consenso entre virologistas e epidemiologistas de que o vírus da gripe não se originou na Espanha. Entretanto, dificilmente essa pandemia deixará de ser conhecida como gripe espanhola.

KLAJMAN, C. A gripe sob a ótica da história ecológica. História Revista, n. 3, set.-dez. 2015 (adaptado).

De acordo com o texto, a denominação recebida pela pandemia do começo do século XX foi determinada pelo(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'precariedade dos conhecimentos da medicina militar.', 0),
    (@question_id, 'B', 'retaliação da tríplice aliança aos soldados desertores.', 0),
    (@question_id, 'C', 'controle dos relatos oriundos de campos de batalha.', 1),
    (@question_id, 'D', 'emprego de armas biológicas em confrontos transnacionais.', 0),
    (@question_id, 'E', 'circulação de refugiados contaminados em áreas conflagradas.', 0);

-- 1º Dia, questão 55 (53 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 28, NULL, 'TEXTO I

A partir do século IV, com os imperadores ditos cristãos, o teatro e a dança foram condenados. O batismo era recusado aos que atuavam no circo ou na pantomima. E em 398, no Concílio de Cartago, os que iam ao teatro nos dias santos foram excomungados.

DINIZ, T.; SANTOS, G. História da dança. Disponível em: www.uel.br. Acesso em: 18 out. 2023.

TEXTO II

A dança no ato litúrgico cumpre tanto um papel de adoração quanto mercadológico. Não à toa há aceitação, expansão e investimento nessa prática percebida massivamente pela ampla divulgação principalmente nas redes sociais. A expansão e ampliação por via de festivais, aulas, cursos, palestras etc. consolida a criação de um mercado específico para esse público.

SANTOS, Z. Dança gospel. Salvador: UFBA, 2021.

A percepção sobre a dança e a cultura corporal apresenta aspectos relacionados, respectivamente, a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'segregação social e intolerância eclesiástica.', 0),
    (@question_id, 'B', 'rituais eucarísticos e sacramentos da Igreja.', 0),
    (@question_id, 'C', 'transe individual e progresso intelectual.', 0),
    (@question_id, 'D', 'penitência pessoal e juramento coletivo.', 0),
    (@question_id, 'E', 'dogmatismo religioso e adesão de fiéis.', 1);

-- 1º Dia, questão 56 (54 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 15, NULL, 'A ideia de êxodo urbano assume ares caricaturais. Pega a mais reduzida parte da pirâmide social e projeta para todos seus desejos defensivos. Se o êxodo urbano for compreendido como aquisição de uma segunda residência, então não estamos, de fato, falando de êxodo, mas da reversão de um excedente de renda de uma pequena fração da elite para as franjas metropolitanas. Se o êxodo urbano for compreendido como retorno aos municípios menos povoados, então não estamos, de fato, falando de êxodo urbano, mas de um movimento de migração de pessoas cuja estabilidade no emprego e a elevada renda lhes permitem simular, nas ilhas urbanas do interior agropecuário, a vida urbana metropolitana.

ARRAIS, T. A. O distópico êxodo urbano. Revista e, n. 11, maio 2021 (adaptado).

A crítica apresentada no texto evidencia uma dinâmica socioespacial marcada pela', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'valorização de tradições rurais.', 0),
    (@question_id, 'B', 'redução de plantações agrícolas.', 0),
    (@question_id, 'C', 'estagnação de atividades comerciais.', 0),
    (@question_id, 'D', 'precariedade de infraestruturas rodoviárias.', 0),
    (@question_id, 'E', 'seletividade de deslocamentos populacionais.', 1);

-- 1º Dia, questão 57 (55 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 22, NULL, 'A “invenção” dessa nova anatomia política não deve ser entendida como uma descoberta súbita. Mas como uma multiplicidade de processos muitas vezes mínimos, de origens diferentes, de localizações esparsas, que se recordam, que se repetem, ou se imitam, apoiam-se uns sobre os outros e esboçam aos poucos a fachada de um método geral. Encontramo-los em funcionamento nos colégios, muito cedo; mais tarde, nas escolas primárias, no espaço hospitalar e na organização militar.

FOUCAULT, M. Vigiar e punir. Petrópolis: Vozes, 2011.

O texto indica o seguinte aspecto da disciplina como ferramenta política:', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Expansão das técnicas de suplício.', 0),
    (@question_id, 'B', 'Judicialização das relações de poder.', 0),
    (@question_id, 'C', 'Dissolução das distinções de nobreza.', 0),
    (@question_id, 'D', 'Capilarização das práticas de controle.', 1),
    (@question_id, 'E', 'Espetacularização das medidas de penitência.', 0);

-- 1º Dia, questão 58 (56 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 13, NULL, 'Por séculos, o Rio Reno tem sido uma rota de navegação confiável, ajudando a criar gigantes fabris ao longo de suas margens. No entanto, esses dias parecem estar no fim, pois o nível da água tem diminuído regularmente, o que impede a navegação fluvial do final do verão até o outono. Isso mostra como a crise climática atinge as economias dos países ricos.

Disponível em: www.estadao.com.br. Acesso em: 10 ago. 2023 (adaptado).

Qual é o efeito econômico do problema ambiental apresentado no texto?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Supressão da extração de minério.', 0),
    (@question_id, 'B', 'Intensificação da atividade de pesca.', 0),
    (@question_id, 'C', 'Encarecimento da logística de transporte.', 1),
    (@question_id, 'D', 'Inviabilização da agricultura de subsistência.', 0),
    (@question_id, 'E', 'Sucateamento da indústria de transformação.', 0);

-- 1º Dia, questão 60 (50 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 22, NULL, 'O direito não é a justiça. O direito é o elemento do cálculo, é justo que haja um direito, mas a justiça é incalculável, ela exige que se calcule o incalculável; e as experiências aporéticas são experiências tão improváveis quanto necessárias da justiça, isto é, são momentos em que a decisão entre o justo e o injusto nunca é garantida por uma regra.

DERRIDA, J. Força de lei. São Paulo: Martins Fontes, 2010 (adaptado).

De acordo com o texto, ainda que estejam em desconformidade com o ordenamento jurídico, são exemplos de ação justa:', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Casos de desobediência civil.', 1),
    (@question_id, 'B', 'Repressões do aparato estatal.', 0),
    (@question_id, 'C', 'Conflitos de natureza intercontinental.', 0),
    (@question_id, 'D', 'Manifestações do movimento sindical.', 0),
    (@question_id, 'E', 'Mobilizações de agremiações estudantis.', 0);

-- 1º Dia, questão 61 (51 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 27, NULL, 'Sinal fechado

Olá, como vai?
Eu vou indo e você, tudo bem?
Tudo bem, eu vou indo, correndo
Pegar meu lugar no futuro, e você?
Tudo bem, eu vou indo em busca
De um sono tranquilo, quem sabe?
Quanto tempo...
Pois é, quanto tempo...
Me perdoe a pressa
É a alma dos nossos negócios...
Oh, não tem de que
Eu também só ando a cem
Quando é que você telefona?
Precisamos nos ver por aí
Pra semana, prometo,
Talvez nos vejamos, quem sabe?
Quanto tempo...

Pois é, quanto tempo...
Tanta coisa que eu tinha a dizer
Mas eu sumi na poeira das ruas
Eu também tenho algo a dizer
Mas me foge a lembrança
Por favor, telefone, eu preciso beber
Alguma coisa rapidamente
Pra semana...
O sinal...
Eu procuro você...
Vai abrir! Vai abrir!
Prometo, não esqueço
Por favor, não esqueça
Não esqueço, não esqueço
Adeus...

PAULINHO DA VIOLA. Foi um rio que passou em minha vida. Rio de Janeiro: Odeon, 1970.

A letra da canção apresenta a permanência de uma situação da vida cotidiana ao destacar a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'diminuição do comportamento competitivo.', 0),
    (@question_id, 'B', 'importância da memória coletiva.', 0),
    (@question_id, 'C', 'redução da mobilidade urbana.', 0),
    (@question_id, 'D', 'efemeridade dos vínculos de afetividade.', 1),
    (@question_id, 'E', 'obsolescência dos meios de comunicação.', 0);

-- 1º Dia, questão 62 (70 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 11, NULL, 'Nos Estados Unidos, um hotel perto de um parque foi engolido por uma enorme cratera. E não é a primeira vez que esse tipo de incidente acontece na Flórida. Em minutos, uma ala inteira do hotel era engolida pela cratera de 30 metros de diâmetro e cinco de profundidade. O diretor do resort em Clermont, perto dos parques temáticos da Flórida, diz que o prédio tinha 15 anos, e que até então o solo nunca havia apresentado problemas. Os afundamentos são um fenômeno comum na Flórida por causa das formações de calcário e argila no subsolo, mas às vezes chuvas fortes, ou a drenagem do solo para construção, acabam contribuindo para o surgimento desses buracos.

Disponível em: http://g1.globo.com. Acesso em: 10 dez. 2024 (adaptado).

A situação de desmoronamento do solo descrita no texto origina-se da', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'cristalização da estrutura geológica.', 0),
    (@question_id, 'B', 'ação do intemperismo químico.', 1),
    (@question_id, 'C', 'recomposição da mata ciliar.', 0),
    (@question_id, 'D', 'acumulação de sedimentos orgânicos.', 0),
    (@question_id, 'E', 'impermeabilização da superfície ocupada.', 0);

-- 1º Dia, questão 63 (71 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 9, NULL, 'Moradores de Berlim protestaram contra a demolição de um trecho do muro que dividiu a cidade durante quase três décadas. Tratado como patrimônio cultural e histórico da cidade, o East Side Gallery, repleto de grafites emblemáticos, está na mira de uma construtora que pretende levantar um condomínio de luxo às margens do Rio Spree, que corta a cidade.

ALMEIDA, R. Alemães impedem demolição de último trecho original do muro de Berlim. Disponível em: http://operamundi.uol.com.br. Acesso em: 2 fev. 2021 (adaptado).

A demolição do símbolo histórico mencionado representa uma', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'violação da memória coletiva.', 1),
    (@question_id, 'B', 'alteração das fronteiras políticas.', 0),
    (@question_id, 'C', 'adesão à arquitetura neoclássica.', 0),
    (@question_id, 'D', 'negação das influências orientais.', 0),
    (@question_id, 'E', 'reorganização da mobilidade urbana.', 0);

-- 1º Dia, questão 64 (72 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 19, NULL, 'TEXTO I

Em conjunto: todo e não todo, unido e separado, em consonância e em dissonância. De todos um e de um todos. (Fragmento B10)

TEXTO II

Deus é dia-noite, inverno-verão, guerra-paz, saciedade-fome. (Fragmento B67)

Pré-socráticos. São Paulo: Nova Cultural, 1996.

A característica do pensamento do filósofo Heráclito, registrada nos fragmentos mencionados, é a ênfase na', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'qualidade imperecível do mundo.', 0),
    (@question_id, 'B', 'degradação material da natureza.', 0),
    (@question_id, 'C', 'imobilidade imanente do universo.', 0),
    (@question_id, 'D', 'distribuição dicotômica do cosmos.', 1),
    (@question_id, 'E', 'desordem incontornável das coisas.', 0);

-- 1º Dia, questão 65 (73 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 1, NULL, 'Entre esses preconceitos estava o canibalismo. A prática não era, porém, uma mentira, uma invenção europeia, mas um ritual controlado por regras. Entre os tupis, por exemplo, os guerreiros se sentiam honrados quando morriam em um banquete canibal. Para os europeus, no entanto, comer carne humana era abominável, pois nem mesmo os leões ingeriam seus semelhantes. Portanto, para os conquistadores, o canibalismo era sinônimo de barbarismo e da incapacidade de se autogovernar.

RAMINELLI, R. Canibalismo para alemão ver. In: FIGUEIREDO, L. (Org.). História do Brasil para ocupados. Rio de Janeiro: Casa da Palavra, 2013 (adaptado).

No texto, europeus e ameríndios atribuíram à prática relatada, respectivamente, o significado de', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'selvageria — empoderamento.', 1),
    (@question_id, 'B', 'impetuosidade — resistência.', 0),
    (@question_id, 'C', 'fanatismo — humilhação.', 0),
    (@question_id, 'D', 'intolerância — violência.', 0),
    (@question_id, 'E', 'repressão — justiça.', 0);

-- 1º Dia, questão 66 (74 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 31, NULL, 'Fragmentada em pequenos grupos, fragilizada pela ausência de algum tipo de organização ampla, tendo que carregar o peso do preconceito racial que se transfere do negro para a cultura negra, a religião dos orixás tem poucas chances de se sair melhor na competição — desigual — com outras religiões. Silenciosamente, assistimos hoje a um verdadeiro massacre das religiões afro-brasileiras.

PRANDI, R. O Brasil com axé. Estudos Avançados, n. 52, dez. 2004.

No processo de invisibilização dos cultos afro-brasileiros, o texto associa os seguintes elementos essenciais:', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Ausência de soluções partidárias e rejeição da laicidade.', 0),
    (@question_id, 'B', 'Precariedade de coesão representativa e recusa da alteridade.', 1),
    (@question_id, 'C', 'Insuficiência de estruturas decisórias e exclusão da dialogicidade.', 0),
    (@question_id, 'D', 'Inexistência de direitos constitucionais e negação do ecumenismo.', 0),
    (@question_id, 'E', 'Efemeridade das relações políticas e debilidade do pertencimento étnico.', 0);

-- 1º Dia, questão 67 (75 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 16, NULL, 'A partir do século XIX e, principalmente, no século XX, iniciou-se o uso de produtos sintéticos na agricultura. No Brasil, a partir da década de 1960 e, posteriormente, nos anos 1970, os inseticidas ampliam sua fatia no mercado dos defensivos agrícolas.

TROPPMAIR, H. Biogeografia e meio ambiente. Rio de Janeiro: TB, 2012 (adaptado).

Nos espaços agrícolas, a mudança técnica descrita provocou a diminuição da', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'desagregação de camadas pedológicas.', 0),
    (@question_id, 'B', 'diversidade de polinizadores naturais.', 1),
    (@question_id, 'C', 'contaminação de lençóis freáticos.', 0),
    (@question_id, 'D', 'produtividade da terra cultivada.', 0),
    (@question_id, 'E', 'ocorrência da erosão laminar.', 0);

-- 1º Dia, questão 68 (76 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 30, NULL, 'Os direitos do homem constituem uma classe variável, como a história destes últimos séculos demonstra suficientemente. O elenco dos direitos do homem se modificou, e continua a se modificar, com a mudança das condições históricas, ou seja, dos carecimentos e dos interesses, das classes no poder, dos meios disponíveis para a realização dos mesmos, das transformações técnicas. Direitos que foram declarados absolutos no final do século XVIII foram submetidos a radicais limitações nas declarações contemporâneas; direitos que as declarações do século XVIII nem sequer mencionavam, como os direitos sociais, são agora proclamados com grande ostentação nas recentes declarações.

BOBBIO, N. A era dos direitos. Rio de Janeiro: Elsevier, 2004.

Os argumentos apresentados no texto sustentam que os direitos humanos são variáveis porque os considera como', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'fenômenos espontâneos.', 0),
    (@question_id, 'B', 'conquistas atemporais.', 0),
    (@question_id, 'C', 'convenções coletivas.', 1),
    (@question_id, 'D', 'resquícios religiosos.', 0),
    (@question_id, 'E', 'imposições políticas.', 0);

-- 1º Dia, questão 70 (78 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 1, NULL, 'A Roda dos Expostos de Salvador (Bahia) data de 1726 e foi uma das instituições implantadas com o objetivo de acolher e prover um encaminhamento para a criança recém-nascida abandonada. Essa assistência fundamentava-se em práticas caritativas, paternalistas e imediatistas que visavam ao cuidado básico da criança e, especialmente, à salvação da sua alma, via batismo, devido à alta mortalidade.

BORRIONE, R.; CHAVES, A. M. Análise documental e contexto de desenvolvimento. Disponível em: www.scielo.br. Acesso em: 15 ago. 2013.

A instituição apresentada no texto justificava-se pelo(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'defesa de direitos humanos.', 0),
    (@question_id, 'B', 'preceito de natureza sociorreligiosa.', 1),
    (@question_id, 'C', 'reconhecimento de vínculos familiares.', 0),
    (@question_id, 'D', 'discurso de cunho técnico-científico.', 0),
    (@question_id, 'E', 'ideologia de caráter abolicionista.', 0);

-- 1º Dia, questão 71 (79 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 21, NULL, 'A missão dos governantes consiste em promover a felicidade da sociedade, punindo e recompensando. A parte da missão de governo que consiste em punir constitui mais particularmente o objeto da lei penal. A obrigatoriedade ou necessidade de punir uma ação é proporcional à medida que tal ação tende a perturbar a felicidade e à medida que a tendência do referido ato é perniciosa. A felicidade consiste naquilo que já vimos, ou seja, em desfrutar prazeres e em estar isento de dores.

BENTHAM, J. Uma introdução aos princípios da moral e da legislação. São Paulo: Abril Cultural, 1974 (adaptado).

Qual perspectiva de justiça emerge da relação, estabelecida no texto, entre punição e felicidade?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Aplicação de meios para atingir um fim.', 1),
    (@question_id, 'B', 'Imposição de regras para estabelecer um dever.', 0),
    (@question_id, 'C', 'Sobreposição de princípios para fundamentar um direito.', 0),
    (@question_id, 'D', 'Criação de parâmetros para reconhecer uma prescrição.', 0),
    (@question_id, 'E', 'Elaboração de convenções para referendar um costume.', 0);

-- 1º Dia, questão 72 (80 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 15, NULL, 'Cidade sustentável é o assentamento humano constituído por uma sociedade com consciência de seu papel de agente transformador dos espaços e cuja relação não se dá pela razão natureza-objeto, e sim por uma ação sinérgica entre prudência ecológica, eficiência energética e equidade socioespacial.

ROMERO, M. Frentes do urbano para a construção de indicadores de sustentabilidade intraurbana. Paranoá, n. 4, nov. 2007.

Uma medida pública que contradiz o modelo de cidade exposto no texto é a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'coleta seletiva de lixo.', 0),
    (@question_id, 'B', 'reutilização das águas pluviais.', 0),
    (@question_id, 'C', 'canalização dos cursos hídricos.', 1),
    (@question_id, 'D', 'arborização das avenidas centrais.', 0),
    (@question_id, 'E', 'educação ambiental da comunidade.', 0);

-- 1º Dia, questão 73 (62 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, NULL, 4, 'Para que a utopia nasça, é preciso duas condições. A primeira é a forte sensação de que o mundo não está funcionando adequadamente e deve ter seus fundamentos revistos para que se reajuste. A segunda condição é a existência de uma confiança no potencial humano à altura da tarefa de reformar o mundo, a crença de que nós, seres humanos, podemos fazê-lo, sendo capazes de perceber o que está errado com o mundo, o que precisa ser modificado, quais são os pontos problemáticos, e ter força e coragem para extirpá-los.

BAUMAN, Z. apud OLIVEIRA, D. Bauman: para que a utopia renasça, é preciso confiar no potencial humano. Revista Cult, jan. 2017.

De acordo com o autor, qual é a função da utopia no contexto das transformações sociais?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Habilitar memórias afetivas.', 0),
    (@question_id, 'B', 'Apontar alternativas possíveis.', 1),
    (@question_id, 'C', 'Legitimar experiências pretéritas.', 0),
    (@question_id, 'D', 'Perpetuar paradigmas científicos.', 0),
    (@question_id, 'E', 'Empreender soluções conclusivas.', 0);

-- 1º Dia, questão 75 (64 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 4, NULL, 'Em toda a sua carreira política, Getúlio Vargas sempre contara com o seu talento pessoal de persuasão e poder de manipulação. Agora, porém, seus amigos começavam a perceber que ele parecia envelhecido e cansado. A principal figura da oposição, concordavam eles, era o belicoso jornalista Carlos Lacerda. Se ao menos pudessem “removê-lo” do cenário político, talvez Vargas se salvasse da situação. Esses seguidores decidiram tomar o assunto em suas próprias mãos com o atentado da Rua Tonelero.

SKDIMORE, T. Brasil: de Getúlio a Castelo. Rio de Janeiro: Paz e Terra, 2003 (adaptado).

Nesse contexto, a ação dos aliados de Getúlio Vargas teve como consequência imediata o(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'intensificação dos ataques do grupo udenista.', 1),
    (@question_id, 'B', 'intervenção dos sindicatos no conflito partidário.', 0),
    (@question_id, 'C', 'mobilização da sociedade na defesa do governo.', 0),
    (@question_id, 'D', 'abandono da censura aos meios de comunicação.', 0),
    (@question_id, 'E', 'apoio dos parlamentares aos candidatos oposicionistas.', 0);

-- 1º Dia, questão 76 (65 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 23, NULL, 'O que eu queria ter sido

Um nome para o que sou, importa muito pouco. Importa o que eu gostaria de ser. O que eu gostaria de ser era uma lutadora. Quero dizer, uma pessoa que luta pelo bem dos outros. Isso desde pequena eu quis. Por que foi o destino me levando a escrever o que já escrevi, em vez de também desenvolver em mim a qualidade de lutadora que eu tinha? Em pequena, minha família por brincadeira chamava-me de “a protetora dos animais”. Porque bastava acusarem uma pessoa para eu imediatamente defendê-la. E eu sentia o drama social com tanta intensidade que vivia de coração perplexo diante das grandes injustiças a que são submetidas as chamadas classes menos privilegiadas.

LISPECTOR, C. Aprendendo a viver. Rio de Janeiro: Rocco Digital, 2013.

A reflexão contida no texto faz referência aos pressupostos de uma doutrina ética representada pelo', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'estado eudaimônico.', 0),
    (@question_id, 'B', 'referencial hedonista.', 0),
    (@question_id, 'C', 'pensamento altruísta.', 1),
    (@question_id, 'D', 'movimento antiespecista.', 0),
    (@question_id, 'E', 'comportamento pragmático.', 0);

-- 1º Dia, questão 77 (66 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 22, NULL, 'Independência de pensamento, autonomia e direito à oposição política estão perdendo sua função crítica básica numa sociedade que parece cada vez mais capaz de atender às necessidades dos indivíduos através da forma pela qual é organizada. Nas condições de um padrão de vida crescente, o não conformismo com o próprio sistema parece socialmente inútil, principalmente quando acarreta desvantagens econômicas e políticas tangíveis e ameaça o funcionamento suave do todo.

MARCUSE, H. Ideologia da sociedade industrial. Rio de Janeiro: Zahar, 1969.

Qual é a característica da sociedade industrial do século XX apresentada no texto?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Gestão integrada.', 0),
    (@question_id, 'B', 'Relativismo moral.', 0),
    (@question_id, 'C', 'Desobediência civil.', 0),
    (@question_id, 'D', 'Realidade unidimensional.', 1),
    (@question_id, 'E', 'Desenvolvimento tecnológico.', 0);

-- 1º Dia, questão 78 (67 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 16, NULL, 'Do ponto de vista administrativo e judicial, a desapropriação da fazenda Cabaceiras, localizada na região sul do estado do Pará, é a primeira da história motivada pelo desrespeito à função social de um imóvel rural, como prevê a Constituição Federal de 1988, em decorrência da exploração de “trabalho em condições análogas à escravidão”. Na realidade, essa desapropriação não se deu apenas pela ocorrência de relações de trabalho extremamente degradantes, mas também pelas graves infrações ambientais verificadas na fazenda — o que também configura um ataque à função social da terra, de acordo com os preceitos constitucionais.

BARROS, C. J. A conquista da fazenda Cabaceiras. Disponível em: www.agb.org.br. Acesso em: 15 maio 2023.

O texto considera a promoção da justiça como um mecanismo de', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'manutenção da ordem agrária.', 0),
    (@question_id, 'B', 'legitimação da propriedade privada.', 0),
    (@question_id, 'C', 'hegemonia das classes dominantes.', 0),
    (@question_id, 'D', 'revisão das desigualdades econômicas.', 1),
    (@question_id, 'E', 'homogeneização da distribuição fundiária.', 0);

-- 1º Dia, questão 80 (69 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 6, NULL, 'A difusão pelos cônegos do ideal apostólico, bem como a influência dos eremitas e dos pregadores errantes que na sua esteira propagavam temas evangélicos, contribuíram para fazer nascer, entre os fiéis, o desejo de se erguerem ao nível espiritual do clero e de obterem a sua salvação, sem que para isso tivessem de renunciar ao seu estado. Pela primeira vez, a Igreja entreabria as portas da graça em benefício da totalidade dos fiéis, colocando, como única condição, a sua partida para o Oriente, a fim de aí lutarem contra os inimigos de Cristo.

VAUCHEZ, A. A espiritualidade da Idade Média Ocidental, séc. VIII-XIII. Lisboa: Estampa, 1995.

Conforme o texto, no imaginário dos fiéis cristãos do período medieval, a salvação era alcançada por meio das', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'palavras escritas.', 0),
    (@question_id, 'B', 'práticas litúrgicas.', 0),
    (@question_id, 'C', 'ações voluntárias.', 1),
    (@question_id, 'D', 'ideias messiânicas.', 0),
    (@question_id, 'E', 'celebrações coletivas.', 0);

-- 1º Dia, questão 81 (86 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, NULL, 2, 'Pesquisa pioneira no Brasil propõe o uso de energia do solo para climatizar edifícios

Pela primeira vez na história, o Brasil terá um prédio que usa energia do solo para climatizar seus ambientes. Uma pesquisa inovadora avaliou o uso das fundações de edifícios como meio para a troca de energia térmica entre o prédio e o subsolo. A energia geotérmica é aquela encontrada dentro da crosta terrestre, seja no solo, nas rochas ou mesmo na água, sendo identificada pela temperatura. Essa energia pode ser transferida para a superfície por processos de troca térmica a partir das fundações da edificação.

Disponível em: https://jornal.usp.br. Acesso em: 7 nov. 2021 (adaptado).

Para o sistema elétrico brasileiro, a expansão de tecnologias como a descrita no texto representa uma tendência de', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'homogeneização da oferta da matriz produtiva.', 0),
    (@question_id, 'B', 'ampliação do consumo em unidades residenciais.', 0),
    (@question_id, 'C', 'redução da pressão sobre as usinas hidrelétricas.', 1),
    (@question_id, 'D', 'estagnação do consumo em unidades industriais.', 0),
    (@question_id, 'E', 'superação da necessidade de recursos renováveis.', 0);

-- 1º Dia, questão 82 (87 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, NULL, 1, 'Durante a realeza, e nos primeiros anos republicanos, as leis eram transmitidas oralmente de uma geração para outra. A ausência de uma legislação escrita permitia aos patrícios manipular a justiça conforme seus interesses.

COULANGES, F. A cidade antiga. São Paulo: Martins Fontes, 2000.

A conjuntura sociopolítica da Roma Antiga, conforme apresentada no texto, foi contestada pelos', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'monarcas, que queriam expandir o código jurídico.', 0),
    (@question_id, 'B', 'plebeus, que almejavam participar da vida política.', 1),
    (@question_id, 'C', 'nobres, que desejavam superar a relação de dependência.', 0),
    (@question_id, 'D', 'camponeses, que aspiravam diminuir a jornada de trabalho.', 0),
    (@question_id, 'E', 'estrangeiros, que ambicionavam acessar os espaços públicos.', 0);

-- 1º Dia, questão 83 (88 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 30, NULL, 'Os povos indígenas têm o direito de manter, controlar, proteger e desenvolver seu patrimônio cultural, seus conhecimentos do passado, suas expressões culturais e as manifestações de suas ciências, tecnologias e culturas, compreendidos os recursos humanos e genéticos, as sementes, os medicamentos, o conhecimento das propriedades da fauna e da flora, as transmissões orais, as literaturas, os desenhos, os esportes, os jogos e as artes visuais e interpretativas.

Declaração das Nações Unidas sobre os Direitos dos Povos Indígenas. 107ª Sessão Plenária, 13 de setembro de 2007. Disponível em: www.un.org. Acesso em: 18 abr. 2015 (adaptado).

Os direitos reconhecidos no texto representam a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'multiplicidade dos troncos linguísticos.', 0),
    (@question_id, 'B', 'ampliação das influências externas.', 0),
    (@question_id, 'C', 'complexidade dos saberes tradicionais.', 1),
    (@question_id, 'D', 'sofisticação das práticas de sobrevivência.', 0),
    (@question_id, 'E', 'diversificação da economia de subsistência.', 0);

-- 1º Dia, questão 84 (89 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, NULL, 1, 'TEXTO I

Eu escolhi Bezalel e lhe dei competência e habilidade para fazer todo tipo de trabalho artístico; para fazer desenhos e trabalhar em ouro, prata e bronze; para lapidar e montar pedras preciosas; para entalhar madeira; e para fazer todo tipo de artesanato.

BÍBLIA. Êxodo 31, 2-5. São Paulo: Paulinas, 2005.

TEXTO II

Moisés foi educado em toda a sabedoria dos egípcios e veio a ser poderoso em palavras e obras.

BÍBLIA. Atos 7, 22. São Paulo: Paulinas, 2005.

TEXTO III

Se conhecêsseis a ciência certa, logo renunciarias à ostentação.

ALCORÃO. Sura 102, 5. São Paulo: Tangará, 1975.

Escritos em temporalidades diferentes (Antiguidade e Idade Média), os textos sagrados aproximam-se ao mostrar o papel da ciência para o(a)', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'emergência das universidades e escolas confessionais.', 0),
    (@question_id, 'B', 'crescimento religioso e movimentos sectários.', 0),
    (@question_id, 'C', 'avanço da intolerância e rivalidades nacionais.', 0),
    (@question_id, 'D', 'preservação das tradições e rituais místicos.', 0),
    (@question_id, 'E', 'desenvolvimento social e práticas laborais.', 1);

-- 1º Dia, questão 85 (90 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, NULL, 1, 'Em 1914, uma expedição estudantil saiu da Rússia em direção à América do Sul, sendo considerada a segunda campanha científica da Rússia no continente depois da longa viagem do barão Langsdorff pelo interior do Brasil na primeira metade do século XIX. O empreendimento foi enviado pelo Museu de Antropologia e Etnografia de São Petersburgo e integrado por cinco jovens cientistas, sendo dois zoólogos, dois etnógrafos e um antropólogo, cujo objetivo era a coleta de material de valor biológico e etnográfico para compor coleções nas instituições que participaram de seu financiamento. A expedição passou por países como Brasil, Paraguai e Argentina, resultando em amplo material manuscrito e algumas publicações, além dos objetos coletados.

CARNEIRO, L. A. F. A Rússia no Brasil do início do século XX. Rio de Janeiro: Casa de Oswaldo Cruz, 2015 (adaptado).

Além do significado científico, o evento mencionado conectava-se a um projeto nacionalista de caráter', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'imperialista e colonizador.', 1),
    (@question_id, 'B', 'militar e disciplinador.', 0),
    (@question_id, 'C', 'lúdico e filantrópico.', 0),
    (@question_id, 'D', 'mercantil e predatório.', 0),
    (@question_id, 'E', 'religioso e humanitário.', 0);

-- 1º Dia, questão 86 (81 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 30, NULL, 'Em decorrência da naturalização da inferioridade social da mulher e da concepção de justiça do Estado, baseada na igualdade abstratamente concebida, torna-se possível convencer o Estado burguês a conceber e implementar políticas públicas cujo conteúdo se define pela discriminação positiva de mulheres, embora isso aparentemente seja paradoxal.

SAFFIOTI, H.; ALMEIDA, S. S. Violência de gênero, poder e impotência. Rio de Janeiro: Revinter, 1995 (adaptado).

No contexto atual, qual medida atende ao tipo de política pública abordada no texto?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Ampliação do sufrágio universal.', 0),
    (@question_id, 'B', 'Diminuição da jornada de trabalho.', 0),
    (@question_id, 'C', 'Extinção do crime de feminicídio.', 0),
    (@question_id, 'D', 'Redução da licença-maternidade.', 0),
    (@question_id, 'E', 'Criação de delegacia especializada.', 1);

-- 1º Dia, questão 87 (82 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 19, NULL, 'A cidade justa é governada pelos filósofos, administrada pelos cientistas, protegida pelos guerreiros e mantida pelos produtores. Cada classe cumprirá sua função para o bem da pólis, racionalmente dirigida pelos filósofos. Em contrapartida, a cidade injusta é aquela onde o governo está nas mãos dos proprietários — que não pensam no bem comum da pólis e lutarão por interesses econômicos particulares.

CHAUÍ, M. Convite à filosofia. São Paulo: Ática, 2000.

O texto apresenta a estrutura de governo da cidade ideal pensada por Platão, que postula uma indissociabilidade entre', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'cognoscência e relação intersubjetiva.', 0),
    (@question_id, 'B', 'mitologia e teorias cosmogônicas.', 0),
    (@question_id, 'C', 'cidadania e primazia da retórica.', 0),
    (@question_id, 'D', 'moralidade e virtudes cardeais.', 0),
    (@question_id, 'E', 'ética e exercício do poder.', 1);

-- 1º Dia, questão 88 (83 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 1, NULL, 'A Impressão Régia do Rio de Janeiro foi criada pelo decreto de 13 de maio de 1808 para dar continuidade à nova sede do império português. A nova tipografia é criada em um momento no qual o projeto de reforma do império se transforma em um projeto de construção de um novo império português na América. Frente às tensões políticas que essa nova situação criava, a tipografia atuou na legitimação e sustentação daquele projeto político.

BARRA, S. H. S. A Impressão Régia do Rio de Janeiro e a colonização dos sertões na construção do novo império português na América (1808-1822). Topoi, n. 31, jul.-dez. 2015 (adaptado).

A função política da tecnologia mencionada no texto favoreceu a nova sede do Império português por', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'estabelecer as normativas ideológicas aos periódicos.', 0),
    (@question_id, 'B', 'propagar as diretrizes administrativas às capitanias.', 0),
    (@question_id, 'C', 'divulgar as conquistas às metrópoles europeias.', 0),
    (@question_id, 'D', 'integrar as regiões territoriais ao poder central.', 1),
    (@question_id, 'E', 'difundir as publicações às elites lusitanas.', 0);

-- 1º Dia, questão 89 (84 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 14, NULL, 'Com o objetivo de auxiliar a prevenção e o combate a incêndios no Cerrado, o Centro de Sensoriamento Remoto da Universidade Federal de Minas Gerais (UFMG) desenvolveu um modelo computacional capaz de prever o comportamento do fogo. Essa tecnologia integra o projeto Monitoramento do Cerrado, do qual também participa o Instituto Nacional de Pesquisas Espaciais (Inpe). O instituto produz e processa dados de satélite sobre focos de calor para determinar a localização de incêndios no bioma. Esses focos são o ponto de ignição para o modelo desenvolvido na UFMG: com base em outros dados, como relevo, ventos, umidade e biomassa seca, a ferramenta estima as áreas com maior risco de incêndio e prevê a intensidade e a direção de propagação do fogo no Cerrado brasileiro.

LAGES, L. Tecnologia antiqueimada. Revista Minas Faz Ciência, n. 84, dez. 2020 – jan.-fev. 2021 (adaptado).

Em relação à causa ambiental, o uso da tecnologia mencionada indica a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'influência da produção orgânica.', 0),
    (@question_id, 'B', 'relevância da infraestrutura viária.', 0),
    (@question_id, 'C', 'significância da cosmologia nativa.', 0),
    (@question_id, 'D', 'importância de pesquisas acadêmicas.', 1),
    (@question_id, 'E', 'interferência de interesses estrangeiros.', 0);

-- 1º Dia, questão 90 (85 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_1, 28, NULL, 'TEXTO I

O cinema constituía um encanto indefinível: além do entretenimento delicioso das vidas alheias por meio dos filmes, o pretexto amável para os encontros de olhos, para a mostra dos vestidos, para as tagarelices com as vizinhas de cadeiras.

SETTE, M. Anquinhas e bernardas. São Paulo: Livraria Martins, 1940 (adaptado).

TEXTO II

O pouco caso na seleção dos filmes para serem exibidos nos espetáculos diurnos tem sido causa de inúmeros dissabores sofridos pelos progenitores que levam os seus filhos ao cinema para recreá-los. Em toda parte, tanto nas casas de famílias como nos colégios, só se ouve a petizada falar em fitas, em artistas cinematográficos, nas proezas de William Hart ou nas façanhas de Mutt.

O cinema e a criança. Diário de Pernambuco. 29 ago. 1925 (adaptado).

Publicados na primeira metade do século XX, os textos apresentam', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'conclusões semelhantes sobre o mesmo suporte midiático.', 0),
    (@question_id, 'B', 'argumentos contrários sobre o mesmo tipo de diversão.', 1),
    (@question_id, 'C', 'discursos contraditórios sobre a mesma didática escolar.', 0),
    (@question_id, 'D', 'regras teológicas acerca de um mesmo recurso pedagógico.', 0),
    (@question_id, 'E', 'justificativas idênticas acerca de um mesmo aspecto cultural.', 0);

-- ENEM 2025 — 2º Dia ------------------------------------------------------

-- 2º Dia, questão 91 (99 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 36, NULL, 'O monstro de gila, um lagarto encontrado em um deserto dos Estados Unidos, apresenta adaptações à falta de alimento nesse ambiente. Esse lagarto possui um hormônio que controla os níveis de açúcar em seu próprio sangue. Foi observado que esse hormônio também controla os níveis de açúcar no sangue de pessoas diabéticas.

RUSSELL, C. Ozempic e outros remédios inspirados em veneno de animais. Disponível em: www.bbc.com. Acesso em: 6 dez. 2024 (adaptado).

Considerando que animais de um mesmo grupo taxonômico podem apresentar características adaptativas semelhantes em ambientes similares, onde seria mais provável encontrar lagartos com essas características no território brasileiro?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Cerrado.', 0),
    (@question_id, 'B', 'Pampas.', 0),
    (@question_id, 'C', 'Caatinga.', 1),
    (@question_id, 'D', 'Restinga.', 0),
    (@question_id, 'E', 'Pantanal.', 0);

-- 2º Dia, questão 92 (100 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 59, NULL, 'Existe um processo de purificação de água em que são removidos os sais dissolvidos. A água que passa por esse processo é muito utilizada em laboratórios de química, em indústrias (como solvente), em baterias de carros etc. Entretanto, esse tipo de água não é adequado para ingestão, pois pode causar problemas de saúde, como carência iônica e diarreia.

Essa água é chamada de', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'dura.', 0),
    (@question_id, 'B', 'pesada.', 0),
    (@question_id, 'C', 'sanitária.', 0),
    (@question_id, 'D', 'destilada.', 1),
    (@question_id, 'E', 'oxigenada.', 0);

-- 2º Dia, questão 93 (101 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 37, NULL, 'Por que os olhos ficam vermelhos em algumas fotografias?

Em fotos tiradas com câmeras fotográficas antigas, por vezes as pessoas aparecem com os olhos vermelhos. Isso ocorre porque a luz do flash da câmera incide diretamente no globo ocular, sendo refletida por uma região repleta de vasos sanguíneos.

Disponível em: www.uol.com.br. Acesso em: 14 jun. 2017 (adaptado).

Esse efeito é mais comum à noite ou em lugares pouco iluminados porque, com a pupila', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'dilatada, chega mais luz à retina.', 1),
    (@question_id, 'B', 'retraída, chega mais luz vermelha à retina.', 0),
    (@question_id, 'C', 'retraída, chega mais luz vermelha aos bastonetes.', 0),
    (@question_id, 'D', 'retraída, chegam menos luzes azul e verde aos cones.', 0),
    (@question_id, 'E', 'dilatada, chegam menos luzes azul e verde aos bastonetes.', 0);

-- 2º Dia, questão 94 (102 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 39, NULL, 'O sashimi (filé de peixe cru) de baiacu é uma iguaria muito apreciada no Japão. Entretanto, sua ingestão pode causar a morte por parada respiratória, pois esse peixe contém uma potente neurotoxina termoestável, a tetrodotoxina, que é produzida e armazenada nas gônadas e vísceras.

Que ação poderia evitar essa intoxicação?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Criar os peixes em cativeiro.', 0),
    (@question_id, 'B', 'Realizar a pesca com redes.', 0),
    (@question_id, 'C', 'Consumir peixes cozidos ou fritos.', 0),
    (@question_id, 'D', 'Preparar o peixe em condições adequadas de higiene.', 0),
    (@question_id, 'E', 'Manusear o peixe sem provocar o rompimento dos órgãos internos.', 1);

-- 2º Dia, questão 95 (103 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 32, NULL, 'A deficiência da enzima lipase ácida causa uma doença em que o portador apresenta células que não degradam colesterol esterificado nem triglicerídeos, resultando no depósito desses compostos em diversos órgãos, principalmente no fígado.

ANDERSON, R. A. et al. In Situ Localization of the Genetic Locus Encoding [...]. Genomics, n. 1, jan. 1993 (adaptado).

Essa doença resulta da insuficiência funcional de qual estrutura celular?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Lisossomos.', 1),
    (@question_id, 'B', 'Ribossomos.', 0),
    (@question_id, 'C', 'Mitocôndrias.', 0),
    (@question_id, 'D', 'Peroxissomos.', 0),
    (@question_id, 'E', 'Retículo endoplasmático liso.', 0);

-- 2º Dia, questão 96 (104 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 36, NULL, 'A quantidade de CO₂ na atmosfera da Terra aumentou em 50%, e a temperatura está agora cerca de 1,2 °C mais quente em comparação ao século XIX. O ritmo de aumento da temperatura precisa diminuir se quisermos evitar as piores consequências das mudanças climáticas. Cientistas afirmam que o aquecimento global precisa ser mantido em 1,5 °C até o ano 2100. No entanto, a menos que outras ações sejam tomadas, o planeta ainda pode aquecer mais de 2 °C até o final deste século. Na prática, os países precisam enfrentar as mudanças climáticas com ações mitigadoras, que reduzam tanto as emissões quanto os níveis de CO₂ na atmosfera.

Um guia rápido para entender as mudanças climáticas. Disponível em: www.bbc.com. Acesso em: 6 dez. 2021 (adaptado).

Qual ação mitigadora auxilia na remoção desse gás presente na atmosfera, reduzindo seus níveis?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Plantar mais árvores.', 1),
    (@question_id, 'B', 'Instalar mais usinas eólicas.', 0),
    (@question_id, 'C', 'Ampliar o uso de energia solar.', 0),
    (@question_id, 'D', 'Manter os combustíveis fósseis no solo.', 0),
    (@question_id, 'E', 'Produzir menos resíduos sólidos urbanos.', 0);

-- 2º Dia, questão 97 (95 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 46, NULL, 'TEXTO I

As mariposas

As mariposa, quando chega o frio
Fica dando volta em volta da lâmpida pra se esquentá. [sic]

BARBOSA, A. Reviva: Adoniran Barbosa. São Paulo: Som Livre, 2002 (fragmento).

TEXTO II

As mariposas se aproximam das lâmpadas atraídas pela luz, pois, sendo basicamente criaturas noturnas, estão adaptadas a seguir o brilho da lua, em um processo conhecido como orientação transversal. Assim, o que o sambista Adoniran Barbosa, no Texto I, descreve não é a causa, mas sim uma das consequências possíveis dessa aproximação. De fato, o calor gerado pelas lâmpadas, sobretudo as incandescentes, pode aquecer as mariposas.

HERTZBERG, R. Por que as mariposas são tão atraídas por luzes fortes? National Geographic, nov. 2020 (adaptado).

Nesse contexto, o processo de transferência de calor para as mariposas que independe da presença de fluidos é a', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'reflexão.', 0),
    (@question_id, 'B', 'refração.', 0),
    (@question_id, 'C', 'irradiação.', 1),
    (@question_id, 'D', 'dispersão.', 0),
    (@question_id, 'E', 'convecção.', 0);

-- 2º Dia, questão 98 (96 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 36, NULL, 'Os sapinhos-ponta-de-flecha constituem um grupo de espécies encontradas na América Central e do Sul. Seus venenos são obtidos por meio do consumo de algumas formigas e cupins que se alimentam de plantas que contêm esses venenos. Esses anfíbios são usados para envenenar as flechas das zarabatanas dos caçadores nativos. Quando capturados e criados em condições artificiais, ou quando nascidos em cativeiro, não são tóxicos.

BADIO, B. et al. Epibatidine: Discovery and Definition as a Potent Analgesic and Nicotinic Agonist. Med. Chem. Res., n. 4, 1994 (adaptado).

A perda da capacidade de se obter a toxina nos nascidos em cativeiro é causada pela', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'diferença de umidade entre os ambientes.', 0),
    (@question_id, 'B', 'ausência de alimentação natural.', 1),
    (@question_id, 'C', 'adaptação ao novo ambiente.', 0),
    (@question_id, 'D', 'mudança de comportamento.', 0),
    (@question_id, 'E', 'variabilidade genética.', 0);

-- 2º Dia, questão 100 (98 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 36, NULL, 'O bioma Cerrado é caracterizado por apresentar ampla e natural diversidade de espécies vegetais. Nos últimos tempos, o homem vem modificando esse cenário pela inserção de plantas exóticas nesse ambiente. Exemplo disso é o cultivo do capim-gordura, nativo do continente africano e utilizado nesse tipo de ecossistema como forma de pastagem. É importante ressaltar que essa espécie vegetal é capaz de se espalhar por grandes áreas, devido à sua agressividade e poder competitivo.

CARLOS JR., L. A.; BARBOSA, N. P. U.; FERNANDES, G. W. O capim-gordura e as invasões no Cerrado brasileiro. Jornal do Biólogo, mar.-jun. 2008 (adaptado).

Em longo prazo, essa ação do homem pode gerar qual consequência?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Diversificar nichos ecológicos.', 0),
    (@question_id, 'B', 'Assorear as nascentes do bioma.', 0),
    (@question_id, 'C', 'Dificultar a infiltração de água na terra.', 0),
    (@question_id, 'D', 'Diminuir as espécies nativas do bioma.', 1),
    (@question_id, 'E', 'Contribuir com a redução das queimadas.', 0);

-- 2º Dia, questão 104 (110 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 53, NULL, 'A química nuclear é uma importante ferramenta na produção de substâncias utilizadas na área da saúde humana. A radiação emitida pelo cobalto-60 é utilizada na medicina como ferramenta de diagnóstico e no tratamento do câncer. No entanto, esse radioisótopo tem um tempo de armazenamento limitado, pois seu tempo de meia-vida é de 5,3 anos. Considere um frasco com uma amostra contendo 2,00 mg de cobalto-60, armazenado durante um período de 26,5 anos.

A massa de cobalto-60, em miligrama, que restará ao final desse tempo é mais próxima de', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '2,00 mg.', 0),
    (@question_id, 'B', '1,00 mg.', 0),
    (@question_id, 'C', '0,40 mg.', 0),
    (@question_id, 'D', '0,13 mg.', 0),
    (@question_id, 'E', '0,06 mg.', 1);

-- 2º Dia, questão 106 (112 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 37, NULL, 'Golden Rice, ou arroz dourado, é uma variedade de arroz enriquecida em betacaroteno, precursor da vitamina A. Foi desenvolvida para ajudar a combater a doença decorrente da deficiência dessa vitamina. Para a obtenção dessa variedade de arroz, foram utilizados métodos de engenharia genética.

BORÉM, A.; SANTOS, F. R. Entendendo a biotecnologia. Viçosa: UFV, 2008 (adaptado).

Esse alimento contribui para diminuir a carência associada a qual doença?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Hemofilia.', 0),
    (@question_id, 'B', 'Escorbuto.', 0),
    (@question_id, 'C', 'Raquitismo.', 0),
    (@question_id, 'D', 'Cegueira noturna.', 1),
    (@question_id, 'E', 'Anemia perniciosa.', 0);

-- 2º Dia, questão 113 (121 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 64, NULL, 'A dimetilamina é uma substância de elevada toxidez que entra em ebulição a 7 °C. Na madrugada de um dia frio, essa substância se espalhou por uma rodovia após um acidente de trânsito envolvendo um caminhão que transportava esse produto. Para minimizar a agressão ao meio ambiente, bem como evitar a evaporação da dimetilamina com a elevação da temperatura ao longo do dia, um químico considerou o uso de algumas substâncias ou misturas como água, vinagre, óleo de soja, sal de cozinha e bicarbonato de sódio.

Dentre as opções apresentadas, o tratamento correto para minimizar esse problema é usar', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'água.', 0),
    (@question_id, 'B', 'vinagre.', 1),
    (@question_id, 'C', 'óleo de soja.', 0),
    (@question_id, 'D', 'sal de cozinha.', 0),
    (@question_id, 'E', 'bicarbonato de sódio.', 0);

-- 2º Dia, questão 117 (131 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 58, NULL, 'O Brasil é o maior produtor mundial de nióbio (massa molar = 93 g mol⁻¹), metal utilizado na fabricação de vários tipos de aço: automotivos, estruturais e inoxidáveis. O processo utilizado na produção do nióbio é a redução aluminotérmica de Nb₂O₅ com excesso de 10% de Al (massa molar = 27 g mol⁻¹), em relação à quantidade estequiométrica da reação, representada pela equação química:

3 Nb₂O₅ (s) + 10 Al (s) → 6 Nb (s) + 5 Al₂O₃ (s)

Uma engenheira metalúrgica estimou a massa de alumínio necessária para produzir 9,3 kg de nióbio, nas condições descritas, para a produção de um lote de peças de aço encomendado por uma indústria, considerando um rendimento de 100%.

Disponível em: www.cbmm.com.br. Acesso em: 17 out. 2015 (adaptado).

A massa de alumínio, em quilograma, estimada pela engenheira é mais próxima de', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '2,7 kg.', 0),
    (@question_id, 'B', '3,0 kg.', 0),
    (@question_id, 'C', '4,1 kg.', 0),
    (@question_id, 'D', '4,5 kg.', 0),
    (@question_id, 'E', '5,0 kg.', 1);

-- 2º Dia, questão 119 (124 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 63, NULL, 'Objetos de prata escurecem em contato com compostos contendo enxofre por causa da formação de uma fina camada de sulfeto de prata. Um método simples para clarear o objeto consiste em forrar um recipiente com papel alumínio, adicionar ao recipiente uma solução aquosa de cloreto de sódio e, enfim, mergulhar o objeto de prata enegrecido. Em cerca de três minutos, a prata volta à coloração original. As seguintes semirreações e os respectivos potenciais-padrão de redução são úteis para a compreensão dos fenômenos ocorridos.

Ag₂S (s) + 2 e⁻ → 2 Ag (s) + S²⁻ (aq)    E° = −0,69 V
O₂ (g) + 4 H⁺ (aq) + 4 e⁻ → 2 H₂O (l)    E° = +1,23 V
Al³⁺ (aq) + 3 e⁻ → Al (s)    E° = −1,68 V

SARTORI, E. R.; BATISTA, E. F.; FATIBELLO-FILHO, O. Escurecimento e limpeza de objetos de prata: um experimento simples e de fácil execução envolvendo reações de oxidação-redução. Química Nova na Escola, n. 30, 2008 (adaptado).

Os valores das diferenças de potencial-padrão das reações que representam o escurecimento e o clareamento do objeto de prata são, respectivamente:', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '+0,54 V e +2,37 V.', 0),
    (@question_id, 'B', '+1,92 V e +0,99 V.', 1),
    (@question_id, 'C', '−0,15 V e +5,43 V.', 0),
    (@question_id, 'D', '+2,61 V e +1,29 V.', 0),
    (@question_id, 'E', '+0,15 V e −1,29 V.', 0);

-- 2º Dia, questão 120 (125 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 58, NULL, 'Apaixonada por culinária e química, uma chefe de cozinha calculou que, para promover o crescimento adequado da massa durante o cozimento de um bolo a 180 °C (453 K) e 1,00 atm, ela precisaria utilizar uma quantidade de fermento químico suficiente para produzir um volume de gás igual a 4,00 L. Com esse objetivo, ela escolheu utilizar o bicarbonato de amônio, um composto que, sob aquecimento, degrada-se em três gases distintos, que são os responsáveis pelo crescimento da massa. A decomposição do bicarbonato de amônio ocorre conforme a equação química apresentada e, nas condições do cozimento, seu rendimento é de 80%.

NH₄HCO₃ (s) → NH₃ (g) + CO₂ (g) + H₂O (g)

Considere que a mistura dos gases se comporta como gás ideal nas condições de cozimento utilizadas pela chefe.

Dados: Massa molar do NH₄HCO₃ = 79 g ⋅ mol⁻¹ e R = 0,082 atm ⋅ L ⋅ mol⁻¹ ⋅ K⁻¹.

A massa, em grama, de bicarbonato de amônio que ela deve utilizar é mais próxima de', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '2,3 g.', 0),
    (@question_id, 'B', '3,5 g.', 1),
    (@question_id, 'C', '5,9 g.', 0),
    (@question_id, 'D', '6,8 g.', 0),
    (@question_id, 'E', '8,9 g.', 0);

-- 2º Dia, questão 122 (116 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 35, NULL, 'As cangas são ambientes resultantes da atuação, ao longo de milhões de anos, de chuvas, enxurradas, calor e ventos em rochas ricas em ferro. São compostas por até 90% de óxidos de ferro e contêm solos muito ácidos, rasos, com reduzidos índices de fertilidade e temperaturas que atingem quase 70 °C na superfície. Ao contrário do que se imagina, esse tipo de ambiente abriga comunidades de plantas e animais com elevado número de espécies e distribuição restrita.

CARMO, F. F. et al. Ilhas de ferro estratégicas para a conservação. Disponível em: http://cienciahoje.org.br. Acesso em: 2 dez. 2018 (adaptado).

A seleção adaptativa nesses ambientes favorece a ocorrência de espécies', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'exóticas.', 0),
    (@question_id, 'B', 'migratórias.', 0),
    (@question_id, 'C', 'endêmicas.', 1),
    (@question_id, 'D', 'dominantes.', 0),
    (@question_id, 'E', 'generalistas.', 0);

-- 2º Dia, questão 123 (117 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 62, NULL, 'Para a produção de titânio metálico ou dióxido de titânio puros, é necessário fazer a cloração do minério que contém dióxido de titânio, com o objetivo de separar esse óxido de outras substâncias presentes no minério. A principal reação química de cloração é representada por:

TiO₂ (s) + 2 Cl₂ (g) ⇌ TiCl₄ (g) + O₂ (g)    ΔH = +175 kJ/mol

Depois da cloração, o tetracloreto de titânio é condensado e convertido, novamente, a dióxido de titânio.

Após o sistema atingir o equilíbrio químico, como a formação do TiCl₄ pode ser favorecida?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Aumentando a pressão total do sistema.', 0),
    (@question_id, 'B', 'Diminuindo a temperatura do sistema.', 0),
    (@question_id, 'C', 'Aumentando a pressão parcial de O₂.', 0),
    (@question_id, 'D', 'Aumentando a pressão parcial de Cl₂.', 1),
    (@question_id, 'E', 'Variando a quantidade de TiO₂.', 0);

-- 2º Dia, questão 124 (118 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 45, NULL, 'A laje de um depósito de bebidas tem 50 m² de área útil de armazenamento e foi projetada para suportar pressões de até 10⁴ Pa. O gerente do estabelecimento pretende armazenar um produto cuja densidade é 1 250 kg/m³. Considere a aceleração da gravidade igual a 10 m/s².

A altura máxima, em metro, de empilhamento do produto que essa laje é capaz de suportar é', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '0,16 m.', 0),
    (@question_id, 'B', '0,50 m.', 0),
    (@question_id, 'C', '0,80 m.', 1),
    (@question_id, 'D', '1,60 m.', 0),
    (@question_id, 'E', '8,00 m.', 0);

-- 2º Dia, questão 130 (92 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 34, NULL, 'A maioria dos seres vivos tem um relógio biológico (ciclo circadiano), que regula as mudanças metabólicas e comportamentais de acordo com o ciclo de 24 horas de rotação da Terra. Em 2015, um artigo publicado na revista Science Advantage mostrou ser possível a transferência dos genes do relógio circadiano da cianobactéria Synechococcus elongatus para o genoma da bactéria Escherichia coli, um organismo não circadiano.

CHEN, A. H. et al. Transplantability of a Circardian Lock to Noncircardian Organism. Science Advantage, n. 1, 2015 (adaptado).

Estarão presentes no organismo geneticamente modificado os genes do', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'metabolismo de E. coli, apenas.', 0),
    (@question_id, 'B', 'ciclo circadiano de E. coli, apenas.', 0),
    (@question_id, 'C', 'metabolismo de S. elongatus e do ciclo circadiano de E. coli.', 0),
    (@question_id, 'D', 'ciclo circadiano de S. elongatus e do metabolismo de E. coli.', 1),
    (@question_id, 'E', 'ciclo circadiano de S. elongatus e do ciclo circadiano de E. coli.', 0);

-- 2º Dia, questão 131 (93 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 39, NULL, 'Os funcionários de um zoológico observaram um aumento na taxa de mortalidade de aves aquáticas por afogamento. Um grupo de biólogos analisou o comportamento das aves por várias semanas e observou que elas apresentavam dificuldade de flutuação, por causa do encharcamento das penas com água.

O aumento na taxa de mortalidade dessas aves estava associado a uma redução na', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'dilatação do papo.', 0),
    (@question_id, 'B', 'reposição de penas das asas.', 0),
    (@question_id, 'C', 'secreção da glândula uropigial.', 1),
    (@question_id, 'D', 'formação da membrana natatória.', 0),
    (@question_id, 'E', 'largura das cavidades de ossos pneumáticos.', 0);

-- 2º Dia, questão 137 (144 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 110, NULL, 'Uma fábrica utilizou uma impressora 3D para produzir o protótipo de uma peça. O protótipo tem forma de um poliedro convexo, obtido pela justaposição de dois sólidos distintos, um com a forma de um prisma hexagonal regular reto e o outro com a forma de um tronco de pirâmide hexagonal reta. A base maior do tronco de pirâmide coincide com uma das bases do prisma.

Após a impressão do protótipo, ele foi encaminhado ao setor de customização para realização da pintura de sua superfície. O critério definido para realização da pintura considera que faces congruentes entre si devem ser pintadas com uma mesma cor, e faces não congruentes entre si devem apresentar cores distintas.

Qual é a quantidade de cores utilizadas para pintar o protótipo?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '9', 0),
    (@question_id, 'B', '8', 0),
    (@question_id, 'C', '6', 0),
    (@question_id, 'D', '4', 1),
    (@question_id, 'E', '3', 0);

-- 2º Dia, questão 139 (146 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 101, NULL, 'No atletismo, um grande desafio da prova de 100 metros rasos é a sua conclusão num tempo abaixo da marca de referência dos 10,00 segundos. Vários atletas já alcançaram esse feito. Em 2009, o jamaicano Usain Bolt estabeleceu o recorde mundial masculino dessa prova, com o tempo de 9,58 segundos.

Qual é a diferença, em segundo, entre a marca de referência e a marca estabelecida por Usain Bolt em 2009?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '0,02', 0),
    (@question_id, 'B', '0,42', 1),
    (@question_id, 'C', '0,52', 0),
    (@question_id, 'D', '1,02', 0),
    (@question_id, 'E', '1,42', 0);

-- 2º Dia, questão 141 (136 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 102, NULL, 'Uma pessoa pretende instalar um kit de gás natural veicular (GNV) em seu carro. Na loja que escolheu para realizar a compra e instalação desse kit, havia cinco modelos de cilindro para armazenamento do gás, cujas capacidades, em metro cúbico, eram, respectivamente: 10, 14, 17, 21 e 25. O preço do cilindro é proporcional à sua capacidade. Esse carro rodará 30 km diariamente, 7 dias por semana, e o consumo do GNV é de 1 m³ a cada 13 km rodados. A pessoa escolherá o modelo de cilindro de menor preço e que garanta apenas um abastecimento semanal.

Nessas condições, qual será a capacidade, em metro cúbico, do cilindro escolhido por essa pessoa?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '10', 0),
    (@question_id, 'B', '14', 0),
    (@question_id, 'C', '17', 1),
    (@question_id, 'D', '21', 0),
    (@question_id, 'E', '25', 0);

-- 2º Dia, questão 144 (140 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 102, NULL, 'Em um laboratório, um recipiente contém 10 litros de uma solução composta apenas pelas substâncias S₁ e S₂. Dessa solução, 99,95% é de S₁. Uma quantidade de S₁ será retirada dessa solução, mantendo a quantidade inicial de S₂, de modo que 99,90% da nova solução seja de S₁.

Qual é a quantidade de S₁, em litro, que será retirada?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '0,0050', 0),
    (@question_id, 'B', '0,0100', 0),
    (@question_id, 'C', '0,5000', 0),
    (@question_id, 'D', '4,9775', 0),
    (@question_id, 'E', '5,0000', 1);

-- 2º Dia, questão 145 (141 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 102, NULL, 'Uma distribuidora de combustível possui caminhões-tanque com capacidade de 30 000 litros cada. Em qualquer transporte realizado por esses caminhões, um mesmo volume de combustível é descartado, pois fica com muitas impurezas. Esse volume descartado independe da quantidade transportada.

Um posto de combustível encomendou 10 000 litros de gasolina dessa distribuidora, que enviou 10 200 litros, considerando o volume descartado no transporte. Mesmo assim, a quantidade de gasolina entregue ao posto foi de 9 900 litros.

Em um novo pedido, esse posto solicitou que fosse entregue exatamente o dobro do volume de gasolina encomendado no pedido anterior.

Utilizando o mesmo caminhão da entrega anterior, qual é o volume mínimo de gasolina, em litro, que a distribuidora deverá enviar para garantir a entrega da quantidade encomendada nesse novo pedido?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '20 100', 0),
    (@question_id, 'B', '20 200', 0),
    (@question_id, 'C', '20 300', 1),
    (@question_id, 'D', '20 400', 0),
    (@question_id, 'E', '20 600', 0);

-- 2º Dia, questão 148 (150 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 102, NULL, 'Uma fábrica de tijolos ecológicos com 3 funcionários, cada um trabalhando 6 horas diárias, produz 720 unidades por dia. Para atender ao crescimento da demanda por esse tipo de tijolo, essa fábrica passou a ter 5 funcionários, cada um trabalhando 9 horas por dia, aumentando, assim, sua capacidade de produção. Todos os funcionários produzem igual quantidade de tijolos a cada hora, independentemente de trabalharem 6 ou 9 horas diárias.

O número de tijolos fabricados diariamente após o aumento da capacidade de produção é', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '800.', 0),
    (@question_id, 'B', '1 080.', 0),
    (@question_id, 'C', '1 200.', 0),
    (@question_id, 'D', '1 800.', 1),
    (@question_id, 'E', '2 520.', 0);

-- 2º Dia, questão 149 (151 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 101, NULL, 'Para acompanhar o fluxo de visitantes em seu prédio, uma empresa estabeleceu um código de identificação para a visitação. De acordo com a regra estabelecida, cada visitante será identificado com um código sequencial numérico com 7 dígitos, determinado, da esquerda para a direita, da seguinte forma:

• o primeiro dígito indica o andar ao qual o visitante se dirige, que é um número de 1 a 4;
• os dois próximos dígitos correspondem ao número do setor da empresa ao qual o visitante se destina. Esse número varia de 01 a 20;
• os três dígitos seguintes correspondem ao número do funcionário da empresa com quem o visitante irá se reunir. Esse número varia de 001 a 135;
• o último dígito indica se o visitante chegou à empresa pela manhã, dígito 0, ou à tarde, dígito 1.

Um visitante chegou à empresa às 10 horas da manhã para se reunir com o funcionário identificado pelo número 109, que trabalha no setor 08 da empresa, localizado no 2º andar.

O código de identificação desse visitante é', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '0109082.', 0),
    (@question_id, 'B', '0281090.', 0),
    (@question_id, 'C', '1010982.', 0),
    (@question_id, 'D', '2081090.', 1),
    (@question_id, 'E', '2810910.', 0);

-- 2º Dia, questão 150 (152 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 107, NULL, 'Quatro candidatos se apresentaram para realizar a prova de um concurso. Antes de iniciar a prova, os celulares dos quatro candidatos foram recolhidos pelo aplicador, que os guardou, cada um, dentro de um envelope preto. Ao término da prova, o aplicador devolveu os quatro envelopes com os celulares aos quatro candidatos, de maneira aleatória, já que não havia feito a identificação dos envelopes.

A probabilidade de que todos os candidatos tenham recebido de volta os envelopes com os seus respectivos celulares é', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '1/2', 0),
    (@question_id, 'B', '1/10', 0),
    (@question_id, 'C', '1/16', 0),
    (@question_id, 'D', '1/24', 1),
    (@question_id, 'E', '1/256', 0);

-- 2º Dia, questão 157 (162 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 102, NULL, 'Uma empresa produziu, em um determinado mês, 110 toneladas de plástico a partir de derivados de petróleo e 80 toneladas a partir de plásticos reciclados. O custo para reciclar uma tonelada de plástico é de R$ 500,00, que equivale a 5% do custo para produzir a mesma quantidade de plástico a partir de derivados de petróleo. Para o mês seguinte, a meta dessa empresa é produzir a mesma quantidade de plástico que foi produzida nesse mês, mas com redução de, pelo menos, 50% no custo de produção.

Para que no mês seguinte a empresa atinja a meta, a quantidade mínima de toneladas de plástico que devem ser produzidas a partir de reciclagem deverá ser', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '135.', 0),
    (@question_id, 'B', '140.', 1),
    (@question_id, 'C', '155.', 0),
    (@question_id, 'D', '160.', 0),
    (@question_id, 'E', '175.', 0);

-- 2º Dia, questão 160 (155 no Caderno Azul). Gabarito: B.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 106, NULL, 'Dez casais fundaram um grupo de dança e decidiram constituir uma diretoria com três cargos: presidente, secretário e tesoureiro. Para maior representatividade, decidiu-se que no máximo uma pessoa por casal poderá ocupar um cargo nessa diretoria.

Quantas diretorias diferentes podem ser constituídas por esses 10 casais?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '10 × 9 × 8', 0),
    (@question_id, 'B', '20 × 18 × 16', 1),
    (@question_id, 'C', '20 × 19 × 18', 0),
    (@question_id, 'D', '10 × 9 × 8 × 2', 0),
    (@question_id, 'E', '20 × 18 × 16 × 2', 0);

-- 2º Dia, questão 162 (157 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 102, NULL, 'O cortisol é um hormônio produzido pelas glândulas adrenais e pode ser considerado um importante marcador do estresse fisiológico. Em um estudo desenvolvido com enfermeiros, foi verificado que a concentração de cortisol salivar em um dia de trabalho, denotada por T, era, em média, 1,59 vezes a concentração de cortisol salivar em um dia de folga, denotada por F.

ROCHA, M. C. P. et al. Estresse em enfermeiros: o uso do cortisol salivar no dia de trabalho e de folga. Rev. Esc. Enferm. USP, n. 5, 2013 (adaptado).

Nesse estudo, a relação obtida entre T e F foi', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'T = 1,59 + F', 0),
    (@question_id, 'B', 'F = 1,59 + T', 0),
    (@question_id, 'C', 'T/F = 1,59', 1),
    (@question_id, 'D', 'F/T = 1,59', 0),
    (@question_id, 'E', 'F ⋅ T = 1,59', 0);

-- 2º Dia, questão 163 (158 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 113, NULL, 'Um estacionamento possui 120 vagas para veículos, e todas essas vagas estão ocupadas. Cada cliente paga uma mensalidade para utilizar uma vaga, que é calculada com base nas despesas mensais do estacionamento e no lucro pretendido. As despesas mensais do estacionamento são: R$ 14 240,00 com manutenção mais R$ 36,00 de seguro por veículo. O lucro do estacionamento é determinado pela diferença do valor arrecadado com as mensalidades pelas despesas efetuadas. A partir do mês seguinte, o valor do seguro por veículo aumentará em 20%, e as despesas com manutenção permanecerão sem alterações. Com isso, o dono do estacionamento reajustará as mensalidades para obter um lucro mensal de R$ 10 000,00. Apesar desse reajuste, todas as vagas continuarão ocupadas.

O valor, em real, da mensalidade reajustada será', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '185,60.', 0),
    (@question_id, 'B', '226,09.', 0),
    (@question_id, 'C', '245,20.', 1),
    (@question_id, 'D', '268,93.', 0),
    (@question_id, 'E', '285,60.', 0);

-- 2º Dia, questão 164 (159 no Caderno Azul). Gabarito: D.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 102, NULL, 'O dono de uma sorveteria armazena sorvete em potes de 20 000 cm³. Ele serve o sorvete em taças, em porções de 250 mL.

A quantidade de taças que ele consegue servir a partir de um pote cheio de sorvete é', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '5.', 0),
    (@question_id, 'B', '8.', 0),
    (@question_id, 'C', '50.', 0),
    (@question_id, 'D', '80.', 1),
    (@question_id, 'E', '800.', 0);

-- 2º Dia, questão 168 (171 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 110, NULL, 'Um confeiteiro passou a produzir tortas em formato de cilindro circular reto, com raio da base variando entre 12 cm e 16 cm e altura de 6 cm. Essas tortas deverão ser embaladas em caixas com formato de prisma reto de base quadrada, de modo que seja possível acomodar a torta em seu interior e ainda restar pelo menos 1 cm de distância entre a torta e as superfícies internas da caixa, lateral e superior. Ele dispõe, originalmente, de caixas no formato pretendido, cujas dimensões internas são 14 cm de comprimento do lado da base e 7 cm de altura, que não atendem às suas necessidades. Portanto, ele comprará novas caixas, com o mesmo formato das caixas originais, mas com comprimento do lado da base maior, que sejam adequadas para embalar todos os tipos de torta que produz.

A aresta da base das novas caixas deve ser, no mínimo, quantos centímetros maior do que a das caixas originais?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '4', 0),
    (@question_id, 'B', '12', 0),
    (@question_id, 'C', '16', 0),
    (@question_id, 'D', '18', 0),
    (@question_id, 'E', '20', 1);

-- 2º Dia, questão 169 (172 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 103, NULL, 'Um empresário utiliza máquinas cuja pressão interna P, em atmosfera, depende do tempo contínuo de utilização t, em hora, e de um parâmetro positivo K, que define o modelo da máquina, segundo a expressão:

P = 4 ⋅ log[−K ⋅ (t + 1) ⋅ (t − 19)]

O fabricante dessas máquinas recomenda ao usuário que a pressão interna desse tipo de máquina não ultrapasse 10 atmosferas durante seu funcionamento.

O empresário pretende comprar novas máquinas desse tipo que deverão funcionar, diariamente, por um período contínuo de 10 horas. Para isso, precisa definir o modelo de máquina a ser adquirida escolhendo o maior valor possível do parâmetro K, atendendo à recomendação do fabricante.

O maior valor a ser escolhido para K é', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '10^0,5', 1),
    (@question_id, 'B', '10^8', 0),
    (@question_id, 'C', '10^2,5 / 84', 0),
    (@question_id, 'D', '10^2,5 / 99', 0),
    (@question_id, 'E', '25 × 10⁻²', 0);

-- 2º Dia, questão 173 (175 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 106, NULL, 'Um pai comprou oito presentes diferentes (dentre os quais, uma bicicleta e um celular) para dar a seus três filhos. Ele pretende distribuir os presentes de modo que o filho mais velho e o mais novo recebam três presentes cada um, e o do meio receba os dois presentes restantes. O mais velho ganhará, entre seus presentes, ou uma bicicleta ou um celular, mas não ambos.

De quantas maneiras distintas a distribuição dos presentes pode ser feita?', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '36', 0),
    (@question_id, 'B', '53', 0),
    (@question_id, 'C', '300', 1),
    (@question_id, 'D', '360', 0),
    (@question_id, 'E', '560', 0);

-- 2º Dia, questão 174 (176 no Caderno Azul). Gabarito: C.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 102, NULL, 'A final de um campeonato de futebol foi disputada em 2 tempos regulamentares, de 45 minutos cada, sem acréscimos, com uma prorrogação de 30 minutos, também sem acréscimos. Um jogador entrou no início do segundo tempo, com um equipamento para medir a distância percorrida durante sua participação no jogo. Ao final do segundo tempo regulamentar, esse jogador havia percorrido 4,5 km. Ele manteve na prorrogação a mesma velocidade média que havia mantido no segundo tempo regulamentar.

A distância percorrida por esse jogador durante sua participação na partida, em quilômetro, foi', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', '4,5.', 0),
    (@question_id, 'B', '6,0.', 0),
    (@question_id, 'C', '7,5.', 1),
    (@question_id, 'D', '9,0.', 0),
    (@question_id, 'E', '12,0.', 0);

-- 2º Dia, questão 176 (178 no Caderno Azul). Gabarito: E.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 107, NULL, 'Três dados cúbicos, com faces numeradas de 1 a 6, foram utilizados em um jogo. Artur escolheu dois dados, e João ficou com o terceiro. O jogo consiste em ambos lançarem seus dados, observarem os números nas faces voltadas para cima e compararem o maior número obtido por Artur com o número obtido por João. Vence o jogador que obtiver o maior número. Em caso de empate, a vitória é de João.

O jogador que tem a maior probabilidade de vitória é', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'Artur, com probabilidade de 2/3', 0),
    (@question_id, 'B', 'João, com probabilidade de 4/9', 0),
    (@question_id, 'C', 'Artur, com probabilidade de 91/216', 0),
    (@question_id, 'D', 'João, com probabilidade de 91/216', 0),
    (@question_id, 'E', 'Artur, com probabilidade de 125/216', 1);

-- 2º Dia, questão 177 (179 no Caderno Azul). Gabarito: A.
INSERT INTO question (entrance_exam_id, topic_id, subject_id, statement, difficulty) VALUES
    (@enem_2025_dia_2, 102, NULL, 'A luminância de um objeto é a grandeza que descreve a quantidade de luz produzida ou refletida por sua superfície. Ela está definida como a razão entre a intensidade luminosa, medida em candela (cd), e o quadrado da distância do objeto até o foco de luz, medida em metro (m).

A unidade de medida da luminância de um objeto é', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO answer_option (question_id, letter, text, right_answer) VALUES
    (@question_id, 'A', 'cd/m²', 1),
    (@question_id, 'B', 'm²/cd', 0),
    (@question_id, 'C', 'cd/m', 0),
    (@question_id, 'D', 'm/cd', 0),
    (@question_id, 'E', 'm/cd²', 0);
