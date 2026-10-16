-- Dificuldade das questões do ENEM 2025 inseridas na V11, pelo parâmetro b
-- (dificuldade) da TRI publicado pelo Inep nos microdados do ENEM 2025
-- (ITENS_PROVA_2025.csv, provas LC 1462, CH 1449, CN 1486, MT 1474: Caderno 3 Branco e
-- Caderno 6 Cinza da aplicação regular).
--
-- Cada área tem a sua própria escala na TRI, então o corte é feito por área:
-- os itens da prova são divididos em tercis de b (1 = fácil, 2 = média,
-- 3 = difícil), contando todos os itens com parâmetro, e não só os cadastrados.
--   Linguagens: b <= 0.595 -> 1; b <= 0.960 -> 2; acima -> 3
--   Ciências Humanas: b <= 0.617 -> 1; b <= 1.279 -> 2; acima -> 3
--   Ciências da Natureza: b <= 0.871 -> 1; b <= 1.596 -> 2; acima -> 3
--   Matemática: b <= 1.260 -> 1; b <= 2.202 -> 2; acima -> 3
--
-- Ficam em 2 as questões 120 e 169 do 2º Dia, que o Inep publicou sem
-- parâmetro (problema de convergência na calibração).
--
-- A questão é localizada pelo SHA-256 do enunciado gravado pela V11, porque
-- os ids variam entre bancos. Questão cujo enunciado ou dificuldade já foi
-- alterado depois da V11 não é tocada. As questões do tercil do meio já estão
-- com 2 e não aparecem na lista.
CREATE TEMPORARY TABLE enem_2025_difficulty (
  `stage` varchar(40) NOT NULL,
  `statement_sha256` char(64) NOT NULL,
  `difficulty` smallint(6) NOT NULL,
  PRIMARY KEY (`stage`, `statement_sha256`)
);

INSERT INTO enem_2025_difficulty (stage, statement_sha256, difficulty) VALUES
    ('1º Dia', 'fadeb812fce81bb254b694fddfe60fc88bf06ef4909d8d25e20b8ee7a7de347d', 1), -- 03 inglês: b = 0.372
    ('1º Dia', '0070f1002228fda0b490fd90f63bf32cd91ec1a77d6c460547b7ea690ec39dd5', 1), -- 02 espanhol: b = 0.443
    ('1º Dia', '056d29f4e05fff1fe60836f3937d2279a0f885b25bbf11a0b687e2fa85b62506', 1), -- 03 espanhol: b = 0.365
    ('1º Dia', '0e119680ab7e5ea569f99a95caf13502bebba7fcda73570831beed5c027168ca', 3), -- 04 espanhol: b = 1.323
    ('1º Dia', '7472224a6266a54b406904b9b629d9433259e70c4241800558722c63add425b9', 1), -- 07: b = 0.489
    ('1º Dia', 'a7dcca247917dbb423515d64fc3b33f34c88292ba50d60f7842181df6c47aee5', 3), -- 08: b = 1.085
    ('1º Dia', '9d0bac4aa251448e8961de67ec00c45ba39a7331723fc87f3b772e7f2dd723b0', 3), -- 09: b = 1.239
    ('1º Dia', 'ff76de56bf57162c590d0faa8a0d57bafc868be5b055f4af1e0ceb30e6722de4', 3), -- 10: b = 1.037
    ('1º Dia', '7d4dcf879c4b9e10daf4058f6596ae3330e1bef9f6eaf72b527939efcf9642f7', 1), -- 11: b = -0.028
    ('1º Dia', '202189e31545fd63e5abe588f28d88b1103dc1502ee52a2424aba56e80a22db2', 1), -- 12: b = -0.298
    ('1º Dia', 'd3182e835b0988f2163d49fb333e01ec9c0f31cba6ff2f9116e74710d5b58e41', 3), -- 17: b = 1.949
    ('1º Dia', 'e33eb7e7a9d1b6a9200f136ca754c36a2fedfebba107a15b2ff4a2e7c9b669aa', 1), -- 19: b = 0.186
    ('1º Dia', 'b7c87315096cfb509df98517271959798a46e72d832669ebd1a3b5d729a4e8e6', 1), -- 27: b = -0.041
    ('1º Dia', 'a3e649f131c5217ad61086c99d4b06dfb1f93d40cbc3a9ec459e56de29a3825e', 1), -- 28: b = 0.541
    ('1º Dia', '31530f176fc78632766b25420a438c53077cee5ab38b51f361e89d7d3adfb3f8', 3), -- 29: b = 1.071
    ('1º Dia', 'cc9b61d90a1b108b62d5512f8c17a8500748c6a101e2f19d2d206318b666df7c', 3), -- 31: b = 1.310
    ('1º Dia', '324489b03e3bd6fe6891df6acee9c85447d43a5c3b68d8091663097354f19cf4', 3), -- 32: b = 1.009
    ('1º Dia', 'd36a549b21426c736c8100fb9cb81e5b4384afc2260a22657ab212f346162d92', 3), -- 33: b = 1.724
    ('1º Dia', 'f6f3d7804edc1889603d638b8b019283b75c9489fcec04b57ddbde601bb0814b', 3), -- 35: b = 1.505
    ('1º Dia', '834b2e6513c4200bf210f50dfbda9d713547faa23cbc24a476df35acde7e18a0', 1), -- 37: b = 0.369
    ('1º Dia', 'cdfda1ed28bc97e3a81c88c85de7509440755fd313503bdba4327d356f44c211', 3), -- 38: b = 1.535
    ('1º Dia', 'fb6b35b2fb48fd28a5a9a58fe8be1b83c1a17268bcef7a7800721cb67577cd81', 3), -- 45: b = 0.976
    ('1º Dia', '41264bad9bf46b43995d3f5fcdee856deca3c1b891367647f4c3727a905338de', 1), -- 46: b = 0.327
    ('1º Dia', 'c7164668ac06f6650dd5860acc2ed706ff3a4035c1a8dbfc5f90ca0378f85c2e', 3), -- 48: b = 1.385
    ('1º Dia', '5e23e4cd6e8ff3f9287d9529e4b8e45b0eeb8bebb72d1683c21c4340478dc3c6', 3), -- 49: b = 1.519
    ('1º Dia', '90c2c374673d732a6d14e99eee205cd9a4e20b1974ec1d655c3d0670479c42f1', 1), -- 50: b = 0.017
    ('1º Dia', '39f437324f0190ff7433c56b7510f64caa411e07bc200822c9dc3d22f2e5234d', 3), -- 54: b = 1.303
    ('1º Dia', '256d2bd1b84ca8d3b293812727f242955c4efa3f6e6cbcee8c9fc4fc364d9841', 1), -- 56: b = 0.382
    ('1º Dia', 'e6662e8fa8f04a1c09ce3024e3d81345f1b6765ae226f6228cc08d797681601c', 1), -- 58: b = 0.439
    ('1º Dia', '2613ff06bb2ad33282745e936eac913ccaf40c84ec9ecc4ab42b3a1a24accd45', 3), -- 60: b = 2.184
    ('1º Dia', 'c35b0e4d465e3f1a3f4f0011cac8e0427d3adc13cfa997e1e06c768693d9f67b', 1), -- 61: b = 0.483
    ('1º Dia', '68d7f76b13ebbc8f9f00bb1c5290a39622206a91ed3ce675e1c0fde178841ddc', 3), -- 62: b = 1.752
    ('1º Dia', '0a897788e4741f454d00257edd3e920e4ad39a65f6cb5c5127d393126585ba29', 1), -- 63: b = 0.318
    ('1º Dia', 'e7c40f8218275e5201df8eb872b1a34742511b659b2e7397ec28ca144a1f0a47', 1), -- 65: b = 0.391
    ('1º Dia', 'f4bd26b6be44e3415592c91458d91080c190e48dc92368c57cc8de2656f7de29', 3), -- 66: b = 1.647
    ('1º Dia', '227d3501634fec70ec2d6ae91054f9e1e09f6ff4aef3158e45eb45dc0d6da6bd', 3), -- 68: b = 1.567
    ('1º Dia', 'e419271b47d6f452c2e3ca36f58818770a4f6bcdcea2e1388dcf4064f8972340', 3), -- 71: b = 2.192
    ('1º Dia', '18ff43baf2ee66e38490b0d43e3cd2e37f6ccee9cd2c5e7263404240c4cd46e8', 1), -- 73: b = 0.510
    ('1º Dia', '283eb92ea36382412d9dd1c451ab82fdef9347ccd9b0fc004235f4f795ab8411', 3), -- 75: b = 2.091
    ('1º Dia', 'e45c9d7c84be4d5aaeddff75547ffb8717f9c79921be33d5d57ae690f7ae716e', 1), -- 76: b = 0.174
    ('1º Dia', 'd9d4e515c535cdf5938b4299fd09796525908a8441149affa2c9c8ae3a583196', 3), -- 77: b = 2.721
    ('1º Dia', '88c209dce2ca21c152ef82d0007844f52f475a677cc2b9b9d2c8fa43394d362e', 3), -- 78: b = 2.679
    ('1º Dia', '263b75bfaf679918d4e6654484f5702cc08f6b45ca0b41ae55b6e2a183e03fb6', 3), -- 80: b = 1.369
    ('1º Dia', '9577eee177a5939b26b4be1b36915785f55bd3a769bddb96044100bf709f00e2', 1), -- 83: b = 0.498
    ('1º Dia', 'd5d3046ef44e556d116c6b3548104865a9ab720316d3dc0b2f3d74b5ee8b7dd7', 3), -- 85: b = 2.652
    ('1º Dia', 'b11cd3562c1bcf329000ba5a44e9f643fa8f1e14e279d8bfd2eaf61a89696fd2', 3), -- 86: b = 1.410
    ('1º Dia', '92a53644a7b3bd24c304db8c44264b1974f314300a009ace5c1bcedbb6cc30a9', 1), -- 87: b = 0.461
    ('1º Dia', '253016a677a3151c55a62c64d10fe05017553e288a02b07c3b91902f9b214f7e', 3), -- 88: b = 1.590
    ('1º Dia', '2aeb0aeb1c01561e2e421480879a930d190ee980399d8e67ee51dea5e4186953', 1), -- 89: b = 0.418
    ('1º Dia', '98c7aa52584861a40e0cd1a4d76ce6789731ddf0c70440d516af10278e7d4299', 1), -- 90: b = 0.407
    ('2º Dia', 'dfd989176363a825ca114d55ccc9349dc36e3aeab098478172f642b04fa42489', 1), -- 91: b = -0.024
    ('2º Dia', 'fc1927cf4cf4259526e9b88c695e9eb1ad4d457331a29fbcd55d54bc5ec34d3e', 1), -- 92: b = -0.221
    ('2º Dia', '135bd0976280f2ba848f024ac773a85a689248d8579bdbde55bc4175014bfd68', 1), -- 94: b = -0.137
    ('2º Dia', '705c931ec060afcf141f3420a24ea9b30046938341c9060391aaaa2ae3881e23', 1), -- 96: b = 0.335
    ('2º Dia', 'd69f9fbb36053694bd12e635fb58ca82fd61bff45d3ee4c62b57ae6588aa4353', 1), -- 97: b = 0.812
    ('2º Dia', 'efd69b067b624fab7e994670d3c30e3fb4e807e2667644586d71b5d3e5379620', 1), -- 98: b = 0.057
    ('2º Dia', 'f7e25fb4fa304ab91e161790a8ca30412f09374f71e64b652e5378992ac65a22', 1), -- 100: b = -0.111
    ('2º Dia', 'e0e4adc745678847cb3e9b95c9e39f8c0d9140b9b82f9e9b0af3f05bf7aa4749', 3), -- 113: b = 2.224
    ('2º Dia', 'cecec8324577739c5c1408bf857fabf436a3bf48e035aa2f604333ff6e5535b5', 3), -- 117: b = 1.930
    ('2º Dia', 'aab02078034a75c91de25a7631ab74b0cd1f0cd35d57208ed5cc79b48bf460d9', 3), -- 119: b = 2.183
    ('2º Dia', '3390cd9cdee7adbdd776ddeba2ef5b8554917ead8a5a4d5b75c21327dce9970a', 3), -- 123: b = 1.925
    ('2º Dia', 'b49775c680f8a9052dcc16475cd56228ee6cd49260a6dc58bf101e387dc3913e', 3), -- 124: b = 1.757
    ('2º Dia', '221649c6e5981f2a68fe549e9e18a54ab74ef8702d86cf92589d9d9fe2d3e800', 1), -- 130: b = 0.534
    ('2º Dia', '623bd9497f4e36482e977016a1af9b770d5a535430b8d5db83583d91ae69610c', 3), -- 137: b = 2.980
    ('2º Dia', '9e78a32fafe8c2c9ad0af567fa43464e800fbdf0f4076e2835dcbe97d807e630', 1), -- 139: b = 0.850
    ('2º Dia', '6de3f827794a8f3ca0504d1cd9ec4e96bb662832829a05e9d955cc946915ebe5', 1), -- 141: b = 0.878
    ('2º Dia', '3355cf0484e4f47e8ebe877baac1ac92b7dec14d73b51935df5068080446dd05', 3), -- 144: b = 2.925
    ('2º Dia', 'b79cb6a2d3cb6901105b7b98065fa50053b39cf5c1d986a48545151e2698f00f', 1), -- 149: b = 0.091
    ('2º Dia', '9b7ca82a08d8fabe2f0586b599e72e399c59472467dcc8cab480c729f589dddc', 3), -- 150: b = 2.227
    ('2º Dia', '9351497ab72aa4b74e5aca31ec16688f58628c759a58bf71277f2222138cf88c', 3), -- 157: b = 2.463
    ('2º Dia', 'c2462e10e962b2bab4f4176000f45da1f1589d14a74fbc759f8063f40243452d', 3), -- 160: b = 2.286
    ('2º Dia', '922dc8f3dcab797021c46b3d281f263d1bd8f6ee71a464785ea71afff64e37e5', 1), -- 162: b = 1.244
    ('2º Dia', '3d33408e3076c8479ad2e98cb3c19e9f62d954fb16329136e86118aefb12bf41', 3), -- 168: b = 2.574
    ('2º Dia', '2da19da538f7d8ad98fe74a9790b472c9620757c17347a45c23eadd0da599ddd', 3), -- 173: b = 3.464
    ('2º Dia', '0eaf4a2aed987c9791ef8ca2e56f195c2e05c2245e0a51b836a03ac5d0ff7305', 1), -- 174: b = 1.055
    ('2º Dia', 'f9762244fdbd4de4a231e68c960596b1d8741460febf1581fbe558ba4252ded0', 3), -- 176: b = 2.808
    ('2º Dia', 'e6852a61de8eeaf537106d1564e5ed6da5d06aacacfa11b582a529aba1a177ff', 1); -- 177: b = 0.456

UPDATE question q
    JOIN entrance_exam e ON e.id = q.entrance_exam_id
    JOIN institution i ON i.id = e.institution_id
    JOIN enem_2025_difficulty d ON d.stage = e.stage
                               AND d.statement_sha256 = SHA2(q.statement, 256)
SET q.difficulty = d.difficulty
WHERE i.acronym = 'INEP'
  AND e.name = 'ENEM'
  AND e.year = 2025
  AND q.difficulty = 2;

DROP TEMPORARY TABLE enem_2025_difficulty;
