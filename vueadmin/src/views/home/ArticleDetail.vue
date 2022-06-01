<template>
  <div style="color: #666">
    <div style="margin: 20px 0; ">
      <div class="pd-10" style="font-size: 20px; color: #3F5EFB; cursor: pointer">{{ article.name }}</div>
      <div style="font-size: 14px; margin-top: 10px">
        <i class="el-icon-time" style="margin-left: 10px"></i> <span>{{ article.time }}</span>
      </div>
    </div>

    <div style="margin: 20px 0">
      <mavon-editor
          class="md"
          :value="article.content"
          :subfield="false"
          :defaultOpen="'preview'"
          :toolbarsFlag="false"
          :editable="false"
          :scrollStyle="true"
          :ishljs="true"
      />
    </div>

  </div>
</template>

<script>

export default {
  name: "Article",
  data() {
    return {
      article: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      id: this.$route.query.id,
    }
  },
  created() {
    this.load()
  },

  methods: {
    load() {
      this.axios.get("/article/" + this.id).then(res => {
        this.article = res.data
      })
    },

  }
}
</script>

<style scoped>

</style>