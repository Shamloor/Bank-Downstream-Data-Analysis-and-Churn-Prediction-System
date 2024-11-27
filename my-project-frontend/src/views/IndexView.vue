<template>
  <div>
    <div class="navbar" :style="{ backgroundColor: navbarBg }">
      <div class="logo">APen</div>
      <el-button @click="scrollTo('home')">主页</el-button>
      <el-button @click="handleNavigation('RealTimeDisplay', 'cao')">实时修改日志</el-button>
      <el-button @click="handleNavigation('DataAnalysis', 'cao')">分析可视化</el-button>
      <el-button @click="handleNavigation('ModelTraining', 'cao')">训练模型</el-button>
      <el-button @click="handleNavigation('NewDataPrediction', 'cao')">预测新数据</el-button>
      <div class="profile-icon" @click="toggleProfilePanel">
        <img src="./img/个人中心.png" style="max-width: 50%; max-height: 50%; width: auto; height: auto;">
      </div>
    </div>
    <div class="profile-panel" :style="{ right: profilePanelStyle.right, backgroundColor: navbarBg }">
      <div class="profile-content">
        <img src="./img/用户头像.jpg" alt="User Avatar" class="user-avatar"/>
        <div class="username" id="username-display"></div>
        <div @click="showHelp" class="help-link">Status: Analyzed</div>
        <el-button @click="userLogout" style="width: 100%; font-size: large;">Logout</el-button>
      </div>
    </div>
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
              <button style="font-size: 32px; color: #ffd900; font-weight: bold; margin-left: -25px;" @click="handleNavigation('DataAnalysis', 'cao')">立刻开始全新的可视化！</button>
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

    <div class="analysis-block">
      <div id="balanceChart" class="chart" style="width: 100%; height: 400px;"></div>
    </div>

    <!-- 每日数据分析可视化模块 -->
    <div v-if="!dataSetUploaded" id="DataAnalysis" class="content-section">
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
        <div id="balanceChart" class="chart" style="width: 100%; height: 400px;"></div>
      </div>
      <!-- 客户流失率 -->
      <div v-if="activeView === 'churn_rate'" class="analysis-block">
        <div id="churnChart" class="chart" style="width: 100%; height: 400px;"></div>
      </div>
      <!-- 客户特征分布 -->
      <div v-if="activeView === 'feature_distribution'" class="analysis-block">
        <div id="featureChart" class="chart" style="width: 100%; height: 400px;"></div>
      </div>
      <!-- 客户价值细分 -->
      <div v-if="activeView === 'value_segmentation'" class="analysis-block">
        <div id="valueChart" class="chart" style="width: 100%; height: 400px;"></div>
      </div>

    </div>

    <!-- 模型训练模块 -->
    <div v-if="!dataSetUploaded" id="ModelTraining" class="content-section">
      <div class="line" style="border-bottom: 1px solid #c0c0c0;"></div>
      <header style="margin-top: 70px; margin-left: 20px;">
        <h1>模型训练</h1>
      </header>
      <div class="button">
        <button style="font-size: 20px;" @click="downloadMFile()">开始训练</button>
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
        <img src="./img/roc_curve.png" alt="" class="chart" style="background-color: #cfcfcf;">
        <img src="./img/feature_importances.png" alt="" class="chart" style="background-color: #cfcfcf;">
      </div>
    </div>

    <!-- 用户流失率预测模块 -->
    <div v-if="!dataSetUploaded" id="NewDataPrediction" class="content-section">
      <div class="line" style="border-bottom: 1px solid #c0c0c0;"></div>
      <header style="margin-top: 70px; margin-left: 20px;">
        <h1>上传新数据进行预测</h1>
      </header>
      <div class="upload-area" @dragover.prevent="onDragOver" @drop.prevent="onFileDrop">
        <input type="file" id="file" ref="fileInput" @change="handleFileUpload" accept=".csv" hidden>
        <p v-if="!selectedFile">将文件拖拽到这里或<span class="upload-link" @click="triggerXFileInput">选择一个文件</span></p>
        <p v-else>{{ selectedFile.name }}</p>
      </div>
      <div class="button" style="margin-left: 42%;">
        <button @click="uploadFile" :disabled="!selectedFile" style="border: 0px;">上传</button>
        <button @click="downloadFile" :disabled="!selectedFile" style="border: 0px;">开始预测</button>
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

    <footer class="footer">
      Produced on the Chrome | Contact: <a href="mailto:1301585528@qq.com">1301585528@qq.com</a>
    </footer>
  </div>
</template>

<script setup>
import { logout } from '@/net';
import router from "@/router";
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import worldMapData from 'echarts/map/json/world.json';
import { ElMessage, ElLoading } from 'element-plus';
import { get, post } from "@/net";
import * as echarts from 'echarts';
import axios from 'axios';


const showProfilePanel = ref(false);
const profilePanelStyle = ref({right: '-300px'});  // Control panel style
const activeSection = ref('home');  // Initial active section value
const navbarBg = ref('#1a1a1a');  // Set initial color based on the initial active section
const currentIndex = ref(0);
const startX = ref(0);
const threshold = 50;
const images = ref([
  { src: 'src/views/img/轮播_商业大佬.png', alt: '轮播_商业大佬' },
  { src: 'src/views/img/轮播_银行牌匾.jpg', alt: '轮播_银行牌匾' },
  { src: 'src/views/img/轮播_银行入口.jpg', alt: '轮播_银行入口' },
  { src: 'src/views/img/轮播_古典银行.jpg', alt: '轮播_古典银行' }
]);
let autoScrollInterval = null;
const sidebarStyle = ref({ left: '-270px' });  // Control sidebar style
const activeView = ref('balance_distribution'); // Currently Active View
const chartData = ref({}); // All data from backend
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
let dataSetUploaded = ref(false);
const chartInstance = ref(null); // 定义数据和图表实例


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
    RealTimeDisplay: document.getElementById('RealTimeDisplay').offsetTop,
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

function initWorldMap(chart) {
  const userData = ref(tablesx.train_geography.processedData.value.map(data => ({
    name: data[0],  // Country name
    value: data[1]  // User count
})));
  console.log(userData)
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
    { min: 90000, label: '90K+', color: '#8A0808' },
    { min: 40000, max: 89999, label: '40K - 90K', color: '#B40404' },
    { min: 10000, max: 39999, label: '10K - 40K', color: '#DF0101' },
    { max: 9999, label: '<10K', color: '#FBEFEF' }
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
        mapType: 'world',  // Use world map
        roam: true,
        center: [15, 50],  // Focus on the approximate center of Europe
        zoom: 8,
        data: userData.value,
        emphasis: {
          label: {
            show: false
          },
          itemStyle: {
            areaColor: '#c9dfaf'  // Highlight state area color
          }
        }
      }
    ]
  };

  echarts.registerMap('world', worldMapData);  // Register map data
  chart.setOption(option);
}

function xx() {
  const chart = echarts.init(document.getElementById('worldChart'));
  initWorldMap(chart);
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  //checkDataSetStatus();

  if (dataSetUploaded.value == false)
  {
    fenmysql();
  }

  if (sessionStorage.getItem('uploadSuccess') === 'true') {
    // Delay for a while to show the message, to ensure the page has finished loading
    ElMessage.success('上传成功');
      // Clear the status to prevent repeated display of the message on refresh
      sessionStorage.removeItem('uploadSuccess');
  }

  autoScrollInterval = setInterval(next, 3000)

});

function toggleSidebar() {
  if (sidebarStyle.value.left === '0px') {
    sidebarStyle.value.left = '-270px';  // Hide sidebar
  } else {
    sidebarStyle.value.left = '0px';     // Show sidebar
  }
}

function setActiveView(view) {
  activeView.value = view;
  nextTick(() => {
    xx();  // Assuming initMap is the function to initialize the map
  });
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

// Wrap a new post method to add loading indicator and error handling
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

function sshcommand(command) {

  const formData = new FormData();
  formData.append('command', command);

  // Use the enhanced post request
  enhancedPost('/api/ssh/command', formData, () => {
    sessionStorage.setItem('uploadSuccess', 'true');
    ElMessage.success('visual successful');
    location.reload();
  });
}

// Upload file logic
function uploadFile() {
  
  const formData = new FormData();
  formData.append('file', selectedFile.value);  // selectedFile.value is the file you got from <input type="file">

  post('/api/files/upload', formData
  , () => {
                ElMessage.success('Upload successful')
            });

}

// Execute all shell scripts
function xxxx() {
  uploadUserFile()
  sshcommand("training/shell/exec_visual.sh");
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

  get(`/api/files/download/${filename}`, 
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

// Fetches a file via GET request, processes its data for preview, and updates the UI with parsed results.
function downloadMFile() {
  const loadingInstance = ElLoading.service({
          lock: true,
          text: 'Processing, please wait...',
          background: 'rgba(0, 0, 0, 0.7)'
        });
  get(`/api/ssh/train/fuck`, 
    (data) => {
        const lines = data.split('\n');
        mpreviewData1 = lines.slice(0, Math.min(10, lines.length));
        console.log(mpreviewData1)
        mheaders = mpreviewData1[0].split(',').map(header => header.trim());
        mprocessedData = mpreviewData1.slice(1, 2).map(line =>
          line.split(',').map(value => value.trim())
        );
        componentKey1.value++;
        componentKey2.value++;
        componentKey3.value++;
        loadingInstance.close();
    }, 
    undefined,  // No intermediate processing function defined here
    (message) => {
        ElMessage.warning(`File download failed: ${message}`);
        coldTime.value = 0;  // This coldTime handling may not apply to the download logic, it should be adjusted or removed
        loadingInstance.close();
    });

}

function checkDataSetStatus() {
  try {
    const username = sessionStorage.getItem('username');
    console.log(username)
        // Get the div element with ID 'username-display'
    let displayDiv = document.getElementById('username-display');
    // Set the text content of the div to the value of the username variable
    displayDiv.textContent = `Username: ${username}`;

    const message1 = username + '.csv';

    get(`/api/ssh/status/${message1}`,
    (data) => {
        console.log(data)
        if (data.includes('Error: File does not exist at path'))
        {
          ElMessage.warning('Please upload dataset!');
          dataSetUploaded.value = true;
        }
        else
        {
          dataSetUploaded.value = false;
        }
    },
    undefined,  // No intermediate processing function defined here
    (message) => {
        ElMessage.warning(`Download failed: ${message}`);
        coldTime.value = 0;  // This coldTime handling may not apply to the download logic, it should be adjusted or removed
    });
  } catch (error) {
    ElMessage.error('Failed to check dataset status: ' + error.message);
    console.log("Fuck")
  }
}

function handleNavigation(defaultId, alternateId) {
    if (dataSetUploaded.value) {
        scrollTo(alternateId);
        ElMessage.warning('Please upload dataset first');
    } else {
        scrollTo(defaultId);
    }
}

// 获取数据函数
async function fetchBalanceDistribution() {
  try {
    const response = await axios.post(`/api/mysql/data/ads_customer_balance_distribution`);
    if (response.data.code === 200) {
      const chartData = response.data.data; // 假设返回的数据格式符合需求
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
  if (!chartInstance.value) {
    chartInstance.value = echarts.init(document.getElementById('balanceChart'));
  }

  const balanceRanges = data.map(item => item.BalanceRange);
  const customerCounts = data.map(item => item.CustomerCount);

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


onMounted(() => {
  fetchBalanceDistribution();
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});



</script>


<style scoped>
/* General font and background color adjustments */
body {
  font-family: 'Arial', sans-serif;
  color: #fff;
  background-color: #121212;
  overflow: hidden;
}

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

.content-section {
  min-height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #333; /* Add a dividing line */
  display: flex;
}

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

@keyframes click-effect {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(0.95); /* Slightly shrink on press */
  }
  100% {
    transform: scale(1);
  }
}


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

.user-avatar {
  width: 50px; /* Avatar size */
  height: 50px; /* Avatar size */
  border-radius: 50%; /* Circular avatar */
  object-fit: cover;
  margin-bottom: 10px;
}

.username, .help-link {
  cursor: pointer;
  width: 100%;
  text-transform: uppercase; /* Text uppercase */
  margin-bottom: 5px;
}

.help-link:hover {
  text-decoration: underline; /* Underline on hover */
}

#home { background-color: #1a1a1a; }
#RealTimeDisplay {background-color: #2a2a2a; }
#DataAnalysis { background-color: #3a3a3a; }
#ModelTraining { background-color: #4a4a4a; }
#NewDataPrediction { background-color: #5a5a5a; }

@keyframes blink {
  from { color: #d6c7c7; }
  to { color: #ccc; }
}

#home {
  margin: auto;
  flex-direction: column;
  border: 0px;
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
    font-size: 1.2rem; /* Detail description font size */
}

article h2 {
    color: #c0c0c0; /* Core features title color */
}

ul {
    list-style: inside square; /* List style */
}

.actions button {
    margin: 10px;
    padding: 12px 24px;
    background-color: #333;
    border: none;
    color: white;
    cursor: pointer;
    transition: background-color 0.3s;
    margin-top: 30px;
}

.actions button:hover {
    background-color: #555; /* Hover effect */
}

#ning button {
    margin: 10px;
    padding: 12px 24px;
    background-color: #333;
    border: none;
    color: white;
    cursor: pointer;
    transition: background-color 0.3s;
    margin-top: 30px;
}

#ning button:hover {
    background-color: #555; /* Hover effect */
}

.dashboard-image {
    width: 100%; /* Ensure image is fully displayed within the container */
    margin-top: 1rem;
}

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

#DataAnalysis {
  margin: auto;
  flex-direction: column;
}

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

#cao{
  flex-direction: column;
}
#cao .upload-area {
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

#cao .upload-area:hover {
  background-color: #757575;
}

#cao .upload-link {
  color: blue;
  cursor: pointer;
  text-decoration: underline;
}

#cao .display-data {
  margin: 20px;
}

#cao .button {
  margin-left: 30%;
  margin-bottom: 0px;
}
#cao button {
  margin: 5px;
  padding: 10px 15px;
  cursor: pointer;
}
#cao button:hover
{
  border: 1px #ffd900;
}

#NewDataPrediction input[type="file"] {
  margin-bottom: 10px;
}


.sidebar {
  position: fixed;
  left: -270px;
  top: 350px;
  bottom: 0;
  width: 250px;
  height: 300px;
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
  padding: 10px;
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
  background-color: #333;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  width: 80%;
  margin-left: 10%;
}

.row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.chart {
  width: 48%;
  object-fit: contain;
  margin: 5px;
  flex: 1;
  background-color: #7a7a7a;
  padding: 10px;
  border-radius: 8px;
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

.table-container table {
  width: 100%; /* Table width is 100% */
  border-collapse: collapse; /* Borders are collapsed into a single border */
}

.table-container th, td {
  border: 1px solid #000000; /* Cell border */
  text-align: center;
  padding: 8px; /* Text alignment to the left */
}

.sidebar-btn:hover {
  background-color: #555;
}

.toggle-sidebar-button:hover {
  border: 1px solid #ffd900;
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

.chart {
  width: 100%;
  height: 400px;
}

/* Responsive font sizes */
@media (max-width: 600px) {
  th, td {
    font-size: 3vw; /* Adjust font size for small screens */
  }
}

/* Increase font size appropriately on large screens */
@media (min-width: 601px) {
  th, td {
    font-size: 0.9em; /* Fixed font size for large screens */
  }
}


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
</style>

