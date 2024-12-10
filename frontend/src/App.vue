<template>
  <div id="app">
    <el-container>
      <el-header>
        <div class="nav-container">
          <router-link to="/" class="logo">
            面试经验管理系统
          </router-link>
          <div class="nav-menu">
            <router-link to="/" class="nav-item" :class="{ active: $route.name === 'Interviews' }">
              面试经验
            </router-link>
            <router-link to="/questions" class="nav-item" :class="{ active: $route.name === 'Questions' }">
              面试题目
            </router-link>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </div>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  text-decoration: none;
  margin-right: 40px;
}

.nav-menu {
  display: flex;
  gap: 20px;
}

.nav-item {
  color: #606266;
  text-decoration: none;
  font-size: 16px;
  padding: 0 15px;
  line-height: 60px;
  display: inline-block;
  transition: all 0.3s;
}

.nav-item:hover {
  color: #409EFF;
}

.nav-item.active {
  color: #409EFF;
  border-bottom: 2px solid #409EFF;
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>
