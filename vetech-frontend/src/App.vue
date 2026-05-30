<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <el-container class="app-shell">
    <el-header height="56px" class="app-header">
      <div class="app-title">差旅报销管理</div>
      <nav class="app-nav">
        <RouterLink to="/">报销单列表</RouterLink>
        <RouterLink to="/reimbursements/new">新建报销单</RouterLink>
      </nav>
    </el-header>
    <el-main class="app-main">
      <RouterView />
    </el-main>
  </el-container>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #1f2a44;
  color: #fff;
  padding: 0 24px;
}

.app-title {
  font-size: 18px;
  font-weight: 600;
}

.app-nav {
  display: flex;
  gap: 16px;
}

.app-nav a {
  color: #fff;
  text-decoration: none;
  font-size: 14px;
}

.app-nav a.router-link-exact-active {
  font-weight: 600;
}

.app-main {
  padding: 24px;
  background: #f5f7fb;
}
</style>
