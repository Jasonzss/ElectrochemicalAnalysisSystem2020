<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme

export default {
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '500px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val)
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    },
    setOptions(param) {
      const { potential, current, newCurrent } = param
      console.log(potential)
      console.log(current)
      console.log(newCurrent)
      this.chart.setOption({
        xAxis: {
          // data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          data: potential,
          boundaryGap: false,
          axisTick: {
            show: false
          }
        },
        grid: {
          left: 10,
          right: 10,
          bottom: 20,
          top: 30,
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          padding: [5, 10]
        },
        yAxis: {
          axisTick: {
            show: false
          }
        },
        legend: {
          // data: ['expected', 'actual']
          // data: ['波形曲线', '测试线']
          data: ['最初波形曲线', '最新波形曲线']
        },
        /* 工具栏 */
        toolbox: {
          show: true,
          feature: {
            dataZoom: {
              yAxisIndex: 'none'
            },
            dataView: { readOnly: false },
            // magicType: { type: ['line', 'bar'] },
            magicType: { type: ['line'] },
            restore: {},
            saveAsImage: {}
          },
          iconStyle: {
            borderColor: '#ECB16E'
          }
        },
        series: [{
          name: '最初波形曲线', itemStyle: {
            normal: {
              color: '#FF005A',
              lineStyle: {
                color: '#FF005A',
                width: 2
              }
            }
          },
          smooth: true,
          type: 'line',
          data: current,
          animationDuration: 2800,
          animationEasing: 'cubicInOut'
        },
        {
          name: '最初波形曲线',
          smooth: true,
          type: 'line',
          itemStyle: {
            normal: {
              color: '#3888fa',
              lineStyle: {
                color: '#3888fa',
                width: 2
              },
              areaStyle: {
                color: '#f3f8ff'
              }
            }
          },
          data: newCurrent,
          animationDuration: 2800,
          animationEasing: 'quadraticOut'
        }]
      })
//       this.chart.getZr().on('click', function(params) {
//         // 获取像素坐标点
//         const pointInPixel = [params.offsetX, params.offsetY]
//         const { target, topTarget } = params
//         // 判断点击的点在  点击在折线的拐点 || 折线上
//         if (target?.z === 2 || topTarget?.z === 2) {
//           // 获取这条折线的 信息 也就是 index
//           // 如果是拐点，直接读取 target.seriesIndex
//           // 如果是折线上的点，读取 topTarget 对象下的继续寻找parent的信息的index
//           const axs = target
//             ? target.seriesIndex
//             : topTarget.parent?.parent?.__ecComponentInfo?.index
//           console.log(axs)
//         }
//       })
// // 将可以响应点击事件的范围内，鼠标样式设为pointer--------------------
//       this.chart.getZr().on('mousemove', function(params) {
//         const { topTarget } = params
//         // 给折线的鼠标悬浮 变为 小手
//         if (topTarget?.z === 2) {
//           this.chart.getZr().setCursorStyle('pointer')
//         }
//       })
    }
  }
}
</script>
