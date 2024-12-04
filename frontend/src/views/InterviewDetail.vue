<template>
  <div class="interview-detail">
    <div class="detail-header">
      <el-button icon="el-icon-back" @click="goBack">返回列表</el-button>
      <h2>面试经验详情</h2>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="interview" class="detail-content">
      <div class="info-section">
        <div class="info-item">
          <span class="label">公司名称：</span>
          <span class="value">{{ interview.company }}</span>
        </div>
        <div class="info-item">
          <span class="label">职位：</span>
          <span class="value">{{ interview.post || '未指定' }}</span>
        </div>
        <div class="info-item">
          <span class="label">标题：</span>
          <span class="value">{{ interview.title }}</span>
        </div>
        <div class="info-item">
          <span class="label">更新时间：</span>
          <span class="value">{{ formatDate(interview.editTime) }}</span>
        </div>
      </div>

      <div class="content-section">
        <h3>面试内容</h3>
        <div class="markdown-body" v-html="renderedContent"></div>
      </div>
    </div>

    <div v-else class="error-container">
      <el-empty description="未找到相关面试经验"></el-empty>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import MarkdownIt from 'markdown-it'
import 'github-markdown-css'

const md = new MarkdownIt({
  html: true,
  breaks: true,
  linkify: true
})

export default {
  name: 'InterviewDetail',
  data() {
    return {
      interview: null,
      loading: true
    }
  },
  computed: {
    renderedContent() {
      if (!this.interview?.content) return ''
      return md.render(this.interview.content)
    }
  },
  methods: {
    ...mapActions('interview', ['fetchInterviewById']),
    
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      }).replace(/\//g, '-')
    },

    goBack() {
      this.$router.push({
        name: 'Interviews',
        query: {
          page: this.$route.query.page || 1,
          size: this.$route.query.size || 20,
          title: this.$route.query.title,
          company: this.$route.query.company
        }
      })
    },

    async loadInterviewDetail() {
      try {
        this.loading = true
        const id = this.$route.params.id
        const response = await this.fetchInterviewById(id)
        this.interview = response
      } catch (error) {
        console.error('获取面试详情失败:', error)
        this.$message.error('获取面试详情失败，请稍后重试')
      } finally {
        this.loading = false
      }
    }
  },
  created() {
    this.loadInterviewDetail()
  }
}
</script>

<style scoped>
.interview-detail {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  gap: 20px;
}

.detail-header h2 {
  margin: 0;
}

.loading-container {
  padding: 20px;
}

.detail-content {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.info-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;
}

.info-item {
  margin-bottom: 15px;
  line-height: 1.6;
}

.info-item:last-child {
  margin-bottom: 0;
}

.label {
  font-weight: bold;
  color: #606266;
  margin-right: 10px;
  display: inline-block;
  width: 80px;
}

.value {
  color: #303133;
}

.content-section {
  margin-top: 20px;
}

.content-section h3 {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  color: #303133;
}

/* markdown样式 */
.markdown-body {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;
  height: auto;
  min-height: 200px;
  overflow-y: visible;
  word-break: break-word;
}

/* 确保markdown样式不被scoped影响 */
:deep(.markdown-body) {
  font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Helvetica, Arial, sans-serif;
  /* 代码块样式优化 */
  pre {
    background-color: #f6f8fa;
    border-radius: 6px;
    padding: 16px;
    overflow-x: auto;
  }
  code {
    background-color: rgba(175, 184, 193, 0.2);
    border-radius: 6px;
    padding: 0.2em 0.4em;
    font-size: 85%;
  }
  pre code {
    background-color: transparent;
    padding: 0;
  }
  /* 列表样式优化 */
  ul, ol {
    padding-left: 2em;
  }
  /* 表格样式优化 */
  table {
    border-spacing: 0;
    border-collapse: collapse;
    margin: 16px 0;
    width: 100%;
  }
  table th,
  table td {
    padding: 6px 13px;
    border: 1px solid #d0d7de;
  }
  table tr:nth-child(2n) {
    background-color: #f6f8fa;
  }
}

.error-container {
  text-align: center;
  padding: 40px 0;
}

@media screen and (max-width: 768px) {
  .interview-detail {
    padding: 10px;
  }

  .info-section {
    padding: 15px;
  }

  .label {
    width: auto;
    display: block;
    margin-bottom: 5px;
  }

  .markdown-body {
    padding: 15px;
  }
}
</style>
