<template>
    <div>
      <button @click="toggleProfilePanel">Toggle Panel</button>
      <transition name="slide">
        <div v-if="showProfilePanel" style="position: fixed; right: 0; top: 60px; width: 300px; background-color: white; box-shadow: -1px 0 10px rgba(0,0,0,0.5);">
          Panel Content Here
        </div>
      </transition>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const showProfilePanel = ref(false);
  
  function toggleProfilePanel() {
    showProfilePanel.value = !showProfilePanel.value;
  }
  </script>
  
  <style scoped>
  .slide-enter-active, .slide-leave-active {
    transition: right 0.3s ease-in-out;
  }
  
  .slide-enter, .slide-leave-to {
    right: -300px;
  }
  
  .slide-enter-to, .slide-leave {
    right: 0;
  }
  </style>
  