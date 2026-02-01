<template>
  <ul>
    <li v-for="u in dados" :key="u.uf">
      {{ u.uf }} — R$ {{ u.total_despesas }}
    </li>
  </ul>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { api } from "../services/api";

const dados = ref([]);

onMounted(async () => {
  dados.value = (await api.get("/despesas-por-uf")).data.data;
});
</script>
