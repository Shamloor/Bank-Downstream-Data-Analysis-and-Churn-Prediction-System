<template>
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
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios'; // 引入axios

/* 可视化部分 */
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
      if (dataList.value.length > 5) {
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
</script>

<style scoped>
/* 实时展示部分 */
.message-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #331b1b;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(104, 10, 10, 0.1);
}

.data-list {
  display: flex;
  flex-direction: column-reverse;
  max-height: 400px;

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
  padding: 10px;
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
</style>