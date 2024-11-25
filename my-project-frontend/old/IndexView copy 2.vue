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
    <div class="line" style="border-bottom: 1px solid #c0c0c0;"></div>
    <header style="margin-top: 70px; margin-left: 20px;">
      <h1>可视化分析</h1>
    </header>
    <div id="mapss" style="width:500px;height:500px; border: 1px solid;">good</div>
  </div>
  <div id="bankRelation" class="content-section">
    <div class="line" style="border-bottom: 1px solid #c0c0c0;"></div>
    <header style="margin-top: 70px; margin-left: 20px;">
      <h1>模型训练</h1>
    </header>
    <div class="button">
      <button style="font-size: 20px;" @click="downloadFile">开始训练</button>
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
      <ul>
        <li v-for="(data, index) in previewData" :key="index">{{ data }}</li>
      </ul>
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


import china from 'echarts/map/json/china.json'
import * as echarts from 'echarts';

const initMap = () => {
  echarts.registerMap('china', china);
  const myChart = echarts.init(document.getElementById('mapss'));
  // 执行其他初始化操作
};


onMounted(() => {
  initMap();
  window.addEventListener('scroll', handleScroll);
});

</script>


<style scoped>
/* 整体字体和背景色调整 */
body {
  font-family: 'Arial', sans-serif;
  color: #fff;
  background-color: #121212;
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


