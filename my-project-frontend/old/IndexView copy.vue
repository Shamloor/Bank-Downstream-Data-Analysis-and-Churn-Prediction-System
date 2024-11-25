<template>
  <div>
    <div class="navbar" :style="{ backgroundColor: navbarBg }">
      <div class="logo">LOGO</div>
      <el-button @click="scrollTo('home')">首页</el-button>
      <el-button @click="scrollTo('userInfo')">用户信息展示</el-button>
      <el-button @click="scrollTo('bankRelation')">用户与银行关系展示</el-button>
      <el-button @click="scrollTo('userBehavior')">用户行为预测</el-button>
      <div class="profile-icon" @click="toggleProfilePanel">
        <img src="./img/点赞.png" alt="Profile Icon"> <!-- 替换为实际图标路径 -->
      </div>
    </div>
  </div>
  <transition name="slide">
    <div class="profile-panel" v-if="showProfilePanel">
      <img src="https://th.bing.com/th/id/OIP.8KgoeStvVSsE7KolSwapiAHaHa?rs=1&pid=ImgDetMain" alt="User Avatar" class="user-avatar"> <!-- 替换为用户头像路径 -->
      <div class="user-name">用户名</div>
      <el-button @click="logout">退出登录</el-button>
    </div>
  </transition>
  <div id="home" class="content-section">首页内容</div>
  <div id="userInfo" class="content-section">用户信息展示内容</div>
  <div id="bankRelation" class="content-section">用户与银行关系展示内容</div>
  <div id="userBehavior" class="content-section">用户行为预测内容</div>
<div>
  <el-button @click="userLogout">退出登录</el-button>
</div>
</template>




<script setup>
import { logout } from '@/net'
import router from "@/router";
import { ref, onMounted, onUnmounted } from 'vue';

const showProfilePanel = ref(false);

function toggleProfilePanel() {
  console.log("toggleProfilePanel called"); 
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

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
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
  justify-content: start; /* 按钮靠左 */
  padding: 10px 0;
  background-color: #333;
  /* box-shadow: 0 2px 4px rgba(0, 0, 0, 0.5); */
  z-index: 1000;
  transition: background-color 0.5s ease; /* 添加过渡效果 */
}

.logo {
  font-size: 24px; /* 更大的LOGO字体 */
  font-weight: bold;
  color: white;
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
}

#home { background-color: #1a1a1a; }
#userInfo { background-color: #2a2a2a; }
#bankRelation { background-color: #3a3a3a; }
#userBehavior { background-color: #4a4a4a; }

@keyframes blink {
  from { color: #fff; }
  to { color: #ccc; }
}

.profile-icon {
  cursor: pointer;
}

.profile-panel {
  position: fixed;
  right: -300px; /* 初始隐藏在视图外 */
  top: 60px; /* 从导航栏下方开始 */
  width: 300px;
  height: 300px; /* 限制高度，不覆盖整个页面 */
  background-color: #fff;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
  transition: right 0.5s ease-in-out; /* 平滑滑动特效 */
  z-index: 1000;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 20px;
}

.user-name {
  margin: 20px;
  font-size: 18px;
  font-weight: bold;
}

.slide-enter-active, .slide-leave-active {
  transition: right 0.5s ease-in-out;
}

.slide-enter, .slide-leave-to /* 初始和结束状态 (隐藏状态) */ {
  right: -300px;
}
</style>
