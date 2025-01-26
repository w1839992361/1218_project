<script setup>
import { RouterView } from 'vue-router';
import Header from './header.vue';
import Footer from './footer.vue';
</script>

<template>
  <div class="min-h-screen flex flex-col bg-gray-100">
    <Header class="bg-white shadow-md" />

    <main class="flex-grow container mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="bg-white rounded-lg shadow-lg p-6">
        <RouterView />
      </div>
    </main>

    <Footer class="bg-gray-800 text-white" />
  </div>
</template>

<style scoped>
@media (min-width: 1024px) {
  main {
    max-width: 1368px;
  }
}

.router-view-transition-enter-active,
.router-view-transition-leave-active {
  transition: opacity 0.3s ease;
}

.router-view-transition-enter-from,
.router-view-transition-leave-to {
  opacity: 0;
}
</style>