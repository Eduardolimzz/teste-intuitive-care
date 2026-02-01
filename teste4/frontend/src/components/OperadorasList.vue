<template>
  <ul>
    <li v-for="o in operadoras" :key="o.cnpj">
      {{ o.razao_social }} — {{ o.uf }}
      <button @click="$emit('detalhar', o.cnpj)">Ver</button>
    </li>
  </ul>
</template>

<script setup>
defineProps({ operadoras: Array });
</script>
