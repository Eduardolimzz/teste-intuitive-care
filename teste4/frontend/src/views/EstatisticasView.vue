<script setup>
import { ref, onMounted } from "vue";
import { api } from "../services/api";

const dados = ref(null);

onMounted(async () => {
  const res = await api.get("/estatisticas");
  dados.value = res.data;
});
</script>

<template>
  <h1>Estatísticas Gerais</h1>

  <div v-if="dados">
    <p>Total de Despesas: <b>R$ {{ dados.total_despesas }}</b></p>
    <p>Média por Operadora: <b>R$ {{ dados.media_despesas }}</b></p>

    <h3>Top 5 Operadoras</h3>
    <ul>
      <li v-for="op in dados.top5_operadoras" :key="op.razao_social">
        {{ op.razao_social }} — R$ {{ op.total }}
      </li>
    </ul>
  </div>
</template>
