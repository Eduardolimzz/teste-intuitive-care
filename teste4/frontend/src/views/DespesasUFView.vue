<script setup>
import { ref, onMounted } from "vue";
import { api } from "../services/api";
import Chart from "chart.js/auto";

const canvas = ref(null);
const carregou = ref(false);

onMounted(async () => {
  const res = await api.get("/despesas-por-uf");

  console.log("RESPOSTA COMPLETA:", res.data);

  // 🔥 AQUI ESTÁ O AJUSTE
  const dados = res.data.data;

  if (!dados || dados.length === 0) {
    console.warn("Nenhum dado retornado para despesas por UF");
    return;
  }

  const labels = dados.map(item => item.uf);
  const valores = dados.map(item => Number(item.total_despesas));

  new Chart(canvas.value, {
    type: "bar",
    data: {
      labels,
      datasets: [
        {
          label: "Total de Despesas (R$)",
          data: valores,
        },
      ],
    },
    options: {
      responsive: true,
    },
  });

  carregou.value = true;
});
</script>

<template>
  <h1>Despesas por UF</h1>

  <p v-if="!carregou">Carregando gráfico...</p>

  <canvas ref="canvas"></canvas>
</template>
