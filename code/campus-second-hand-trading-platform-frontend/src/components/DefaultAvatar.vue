<template>
  <div
    class="default-avatar"
    :style="{ width: avatarSize, height: avatarSize }"
    :aria-label="displayCharacter"
    role="img"
  >
    {{ displayCharacter }}
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";

const props = withDefaults(
  defineProps<{
    name?: string | null;
    fallbackName?: string | null;
    size?: number | string;
  }>(),
  {
    name: "",
    fallbackName: "",
    size: 40,
  },
);

const avatarSize = computed(() => {
  const size = typeof props.size === "number"
    ? `${props.size}px`
    : props.size;
  return size || "40px";
});

const displayCharacter = computed(() => {
  const source = props.name?.trim() || props.fallbackName?.trim() || "用";
  return Array.from(source)[0]?.toUpperCase() || "用";
});
</script>

<style scoped>
.default-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 50%;
  background: var(--el-color-primary, #fb1e47);
  color: #ffffff;
  font-size: 1.35em;
  font-weight: 600;
  line-height: 1;
  user-select: none;
}
</style>
