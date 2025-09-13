-- ==============================================================
-- Dados Iniciais para Sistema de Inventário Distribuído
-- ==============================================================

-- Dados de estoque por loja para testes

-- LOJA 1: Shopping Center
INSERT INTO estoque_produto (produto_id, loja_id, quantidade, reservado, estoque_minimo, ultima_atualizacao, versao) 
VALUES 
(101, 1, 100, 0, 10, CURRENT_TIMESTAMP, 1),
(102, 1, 50, 0, 5, CURRENT_TIMESTAMP, 1),
(103, 1, 200, 0, 20, CURRENT_TIMESTAMP, 1),
(104, 1, 80, 0, 8, CURRENT_TIMESTAMP, 1),
(105, 1, 30, 0, 5, CURRENT_TIMESTAMP, 1),
(106, 1, 150, 0, 15, CURRENT_TIMESTAMP, 1),
(107, 1, 120, 0, 12, CURRENT_TIMESTAMP, 1),
(108, 1, 60, 0, 10, CURRENT_TIMESTAMP, 1),
(109, 1, 40, 0, 5, CURRENT_TIMESTAMP, 1),
(110, 1, 90, 0, 10, CURRENT_TIMESTAMP, 1);

-- LOJA 2: Centro da Cidade
INSERT INTO estoque_produto (produto_id, loja_id, quantidade, reservado, estoque_minimo, ultima_atualizacao, versao) 
VALUES 
(101, 2, 80, 0, 10, CURRENT_TIMESTAMP, 1),
(102, 2, 30, 0, 5, CURRENT_TIMESTAMP, 1),
(103, 2, 150, 0, 20, CURRENT_TIMESTAMP, 1),
(104, 2, 60, 0, 8, CURRENT_TIMESTAMP, 1),
(105, 2, 25, 0, 5, CURRENT_TIMESTAMP, 1),
(106, 2, 100, 0, 15, CURRENT_TIMESTAMP, 1),
(107, 2, 90, 0, 12, CURRENT_TIMESTAMP, 1),
(108, 2, 45, 0, 10, CURRENT_TIMESTAMP, 1),
(109, 2, 30, 0, 5, CURRENT_TIMESTAMP, 1),
(110, 2, 70, 0, 10, CURRENT_TIMESTAMP, 1);

-- LOJA 3: Bairro Norte
INSERT INTO estoque_produto (produto_id, loja_id, quantidade, reservado, estoque_minimo, ultima_atualizacao, versao) 
VALUES 
(101, 3, 60, 0, 10, CURRENT_TIMESTAMP, 1),
(102, 3, 25, 0, 5, CURRENT_TIMESTAMP, 1),
(103, 3, 180, 0, 20, CURRENT_TIMESTAMP, 1),
(104, 3, 70, 0, 8, CURRENT_TIMESTAMP, 1),
(105, 3, 20, 0, 5, CURRENT_TIMESTAMP, 1),
(106, 3, 120, 0, 15, CURRENT_TIMESTAMP, 1),
(107, 3, 100, 0, 12, CURRENT_TIMESTAMP, 1),
(108, 3, 55, 0, 10, CURRENT_TIMESTAMP, 1),
(109, 3, 35, 0, 5, CURRENT_TIMESTAMP, 1),
(110, 3, 85, 0, 10, CURRENT_TIMESTAMP, 1);

-- LOJA 4: Outlet
INSERT INTO estoque_produto (produto_id, loja_id, quantidade, reservado, estoque_minimo, ultima_atualizacao, versao) 
VALUES 
(101, 4, 120, 0, 10, CURRENT_TIMESTAMP, 1),
(102, 4, 40, 0, 5, CURRENT_TIMESTAMP, 1),
(103, 4, 250, 0, 20, CURRENT_TIMESTAMP, 1),
(104, 4, 90, 0, 8, CURRENT_TIMESTAMP, 1),
(105, 4, 15, 0, 5, CURRENT_TIMESTAMP, 1),
(106, 4, 80, 0, 15, CURRENT_TIMESTAMP, 1),
(107, 4, 110, 0, 12, CURRENT_TIMESTAMP, 1),
(108, 4, 70, 0, 10, CURRENT_TIMESTAMP, 1),
(109, 4, 25, 0, 5, CURRENT_TIMESTAMP, 1),
(110, 4, 95, 0, 10, CURRENT_TIMESTAMP, 1);

-- LOJA 5: Online Store
INSERT INTO estoque_produto (produto_id, loja_id, quantidade, reservado, estoque_minimo, ultima_atualizacao, versao) 
VALUES 
(101, 5, 200, 0, 10, CURRENT_TIMESTAMP, 1),
(102, 5, 75, 0, 5, CURRENT_TIMESTAMP, 1),
(103, 5, 300, 0, 20, CURRENT_TIMESTAMP, 1),
(104, 5, 150, 0, 8, CURRENT_TIMESTAMP, 1),
(105, 5, 50, 0, 5, CURRENT_TIMESTAMP, 1),
(106, 5, 200, 0, 15, CURRENT_TIMESTAMP, 1),
(107, 5, 180, 0, 12, CURRENT_TIMESTAMP, 1),
(108, 5, 100, 0, 10, CURRENT_TIMESTAMP, 1),
(109, 5, 60, 0, 5, CURRENT_TIMESTAMP, 1),
(110, 5, 120, 0, 10, CURRENT_TIMESTAMP, 1);

-- Produtos com estoque baixo (para testes de alertas)
INSERT INTO estoque_produto (produto_id, loja_id, quantidade, reservado, estoque_minimo, ultima_atualizacao, versao) 
VALUES 
(201, 1, 3, 0, 5, CURRENT_TIMESTAMP, 1),
(202, 2, 4, 0, 10, CURRENT_TIMESTAMP, 1),
(203, 3, 2, 0, 8, CURRENT_TIMESTAMP, 1),
(204, 4, 1, 0, 5, CURRENT_TIMESTAMP, 1),
(205, 5, 3, 0, 15, CURRENT_TIMESTAMP, 1);

-- ==============================================================
-- Legendas dos Produtos para Referência:
-- ==============================================================
-- 101: Smartphone Samsung Galaxy
-- 102: Notebook Dell Inspiron  
-- 103: Camiseta Nike
-- 104: Tênis Adidas
-- 105: Perfume Channel
-- 106: Livro "Clean Code"
-- 107: Mouse Logitech
-- 108: Fone JBL
-- 109: Relógio Casio
-- 110: Mochila Eastpak
-- 201-205: Produtos com estoque baixo (teste de alertas)

-- ==============================================================
-- Legendas das Lojas para Referência:
-- ==============================================================
-- 1: Shopping Center
-- 2: Centro da Cidade
-- 3: Bairro Norte
-- 4: Outlet
-- 5: Online Store