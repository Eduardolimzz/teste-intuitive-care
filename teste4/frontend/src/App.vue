<template>
  <div>
    <nav class="navbar">
      <router-link to="/operadoras">Operadoras</router-link>
      <router-link to="/estatisticas">Estatísticas</router-link>
      <router-link to="/despesas-uf">Despesas por UF</router-link>
    </nav>

    <div class="container">
      <router-view />
    </div>
  </div>
</template>

<style>
body {
  margin: 0;
  font-family: Arial, sans-serif;
  background: #1e1e1e;
  color: #fff;
}

.navbar {
  display: flex;
  gap: 20px;
  padding: 15px;
  background: #000;
}

.navbar a {
  color: #4fc3f7;
  text-decoration: none;
  font-weight: bold;
}

.navbar a.router-link-active {
  text-decoration: underline;
}

.container {
  padding: 20px;
}
</style>
