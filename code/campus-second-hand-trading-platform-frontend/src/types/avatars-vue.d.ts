declare module "@tsyanst/avatars-vue" {
  import type { DefineComponent } from "vue";

  export const GradientAvatar: DefineComponent<{
    seed?: string | number;
    size?: string | number;
    colors?: string[];
    pattern?: string;
  }>;
}
