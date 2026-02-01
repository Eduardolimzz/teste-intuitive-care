<template>
  <div>
    <nav class="navbar">
      <div class="brand">Intuitive Care - Eduardo Lima dos Santos</div>
    </nav>

    <div class="container">
      <router-view />
    </div>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: Arial, sans-serif;
  background: #1a1a1a;
  color: #ccc;
}

.navbar {
  background: #252525;
  padding: 15px 30px;
  border-bottom: 1px solid #333;
}

.brand {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}
</style>