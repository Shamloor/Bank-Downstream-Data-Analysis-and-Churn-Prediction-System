<template>
  <div class="analysis-block">
    <div id="balanceChart" class="chart"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';

// 定义图表实例
const chartInstance = ref(null);

axios.defaults.baseURL = '/api'; // 设置基础路径
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`; // 设置请求头
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// 数据获取函数
async function fetchBalanceDistribution() {
  try {

    const response = await axios.post('/mysql/data/ads_customer_balance_distribution'); // 替换为后端接口路径
    console.log(response);
    if (response.data.code === 200) {
      const chartData = response.data.data; // 假设返回数据格式正确
      renderBalanceChart(chartData);
    } else {
      console.error('API Error:', response.data.message);
    }
  } catch (error) {
    console.error('Fetch Error:', error);
  }
}

// 渲染图表函数
function renderBalanceChart(data) {
  // 获取图表 DOM 容器
  const chartDom = document.getElementById('balanceChart');
  if (!chartDom) {
    console.error('Chart DOM not found');
    return;
  }

  if (!chartInstance.value) {
    chartInstance.value = echarts.init(chartDom);
  }

  const balanceRanges = data.map(item => item.BalanceRange);
  const customerCounts = data.map(item => item.CustomerCount);

  // 配置 ECharts 图表
  const option = {
    title: {
      text: '客户余额分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '客户数量',
        type: 'pie',
        radius: '50%',
        data: balanceRanges.map((range, index) => ({
          value: customerCounts[index],
          name: range
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };

  chartInstance.value.setOption(option);
}

// 生命周期钩子，加载数据
onMounted(() => {
  fetchBalanceDistribution();
});
</script>

<style scoped>
.chart {
  width: 100%;
  height: 400px;
}
</style>
