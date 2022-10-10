<template>
  <div class="pagination">
    <el-pagination
      background
      layout="prev,pager,next"
      :total="datas[0].length"
      :page-size="datas[1]"
      class="pagination"
      @current-change="changePage"
      @size-change="changePage"
    />
  </div>
</template>

<script>
export default {
  props: {
    datas: {
      type: Array,
      default: function() {
        return []
      }
    }
  },
  data() {
    return {
      filterData: []
    }
  },
  methods: {
    changePage(currentPage) {
      if (currentPage * this.datas[1] < this.datas[0].length) {
        this.filterData = this.datas[0].filter((data, key) => {
          if (key >= this.datas[1] * (currentPage - 1) && key < this.datas[1] * currentPage) return data
        })
      } else {
        this.filterData = this.datas[0].filter((data, key, bars) => {
          if (key >= this.datas[1] * (currentPage - 1) && key < bars.length) return data
        })
      }
      this.$emit('setShowData', this.filterData)
    }
  }
}
</script>

<style>

</style>
