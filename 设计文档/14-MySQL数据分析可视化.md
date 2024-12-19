+ `controller/MysqlController` 前端需要调用获取可视化相关信息时会调用这个文件
+ `service/MysqlService` Controller背后的具体逻辑
+ 前端获取数据以及Echart逻辑 (关于展示的Echart图可能为了美观而修改)

```javascript
// region 可视化
const sidebarStyle = ref({ left: '-270px' });  // Control sidebar style

// 默认activeView
const activeView = ref('balance_distribution'); // Currently Active View

// 存储不同的图表实例
const chartInstances = {
  balanceChart: null,
  churnChart: null,
  featureChart: null,
  valueChart: null
};

// 数据获取和可视化函数
async function fetchDataAndRenderChart(tableName) {
  try {
    // 动态构建 API 路径
    const response = await axios.post(`/mysql/data/${tableName}`);
    console.log("Now print the structure of response", response);

    // 如果接口返回的状态码是200
    if (response.data.code === 200) {
      const chartData = response.data.data; // 假设返回数据格式正确

      // 根据 tableName 渲染不同的图表
      if (tableName === 'ads_customer_balance_distribution') {
        renderBalanceDistribution(chartData);
      } else if (tableName === 'ads_customer_churn_rate') {
        renderChurnRate(chartData);
      } else if (tableName === 'ads_customer_feature_distribution') {
        renderFeatureDistribution(chartData);
      } else if (tableName === 'ads_customer_value_segmentation') {
        renderValueSegmentation(chartData);
      } else {
        console.error('Invalid tableName:', tableName);
      }

    } else {
      console.error('API Error:', response.data.message);
    }
  } catch (error) {
    console.error('Fetch Error:', error);
  }
}
// 渲染ads_customer_balance_distribution
function renderBalanceDistribution(data) {
  // 获取图表 DOM 容器
  const chartDom = document.getElementById('balanceChart');
  if (!chartDom) {
    //console.error('Chart DOM not found');
    return;
  }

  // 检查图表实例是否已存在，如果不存在则创建一个新的实例
  if (!chartInstances.balanceChart) {
    chartInstances.balanceChart = echarts.init(chartDom);
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

  chartInstances.balanceChart.setOption(option);
}
// 渲染ads_customer_churn_rate
function renderChurnRate(data) {
  // 获取图表 DOM 容器
  const chartDom = document.getElementById('churnChart');
  if (!chartDom) {
    //console.error('Churn Chart DOM not found');
    return;
  }

  // 检查图表实例是否已存在，如果不存在则创建一个新的实例
  if (!chartInstances.churnChart) {
    chartInstances.churnChart = echarts.init(chartDom);
  }

  // 从数据中提取相关字段
  const regions = [...new Set(data.map(item => item.Geography))];  // 地理位置
  const ageGroups = [...new Set(data.map(item => item.AgeGroup))];  // 年龄组
  const genders = [...new Set(data.map(item => item.Gender))];  // 性别

  const churnRates = data.map(item => item.ChurnRate);  // 流失率
  const totalCustomers = data.map(item => item.TotalCustomerCount);  // 总客户数
  const exitedCustomers = data.map(item => item.ExitedCount);  // 流失客户数

  // 配置 ECharts 图表的共用选项
  const option = {
    title: {
      text: '客户流失率分析',
      subtext: '按地区、年龄组和性别',
      left: 'center',
      top: '10',
      textStyle: {
        fontWeight: 'bold',
        fontSize: 18,
        color: '#2c3e50',
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        return `${params.name}<br>流失率: ${params.value}%<br>流失客户: ${exitedCustomers[params.dataIndex]}<br>总客户: ${totalCustomers[params.dataIndex]}`;
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: regions.concat(ageGroups, genders),
      top: 'middle',
      textStyle: {
        color: '#34495e',
      }
    },
    grid: {
      left: '10%',
      right: '10%',
      bottom: '15%',
      top: '20%',
    },
    xAxis: {
      type: 'category',
      data: regions,  // 或者根据需要使用 `ageGroups` 或 `genders`
      axisLabel: {
        interval: 0,
        rotate: 30,
        fontSize: 12,
      },
      axisLine: {
        lineStyle: {
          color: '#2c3e50',
        }
      }
    },
    yAxis: {
      type: 'value',
      name: '流失率 (%)',
      axisLine: {
        lineStyle: {
          color: '#2c3e50',
        }
      },
      axisLabel: {
        formatter: '{value} %',
      },
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#BDC3C7',
        }
      }
    },
    series: [
      {
        name: '客户流失率',
        type: 'bar',
        data: churnRates,
        itemStyle: {
          color: function(params) {
            // 通过不同的区域/年龄组/性别给不同的条形图设置颜色
            const colorPalette = ['#2980b9', '#e74c3c', '#27ae60', '#f39c12'];
            return colorPalette[params.dataIndex % colorPalette.length];
          },
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}%',
          color: '#fff',
          fontSize: 12,
        },
      },
    ],
  };

  // 将 ECharts 配置应用到图表实例中
  chartInstances.churnChart.setOption(option);
}
// 渲染ads_customer_feature_distribution
function renderFeatureDistribution(data) {
  // 获取图表 DOM 容器
  const chartDom = document.getElementById('featureChart');
  if (!chartDom) {
    //console.error('Chart DOM not found');
    return;
  }

  // 检查图表实例是否已存在，如果不存在则创建一个新的实例
  if (!chartInstances.featureChart) {
    chartInstances.featureChart = echarts.init(chartDom);
  }

  // 提取性别和年龄组的分布数据
  const genders = data.map(item => item.Gender);
  const genderCounts = data.map(item => item.CustomerCount);

  const ageGroups = data.map(item => item.AgeGroup);
  const ageGroupCounts = data.map(item => item.CustomerCount);

  // 配置 ECharts 图表
  const option = {
    title: {
      text: '客户特征分布',
      subtext: '按性别与年龄分组',
      left: 'center',
      textStyle: {
        fontWeight: 'bold',
        fontSize: 18,
        color: '#333'
      },
      subtextStyle: {
        fontSize: 14,
        color: '#777'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
      backgroundColor: 'rgba(50, 50, 50, 0.7)',
      borderColor: '#ccc',
      borderWidth: 1,
      textStyle: {
        color: '#fff'
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      data: [...new Set(genders), ...new Set(ageGroups)],
      textStyle: {
        color: '#444',
        fontSize: 14
      }
    },
    series: [
      {
        name: '性别分布',
        type: 'pie',
        radius: '45%',
        center: ['25%', '50%'],
        data: genders.map((gender, index) => ({
          value: genderCounts[index],
          name: gender
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
            color: '#ff7f50' // 优先显示效果
          }
        },
        itemStyle: {
          normal: {
            color: function(params) {
              const colorList = ['#F44336', '#2196F3', '#4CAF50', '#FFC107'];
              return colorList[params.dataIndex % colorList.length];
            }
          }
        },
      },
      {
        name: '年龄分布',
        type: 'pie',
        radius: '45%',
        center: ['75%', '50%'],
        data: ageGroups.map((ageGroup, index) => ({
          value: ageGroupCounts[index],
          name: ageGroup
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
            color: '#8bc34a' // 优先显示效果
          }
        },
        itemStyle: {
          normal: {
            color: function(params) {
              const colorList = ['#FF5722', '#9C27B0', '#03A9F4', '#8BC34A'];
              return colorList[params.dataIndex % colorList.length];
            }
          }
        },
      }
    ]
  };

  // 使用配置项设置图表
  chartInstances.featureChart.setOption(option);
}
// 渲染ads_customer_value_segmentation
function renderValueSegmentation(data) {
  // 获取图表 DOM 容器
  const chartDom = document.getElementById('valueChart');
  if (!chartDom) {
    //console.error('Chart DOM not found');
    return;
  }

  // 检查图表实例是否已存在，如果不存在则创建一个新的实例
  if (!chartInstances.valueChart) {
    chartInstances.valueChart = echarts.init(chartDom);
  }

  // 提取分层数据
  const valueSegments = data.map(item => item.ValueSegment);
  const customerCounts = data.map(item => item.CustomerCount);
  const avgBalances = data.map(item => item.AvgBalance);
  const avgCreditScores = data.map(item => item.AvgCreditScore);
  const activeRates = data.map(item => item.ActiveCustomerRate);
  const churnRates = data.map(item => item.ChurnRate);

  // 配置 ECharts 图表
  const option = {
    title: {
      text: '客户价值分层分析',
      subtext: '按客户价值段进行细分',
      left: 'center',
      textStyle: {
        fontWeight: 'bold',
        fontSize: 18,
        color: '#333'
      },
      subtextStyle: {
        fontSize: 14,
        color: '#777'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
      backgroundColor: 'rgba(50, 50, 50, 0.7)',
      borderColor: '#ccc',
      borderWidth: 1,
      textStyle: {
        color: '#fff'
      }
    },
    grid: {
      left: '10%',
      right: '10%',
      bottom: '10%',
      top: '15%',
      containLabel: true
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      data: valueSegments,
      textStyle: {
        color: '#444',
        fontSize: 14
      }
    },
    series: [
      // 饼图 - 客户数量分布
      {
        name: '客户数量分布',
        type: 'pie',
        radius: '40%',
        center: ['25%', '50%'],
        data: valueSegments.map((segment, index) => ({
          value: customerCounts[index],
          name: segment
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
            color: '#ff7f50' // 高亮效果
          }
        },
        itemStyle: {
          normal: {
            color: function(params) {
              const colorList = ['#FF5722', '#9C27B0', '#03A9F4', '#4CAF50', '#FFC107'];
              return colorList[params.dataIndex % colorList.length];
            }
          }
        }
      },
      // 柱状图 - 平均余额 & 平均信用分数
      {
        name: '平均余额 & 平均信用分数',
        type: 'bar',
        xAxisIndex: 0,
        yAxisIndex: 0,
        data: avgBalances,
        barWidth: 20,
        itemStyle: {
          color: '#2196F3'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        }
      },
      {
        name: '平均信用分数',
        type: 'bar',
        xAxisIndex: 0,
        yAxisIndex: 0,
        data: avgCreditScores,
        barWidth: 20,
        itemStyle: {
          color: '#8BC34A'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        }
      },
      // 折线图 - 活跃客户率 & 流失率
      {
        name: '活跃客户率',
        type: 'line',
        data: activeRates,
        smooth: true,
        lineStyle: {
          color: '#FF9800'
        },
        symbolSize: 8,
        itemStyle: {
          color: '#FF9800'
        }
      },
      {
        name: '流失率',
        type: 'line',
        data: churnRates,
        smooth: true,
        lineStyle: {
          color: '#F44336'
        },
        symbolSize: 8,
        itemStyle: {
          color: '#F44336'
        }
      }
    ],
    xAxis: [
      {
        type: 'category',
        data: valueSegments,
        axisLabel: {
          rotate: 45,
          textStyle: {
            color: '#333',
            fontSize: 14
          }
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '金额/分数',
        axisLabel: {
          formatter: '{value}'
        },
        splitLine: {
          show: true
        }
      }
    ]
  };

  // 使用配置项设置图表
  chartInstances.valueChart.setOption(option);
}

// 监听 activeView 变化, 切换回时重新渲染图表
watch(activeView, async (newView, oldView) => {
  // 监听不同的 view 类型
  if (newView === 'balance_distribution') {
    await nextTick();  // 等待 DOM 更新完成后初始化图表
    await fetchDataAndRenderChart("ads_customer_balance_distribution");
  } else if (newView === 'churn_rate') {
    await nextTick();
    await fetchDataAndRenderChart("ads_customer_churn_rate");
  } else if (newView === 'feature_distribution') {
    await nextTick();
    await fetchDataAndRenderChart("ads_customer_feature_distribution");
  } else if (newView === 'value_segmentation') {
    await nextTick();
    await fetchDataAndRenderChart("ads_customer_value_segmentation");
  }

  // 离开某个视图时销毁图表实例
  if (oldView === 'balance_distribution') {
    disposeChart('balanceChart');
  } else if (oldView === 'churn_rate') {
    disposeChart('churnChart');
  } else if (oldView === 'feature_distribution') {
    disposeChart('featureChart');
  } else if (oldView === 'value_segmentation') {
    disposeChart('valueChart');
  }
});

// 销毁 ECharts 实例, 保护内存
function disposeChart(chartName) {
  if (chartInstances[chartName]) {
    chartInstances[chartName].dispose();
    chartInstances[chartName] = null;
  }
}

// 启动时执行生命周期钩子
onMounted(() => {
  fetchDataAndRenderChart("ads_customer_balance_distribution");
  fetchDataAndRenderChart("ads_customer_churn_rate");
  fetchDataAndRenderChart("ads_customer_feature_distribution");
  fetchDataAndRenderChart("ads_customer_value_segmentation");
});
// endregion
```




