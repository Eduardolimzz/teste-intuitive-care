<script setup>
import { ref, onMounted } from "vue";
import { api } from "../services/api";

const operadoras = ref([]);
const busca = ref("");
const page = ref(1);

async function carregar() {
  const res = await api.get("/operadoras", {
    params: { page: page.value, busca: busca.value || undefined },
  });
  operadoras.value = res.data.data;
}

onMounted(carregar);
</script>

<template>
  <h1>Operadoras</h1>

  <input
    v-model="busca"
    placeholder="Buscar por CNPJ ou Razão"
    @keyup.enter="carregar"
  />

  <table>
    <thead>
      <tr>
        <th>CNPJ</th>
        <th>Razão Social</th>
        <th>UF</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="op in operadoras" :key="op.cnpj">
        <td>{{ op.cnpj }}</td>
        <td>{{ op.razao_social }}</td>
        <td>{{ op.uf }}</td>
      </tr>
    </tbody>
  </table>

  <button @click="page--; carregar()" :disabled="page === 1">Anterior</button>
  <button @click="page++; carregar()">Próxima</button>
</template>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
}

th, td {
  border: 1px solid #555;
  padding: 8px;
}
</style>
