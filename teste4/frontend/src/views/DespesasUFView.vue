<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { api } from "../services/api";
import Chart from "chart.js/auto";

const router = useRouter();
const canvas = ref(null);
const carregou = ref(false);
const erro = ref(null);
const loading = ref(false);

function voltar() {
  router.push("/home");
}

onMounted(async () => {
  loading.value = true;
  erro.value = null;

  try {
    console.log("Buscando dados de /despesas-por-uf...");

    const res = await api.get("/despesas-por-uf");

    console.log("RESPOSTA COMPLETA:", res);
    console.log("DADOS:", res.data);

    const dados = res.data.data;

    console.log("Array de dados:", dados);
    console.log("Quantidade:", dados ? dados.length : 0);

    if (!dados || dados.length === 0) {
      console.warn("Nenhum dado retornado para despesas por UF");
      erro.value = "Nenhum dado disponível para exibir";
      loading.value = false;
      return;
    }

    // Mostra os primeiros 3 itens
    console.log("Primeiros 3 itens:", dados.slice(0, 3));

    const labels = dados.map(item => item.uf);
    const valores = dados.map(item => {
      const valor = Number(item.total_despesas);
      console.log(`   ${item.uf}: ${item.total_despesas} -> ${valor}`);
      return valor;
    });

    console.log("Labels:", labels);
    console.log("Valores:", valores);

    // Verifica se o canvas existe
    if (!canvas.value) {
      console.error("Canvas não encontrado!");
      erro.value = "Erro ao renderizar gráfico";
      loading.value = false;
      return;
    }

    console.log("Canvas encontrado, criando gráfico...");

    new Chart(canvas.value, {
      type: "bar",
      data: {
        labels,
        datasets: [
          {
            label: "Total de Despesas (R$)",
            data: valores,
            backgroundColor: "#4CAF50",
            borderColor: "#45a049",
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
          tooltip: {
            callbacks: {
              label: function(context) {
                let label = context.dataset.label || '';
                if (label) {
                  label += ': ';
                }
                if (context.parsed.y !== null) {
                  label += 'R$ ' + context.parsed.y.toLocaleString('pt-BR', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                  });
                }
                return label;
              }
            }
          }
        },
        scales: {
          x: {
            ticks: { color: "#888" },
            grid: { color: "#333" },
          },
          y: {
            ticks: {
              color: "#888",
              callback: function(value) {
                return 'R$ ' + value.toLocaleString('pt-BR');
              }
            },
            grid: { color: "#333" },
          },
        },
      },
    });

    console.log("Gráfico criado com sucesso!");
    carregou.value = true;

  } catch (error) {
    console.error("ERRO ao buscar/renderizar dados:", error);
    console.error("Detalhes do erro:", error.response || error.message);
    erro.value = `Erro ao carregar dados: ${error.message}`;
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div>
    <button @click="voltar" class="btn-voltar">← Voltar</button>

    <h1>Despesas por UF</h1>

    <div class="chart-container">
      <p v-if="loading" class="loading">Carregando dados...</p>
      <p v-if="erro" class="erro"> {{ erro }}</p>
      <p v-if="!loading && !erro && !carregou" class="loading">Preparando gráfico...</p>
      <canvas ref="canvas" v-show="carregou"></canvas>
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading {
  color: #888;
  text-align: center;
  padding: 40px;
  font-size: 16px;
}

.erro {
  color: #ff6b6b;
  text-align: center;
  padding: 40px;
  font-size: 16px;
}

canvas {
  max-height: 500px;
  width: 100%;
}
</style>