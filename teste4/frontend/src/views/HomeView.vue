<template>
  <div class="home">
    <h1>Teste 4 - Intuitive Care</h1>
    <p>Selecione uma funcionalidade:</p>

    <div class="menu">
      <RouterLink to="/operadoras" class="menu-item">
        <h3>Listar Operadoras</h3>
        <p>Visualize e busque operadoras de saude</p>
      </RouterLink>

      <RouterLink to="/estatisticas" class="menu-item">
        <h3>Estatisticas Gerais</h3>
        <p>Confira dados consolidados e rankings</p>
      </RouterLink>

      <RouterLink to="/despesas-uf" class="menu-item">
        <h3>Despesas por UF</h3>
        <p>Analise de despesas por estado</p>
      </RouterLink>
    </div>
  </div>
</template>

<style scoped>
.home {
  text-align: center;
  margin-top: 20px;
}

h1 {
  font-size: 28px;
  margin-bottom: 10px;
  color: #fff;
}

p {
  color: #888;
  margin-bottom: 40px;
}

.menu {
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-width: 600px;
  margin: 0 auto;
}

.menu-item {
  background: #252525;
  border: 1px solid #333;
  padding: 25px;
  text-decoration: none;
  color: inherit;
  text-align: left;
  transition: background 0.2s;
}

.menu-item:hover {
  background: #2a2a2a;
  border-color: #555;
}

.menu-item h3 {
  color: #fff;
  margin-bottom: 8px;
  font-size: 18px;
}

.menu-item p {
  color: #888;
  margin: 0;
  font-size: 14px;
}
</style>