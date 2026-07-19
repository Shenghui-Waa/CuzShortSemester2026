<template>
  <router-view />
  <AnnouncementPopup />
  <AiAssistant />
</template>

<script setup lang="ts">
import AnnouncementPopup from "@/components/AnnouncementPopup.vue";
import AiAssistant from "@/components/AiAssistant.vue";
</script>