<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { api } from "../services/api";
import Chart from "chart.js/auto";

const router = useRouter();
const canvas = ref(null);
const carregou = ref(false);

function voltar() {
  router.push("/home");
}

onMounted(async () => {
  const res = await api.get("/despesas-por-uf");

  console.log("RESPOSTA COMPLETA:", res.data);

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
          backgroundColor: "#444",
          borderColor: "#666",
          borderWidth: 1,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          labels: {
            color: "#ccc",
          },
        },
      },
      scales: {
        x: {
          ticks: { color: "#888" },
          grid: { color: "#333" },
        },
        y: {
          ticks: { color: "#888" },
          grid: { color: "#333" },
        },
      },
    },
  });

  carregou.value = true;
});
</script>

<template>
  <div>
    <button @click="voltar" class="btn-voltar">← Voltar</button>

    <h1>Despesas por UF</h1>

    <div class="chart-container">
      <p v-if="!carregou" class="loading">Carregando grafico...</p>
      <canvas ref="canvas"></canvas>
    </div>
  </div>
</template>

<style scoped>
.btn-voltar {
  background: #252525;
  border: 1px solid #333;
  color: #ccc;
  padding: 8px 16px;
  cursor: pointer;
  margin-bottom: 20px;
}

.btn-voltar:hover {
  background: #2a2a2a;
  border-color: #555;
}

h1 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #fff;
}

.chart-container {
  background: #252525;
  border: 1px solid #333;
  padding: 20px;
  min-height: 400px;
}

.loading {
  color: #888;
  text-align: center;
  padding: 40px;
}

canvas {
  max-height: 500px;
}
</style>