<template>
  <div
    v-if="svgContent"
    class="svg-icon"
    v-html="svgContent"
    :style="{ width: `${width}px`, height: `${height}px` }"
  />
</template>

<script>
const svgModules = import.meta.glob("/src/svg/*.svg", { as: "raw" });

export default {
  name: "SvgIcon",
  props: {
    icon: {
      type: String,
      required: true,
    },
    width: {
      type: [String, Number],
      default: 24,
    },
    height: {
      type: [String, Number],
      default: 24,
    },
  },
  data() {
    return {
      svgContent: null,
    };
  },
  watch: {
    icon: {
      immediate: true,
      handler(newIcon) {
        this.loadSvg(newIcon);
      },
    },
  },
  methods: {
    async loadSvg(iconName) {
      const filePath = `/src/svg/${iconName}.svg`;
      if (svgModules[filePath]) {
        this.svgContent = await svgModules[filePath]();
      } else {
        console.error(`SVG file "${iconName}" not found in /src/svg`);
        this.svgContent = null;
      }
    },
  },
};
</script>

<style scoped>
.svg-icon {
  display: inline-block;
  vertical-align: middle;
}
</style>
