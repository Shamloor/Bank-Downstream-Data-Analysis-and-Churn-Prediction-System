<template>
  <div>
    <div class="navbar" :style="{ backgroundColor: navbarBg }">
      <div class="logo">APen</div>
      <el-button @click="scrollTo('home')">首页</el-button>
      <el-button @click="scrollTo('userInfo')">可视化分析</el-button>
      <el-button @click="scrollTo('bankRelation')">以往数据预测</el-button>
      <el-button @click="scrollTo('userBehavior')">上传新数据预测</el-button>
      <div class="profile-icon" @click="toggleProfilePanel">
        <img src="./img/个人中心.png" style="max-width: 150%; max-height: 150%; width: auto; height: auto;">
      </div>
    </div>
  </div>
  <div class="profile-panel" :style="{ right: profilePanelStyle.right, backgroundColor: navbarBg }">
    <div class="profile-content">
      <img src="./img/mm.jpg" alt="用户头像" class="user-avatar"/>
      <div class="username">用户名</div>
      <div @click="showHelp" class="help-link">帮助</div>
      <el-button @click="userLogout" style="width: 100%; font-size: large;">退出登录</el-button>
    </div>
  </div>

  <div id="home" class="content-section">
    <div id="home1">
      <section id="text">
          <header>
              <h1>欢迎来到您的银行账户流失分析中心！</h1>
              <p>我们提供全面的数据分析服务，帮助您洞察金融市场和用户行为。</p>
          </header>
          <article>
              <h2>核心功能</h2>
              <ul>
                  <li>详细的用户信息管理和展示</li>
                  <li>深入分析用户与银行的关系</li>
                  <li>基于数据的用户行为预测</li>
              </ul>
          </article>
          <div class="actions">
              <button @click="scrollTo('userInfo')">数据可视化</button>
              <button @click="scrollTo('bankRelation')">分析用户</button>
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
    <div class="marquee">
      <div class="marquee-content">
        <span v-for="n in 50" :key="n" class="marquee-item">Better Call Saul</span>
      </div>
    </div>
  </div>
  <div id="userInfo" class="content-section">
    <div class="line" style="border-bottom: 1px solid #c0c0c0; display: flex"></div>
    <header style="margin-top: 70px; margin-left: 20px; display: flex; justify-content: space-between;">
      <h1>可视化分析</h1>
      <button style="height: 80%; font-size: 25px; margin-right: 20px; margin-top: 20px;" class="toggle-sidebar-button" @click="toggleSidebar">☰</button>
    </header>
    <div class="sidebar" :style="sidebarStyle">
      <!-- 导航内容 -->
      <ul>
        <li><button class="sidebar-btn" @click="setActiveView('churn')">客户流失分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('gender')">客户性别分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('geography')">客户地理分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('credit')">客户信用卡分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('active')">客户活跃会员分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('products')">客户产品分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('tenure')">客户账户持续期分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('creditScore')">客户信用评分分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('age')">客户年龄分布分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('balance')">客户余额分布分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('salary')">客户估计薪资分布分析</button></li>
        <li><button class="sidebar-btn" @click="setActiveView('fen')">客户地区分布分析</button></li>
      </ul>
    </div>
    <!-- 客户流失分析 -->
    <div v-if="activeView === 'churn'" class="analysis-block">
      <img src="./img/ProgramDVI/df.png" alt="Customer Churn Bar Chart">
      <img src="./img/ProgramDVI/df1(1).png" alt="Customer Churn Pie Chart">
    </div>

    <!-- 客户性别分析 -->
    <div v-if="activeView === 'gender'" class="analysis-block">
      <img src="./img/ProgramDVI/df2.png" alt="Customer Gender Bar Chart">
      <img src="./img/ProgramDVI/df2(1).png" alt="Customer Gender Pie Chart">
    </div>

    <!-- 客户地理分析 -->
    <div v-if="activeView === 'geography'" class="analysis-block">
      <img src="./img/ProgramDVI/df3.png" alt="Customer Geography Bar Chart">
      <img src="./img/ProgramDVI/df3(1).png" alt="Customer Geography Pie Chart">
    </div>

    <!-- 客户信用卡分析 -->
    <div v-if="activeView === 'credit'" class="analysis-block">
      <img src="./img/ProgramDVI/df4.png" alt="Customer Credit Card Bar Chart">
      <img src="./img/ProgramDVI/df4(1).png" alt="Customer Credit Card Pie Chart">
    </div>

    <!-- 客户活跃会员分析 -->
    <div v-if="activeView === 'active'" class="analysis-block">
      <img src="./img/ProgramDVI/df5.png" alt="Customer Active Membership Bar Chart">
      <img src="./img/ProgramDVI/df5(1).png" alt="Customer Active Membership Pie Chart">
    </div>

    <!-- 客户产品分析 -->
    <div v-if="activeView === 'products'" class="analysis-block">
      <img src="./img/ProgramDVI/df6.png" alt="Customer Product Bar Chart">
      <img src="./img/ProgramDVI/df6(1).png" alt="Customer Product Pie Chart">
    </div>

    <!-- 客户账户持续期分析 -->
    <div v-if="activeView === 'tenure'" class="analysis-block">
      <img src="./img/ProgramDVI/df7.png" alt="Customer Tenure Bar Chart">
      <img src="./img/ProgramDVI/df7(1).png" alt="Customer Tenure Pie Chart">
    </div>

    <!-- 客户信用评分分析 -->
    <div v-if="activeView === 'creditScore'" class="analysis-block">
      <img src="./img/ProgramDVI/df8.png" alt="Customer Credit Score Histogram" style="width: 60%;">
    </div>

    <!-- 客户年龄分布分析 -->
    <div v-if="activeView === 'age'" class="analysis-block">
      <img src="./img/ProgramDVI/df9.png" alt="Customer Age Distribution Histogram" style="width: 60%;">
    </div>

    <!-- 客户余额分布分析 -->
    <div v-if="activeView === 'balance'" class="analysis-block">
      <img src="./img/ProgramDVI/df10.png" alt="Customer Balance Distribution Histogram" style="width: 60%;">
    </div>

    <!-- 客户估计薪资分布分析 -->
    <div v-if="activeView === 'salary'" class="analysis-block">
      <img src="./img/ProgramDVI/df11.png" alt="Customer Estimated Salary Distribution Histogram" style="width: 60%;">
    </div>
    <div id="worldChart" v-if="activeView === 'fen'" style="background-color: #757575; height: 600px;"></div>
  </div>
  <div id="bankRelation" class="content-section">
    <div class="line" style="border-bottom: 1px solid #c0c0c0;"></div>
    <header style="margin-top: 70px; margin-left: 20px;">
      <h1>模型训练</h1>
    </header>
    <div class="button">
      <button style="font-size: 20px;" @click="sshcommand('cd training/shell/visual; ./exec_python.sh')">开始训练</button>
      <button style="font-size: 20px;" @click="sshcommand('cd training/shell/flume && ./flume_upload_train.sh')">开始flume</button>
      <button style="font-size: 20px;" @click="sshcommand('cd training/shell/flume; ./kill_flume.sh')">停止flume</button>
    </div>
    <div class="results-container">
        <div class="results-table">
            <table>
                <thead>
                    <tr>
                        <th>指标</th>
                        <th>值</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>准确率</td>
                        <td>{{ accuracy }}</td>
                    </tr>
                    <tr>
                        <td>回归率</td>
                        <td>{{ recall }}</td>
                    </tr>
                    <tr>
                        <td>F1 分数</td>
                        <td>{{ f1Score }}</td>
                    </tr>
                    <!-- 更多指标 -->
                </tbody>
            </table>
        </div>
        <div class="results-chart">
            <!-- 图表容器 -->
            <div id="regressionChart" style="width: 100%; height: 400px;">
              <img src="./img/saul.jpg">
              <img style="margin-left: 10px;" src="./img/saul.jpg">
            </div>
        </div>
    </div>
  </div>
  <div id="userBehavior" class="content-section">
    <div class="line" style="border-bottom: 1px solid #c0c0c0;"></div>
    <header style="margin-top: 70px; margin-left: 20px;">
      <h1>上传新数据预测</h1>
    </header>
    <div class="upload-area" @dragover.prevent="onDragOver" @drop.prevent="onFileDrop">
      <input type="file" id="file" ref="fileInput" @change="handleFileUpload" accept=".csv" hidden>
      <p v-if="!selectedFile">拖拽文件到这里或者 <span class="upload-link" @click="triggerFileInput">选择文件</span></p>
      <p v-else>{{ selectedFile.name }}</p>
    </div>
    <div class="button">
      <button @click="uploadFile" :disabled="!selectedFile">上传</button>
      <button @click="downloadFile">下载</button>
    </div>
    <div class="display-data">
      <h3 style="margin: 0px;">文件预览：</h3>
      <div class="file-preview-container" style="height: 200px;" :key="componentKey">
        <table>
          <thead>
            <tr>
              <th v-for="(header, index) in headers" :key="'header-' + index">{{ header }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, index) in processedData" :key="'row-' + index">
              <td v-for="(item, idx) in row" :key="'cell-' + index + '-' + idx">{{ item }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <footer class="footer">
    苟且偷生出品 | 联系方式: <a href="mailto:2821891910@qq.com">2821891910@qq.com</a>
  </footer>
</template>


<script setup>
// main.ts

// 如果您正在使用CDN引入，请删除下面一行。
import { logout } from '@/net'
import router from "@/router";
import { ref, onMounted, onUnmounted, nextTick} from 'vue';

const showProfilePanel = ref(false);
const profilePanelStyle = ref({right: '-300px'});  // 控制面板的样式

function toggleProfilePanel() {
  if (showProfilePanel.value) {
    profilePanelStyle.value.right = '-300px';  // 隐藏面板
  } else {
    profilePanelStyle.value.right = '0';       // 显示面板
  }
  showProfilePanel.value = !showProfilePanel.value;
}

const activeSection = ref('home');  // 活跃部分的初始值

const navbarBg = ref('#1a1a1a');  // 根据初始活跃部分设置初始颜色

function userLogout() {
  logout(() => router.push("/"))
}

function scrollTo(elementId) {
  activeSection.value = elementId;  // 更新活跃部分
  const element = document.getElementById(elementId);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
    updateNavbarColor(elementId);  // 更新导航栏颜色
  }
}

function updateNavbarColor(section) {
  switch (section) {
    case 'userBehavior':
      navbarBg.value = '#4a4a4a';
      break;
    case 'bankRelation':
      navbarBg.value = '#3a3a3a';
      break;
    case 'userInfo':
      navbarBg.value = '#2a2a2a';
      break;
    default:
      navbarBg.value = '#1a1a1a';
  }
}

function handleScroll() {
  const scrollY = window.scrollY + window.innerHeight / 2;  // 中点位置考虑
  const sections = {
    home: document.getElementById('home').offsetTop,
    userInfo: document.getElementById('userInfo').offsetTop,
    bankRelation: document.getElementById('bankRelation').offsetTop,
    userBehavior: document.getElementById('userBehavior').offsetTop
  };

  for (const section of Object.keys(sections).reverse()) {
    if (scrollY >= sections[section]) {
      if (activeSection.value !== section) {
        activeSection.value = section;  // 更新活跃部分
        updateNavbarColor(section);  // 更新导航栏颜色
      }
      break;
    }
  }
}


onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

const images = ref([
  { src: 'src/views/img/good.jpg', alt: 'Description 1' },
  { src: 'src/views/img/hg.jpg', alt: 'Description 2' },
  { src: 'src/views/img/saul.jpg', alt: 'Description 3' }
]);

const currentIndex = ref(0);
const startX = ref(0);
const threshold = 50;

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
    startX.value = e.touches[0].clientX; // 重置开始点
  }
}

function next() {
  currentIndex.value = (currentIndex.value + 1) % images.value.length;
}

function prev() {
  currentIndex.value = (currentIndex.value - 1 + images.value.length) % images.value.length;
}

import * as echarts from 'echarts';
import worldMapData from 'echarts/map/json/world.json'; // 确保你有正确的路径和世界地图数据


function initWorldMap(chart) {
  const userData = ref([
    { name: 'China', value: 1409517397 },
    { name: 'India', value: 1339180127 },
    { name: 'United States', value: 324459463 },
    // 添加其他国家和相应的用户数
  ]);
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

function xx() {
  const chart = echarts.init(document.getElementById('worldChart'));
  initWorldMap(chart);
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
});
0
const sidebarStyle = ref({ left: '-270px' });  // 控制侧边栏的样式

function toggleSidebar() {
  if (sidebarStyle.value.left === '0px') {
    sidebarStyle.value.left = '-270px';  // 隐藏侧边栏
  } else {
    sidebarStyle.value.left = '0px';     // 显示侧边栏
  }
}

const activeView = ref('churn');  // 管理当前视图的变量

function setActiveView(view) {
  activeView.value = view;
  nextTick(() => {
    xx();  // 假设 initMap 是初始化地图的函数
  });
}

import { ElMessage } from 'element-plus';

const selectedFile = ref(null);
const previewData = ref([]);

// 触发文件选择
function triggerFileInput() {
  document.getElementById('file').click();
}

// 拖拽过程中的处理
function onDragOver(event) {
  event.preventDefault();
}

// 文件放置处理
function onFileDrop(event) {
  handleFileUpload(event);
}

// 处理文件上传选择
function handleFileUpload(event) {
  const files = event.dataTransfer ? event.dataTransfer.files : event.target.files;
  const file = files[0];
  if (!file.name.endsWith('.csv')) {
    ElMessage.error('只能上传CSV文件！');
    return;
  }
  selectedFile.value = file;
}

import {get, post} from "@/net";

//命令运行语句
import { ElLoading} from 'element-plus'

// 封装一个新的post方法，以便添加加载指示和错误处理
function enhancedPost(url, formData, onSuccess) {
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在处理，请稍候...',
    background: 'rgba(0, 0, 0, 0.7)'
  });

  try {
    post(url, formData, () => {
      loadingInstance.close();
      onSuccess();
    });
  } catch (error) {
    loadingInstance.close();
    ElMessage.error('操作失败: ' + error.message);
  }
}

function sshcommand(command) {
  const formData = new FormData();
  formData.append('command', command);

  // 使用增强版的post请求
  enhancedPost('/api/ssh/command', formData, () => {
    ElMessage.success('训练成功');
  });
}

// 上传文件逻辑
function uploadFile() {
  
  const formData = new FormData();
  formData.append('file', selectedFile.value);  // selectedFile.value 是你从 <input type="file"> 获取的文件

  post('/api/files/upload', formData
  , () => {
                ElMessage.success('上传成功')
            });

}

let headers = ref(null);
let previewData1 = ref([]);
let processedData = ref([]);

// 下载文件并预览
function downloadFile() {
  const filename = selectedFile.value.name
  get(`/api/files/download/${filename}`, 
    (data) => {

        const lines = data.split('\n');
        previewData1 = lines.slice(0, Math.min(10, lines.length));
        console.log(previewData1)
        headers = previewData1[0].split(',').map(header => header.trim());
        processedData = previewData1.slice(1).map(line =>
          line.split(',').map(value => value.trim())
        );
        console.log(processedData)
        // 创建一个下载链接
        const url = window.URL.createObjectURL(new Blob([data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);  // 设置文件名
        document.body.appendChild(link);
        link.click();
        ElMessage.success('文件下载成功！');
        componentKey.value++;
    }, 
    undefined,  // 这里没有定义中间处理函数
    (message) => {
        ElMessage.warning(`文件下载失败: ${message}`);
        coldTime.value = 0;  // 此处的 coldTime 处理可能不适用于下载逻辑，应该调整或移除
    });

}

const componentKey = ref(0);

</script>


<style scoped>
/* 整体字体和背景色调整 */
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
  transition: background-color 0.5s ease; /* 添加过渡效果 */
}

.logo {
  font-size: 24px; /* 更大的LOGO字体 */
  font-weight: bold;
  color: #ffd900;
  margin-right: 50px;
  padding-left: 50px;
}

.el-button {
  background-color: transparent; /* 按钮背景色 */
  color: #fff; /* 按钮文字颜色 */
  border: none;
  padding: 10px 20px;
  margin-right: 20px;
  font-weight: bold;
  font-size: larger;
  text-transform: uppercase; /* 文字大写 */
  transition: background-color 0.3s, transform 0.3s;
}

.el-button:hover {
  color: #ccc; /* 悬停时颜色变化 */
}

.el-button:active {
  animation: blink 0.5s step-end infinite alternate; /* 点击闪烁效果 */
}

.content-section {
  min-height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #333; /* 添加分隔线 */
  display: flex;
}

.profile-icon {
  margin-left: 35%;
  margin-right: 5%;
  cursor: pointer;
  transition: transform 0.3s ease; /* 平滑变换效果 */
}

.profile-icon:hover {
  filter: brightness(0.8); /* 鼠标悬浮时增加亮度 */
}

.profile-icon:active {
  animation: click-effect 1s ease;
}

@keyframes click-effect {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(0.95); /* 按下时稍微缩小 */
  }
  100% {
    transform: scale(1);
  }
}


.profile-panel {
  position: fixed;
  right: -300px; /* 初始隐藏在视图外 */
  top: 100px;
  width: 160px;
  height: 200px;
  background-color: #000000;
  transition: right 0.5s ease-in-out; /* 平滑滑动特效 */
  z-index: 1009;
  border: 1px solid #c0c0c0; 
  display: flex;
  flex-direction: column; /* 垂直布局 */
  align-items: center; /* 居中对齐 */
  justify-content: center;
  text-align: center;
}

.user-avatar {
  width: 50px; /* 头像大小 */
  height: 50px; /* 头像大小 */
  border-radius: 50%; /* 圆形头像 */
  object-fit: cover;
  margin-bottom: 10px;
}

.username, .help-link {
  cursor: pointer;
  width: 100%;
  text-transform: uppercase; /* 文字大写 */
  margin-bottom: 5px;
}

.help-link:hover {
  text-decoration: underline; /* 鼠标悬浮时下划线 */
}

#home { background-color: #1a1a1a; }
#userInfo { background-color: #2a2a2a; }
#bankRelation { background-color: #3a3a3a; }
#userBehavior { background-color: #4a4a4a; }

@keyframes blink {
  from { color: #fff; }
  to { color: #ccc; }
}

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
    font-size: 2rem; /* 调整标题字体大小 */
    color: #ffd900; /* 突出显示标题 */
}

header p {
    margin-bottom: 1rem;
    font-size: 1.2rem; /* 细节描述字体大小 */
}

article h2 {
    color: #c0c0c0; /* 核心功能标题颜色 */
}

ul {
    list-style: inside square; /* 列表样式 */
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
    background-color: #555; /* 鼠标悬停效果 */
}

.dashboard-image {
    width: 100%; /* 保证图片在容器中完全显示 */
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
  max-width: 600px; /* 根据需要调整 */
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
 /* 暗色背景提高对比 */
  color: #fff;
  white-space: nowrap;
  position: relative;
  margin-bottom: 10px;
}

.marquee-content {
  display: flex; /* 使用 flex 布局使项目线性排列 */
  animation: marquee 50s linear infinite;
}

.marquee-item {
  margin: 0 20px; /* 在项目之间添加空间 */
  padding: 10px 20px;
  border: 2px solid #ffd900; /* 明亮的边框颜色 */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5); /* 添加阴影以提升立体感 */
}

@keyframes marquee {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-50%);
  }
}

#userInfo {
  margin: auto;
  flex-direction: column;
}

#bankRelation {
  margin: auto;
  flex-direction: column;
}

#bankRelation .button {
  margin-left: 47%;
  margin-bottom: 0px;
}
#bankRelation button {
  margin: 5px;
  padding: 10px 15px;
  cursor: pointer;
  border: 1px solid #666666;
}
#bankRelation button:hover
{
  border: 1px solid #ffd900;
}

#bankRelation .results-container {
    display: flex;
    margin-top: 20px;
    border: 2px dashed #666666;
}

#bankRelation .results-table {
    flex: 1;
    padding-right: 20px;
    margin-top: 80px;
    width: 200px;
    margin-right: 0px;
    margin-left: 40px;
}

#bankRelation .results-chart {
    flex: 2;
    margin-left: -200px;
    margin-top: 40px
}

#bankRelation table {
    width: 200px;
    border-collapse: collapse;
}

#bankRelation th, td {
    border: 1px solid #ffd900;
    padding: 8px;
    text-align: left;
}

#bankRelation th {
    background-color: #666666;
}

#userBehavior{
  flex-direction: column;
}
#userBehavior .upload-area {
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

#userBehavior .upload-area:hover {
  background-color: #757575;
}

#userBehavior .upload-link {
  color: blue;
  cursor: pointer;
  text-decoration: underline;
}

#userBehavior .display-data {
  margin: 20px;
}

#userBehavior .button {
  margin-left: 30%;
  margin-bottom: 0px;
}
#userBehavior button {
  margin: 5px;
  padding: 10px 15px;
  cursor: pointer;
}
#userBehavior button:hover
{
  border: 1px #ffd900;
}

#userBehavior input[type="file"] {
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
  display: flex;  /* 启用 Flexbox 布局 */
  align-items: center;  /* 垂直居中 */
  justify-content: space-around;  /* 子元素之间平均分布空间 */
  margin-bottom: 20px;  /* 每个分析块之间的间距 */
}

.analysis-block img {
  width: 35%;  /* 图片最大宽度，防止图片过大 */
  height: auto;  /* 高度自适应 */
  margin: 10px;  /* 保持图片间有间隙 */
  background-color: #989898;
  border: 2px dashed #ffd900;
}

.sidebar-btn:hover {
  background-color: #555;  
}

.toggle-sidebar-button:hover {
  border: 1px solid #ffd900;
}

.file-preview-container {
  max-height: 300px; /* 或根据需要调整高度 */
  overflow-y: auto; /* 显示垂直滚动条 */
  background-color: #f9f9f9; /* 背景色 */
  border: 1px solid #ccc; /* 边框样式 */
  margin-top: 10px; /* 顶部外边距 */
  width: auto; /* 自动宽度适应内容 */
  color: #000000;
}

.file-preview-container table {
  width: 100%; /* 表格宽度为100% */
  table-layout: fixed; /* 固定表格布局，使各列平均分配宽度 */
  border-collapse: collapse; /* 边框合并为单一边框 */
}

.file-preview-container th, td {
  border: 1px solid #000000; /* 单元格边框 */
  padding: 8px; /* 单元格内边距 */
  text-align: left; /* 文本左对齐 */
  word-wrap: break-word; /* 允许单词内换行 */
}

.file-preview-container th {
  background-color: #a8a8a8; /* 表头背景色 */
}

/* 响应式字体大小 */
@media (max-width: 600px) {
  th, td {
    font-size: 3vw; /* 小屏幕下字体大小调整 */
  }
}

/* 大屏幕适当增加字体大小 */
@media (min-width: 601px) {
  th, td {
    font-size: 0.9em; /* 大屏幕下固定字体大小 */
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
  border-top: 1px solid #c0c0c0; /* 添加顶部边框以区分内容 */
}

</style>


