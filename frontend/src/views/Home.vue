<template>
  <div class="home">
    <h1>Interview Questions Management System</h1>
    <el-card class="welcome-card">
      <div slot="header">
        <span>Welcome to the Interview Questions Management System</span>
      </div>
      <p>This system helps you manage and organize interview questions from different companies.</p>
      <el-button type="primary" @click="$router.push('/interviews')">View Interview Questions</el-button>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.home {
  padding: 20px;
}

.welcome-card {
  max-width: 600px;
  margin: 20px auto;
}

.welcome-card p {
  margin: 20px 0;
}
</style>
