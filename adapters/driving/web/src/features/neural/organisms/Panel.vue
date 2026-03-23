<script setup lang="ts">
import Current from "../../network/molecules/Current.vue";
import Tree from "../molecules/Tree.vue";
</script>
<template>
  <div class="neural-panel">
    <Current />
    <Tree />
  </div>
</template>
<style scoped>
.neural-panel {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding: 12px;
}
</style>
