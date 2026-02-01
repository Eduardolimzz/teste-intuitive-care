<template>
  <div class="home">
    <h1>Teste 4 – Intuitive Care</h1>
    <p>Selecione uma funcionalidade para testar:</p>

    <ul class="menu">
      <li>
        <RouterLink to="/operadoras"> Listar Operadoras</RouterLink>
      </li>
      <li>
        <RouterLink to="/estatisticas"> Estatísticas Gerais</RouterLink>
      </li>
      <li>
        <RouterLink to="/despesas-uf"> Despesas por UF</RouterLink>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.home {
  text-align: center;
  margin-top: 80px;
}
.menu {
  list-style: none;
  padding: 0;
}
.menu li {
  margin: 20px 0;
}
a {
  font-size: 18px;
  color: #42b983;
  text-decoration: none;
}
</style>
