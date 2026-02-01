<template>
  <div class="layout">
    <nav class="menu">
      <RouterLink to="/operadoras">Operadoras</RouterLink>
      <RouterLink to="/estatisticas">Estatísticas</RouterLink>
      <RouterLink to="/despesas-uf">Despesas por UF</RouterLink>
    </nav>

    <main class="content">
      <slot />
    </main>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  background: #1e1e1e;
  color: #fff;
}

.menu {
  display: flex;
  gap: 20px;
  padding: 15px 20px;
  background: #111;
  border-bottom: 1px solid #333;
}

.menu a {
  color: #fff;
  text-decoration: none;
  font-weight: 500;
}

.menu a.router-link-active {
  color: #4fc3f7;
}

.content {
  padding: 20px;
}
</style>
