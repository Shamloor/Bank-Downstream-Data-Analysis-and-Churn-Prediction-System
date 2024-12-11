<template>
  <div class="message-container">
    <div class="data-list">
      <transition-group name="list" tag="div">
        <!-- 显示数据 -->
        <div
            v-for="(item, index) in dataList"
            :key="item.id"
            class="data-item"
            :class="{ 'highlight': item.changed }"
        >
          {{ item.name }} - {{ item.value }}
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// 模拟数据
const dataList = ref([]);
let dataCounter = 1;

// 定时更新数据
const addNewData = () => {
  const newItem = {
    id: dataCounter++,
    name: `Item ${dataCounter}`,
    value: Math.floor(Math.random() * 100),
    changed: true, // 标记数据变化
  };

  dataList.value.unshift(newItem); // 插入新数据到最前面

  // 高亮展示后去掉变化标记
  setTimeout(() => {
    newItem.changed = false;
  }, 1000); // 保持高亮1秒

  // 限制数据长度，避免无限增长
  if (dataList.value.length > 5) {
    dataList.value.pop();
  }
};

// 每隔2秒添加新数据
onMounted(() => {
  setInterval(addNewData, 2000);
});

</script>

<style scoped>
/* 实时展示部分 */
.message-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  background-color: #331b1b;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(104, 10, 10, 0.1);
}

.data-list {
  display: flex;
  width: fit-content;
  flex-direction: column-reverse;
  max-height: 400px;
  overflow-y: auto;
}

/* 隐藏滚动条 */
.data-list::-webkit-scrollbar {
  display: none;
}

.data-item {
  padding: 10px;
  margin: 5px 0;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  transition: background-color 1s ease, transform 0.3s;
}

/* 高亮变化的部分 */
.data-item.highlight {
  animation: highlightAnimation 1s forwards;
  transform: scale(1.05);
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