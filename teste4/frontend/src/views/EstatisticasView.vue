<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { api } from "../services/api";

const router = useRouter();
const dados = ref(null);

function voltar() {
  router.push("/home");
}

onMounted(async () => {
  const res = await api.get("/estatisticas");
  dados.value = res.data;
});
</script>

<template>
  <div>
    <button @click="voltar" class="btn-voltar">← Voltar</button>

    <h1>Estatisticas Gerais</h1>

    <div v-if="dados">
      <div class="stats">
        <div class="stat-box">
          <p class="label">Total de Despesas</p>
          <p class="value">R$ {{ dados.total_despesas }}</p>
        </div>

        <div class="stat-box">
          <p class="label">Media por Operadora</p>
          <p class="value">R$ {{ dados.media_despesas }}</p>
        </div>
      </div>

      <h2>Top 5 Operadoras</h2>
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>Razao Social</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(op, index) in dados.top5_operadoras" :key="op.razao_social">
            <td>{{ index + 1 }}</td>
            <td>{{ op.razao_social }}</td>
            <td>R$ {{ op.total }}</td>
          </tr>
        </tbody>
      </table>
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

h2 {
  font-size: 18px;
  margin: 30px 0 15px 0;
  color: #fff;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-box {
  background: #252525;
  border: 1px solid #333;
  padding: 20px;
}

.label {
  color: #888;
  margin-bottom: 10px;
  font-size: 14px;
}

.value {
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border: 1px solid #333;
  padding: 12px;
  text-align: left;
}

th {
  background: #252525;
  color: #fff;
}

tbody tr:hover {
  background: #252525;
}
</style>