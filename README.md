# Queen — Robocode AI Bot

Queen é um robô avançado desenvolvido para Robocode, combinando mira preditiva, movimentação adaptativa e um sistema simples de aprendizado baseado no comportamento do inimigo. O objetivo é melhorar continuamente sua precisão e evasão ao longo das batalhas, tornando-o competitivo em duelos 1v1.

---

## ✦ Descrição Geral

A Queen utiliza análise contínua do inimigo para ajustar:

- Distância ideal de combate  
- Firepower adaptativo  
- Evasão baseada em detecção de tiros  
- Mira preditiva linear  
- Radar com travamento contínuo  

As estatísticas são armazenadas por nome de inimigo, permitindo que o bot aprenda durante a batalha.

---

## ✦ Principais Funcionalidades

### 1. EnemyStats (Memória por Inimigo)
A Queen registra:
- Velocidade média
- Distância média
- Taxa de acertos (hit rate)
- Quantidade de tiros disparados e acertados

Esses dados são usados para adaptar estratégia e mira.

### 2. Mira Preditiva (Predictive Aiming)
A mira calcula:
- Posição atual do inimigo  
- Velocidade  
- Direção  
- Velocidade da bala  

Depois compensa o movimento para atirar onde o inimigo estará.

### 3. Movimento Adaptativo
A movimentação leva em conta:
- Queda de energia do inimigo (indica tiro)  
- Mudança automática de direção para evasão  
- Ajuste dinâmico de distância ideal  
- Anti-rush / anti-ram para inimigos agressivos  

### 4. Firepower Inteligente
A potência dos tiros é ajustada conforme:
- Distância
- Taxa de acerto
- Energia atual da Queen  
- Estilo de movimento do inimigo  

### 5. Radar Lock
O radar permanece travado no inimigo utilizando overshoot para evitar perda de rastreio.

---

## ✦ Estrutura do Projeto
  aaaa/Queen.java

O arquivo deve ser colocado em:
  /robocode/robots/aaaa/Queen.java
---

## ✦ Como Compilar e Usar

1. Copie a pasta `aaaa` para:
  C:\robocode\robots\

2. Abra o Robocode  
3. Vá em **Robot → Compile**  
4. Selecione `Queen.java`  
5. Inicie uma batalha e escolha a Queen

---

## ✦ Tecnologias Utilizadas

- Java  
- Robocode API  
- Algoritmos preditivos simples  
- Lógica adaptativa por estatísticas  

  
