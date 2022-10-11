<template>
  <el-dialog
    :title="dialog.title"
    :visible.sync="dialog.visible"
    :width="dialog.width"
  >
    <div class="app-container">
      <!--      <span>请选择需要进行建模分析的实验数据</span>-->
      <el-table
        stripe
        border
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        :data="dialog.data"
        :default-sort="{prop: 'expMaterialName', order: 'descending'}"
        @selection-change="selectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="物质名称" width="135" prop="expMaterialName" />
        <el-table-column label="物质类型名" width="135" prop="materialType.materialTypeName" />
        <el-table-column label="物质溶度" width="135" prop="expMaterialSolubility" />
        <el-table-column label="最新电流" width="135" prop="expNewestCurrentAsScientificNotation" />
        <el-table-column label="最新电位" width="135" prop="expNewestPotential" />
        <el-table-column label="缓冲溶液名" width="125" prop="bufferSolution.bufferSolutionName" />
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-view"
              @click="openDrawer('查看', 'view', scope.row)"
            >查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="bottompage">
        <Pagination :datas="PaginationData" @setShowData="getshowdata" />
      </div>

      <span slot="footer" class="dialog-footer" style="float: right">
        <el-button @click="back">返 回</el-button>
        <el-button type="primary" @click="backValue">确 定</el-button>
      </span>
    </div>
  </el-dialog>
</template>

<script>
import Pagination from './Pagination.vue'
export default {
  name: 'SimpleDialog',
  components: {
    Pagination
  },
  props: {
    dialog: {
      type: Object,
      default: function() {
        return {}
      }
    }
  },
  data() {
    return {
      selectExpDataId: []
    }
  },
  computed: {
    PaginationData() {
      return [this.dialog.data, 10]
    }
  },
  methods: {
    back() {
      this.dialog.visible = false
    },
    selectionChange(rows) {
      this.selectExpDataId = rows.map(k => {
        return k.expDataId
      })
    },
    backValue() {
      const ids = this.selectExpDataId
      this.$emit('finishSelectExpDatas', ids)
      this.back()
    },
    // 分页器更新展示数据
    getshowdata(data) {
      this.showdatas = data
    }
  }
}
</script>

<style scoped>
/* 抽屉全局box */
/* 外包div */
.app-container {
  display: flex;
  margin: 0 auto;
  flex-direction: column;
}
/* 展示电流电位盒子 */
.showVA {
  display: flex;
  width: 100vw;
}
/* 输入框盒子 */
.inputBox {
  display: flex;
  flex-direction: row;
  margin: 10px 5px;
}
.inputBar {
  width: 200px;
  line-height: 50px;
}
.inputBox p {
  font-size: 17px;
}
.inputBox p::after {
  content:'';
  display: block;
  position: relative;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: rgb(150, 195, 212);
}
.inputBox span {
  line-height: 50px;
  margin: 0 20px;
  font-size: 20px;
}
/* 按钮盒子 */
.buttonBox {
  text-align: right;
  margin: 10px 20px;
}
</style>
