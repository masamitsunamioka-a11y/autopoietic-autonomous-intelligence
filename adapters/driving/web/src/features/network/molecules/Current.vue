<script setup lang="ts">
import {useNetworkStore} from '../stores/network'
import Lamp from '../atoms/Lamp.vue'
import Name from '../atoms/Name.vue'

const store = useNetworkStore()
</script>
<template>
  <div class="status">
    <Lamp :status="store.current"/>
    <Name :text="store.current"/>
  </div>
</template>
<style scoped>
.status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
</style>
