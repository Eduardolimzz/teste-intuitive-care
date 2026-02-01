<template>
  <button :disabled="page === 1" @click="$emit('change', page - 1)">
    Anterior
  </button>
  <button :disabled="page === totalPages" @click="$emit('change', page + 1)">
    Próxima
  </button>
</template>

<script setup>
defineProps({
  page: Number,
  totalPages: Number
});
</script>
