<script setup lang="ts">
import Episode from '../molecules/Episode.vue'
import Knowledge from '../molecules/Knowledge.vue'
import System from '../molecules/System.vue'
</script>
<template>
  <div class="panel">
    <Episode/>
    <Knowledge/>
    <System/>
  </div>
</template>
<style scoped>
.panel {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
</style>
