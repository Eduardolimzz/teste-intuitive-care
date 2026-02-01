<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { api } from "../services/api";

const router = useRouter();
const operadoras = ref([]);
const busca = ref("");
const page = ref(1);

async function carregar() {
  const res = await api.get("/operadoras", {
    params: { page: page.value, busca: busca.value || undefined },
  });
  operadoras.value = res.data.data;
}

function voltar() {
  router.push("/home");
}

onMounted(carregar);
</script>

<template>
  <div>
    <button @click="voltar" class="btn-voltar">← Voltar</button>

    <h1>Operadoras</h1>

    <div class="search">
      <input
        v-model="busca"
        placeholder="Buscar por CNPJ ou Razao Social"
        @keyup.enter="carregar"
      />
      <button @click="carregar">Buscar</button>
    </div>

    <table>
      <thead>
        <tr>
          <th>CNPJ</th>
          <th>Razao Social</th>
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

    <div class="pagination">
      <button @click="page--; carregar()" :disabled="page === 1">Anterior</button>
      <span>Pagina {{ page }}</span>
      <button @click="page++; carregar()">Proxima</button>
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

.search {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

input {
  flex: 1;
  padding: 10px;
  background: #252525;
  border: 1px solid #333;
  color: #ccc;
  outline: none;
}

input:focus {
  border-color: #555;
}

button {
  padding: 10px 20px;
  background: #252525;
  border: 1px solid #333;
  color: #ccc;
  cursor: pointer;
}

button:hover:not(:disabled) {
  background: #2a2a2a;
  border-color: #555;
}

button:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.pagination span {
  color: #888;
}
</style>