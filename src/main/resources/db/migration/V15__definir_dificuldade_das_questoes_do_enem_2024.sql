-- Dificuldade das questões do ENEM 2024 inseridas na V14.
--
-- Aqui a dificuldade é uma estimativa manual, feita questão a questão e
-- relativa à própria área, na mesma escala da V12 (1 = fácil, 2 = média,
-- 3 = difícil). Os critérios foram:
--   * fácil: texto curto ou direto, resposta localizada no enunciado, conceito
--     básico ou conta de uma etapa;
--   * média: exige inferência ou comparação entre trechos, ou conceito
--     intermediário, ou conta de poucas etapas;
--   * difícil: texto literário ou filosófico denso, alternativas muito
--     próximas, conceito mais específico ou raciocínio em várias etapas
--     (estequiometria, otimização, progressões, probabilidade, geometria).
--
-- A V14 já grava todas as questões com 2. Como na V12, só as questões que
-- saem do nível médio aparecem na lista.
--
-- A questão é localizada pelo SHA-256 do enunciado gravado pela V14, porque
-- os ids variam entre bancos. Questão cujo enunciado ou dificuldade já foi
-- alterado depois da V14 não é tocada.
CREATE TEMPORARY TABLE enem_2024_difficulty (
  `stage` varchar(40) NOT NULL,
  `statement_sha256` char(64) NOT NULL,
  `difficulty` smallint(6) NOT NULL,
  PRIMARY KEY (`stage`, `statement_sha256`)
);

INSERT INTO enem_2024_difficulty (stage, statement_sha256, difficulty) VALUES
    ('1º Dia', '70089b1a9030b28ef0d77d6ed1b5514f10602ee3575e3fcf09d6a94cca3d062c', 1), -- 02, inglês: fácil
    ('1º Dia', 'f1ee2324453e55e74c2193955c7ed4bf0aa4c09ad1b32d97dbb8a8cf3ae2eb7d', 1), -- 02, espanhol: fácil
    ('1º Dia', '50940324e5cf80a4f01b9dcce36252b370c437bbeaa01f4690386b446c828fa8', 1), -- 04, inglês: fácil
    ('1º Dia', '93ae3dd049bb35a4017f55054ad651f24ede6f20b695d777b36227bd1178df09', 1), -- 05, espanhol: fácil
    ('1º Dia', '0935fd9eb34946ca9b2e32e0d2a5340bde2d03bd3be7f7aaff1a23000a7c0007', 1), -- 06: fácil
    ('1º Dia', 'a1f5c0351c414a5560d447e710524a181d9bafd9c80deb5840d6eea4fefa4c74', 1), -- 07: fácil
    ('1º Dia', 'e49998cd38f372f117028348ac0615f221860adc620504dddb0a6a385e037d24', 1), -- 09: fácil
    ('1º Dia', 'adc2abaf06b153a8051de9ba8bf0730b9a958c004359bfd635bd802eeff136d3', 1), -- 10: fácil
    ('1º Dia', 'f87fbeb827fdbfd8d42d928966f0c84350edd3e4068f9986d33c0844bafbf2ba', 1), -- 12: fácil
    ('1º Dia', 'e533b808609e4c1ac0ba5be62ac78b91830eed864b17ea5dff11f76339a34482', 1), -- 13: fácil
    ('1º Dia', 'b5bcb6226a51934d3dc346ec41a34ba91f13ab29c54195cdad56f7be8d26ba62', 1), -- 14: fácil
    ('1º Dia', 'dd4f64304168828f9ecfea47dc8cd64fda4779c319d01677401dc1ad1432440d', 1), -- 15: fácil
    ('1º Dia', '5b0f538f4c01ed9d0234d62f05d0c9b5d142fe23cb1ece789280949a904b8660', 3), -- 16: difícil
    ('1º Dia', '382098e8d2aa6c4cf87b320951abf57a66c92048311afa7f517c18c55b789ff4', 1), -- 17: fácil
    ('1º Dia', 'c5440532373328b17200cfe96b5bb4fb4a4cd0d3821ba0b24458e307ccd866b2', 3), -- 22: difícil
    ('1º Dia', '98f2891793ed3189f42761c46515adcfe5d4f0ea5adcb44dc2a40f2946631493', 3), -- 24: difícil
    ('1º Dia', '4e4e26555be7801bfff8aa47f3647035db249bc6265fd0af0adfbb4be213a4eb', 3), -- 28: difícil
    ('1º Dia', 'a8a1620f10738e165528e75fa5726162571354739c54330764bedf0f9fd44ec4', 3), -- 29: difícil
    ('1º Dia', '5a709ec63e260e42e2f14f76853e1d84c1a1f2b5915770dfc3624d705798ed75', 3), -- 30: difícil
    ('1º Dia', '768884eea0f2456f65c5cd5d2e2df7a98743c723bc5132de754d0baaaf2b3936', 3), -- 31: difícil
    ('1º Dia', '2621e0a9ef62fa51c5dea1da1c3f54fadb5c836a9f5d1b7559b840a470ad4f6a', 1), -- 34: fácil
    ('1º Dia', '2a4869e30671c3d08bc0728b4c565dc8558c070cd93249e38750ea52e3933a11', 3), -- 38: difícil
    ('1º Dia', 'ff30b72dbe35688d1c0a2bd64d0b0adb28749a430433bef088f60e36113123af', 1), -- 41: fácil
    ('1º Dia', '6f563819195a3a4c41450560362bac8e6fad46eeb997ac5df4cab76c476058ad', 1), -- 44: fácil
    ('1º Dia', '73a153a6f77785dceb39e51dea7acdf4e7d5088ea5624259008612f0280d047a', 1), -- 48: fácil
    ('1º Dia', '84743f6b6ff2654651d702b314e4e7dcdfe1306a7ee9ef7639d0f67e4c4235d9', 1), -- 50: fácil
    ('1º Dia', 'e41f6497f712bd2d9013831afc43f9ef932d252d9478141512eb96c841d4fe85', 1), -- 52: fácil
    ('1º Dia', 'bda64c0fe01aa2de6a7274a1dd3c384efa7e0548256dcd0c1c589948e22a8a44', 3), -- 54: difícil
    ('1º Dia', 'd20ccdb64ec9159e28e50f75bbff8df1830fe11a88771459db574ebd2639d74c', 3), -- 55: difícil
    ('1º Dia', 'b660c7eb45ee059537ce62db765d43cf45c801bfe19a5519ccbfd57fe4799022', 1), -- 57: fácil
    ('1º Dia', '6bdc50c1ddb576464a26ddad36e2814c20ba7fab004cb1ca1207f786c3365be0', 1), -- 59: fácil
    ('1º Dia', '7aff70137fc8352079c80b1d8edfbf8208c288bf21445889270ac6b250840964', 3), -- 60: difícil
    ('1º Dia', 'b1c1762f56a215954655d3071f6dc59e966cac1582fe0a6242cc29411f16e6b4', 3), -- 62: difícil
    ('1º Dia', 'd9be272931396c58401a280218371ee80907f934acc44ccc0c74e8ac0e977254', 1), -- 63: fácil
    ('1º Dia', '63def318e040e62105a0f70e2e6a96eac146abd6ce553ca936080cc40b5f2bec', 1), -- 64: fácil
    ('1º Dia', '769ee26c5e4fd80aa058eca4fc5b939f8d57afc1f051e6c274d76e388a8d1572', 1), -- 67: fácil
    ('1º Dia', '7d3992900d5b99894ac47a6415b067ce42187ae3bb0a13de0b589ff22b93cecf', 1), -- 69: fácil
    ('1º Dia', 'edf53cc5cf28606e3d7ad3c2453c84dc38543a2f7105eef709ea5f6be87e1f32', 1), -- 71: fácil
    ('1º Dia', 'b41552e7d2e39f702dc466395042e33ebef550b28241b3537e9202ab145c6cbd', 3), -- 73: difícil
    ('1º Dia', 'e9195d45eb5be35f965eb04b56263084d1f3dc4ada76ea5fb2b20568faa3df74', 3), -- 75: difícil
    ('1º Dia', '81fa27ab02764bf859b0bee63bf99765bb2338c6006324178f76584d0b9d7ab0', 1), -- 79: fácil
    ('1º Dia', '8631635e49a6f4367d5d8c54615ecd7ad27846a183253ca1dd6bb437e098df81', 1), -- 80: fácil
    ('1º Dia', '41dce64fc4638c5cb969ac0160353768367b8bbb7622b17b0c810e479649c137', 1), -- 81: fácil
    ('1º Dia', '0d118c6e194b9f5669ef735f7398f3e74aae01d4fc2594d9be4f48dc43bd3389', 1), -- 83: fácil
    ('1º Dia', 'c99697041966d90f62b5027316d15891935a57d75ff6fe891486f28541b2d348', 3), -- 84: difícil
    ('1º Dia', '49c8d04cb67678b4efb2f3f91fb6e46e5c8b18910c87fa12aa0c2855523c9cba', 1), -- 85: fácil
    ('1º Dia', '899288a83ae59a0827d1585fc09e0e372327e9dc7394ea7e57b6e0767cd390d9', 1), -- 87: fácil
    ('1º Dia', 'e7d6744cb5c010105badc46958d24c608a49e6300204e3d43f725b30a32fcdd3', 1), -- 88: fácil
    ('2º Dia', 'a254baeddf8b83f8122aa19cf4fda396831b75b61a89b3d167dd5dff3375ff1a', 1), -- 93: fácil
    ('2º Dia', '5f46e57c4e41f390972076a5597cdddff8efd944b4032c15c472fdaad1c6adf9', 3), -- 95: difícil
    ('2º Dia', '0cfd0d8ba446b0f961df6912c67471c633a950ffd64afefae990c83b53c51804', 3), -- 97: difícil
    ('2º Dia', '7575ccfecedf7c51a6f1559c01a9a96ee1905eed0fc056d476cdefba0de5e990', 1), -- 105: fácil
    ('2º Dia', 'a84d3375af3a748d044148bb6f2dc865c26dba5f64f86c0b2bf20aa6ae1ef209', 3), -- 106: difícil
    ('2º Dia', 'ad91a2bcd01b883897ed9aa4287cf10cd3befe754f4c539caa12f85077ef597f', 1), -- 107: fácil
    ('2º Dia', '2c08cb25873106929ec262560140d610f03391fd26fdef925b33407f069481c4', 1), -- 108: fácil
    ('2º Dia', '93402447c40621a69fe42eec5fc7acc7913ff2cefa1d1a2b8f39569e113b198d', 1), -- 111: fácil
    ('2º Dia', 'e7d801eb82a47e15f13a1ed17bd21a689b0f4413e969c9b3474b6434d0d851ec', 1), -- 116: fácil
    ('2º Dia', '36bc8bd4ce455cd5a8c9404004345a0c8f8616bc1a21e226d3b1f8171fbdcfa3', 1), -- 126: fácil
    ('2º Dia', 'af48b521936822dff066b4f43802f0a811ac032ecd0ba65419e3dc6f491fe9cd', 3), -- 133: difícil
    ('2º Dia', '61e7c5872bca4cdc625f7c12c2dd2ae2a67cff3d744a73d252abd8a2dd68f16c', 1), -- 137: fácil
    ('2º Dia', '5490c6195e42363081a1b545cd33780705c777aadc3c47ab87039b520d3de211', 1), -- 140: fácil
    ('2º Dia', 'de011b154e81db6868524b1b4384a69beff54708e57ceaa101b144ae24f7324e', 3), -- 143: difícil
    ('2º Dia', '62d5266efd54980a67d153072f6e3eff59bd4178f39b2b4e3d624065f33a21fc', 1), -- 145: fácil
    ('2º Dia', '11d2a75ad86a0276c9f669adb84148df5b9cc53f774aa0cb18d4530b26e7cd6e', 3), -- 146: difícil
    ('2º Dia', 'c983d5a166667ee09d9fbca67a84c6005b06ce68d7d3d0f7967a43a75e2734d2', 1), -- 153: fácil
    ('2º Dia', '10eee65c72481a4a1b9d6e598ca4d219b1098cbfadfb896048ea690f7f0b1a16', 1), -- 156: fácil
    ('2º Dia', 'b398db552d37155f204804fdea02137f4c48a04c6e6ebcff15a749093f29096f', 1), -- 157: fácil
    ('2º Dia', '6d66e80f5eb5762cf8ae6f5fd107c1b43231268472caa8263e3325bf0b37502f', 3), -- 162: difícil
    ('2º Dia', '8eaa1ebdda4b64e186d12e28ab1a38fa719ed18e9c8338faba5de434d94cdc54', 3), -- 165: difícil
    ('2º Dia', '88268ad8481b2d0cf21c103ebf782df81bb8b9828a8efbea368f047afffcf022', 3); -- 171: difícil

UPDATE question q
    JOIN entrance_exam e ON e.id = q.entrance_exam_id
    JOIN institution i ON i.id = e.institution_id
    JOIN enem_2024_difficulty d ON d.stage = e.stage
                               AND d.statement_sha256 = SHA2(REPLACE(q.statement, CHAR(13), ''), 256)
SET q.difficulty = d.difficulty
WHERE i.acronym = 'INEP'
  AND e.name = 'ENEM'
  AND e.year = 2024
  AND q.difficulty = 2;

DROP TEMPORARY TABLE enem_2024_difficulty;
