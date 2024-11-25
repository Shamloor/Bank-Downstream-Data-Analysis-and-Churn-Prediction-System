<template>
  <div>
    <div class="navbar" :style="{ backgroundColor: navbarBg }">
      <div class="logo">APen</div>
      <el-button @click="scrollTo('home')">首页</el-button>
      <el-button @click="handleNavigation('userInfo', 'cao')">可视化分析</el-button>
      <el-button @click="handleNavigation('bankRelation', 'cao')">以往数据预测</el-button>
      <el-button @click="handleNavigation('userBehavior', 'cao')">上传新数据预测</el-button>
      <div class="profile-icon" @click="toggleProfilePanel">
        <img src="./img/个人中心.png" style="max-width: 150%; max-height: 150%; width: auto; height: auto;">
      </div>
    </div>
  </div>
  <div class="profile-panel" :style="{ right: profilePanelStyle.right, backgroundColor: navbarBg }">
    <div class="profile-content">
      <img src="./img/壁纸（4）.jpg" alt="用户头像" class="user-avatar"/>
      <div class="username" id="fuck1"></div>
      <div @click="showHelp" class="help-link">状态：已分析</div>
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
            <button @click="handleNavigation('userInfo', 'cao')">数据可视化</button>
            <button @click="handleNavigation('bankRelation', 'cao')">分析用户</button>
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
        <span v-for="n in 50" :key="n" class="marquee-item">Better Call Apen</span>
      </div>
    </div>
  </div>
  <div v-if="dataSetUploaded" id='cao' class="content-section" style="background-color: #1a1a1a; border: 0px;">
    <header style="margin-top: 70px; margin-left: 20px; display: flex; justify-content: space-between;">
      <h1>请先上传您的银行往期用户记录</h1>
    </header>
    <div class="upload-area" @dragover.prevent="onDragOver" @drop.prevent="onUserFileDrop">
      <input type="file" id="xfile" ref="fileInput" @change="handleUserFileUpload" accept=".csv" hidden>
      <p v-if="!selectedUserFile">拖拽文件到这里或者 <span class="upload-link" @click="triggerFileInput">选择文件</span></p>
      <p v-else>{{ selectedUserFile.name }}</p>
    </div>
    <div class="button">
      <button @click="uploadUserFile" :disabled="!selectedUserFile">上传</button>
    </div>
  </div>
  <div v-if="!dataSetUploaded" id="userInfo" class="content-section">
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
      <div class="row">
        <img src="./img/ProgramDVI/df.png" alt="Customer Churn Bar Chart" class="chart">
      </div>
      <div class="row">
        <img src="./img/ProgramDVI/df1(1).png" alt="Customer Churn Pie Chart" class="chart">
        <div class="table-container">
          <table>
          <thead>
            <tr>
              <th v-for="(header, index) in tablesx.train.headers.value" :key="'header-' + index">{{ header }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, index) in tablesx.train.processedData.value" :key="'row-' + index">
              <td v-for="(item, idx) in row" :key="'cell-' + index + '-' + idx">{{ item }}</td>
            </tr>
          </tbody>
        </table>
        </div>
      </div>
    </div>

    <!-- 客户性别分析 -->
    <div v-if="activeView === 'gender'" class="analysis-block">
      <div class="row">
      <img src="./img/ProgramDVI/df2.png" alt="Customer Gender Bar Chart"  class="chart">
      <img src="./img/ProgramDVI/df2(1).png" alt="Customer Gender Pie Chart" class="chart">
      </div>
    
    </div>

    <!-- 客户地理分析 -->
    <div v-if="activeView === 'geography'" class="analysis-block">
      <div class="row">
      <img src="./img/ProgramDVI/df3.png" alt="Customer Geography Bar Chart"  class="chart">
      <img src="./img/ProgramDVI/df3(1).png" alt="Customer Geography Pie Chart"  class="chart"></div>
      <div class="table-container"><table>
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
        </table></div>
    </div>

    <!-- 客户信用卡分析 -->
    <div v-if="activeView === 'credit'" class="analysis-block">
      <div class="row">
      <img src="./img/ProgramDVI/df4.png" alt="Customer Credit Card Bar Chart"  class="chart" >
      <img src="./img/ProgramDVI/df4(1).png" alt="Customer Credit Card Pie Chart"  class="chart"></div>
      <div class="table-container"><table>
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
        </table></div>
    </div>

    <!-- 客户活跃会员分析 -->
    <div v-if="activeView === 'active'" class="analysis-block">
      <div class="row">
      <img src="./img/ProgramDVI/df5.png" alt="Customer Active Membership Bar Chart"  class="chart">
      <img src="./img/ProgramDVI/df5(1).png" alt="Customer Active Membership Pie Chart"  class="chart"></div>
      <div class="table-container"><table>
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
        </table></div>
    </div>

    <!-- 客户产品分析 -->
    <div v-if="activeView === 'products'" class="analysis-block">
      <div class="row">
      <img src="./img/ProgramDVI/df6.png" alt="Customer Product Bar Chart"  class="chart">
      <img src="./img/ProgramDVI/df6(1).png" alt="Customer Product Pie Chart"  class="chart"></div>
      <div class="table-container"><table>
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
        </table></div>
    </div>

    <!-- 客户账户持续期分析 -->
    <div v-if="activeView === 'tenure'" class="analysis-block">
      <div class="row">
      <img src="./img/ProgramDVI/df7.png" alt="Customer Tenure Bar Chart"  class="chart">
      <img src="./img/ProgramDVI/df7(1).png" alt="Customer Tenure Pie Chart"  class="chart"></div>
      <div class="table-container"><table>
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
        </table></div>
    </div>

    <!-- 客户信用评分分析 -->
    <div v-if="activeView === 'creditScore'" class="analysis-block">
      <div class="row">
      <img src="./img/ProgramDVI/df8.png" alt="Customer Credit Score Histogram" style="width: 60%;"  class="chart"></div>
      <div class="table-container"><table>
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
        </table></div>
    </div>

    <!-- 客户年龄分布分析 -->
    <div v-if="activeView === 'age'" class="analysis-block">
      <div class="row">
      <img src="./img/ProgramDVI/df9.png" alt="Customer Age Distribution Histogram" style="width: 60%;"  class="chart"></div>
      <div class="table-container"><table>
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
        </table></div>
    </div>

    <!-- 客户余额分布分析 -->
    <div v-if="activeView === 'balance'" class="analysis-block">
      <div class="row">
      <img src="./img/ProgramDVI/df10.png" alt="Customer Balance Distribution Histogram" style="width: 60%;"  class="chart"></div>
      <div class="table-container"><table>
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
        </table></div>
    </div>

    <!-- 客户估计薪资分布分析 -->
    <div v-if="activeView === 'salary'" class="analysis-block">
      <div class="row">
      <img src="./img/ProgramDVI/df11.png" alt="Customer Estimated Salary Distribution Histogram" style="width: 60%;"  class="chart"></div>
      <div class="table-container"><table>
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
        </table></div>
    </div>
    <div id="worldChart" v-if="activeView === 'fen'" style="background-color: #757575; height: 600px;"></div>
  </div>
  <div v-if="!dataSetUploaded" id="bankRelation" class="content-section">
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
      <img src="./img/roc_curve.png" alt="Customer Geography Bar Chart" class="chart">
      <img src="./img/feature_importances.png" alt="Customer Geography Pie Chart" class="chart">
    </div>
  </div>
  <div v-if="!dataSetUploaded" id="userBehavior" class="content-section">
    <div class="line" style="border-bottom: 1px solid #c0c0c0;"></div>
    <header style="margin-top: 70px; margin-left: 20px;">
      <h1>上传新数据预测</h1>
    </header>
    <div class="upload-area" @dragover.prevent="onDragOver" @drop.prevent="onFileDrop">
      <input type="file" id="file" ref="fileInput" @change="handleFileUpload" accept=".csv" hidden>
      <p v-if="!selectedFile">拖拽文件到这里或者 <span class="upload-link" @click="triggerXFileInput">选择文件</span></p>
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
              <td style="color: black;" v-for="(item, idx) in row" :key="'cell-' + index + '-' + idx">{{ item }}</td>
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
  const userData = ref(tablesx.train_geography.processedData.value.map(data => ({
    name: data[0],  // 国家名
    value: data[1]  // 用户数量
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
        mapType: 'world',  // 使用世界地图
        roam: true,
        center: [15, 50],  // 聚焦到欧洲的大致中心
        zoom: 8,
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
  checkDataSetStatus();

  if (dataSetUploaded.value == false)
  {
    fenmysql();
  }

  if (sessionStorage.getItem('uploadSuccess') === 'true') {
    // 延迟一段时间后显示消息，以确保页面加载完成
    ElMessage.success('您的数据上传成功！请享受您的分析！');
      // 清除状态，以免再次刷新时重复显示消息
      sessionStorage.removeItem('uploadSuccess');
  }

  
});

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
const selectedUserFile = ref(null);
const previewData = ref([]);

// 触发文件选择
function triggerFileInput() {
  document.getElementById('xfile').click();
}

function triggerXFileInput() {
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

function onUserFileDrop(event) {
  handleUserFileUpload(event);
}


// 处理文件上传选择
function handleFileUpload(event) {
  const files = event.dataTransfer ? event.dataTransfer.files : event.target.files;
  const file = files[0];
  console.log(file);
  if (!file.name.endsWith('.csv')) {
    ElMessage.error('只能上传CSV文件！');
    return;
  }
  selectedFile.value = file;
}

function handleUserFileUpload(event) {
  const files = event.dataTransfer ? event.dataTransfer.files : event.target.files;
  const file = files[0];
  if (!file.name.endsWith('.csv')) {
    ElMessage.error('只能上传CSV文件！');
    return;
  }
  selectedUserFile.value = file;
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

function uploadUserFile() {
  
  const formData = new FormData();
  formData.append('file', selectedUserFile.value);  // selectedFile.value 是你从 <input type="file"> 获取的文件

  enhancedPost('/api/ssh/upload', formData
  , () => {
            });
            sessionStorage.setItem('uploadSuccess', 'true');
  location.reload();

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
        // 处理数据：从第一行开始处理，提取前三列，并将第三列大于0.5的值转为true，否则为false
        processedData = previewData1.slice(1).map((line, index) => {
            // 拆分每行数据为数组
            const values = line.split(',').map(value => value.trim());

            // 提取前三列数据
            const firstThreeColumns = values.slice(0, 2);

            // 将第三列数据转换为布尔值
            const thirdColumnValue = parseFloat(firstThreeColumns[2]); // 假设第三列是数值型

            // 大于0.5转为true，否则转为false
            const isGreaterThanHalf = thirdColumnValue > 0.5;

            // 返回处理后的数据数组
            return [
              ...firstThreeColumns,
              isGreaterThanHalf ? '是' : '否'  // 根据条件转换为是或者否
            ];
          });

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

let mheaders = ref(null);
let mpreviewData1 = ref([]);
let mprocessedData = ref([]);
const defaultImagePath = './img/saul.jpg';

function downloadMFile() {
  get(`/api/ssh/train/fuck`, 
    (data) => {
            const loadingInstance = ElLoading.service({
          lock: true,
          text: '正在处理，请稍候...',
          background: 'rgba(0, 0, 0, 0.7)'
        });
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
    undefined,  // 这里没有定义中间处理函数
    (message) => {
        ElMessage.warning(`文件下载失败: ${message}`);
        coldTime.value = 0;  // 此处的 coldTime 处理可能不适用于下载逻辑，应该调整或移除
        loadingInstance.close();
    });

}

const componentKey = ref(0);
const componentKey1 = ref(0);
const componentKey2 = ref(0);
const componentKey3 = ref(0);

let dataSetUploaded = ref(false);

function checkDataSetStatus() {
  try {
    const username = sessionStorage.getItem('username');
    console.log(username)
        // 获取 ID 为 'displayDiv' 的 div 元素
    let displayDiv = document.getElementById('fuck1');
    // 将 username 变量的值设置到 div 的文本内容中
    displayDiv.textContent = `用户名: ${username}`;

    const message1 = username + '.csv' 

    get(`/api/ssh/status/${message1}`, 
    (data) => {
        console.log(data)
        if (data.includes('Error: File does not exist at path'))
        {
          ElMessage.warning('请上传数据集！');
          dataSetUploaded.value = true;
        }
        else
        {
          dataSetUploaded.value = false;
        }
    }, 
    undefined,  // 这里没有定义中间处理函数
    (message) => {
        ElMessage.warning(`文件下载失败: ${message}`);
        coldTime.value = 0;  // 此处的 coldTime 处理可能不适用于下载逻辑，应该调整或移除
    });
  } catch (error) {
    ElMessage.error('检查数据集状态失败: ' + error.message);
    console.log("cnm")
  }
}


function handleNavigation(defaultId, alternateId) {
    if (dataSetUploaded.value) {
        scrollTo(alternateId);
        ElMessage.warning('请先上传数据集');
    } else {
        scrollTo(defaultId);
    }
}

const tablesx = {
  train: {
    headers: ref([]),
    processedData: ref([])
  },
  train_age: {
    headers: ref([]),
    processedData: ref([])
  },
  train_balance: {
    headers: ref([]),
    processedData: ref([])
  },
  train_creditscore: {
    headers: ref([]),
    processedData: ref([])
  },
  train_estimatedsalary: {
    headers: ref([]),
    processedData: ref([])
  },
  train_exited: {
    headers: ref([]),
    processedData: ref([])
  },
  train_gender: {
    headers: ref([]),
    processedData: ref([])
  },
  train_geography: {
    headers: ref([]),
    processedData: ref([])
  },
  train_hascrcard: {
    headers: ref([]),
    processedData: ref([])
  },
  train_isactivemember: {
    headers: ref([]),
    processedData: ref([])
  },
  train_numofproducts: {
    headers: ref([]),
    processedData: ref([])
  },
  train_tenure: {
    headers: ref([]),
    processedData: ref([])
  }
};

function fenmysql() {

const tables = [
  'train', 
  'train_age', 
  'train_balance', 
  'train_creditscore', 
  'train_estimatedsalary', 
  'train_exited', 
  'train_gender', 
  'train_geography', 
  'train_hascrcard', 
  'train_isactivemember', 
  'train_numofproducts', 
  'train_tenure'
];

tables.forEach(table => {
  get(`/api/mysql/table/${table}`, 
    (data) => {
      const lines = data.split('\n');
      const previewData2 = lines.slice(0, Math.min(10, lines.length));
      console.log(previewData2);
      tablesx[table].headers.value = previewData2[0].split(',').map(header => header.trim());
      tablesx[table].processedData.value = previewData2.slice(1, 101).map(line =>
        line.split(',').map(value => value.trim())
      );
      console.log(tablesx[table].processedData.value)
    }, 
    undefined,  // 这里没有定义中间处理函数
    (message) => {
      ElMessage.warning(`表格 ${table} 下载失败: ${message}`);
      coldTime.value = 0;  // 根据需要调整或移除
    }
  );
});
console.log(tablesx.train_geography.headers.value);

}
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
  width: 100%; /* 表格宽度为100% */
  border-collapse: collapse; /* 边框合并为单一边框 */
}

.table-container th, td {
  border: 1px solid #000000; /* 单元格边框 */
  text-align: center;
  padding: 8px; /* 文本左对齐 */
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
  background-color: #424242; /* 背景色 */
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


