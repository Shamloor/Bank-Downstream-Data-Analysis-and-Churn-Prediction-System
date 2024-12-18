<template>
  <div>
    <!-- 导航栏 -->
    <div class="navbar" :style="{ backgroundColor: navbarBg }">
      <div class="logo">A&P</div>
      <el-button @click="scrollTo('home')">主页</el-button>
      <el-button @click="handleNavigation('RealTimeDisplay')">实时修改日志</el-button>
      <el-button @click="handleNavigation('DataAnalysis')">分析可视化</el-button>
      <el-button @click="handleNavigation('ModelTraining')">训练模型</el-button>
      <el-button @click="handleNavigation('NewDataPrediction')">预测新数据</el-button>
      <div class="profile-icon" @click="toggleProfilePanel">
        <img src="./img/个人中心.png" style="max-width: 50%; max-height: 50%; width: auto; height: auto;">
      </div>
    </div>

    <!-- 用户面板(打开右上角的用户图标) -->
    <div class="profile-panel" :style="{ right: profilePanelStyle.right, backgroundColor: navbarBg }">
      <div class="profile-content">
        <img src="./img/用户头像.jpg" alt="User Avatar" class="user-avatar"/>
        <div class="username" id="username-display"></div>
        <div @click="showHelp" class="help-link">Status: Analyzed</div>
        <el-button @click="userLogout" style="width: 100%; font-size: large;">Logout</el-button>
      </div>
    </div>

    <!-- 主页, 包括主题文本与可视化按钮, 轮播图片, 滚动动画 -->
    <div id="home" class="content-section">
      <div id="home1">
        <section id="text">
          <header style="font-size: 14px;">
            <h1>欢迎来到</h1>
            <h1>银行下游数据分析与预测系统</h1>
            <p style="font-size: 14px;">探索全面的数据分析以了解市场趋势和客户行为。</p>
          </header>
          <article style="font-size: 14px;">
            <h2>关键功能</h2>
            <ul>
              <li>实时展示修改日志</li>
              <li>每日更新下游数据分析结果</li>
              <li>数据建模与预测趋势</li>
            </ul>
          </article>
            <div class="actions">
              <button  @click="sshcommand('/home/niit/bin/everyday_update.sh today > /home/niit/bin/new_everyday_update.log')">立刻开始全新的可视化！</button>
            </div>
        </section>
        <div class="image-slider">
          <div class="carousel">
            <div class="carousel-inner" :style="{ 'transform': `translateX(-${currentIndex * 100}%)` }" @click="next">
              <div class="carousel-item" v-for="(image, index) in images" :key="index">
                <img :src="image.src" :alt="image.alt">
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="marquee" style="margin-top: 60px;">
        <div class="marquee-content">
          <span v-for="n in 50" :key="n" class="marquee-item">待修改</span>
        </div>
      </div>
    </div>

    <!-- 修改日志实时展示模块 -->
    <div id="RealTimeDisplay" class="content-section">
      <div class="line" style="border-bottom: 1px solid #f6f5f5; display: flex"></div>
      <header style="margin-top: 70px; margin-left: 20px; display: flex; justify-content: space-between;">
        <h1>实时可视</h1>
      </header>
      <div class="message-container">
        <div class="data-list">
          <transition-group name="list" tag="div" mode="out-in">
            <!-- 仅显示 newItem.data -->
            <div
                v-for="(item, index) in dataList"
                :key="item.id"
                class="data-item"
                :class="{ 'highlight': item.changed }"
            >
              {{ item.data }}
            </div>
          </transition-group>
        </div>
      </div>
    </div>

    <!-- 每日数据分析可视化模块 -->
    <div id="DataAnalysis" class="content-section">
      <div class="line" style="border-bottom: 1px solid #c0c0c0; display: flex"></div>
      <header style="margin-top: 70px; margin-left: 20px; display: flex; justify-content: space-between;">
        <h1>数据分析</h1>
        <button style="height: 80%; font-size: 25px; margin-right: 20px; margin-top: 20px;" class="toggle-sidebar-button" @click="toggleSidebar">☰</button>
      </header>
      <div class="sidebar" :style="sidebarStyle">
        <!-- Navigation content -->
        <ul>
          <li><button class="sidebar-btn" @click="setActiveView('balance_distribution')">客户余额分布</button></li>
          <li><button class="sidebar-btn" @click="setActiveView('churn_rate')">客户流失率</button></li>
          <li><button class="sidebar-btn" @click="setActiveView('feature_distribution')">客户特征分布</button></li>
          <li><button class="sidebar-btn" @click="setActiveView('value_segmentation')">客户价值细分</button></li>
        </ul>
      </div>

      <!-- 客户余额分布 -->
      <div v-if="activeView === 'balance_distribution'" class="analysis-block">
        <div id="balanceChart" class="chart"></div>
      </div>
      <!-- 客户流失率 -->
      <div v-if="activeView === 'churn_rate'" class="analysis-block">
        <div id="churnChart" class="chart"></div>
      </div>
      <!-- 客户特征分布 -->
      <div v-if="activeView === 'feature_distribution'" class="analysis-block">
        <div id="featureChart" class="chart"></div>
      </div>
      <!-- 客户价值细分 -->
      <div v-if="activeView === 'value_segmentation'" class="analysis-block">
        <div id="valueChart" class="chart"></div>
      </div>

    </div>

    <!-- 模型训练模块 -->
    <div id="ModelTraining" class="content-section">
      <div class="line" style="border-bottom: 1px solid #c0c0c0;"></div>
      <header style="margin-top: 70px; margin-left: 20px;">
        <h1>模型训练</h1>
      </header>
      <div class="button">
        <button style="font-size: 20px;" @click="sshcommand('/home/niit/bin/model_training.sh > /home/niit/bin/model_training.log')">开始训练</button>
      </div>
      <div class="table-container" :key="componentKey1"><table style="width: 100%;">
          <thead>
            <tr>
              <th v-for="(header, index) in mheaders" :key="'header-' + index">{{ header }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, index) in mprocessedData" :key="'row-' + index">
              <td v-for="(item, idx) in row" :key="'cell-' + index + '-' + idx">{{ item }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="row" :key="componentKey2">
        <img src="./img/roc_curve.png" alt="" class="train-chart" style="background-color: #cfcfcf;">
        <img src="./img/feature_importances.png" alt="" class="train-chart" style="background-color: #cfcfcf;">
      </div>
    </div>

    <!-- 用户流失率预测模块 -->
    <div id="NewDataPrediction" class="content-section">
      <div class="line" style="border-bottom: 1px solid #c0c0c0;"></div>
      <header style="margin-top: 70px; margin-left: 20px;">
        <h1>上传新数据进行预测</h1>
      </header>
      <div class="upload-area" @dragover.prevent="onDragOver" @drop.prevent="onFileDrop">
        <input type="file" id="file" ref="fileInput" @change="handleFileUpload" accept=".csv" hidden>
        <p v-if="!selectedFile">将文件拖拽到这里或<span class="upload-link" @click="triggerXFileInput">选择一个文件</span></p>
        <p v-else>{{ selectedFile.name }}</p>
      </div>
      <div class="p-button" style="margin-left: 42%;">
        <button @click="uploadFile" :disabled="!selectedFile" style="border: 0px;">上传</button>
        <button @click="sshcommand('/home/niit/bin/prediction.sh > /home/niit/bin/prediction.log')" :disabled="!selectedFile" style="border: 0px;">开始预测</button>
        <button @click="go" :disabled="!selectedFile" style="border: 0px;">下载结果</button>
      </div>
      <div class="display-data">
        <h3 style="margin: 0px;">文件预览: </h3>
        <div class="file-preview-container" style="height: 300px;" :key="componentKey">
          <table>
            <thead>
              <tr>
                <th v-for="(header, index) in headers" :key="'header-' + index">{{ header }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in processedData" :key="'row-' + index">
                <td style="color: black;" v-for="(item, idx) in row" :key="'cell-' + index + '-' + idx">{{ item }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 注脚 -->
    <footer class="footer">
      Produced on the Chrome | Contact: <a href="mailto:1301585528@qq.com">1301585528@qq.com</a>
    </footer>
  </div>
</template>

<script setup>
import { logout } from '@/net';
import router from "@/router";
import {ref, onMounted, nextTick, watch} from 'vue';
import { ElMessage, ElLoading } from 'element-plus';
import { get, post } from "@/net";
import * as echarts from 'echarts';
import axios from 'axios';

// 获取Token, 以通过Jwt验证
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


const activeSection = ref('home');  // Initial active section value
const navbarBg = ref('#1a1a1a');  // Set initial color based on the initial active section

/* 轮播图片 */
const currentIndex = ref(0);
const startX = ref(0);
const threshold = 50;
const images = ref([
  { src: 'src/views/img/轮播_商业大佬.png', alt: '轮播_商业大佬' },
  { src: 'src/views/img/轮播_银行牌匾.jpg', alt: '轮播_银行牌匾' },
  { src: 'src/views/img/轮播_银行入口.jpg', alt: '轮播_银行入口' },
  { src: 'src/views/img/轮播_古典银行.jpg', alt: '轮播_古典银行' }
]);


const selectedFile = ref(null);
const selectedUserFile = ref(null);
const previewData = ref([]);
let headers = ref(null);
let good = ref(null);
let previewData1 = ref([]);
let processedData = ref([]);
let filename = ref(null);
let mheaders = ref(null);
let mpreviewData1 = ref([]);
let mprocessedData = ref([]);
const componentKey = ref(0);
const componentKey1 = ref(0);
const componentKey2 = ref(0);
const componentKey3 = ref(0);

// region 用户面板
const showProfilePanel = ref(false);
const profilePanelStyle = ref({right: '-300px'});  // Control panel style

// User profile panel
function toggleProfilePanel() {
  if (showProfilePanel.value) {
    profilePanelStyle.value.right = '-300px';  // Hide panel
  } else {
    profilePanelStyle.value.right = '0';       // Show panel
  }
  showProfilePanel.value = !showProfilePanel.value;
}

function userLogout() {
  logout(() => router.push("/"))
}
// endregion

function scrollTo(elementId) {
  activeSection.value = elementId;  // Update active section
  const element = document.getElementById(elementId);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
    updateNavbarColor(elementId);  // Update the navbar color
  }
}

function updateNavbarColor(section) {
  switch (section) {
    case 'NewDataPrediction':
      navbarBg.value = '#5a5a5a';
      break;
    case 'ModelTraining':
      navbarBg.value = '#4a4a4a';
      break;
    case 'DataAnalysis':
      navbarBg.value = '#3a3a3a';
      break;
    case 'RealTimeDisplay':
      navbarBg.value = '#2a2a2a';
      break;
    default:
      navbarBg.value = '#1a1a1a';
  }
}

function handleScroll() {
  const scrollY = window.scrollY + window.innerHeight / 2;  // Consider the midpoint position
  const sections = {
    home: document.getElementById('home').offsetTop,
    // RealTimeDisplay: document.getElementById('RealTimeDisplay').offsetTop,
    DataAnalysis: document.getElementById('DataAnalysis').offsetTop,
    ModelTraining: document.getElementById('ModelTraining').offsetTop,
    NewDataPrediction: document.getElementById('NewDataPrediction').offsetTop
  };

  for (const section of Object.keys(sections).reverse()) {
    if (scrollY >= sections[section]) {
      if (activeSection.value !== section) {
        activeSection.value = section;  // Update active section
        updateNavbarColor(section);  // Update navbar color
      }
      break;
    }
  }
}

function startTouch(e) {
  startX.value = e.touches[0].clientX;
}

function moveTouch(e) {
  const diffX = e.touches[0].clientX - startX.value;
  
  if (Math.abs(diffX) > threshold) {
    if (diffX > 0) {
      prev();
    } else {
      next();
    }
    startX.value = e.touches[0].clientX; // Reset start point
  }
}

function next() {
  currentIndex.value = (currentIndex.value + 1) % images.value.length;
}

function prev() {
  currentIndex.value = (currentIndex.value - 1 + images.value.length) % images.value.length;
}

function toggleSidebar() {
  if (sidebarStyle.value.left === '0px') {
    sidebarStyle.value.left = '-270px';  // Hide sidebar
  } else {
    sidebarStyle.value.left = '0px';     // Show sidebar
  }
}

function setActiveView(view) {
  activeView.value = view;
}

// Trigger file selection
function triggerFileInput() {
  document.getElementById('xfile').click();
}

function triggerXFileInput() {
  document.getElementById('file').click();
}

// Handle during drag
function onDragOver(event) {
  event.preventDefault();
}

// File drop handling
function onFileDrop(event) {
  handleFileUpload(event);
}

function onUserFileDrop(event) {
  handleUserFileUpload(event);
}

// Handle file upload selection
function handleFileUpload(event) {
  const files = event.dataTransfer ? event.dataTransfer.files : event.target.files;
  const file = files[0];
  console.log(file);
  if (!file.name.endsWith('.csv')) {
    ElMessage.error('只能上传CSV格式文件! ');
    return;
  }
  selectedFile.value = file;
}

function handleUserFileUpload(event) {
  const files = event.dataTransfer ? event.dataTransfer.files : event.target.files;
  const file = files[0];
  if (!file.name.endsWith('.csv')) {
    ElMessage.error('只能上传CSV格式文件! ');
    return;
  }
  selectedUserFile.value = file;
}

// 包装函数
function enhancedPost(url, formData, onSuccess) {
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '执行中, 请耐心等待...',
    background: 'rgba(0, 0, 0, 0.7)'
  });

  try {
    post(url, formData, () => {
      loadingInstance.close();
      onSuccess();
    });
  } catch (error) {
    loadingInstance.close();
    ElMessage.error('Operation failed: ' + error.message);
  }
}

// 执行命令
function sshcommand(command) {

  const formData = new FormData();
  formData.append('command', command);

  // Use the enhanced post request
  enhancedPost('/ssh/command', formData, () => {
    sessionStorage.setItem('uploadSuccess', 'true');
    ElMessage.success('visual successful');
    location.reload();
  });
}


// Upload file logic
function uploadFile() {
  
  const formData = new FormData();
  formData.append('file', selectedFile.value);  // selectedFile.value is the file you got from <input type="file">

  post('/ssh/upload', formData
  , () => {
                ElMessage.success('Upload successful')
            });

}

function uploadUserFile() {
  const loadingInstance11 = ElLoading.service({
    lock: true,
    text: '处理中, 请等待...',
    background: 'rgba(0, 0, 0, 0.7)'
  });
  const formData = new FormData();
  formData.append('file', selectedUserFile.value);  // selectedFile.value is the file you got from <input type="file">

  post('/api/ssh/upload', formData
  , () => {
    sessionStorage.setItem('uploadSuccess', 'true');
            });
}

// Download file and preview
function downloadFile() {
  filename = selectedFile.value.name
  const loadingInstance = ElLoading.service({
    lock: true,
    text: 'Processing, please wait...',
    background: 'rgba(0, 0, 0, 0.7)'
  });

  get(`/files/download/${filename}`,
    (data) => {

        const lines = data.split('\n');
        previewData1 = lines.slice(0, Math.min(10, lines.length));
        console.log(previewData1)
        headers = previewData1[0].split(',').map(header => header.trim());
        console.log(headers)
        headers = ['CustomerId', 'Prediction', 'Exit Probability']
        // Process data: start from the first line, extract the first three columns, and convert the third column value greater than 0.5 to true, otherwise false
        processedData = previewData1.slice(1).map((line, index) => {
          // 将每一行数据分割成数组
          const values = line.split(',').map(value => value.trim());

          // 提取前两列数据
          const firstTwoColumns = values.slice(0, 1);
          const threeTwoColumns = values.slice(3, 4).map(value => value.slice(0, -3));
          console.log(threeTwoColumns.slice(2))

          // 将第二列数据转换为布尔值
          const secondColumnValue = parseFloat(values[1]); // 假设第二列是数字

          // 如果大于0.5则转换为 true，否则为 false
          const isGreaterThanHalf = secondColumnValue > 0.5;

          // 返回处理后的数据数组
          return [
            ...firstTwoColumns,
            isGreaterThanHalf ? 'Yes' : 'No',
            ...threeTwoColumns
              // 根据条件转换为 yes 或 no
          ];
      });
        ElMessage.success('Train successfully!');
        console.log(processedData)
        // Create a download link
        good = data
        loadingInstance.close()
    }, 
    undefined,  // No intermediate processing function defined here
    (message) => {
        ElMessage.warning(`Train failed: ${message}`);
        coldTime.value = 0;  // This coldTime handling may not apply to the download logic, it should be adjusted or removed
        loadingInstance.close()
    });

}

// Generates a file from the given data and triggers its download as "predictions.csv".
function go() {
  console.log(good)
  const url = window.URL.createObjectURL(new Blob([good]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', 'predictions.csv');  // Set the filename
  document.body.appendChild(link);
  link.click();
  ElMessage.success('成功下载');
  componentKey.value++;
}

// // Fetches a file via GET request, processes its data for preview, and updates the UI with parsed results.
// function downloadMFile() {
//   const loadingInstance = ElLoading.service({
//           lock: true,
//           text: 'Processing, please wait...',
//           background: 'rgba(0, 0, 0, 0.7)'
//         });
//   get(`/api/ssh/train/fuck`,
//     (data) => {
//         const lines = data.split('\n');
//         mpreviewData1 = lines.slice(0, Math.min(10, lines.length));
//         console.log(mpreviewData1)
//         mheaders = mpreviewData1[0].split(',').map(header => header.trim());
//         mprocessedData = mpreviewData1.slice(1, 2).map(line =>
//           line.split(',').map(value => value.trim())
//         );
//         componentKey1.value++;
//         componentKey2.value++;
//         componentKey3.value++;
//         loadingInstance.close();
//     },
//     undefined,  // No intermediate processing function defined here
//     (message) => {
//         ElMessage.warning(`File download failed: ${message}`);
//         coldTime.value = 0;  // This coldTime handling may not apply to the download logic, it should be adjusted or removed
//         loadingInstance.close();
//     });
//
// }

function handleNavigation(Id) {
  scrollTo(Id);
}




// region 修改日志实时展示
// 数据
const dataList = ref([]);
let lastData = null;  // 用于存储上一次的响应数据

// 从后端获取数据
const fetchData = async () => {
  try {
    const response = await axios.get('/update');

    // 存在时打印 response.data
    console.log('Response data:', response.data);

    // 如果数据与上次相同，跳过这次更新
    if (JSON.stringify(response.data.data) === JSON.stringify(lastData)) {
      console.log('数据没有变化，跳过更新');
      return;
    }

    // 更新 lastData
    lastData = response.data.data;

    if (response.data) {
      const newItem = {
        code: response.data.code,
        data: response.data.data,
        id: response.data.id,
        message: response.data.message,
        changed: true, // 标记数据变化
      };

      console.log("data为" + newItem.data);

      dataList.value.unshift(newItem); // 插入新数据到最前面

      console.log("Latest item in dataList:" + dataList.value[0]);

      // 高亮展示后去掉变化标记
      setTimeout(() => {
        newItem.changed = false;
      }, 1000); // 保持高亮1秒

      // 限制数据长度，避免无限增长
      if (dataList.value.length > 8) {
        dataList.value.pop();
      }
    }
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};

// 每隔5秒从后端获取新数据
onMounted(() => {
  setInterval(fetchData, 5000);
});
// endregion

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

</script>


<style scoped>

/* region 整体布局 */
/* 字体与颜色 */
body {
  font-family: 'Arial', sans-serif;
  color: #fff;
  background-color: #121212;
  overflow: hidden;
}

/* 区域分割线 */
.content-section {
  flex-direction: column;
  min-height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #2e2a2a; /* Add a dividing line */
  display: flex;
}

/* 各模块背景颜色 */
#home { background-color: #1a1a1a; }
#RealTimeDisplay {background-color: #2a2a2a; }
#DataAnalysis { background-color: #3a3a3a; }
#ModelTraining { background-color: #4a4a4a; }
#NewDataPrediction { background-color: #5a5a5a; }
/* endregion */

/* region 导航栏 */
.navbar {
  position: fixed;
  top: 0;
  width: 100%;
  height: 80px;
  display: flex;
  align-items: center;
  padding: 10px 0;
  background-color: #333;
  /* box-shadow: 0 2px 4px rgba(0, 0, 0, 0.5); */
  z-index: 1001;
  transition: background-color 0.5s ease; /* Add transition effect */
}

.logo {
  font-size: 24px; /* Larger LOGO font */
  font-weight: bold;
  color: #ffd900;
  margin-right: 50px;
  padding-left: 50px;
}

/* 跳转按钮 */
.el-button {
  background-color: transparent; /* Button background color */
  color: #fff; /* Button text color */
  border: none;
  padding: 10px 20px;
  margin-right: 20px;
  font-weight: bold;
  font-size: larger;
  text-transform: uppercase; /* Text uppercase */
  transition: background-color 0.3s, transform 0.3s;
}

.el-button:hover {
  color: #ccc; /* Color change on hover */
}

.el-button:active {
  animation: blink 0.5s step-end infinite alternate; /* Click flashing effect */
}

/* 用户按钮 */
.profile-icon {
  margin-left: 35%;
  margin-right: 5%;
  cursor: pointer;
  transition: transform 0.3s ease; /* Smooth transformation effect */
}

.profile-icon:hover {
  filter: brightness(0.8); /* Increase brightness on hover */
}

.profile-icon:active {
  animation: click-effect 1s ease;
}

/* 点击效果 */
@keyframes click-effect {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(0.8); /* Slightly shrink on press */
  }
  100% {
    transform: scale(1);
  }
}

/* 动画效果 */
@keyframes blink {
  from { color: #d6c7c7; }
  to { color: #ccc; }
}

/* 用户面板 */
.profile-panel {
  position: fixed;
  right: -300px; /* Initially hidden outside the view */
  top: 100px;
  width: 160px;
  height: 200px;
  background-color: #000000;
  transition: right 0.5s ease-in-out; /* Smooth sliding effect */
  z-index: 1009;
  border: 1px solid #c0c0c0;
  display: flex;
  flex-direction: column; /* Vertical layout */
  align-items: center; /* Center alignment */
  justify-content: center;
  text-align: center;
}

/* 用户头像 */
.user-avatar {
  width: 50px; /* Avatar size */
  height: 50px; /* Avatar size */
  border-radius: 50%; /* Circular avatar */
  object-fit: cover;
  margin-bottom: 10px;
}

/* 用户名 帮助链接 */
.username, .help-link {
  cursor: pointer;
  width: 100%;
  text-transform: uppercase; /* Text uppercase */
  margin-bottom: 5px;
}

.help-link:hover {
  text-decoration: underline; /* Underline on hover */
}

/* endregion */

/* region 主页 */
#home {
  margin: auto;
  flex-direction: column;
}

#home1 {
  max-width: 1200px;
  margin: auto;
  padding: 20px;
  color: #fff;
  box-sizing: border-box;
  margin-top: -30px;
  display: flex;
  margin-bottom: 0px;
}

#home1 #text {
  margin-top: 140px;
}

header h1 {
  font-size: 2rem; /* Adjust title font size */
  color: #ffd900; /* Highlight the title */
}

header p {
  margin-bottom: 1rem;
  font-size: 1.4rem; /* Detail description font size */
}

article h2 {
  font-size: 1.8rem;
  color: #f6f5f5; /* Core features title color */
}

ul {
  list-style: inside square; /* List style */
}

/* 按钮 button */
.actions button {
  margin: 10px;
  padding: 12px 24px;
  background-color: #333;
  border: solid;
  border-color: #5a5a5a;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 30px;
  font-size: 32px;
  color: #ffd900;
  font-weight: bold;
  margin-left: -25px;
}

.actions button:hover {
  background-color: #555; /* Hover effect */
}

/* 轮播图片 image-slider & carousel */
.image-slider {
  flex: 1;
  width: 100%;
  max-width: 600px; /* Adjust as needed */
  margin-top: 200px;
  margin-left: 50px;
}

.carousel {
  overflow: hidden;
  position: relative;
  width: 100%;
}

.carousel-inner {
  display: flex;
  transition: transform 0.5s ease-in-out;
}

.carousel-item {
  flex: 0 0 100%;
  max-width: 100%;
}

.image-slider img {
  width: 100%;
  display: block;
}

/* 滚动标签 marquee */
.marquee {
  width: 100%;
  overflow: hidden;
  /* Dark background for contrast */
  color: #fff;
  white-space: nowrap;
  position:relative;
  margin-bottom: 10px;
}

.marquee-content {
  display: flex; /* Use flex layout for linear item arrangement */
  animation: marquee 50s linear infinite;
}

.marquee-item {
  margin: 0 20px; /* Add space between items */
  padding: 10px 20px;
  border: 2px solid #ffd900; /* Bright border color */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5); /* Add shadow for a 3D effect */
}

@keyframes marquee {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-50%);
  }
}

/* endregion */

/* region 修改日志实时展示 */
.message-container {
  max-width: 1500px;
  margin: 150px 0 auto;
  padding: 20px;
  background-color: #2e2a2a;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(104, 10, 10, 0.1);
}

.data-list {
  display: flex;
  flex-direction: column-reverse;
  max-height: 800px;

  overflow-y: auto; /* 支持垂直滚动 */
  overflow-x: auto; /* 支持水平滚动 */
  white-space: nowrap; /* 确保所有子容器在同一行内滚动 */
}

/* 仅隐藏Y轴滚动条 */
.data-list::-webkit-scrollbar:horizontal {
  display: block; /* 保留水平滚动条 */
}

.data-list::-webkit-scrollbar:vertical {
  display: none; /* 隐藏垂直滚动条 */
}

.data-item {
  padding: 20px;
  margin: 5px 0;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  transition: background-color 1s ease, transform 0.6s;

  white-space: nowrap; /* 强制单行显示 */
  overflow: hidden; /* 隐藏超出的内容 */
  text-overflow: ellipsis; /* 超出显示省略号 */
  min-width: fit-content; /* 子容器宽度根据内容扩展 */
}

/* 高亮变化的部分 */
.data-item.highlight {
  animation: highlightAnimation 1s forwards;
  transform: scale(1.00);
}

/* 渐变色动画 */
@keyframes highlightAnimation {
  0% {
    background-color: #fff; /* 起始颜色 */
  }
  50% {
    background-color: #ffeb3b; /* 中间渐变颜色 */
  }
  100% {
    background-color: #fff; /* 结束颜色 */
  }
}
/* endregion */

/* region 数据分析 */
#DataAnalysis {
  margin: auto;
  flex-direction: column;
}

.sidebar {
  position: fixed;
  left: -270px;
  top: 350px;
  bottom: 0;
  width: 150px;
  height: 224px;
  background-color: #333;
  transition: left 0.3s;
  z-index: 1002;
  opacity: 80%;
  overflow-y: auto;
  padding: 10px;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.5);
}

.sidebar ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}

.sidebar-btn {
  width: 100%;
  padding: 15px;
  border: 1px solid #555;
  background-color: #444;
  color: white;
  text-align: left;
  margin-bottom: 5px;
  transition: background-color 0.3s;
  cursor: pointer;
}

.analysis-block {
  display: flex;
  flex-direction: column;
  padding: 10px;
  border-radius: 8px;
  width: 90%;
  min-height: 550px;  /* 给整个块元素设置最小高度 */
  margin: 0 auto;  /* 水平居中 */
}

.chart {
  width: 100%;
  height: 800px;
  object-fit: contain;
  margin: 5px;
  flex: 1;
  background-color: #7a7a7a;
  padding: 10px;
  border-radius: 8px;
}

.sidebar-btn:hover {
  background-color: #555;
}

.toggle-sidebar-button:hover {
  border: 1px solid #ffd900;
}
/* endregion */

/* region 模型训练 */
#ModelTraining {
  margin: auto;
  flex-direction: column;
}

#ModelTraining .button {
  margin-left: 47%;
  margin-bottom: 0px;
}

#ModelTraining button {
  margin: 5px;
  padding: 10px 15px;
  cursor: pointer;
  border: 1px solid #666666;
}

#ModelTraining button:hover
{
  border: 1px solid #ffd900;
}

#ModelTraining .results-container {
  display: flex;
  margin-top: 20px;
  border: 2px dashed #666666;
}

#ModelTraining .results-table {
  flex: 1;
  padding-right: 20px;
  margin-top: 80px;
  width: 200px;
  margin-right: 0px;
  margin-left: 40px;
}

#ModelTraining .results-chart {
  flex: 2;
  margin-left: -200px;
  margin-top: 40px
}

#ModelTraining table {
  width: 200px;
  border-collapse: collapse;
}

#ModelTraining th, td {
  border: 1px solid #ffd900;
  padding: 8px;
  text-align: left;
}

#ModelTraining th {
  background-color: #666666;
}

.train-chart {
  width: 45%;
  height: 600px;
  object-fit: contain;
  margin: 5px;
  flex: 1;
  background-color: #7a7a7a;
  padding: 10px;
  border-radius: 8px;
}

.row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.table-container {
  flex: 1;
  margin: 5px;
  background-color: #444;
  padding: 10px;
  border-radius: 8px;
  overflow-x: auto;
  overflow-y: auto;
  max-height: 100%;
  min-height: 0px;
}

.table-container th, td {
  border: 1px solid #000000; /* Cell border */
  text-align: center;
  padding: 8px; /* Text alignment to the left */
}

.table-container table {
  width: 100%; /* Table width is 100% */
  border-collapse: collapse; /* Borders are collapsed into a single border */
}
/* endregion */

/* region 预测新数据 */
#NewDataPrediction{
  flex-direction: column;
}

#NewDataPrediction .upload-area {
  border: 2px dashed #ffd900;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  position: relative;
  width: 40%;
  height: 100px;
  margin: 20px;
  margin-left: 30%;
}

#NewDataPrediction .upload-area:hover {
  background-color: #757575;
}

#NewDataPrediction .upload-link {
  color: blue;
  cursor: pointer;
  text-decoration: underline;
}

#NewDataPrediction .display-data {
  margin: 20px;
}

#NewDataPrediction .button {
  margin-left: 30%;
  margin-bottom: 0px;
}

#NewDataPrediction button {
  margin: 5px;
  padding: 10px 15px;
  cursor: pointer;
}

#NewDataPrediction button:hover
{
  border: 1px #ffd900;
}

#NewDataPrediction input[type="file"] {
  margin-bottom: 10px;
}

.upload-area {
  border: 2px dashed #ffd900;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  position: relative;
  width: 40%;
  height: 100px;
  margin: 20px;
  margin-left: 30%;
}

.upload-area:hover {
  background-color: #757575;
}

.upload-link {
  color: blue;
  cursor: pointer;
  text-decoration: underline;
}

.display-data {
  margin: 20px;
}

.p-button {
  margin-left: 30%;
  margin-bottom: 0px;
}

p-button {
  margin: 5px;
  padding: 10px 15px;
  cursor: pointer;
}

p-button:hover
{
  border: 1px #ffd900;
}

#NewDataPrediction input[type="file"] {
  margin-bottom: 10px;
}

/* 表格元素大小调整 */
@media (max-width: 600px) {
  th, td {
    font-size: 3vw;
  }
}
@media (min-width: 601px) {
  th, td {
    font-size: 0.9em;
  }
}

.file-preview-container {
  max-height: 300px; /* Or adjust the height as needed */
  overflow-y: auto; /* Display vertical scrollbar */
  background-color: #dadada; /* Background color */
  border: 1px solid #ccc; /* Border style */
  margin-top: 10px; /* Top margin */
  width: auto; /* Automatic width to fit content */
  color: #000000;
}

.file-preview-container table {
  width: 100%; /* Table width is 100% */
  table-layout: fixed; /* Fixed table layout so columns are evenly distributed */
  border-collapse: collapse; /* Borders are collapsed into a single border */
}

.file-preview-container th, td {
  border: 1px solid #000000; /* Cell border */
  padding: 8px; /* Cell padding */
  text-align: left; /* Text alignment to the left */
  word-wrap: break-word; /* Allow word wrapping within cells */
}

.file-preview-container th {
  background-color: #a8a8a8; /* Table header background color */
}
/* endregion */

/* region 网页注脚 */
.footer {
  width: 100%;
  padding: 20px 0;
  text-align: center;
  background-color: #4a4a4a;
  color: #fff;
  font-size: 16px;
  box-sizing: border-box;
  border-top: 1px solid #c0c0c0; /* Add a top border to differentiate the content */
}
th {text-align: center;}
/* endregion */

/* region 不知道干什么用 */
/*
#ning button {
    margin: 100px;
    padding: 12px 24px;
    background-color: #333;
    border: none;
    color: #ffffff;
    cursor: pointer;
    transition: background-color 0.3s;
    margin-top: 30px;
}
*/

/*
#ning button:hover {
    background-color: #555;
}
*/

/*
.dashboard-image {
    width: 100%;
    margin-top: 1rem;
}
*/

/*
@media (max-width: 768px) {
    .content-section {
        padding: 10px;
    }

    header h1 {
        font-size: 1.5rem;
    }

    header p, .actions button {
        font-size: 1rem;
    }
}
*/

/* endregion */
</style>

