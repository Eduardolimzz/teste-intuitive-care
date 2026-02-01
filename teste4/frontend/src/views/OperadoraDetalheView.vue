<script setup>
import { ref, onMounted } from "vue";
import { api } from "../services/api";
import { useRoute } from "vue-router";

const route = useRoute();
const cnpj = route.params.cnpj;

const dados = ref(null);
const loading = ref(false);
const error = ref(null);

async function carregar() {
  loading.value = true;
  try {
    const { data } = await api.get(`/operadoras/${cnpj}/despesas`);
    dados.value = data;
  } catch {
    error.value = "Erro ao carregar dados da operadora.";
  } finally {
    loading.value = false;
  }
}

onMounted(carregar);
</script>

<template>
  <div class="container">
    <h2>🏥 {{ dados?.razao_social }}</h2>
    <p><strong>CNPJ:</strong> {{ cnpj }}</p>

    <p v-if="loading">Carregando...</p>
    <p v-if="error">{{ error }}</p>

    <table v-if="dados">
      <thead>
        <tr>
          <th>Ano</th>
          <th>Trimestre</th>
          <th>Valor</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="d in dados.despesas" :key="`${d.ano}-${d.trimestre}`">
          <td>{{ d.ano }}</td>
          <td>{{ d.trimestre }}</td>
          <td>R$ {{ Number(d.valor_despesas).toLocaleString("pt-BR") }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.container {
  padding: 20px;
}
</style>
