<template>
  <div id="worldChart" style="width: 100%; height: 600px;"></div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import worldMapData from 'echarts/map/json/world.json'; // 确保你有正确的路径和世界地图数据

const userData = ref([
  { name: 'China', value: 1409517397 },
  { name: 'India', value: 1339180127 },
  { name: 'United States', value: 324459463 },
  // 添加其他国家和相应的用户数
]);

onMounted(() => {
  const chart = echarts.init(document.getElementById('worldChart'));
  initWorldMap(chart);
});

function initWorldMap(chart) {
  const option = {
    title: {
      text: 'Global User Distribution',
      left: 'center',
      top: 'top'
    },
    tooltip: {
      trigger: 'item',
      formatter: function (params) {
        return `${params.name}: ${params.value ? params.value : 'No data'}`;
      }
    },
    visualMap: {
      type: 'piecewise',
      pieces: [
        { min: 1000000000, label: '1 Billion+', color: '#8A0808' },
        { min: 500000000, max: 999999999, label: '500M - 1B', color: '#B40404' },
        { min: 100000000, max: 499999999, label: '100M - 500M', color: '#DF0101' },
        { min: 50000000, max: 99999999, label: '50M - 100M', color: '#F78181' },
        { min: 1000000, max: 49999999, label: '1M - 50M', color: '#F5A9A9' },
        { max: 999999, label: '<1M', color: '#FBEFEF' }
      ],
      left: 'left',
      top: 'bottom',
      textStyle: {
        color: '#000'
      }
    },
    series: [
      {
        name: 'Users',
        type: 'map',
        mapType: 'world',  // 使用世界地图
        roam: true,
        data: userData.value,
        emphasis: {
          label: {
            show: false
          },
          itemStyle: {
            areaColor: '#c9dfaf'  // 高亮状态下的区域颜色
          }
        }
      }
    ]
  };

  echarts.registerMap('world', worldMapData);  // 注册地图数据
  chart.setOption(option);
}
</script>

<style scoped>
#worldChart {
  width: 100%;
  height: 100%;
}
</style>
