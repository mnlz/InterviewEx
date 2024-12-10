<template>
  <div class="question-detail">
    <div class="detail-header">
      <el-button icon="el-icon-back" @click="goBack">返回列表</el-button>
      <h2>面试题详情</h2>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="question" class="detail-content">
      <div class="info-section">
        <div class="info-item">
          <span class="label">公司名称：</span>
          <span class="value">{{ question.company }}</span>
        </div>
        <div class="info-item">
          <span class="label">类别：</span>
          <span class="value">{{ question.category }}</span>
        </div>
        <div class="info-item">
          <span class="label">更新时间：</span>
          <span class="value">{{ formatDate(question.editTime) }}</span>
        </div>
      </div>

      <div class="content-section">
        <h3>问题内容</h3>
        <div class="question-content">{{ question.question }}</div>
      </div>

      <div class="ai-answer-section">
        <div class="ai-header">
          <h3>AI解答</h3>
          <el-button 
            type="primary" 
            :loading="aiLoading"
            @click="getAIAnswer"
            icon="el-icon-magic-stick">
            获取AI解答
          </el-button>
        </div>
        <div v-if="aiAnswer" class="ai-content markdown-body" v-html="formattedAIAnswer"></div>
      </div>
    </div>

    <div v-else class="error-container">
      <el-empty description="未找到相关面试题"></el-empty>
    </div>
  </div>
</template>

<script>
import MarkdownIt from 'markdown-it'
import 'github-markdown-css'

const md = new MarkdownIt({
  html: true,
  breaks: true,
  linkify: true
})

export default {
  name: 'QuestionDetail',
  data() {
    return {
      question: null,
      loading: true,
      aiLoading: false,
      aiAnswer: '',
      streamController: null,
    }
  },
  computed: {
    formattedAIAnswer() {
      if (!this.aiAnswer) return ''
      return md.render(this.aiAnswer)
    }
  },
  methods: {
    formatDate(date) {
      if (!date) return '';
      return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      }).replace(/\//g, '-');
    },

    goBack() {
      this.$router.push({
        name: 'Questions',
        query: {
          page: this.$route.query.page || 1,
          size: this.$route.query.size || 10,
          categories: this.$route.query.categories,
          keyword: this.$route.query.keyword
        }
      });
    },

    async loadQuestionDetail() {
      try {
        this.loading = true;
        const id = this.$route.params.id;
        const response = await this.$axios.get(`/api/questions/${id}`);
        this.question = response.data;
      } catch (error) {
        console.error('获取面试题详情失败:', error);
        this.$message.error('获取面试题详情失败，请稍后重试');
      } finally {
        this.loading = false;
      }
    },

    async getAIAnswer() {
      if (this.aiLoading) {
        // 如果正在加载，取消之前的请求
        if (this.streamController) {
          this.streamController.abort();
        }
        return;
      }

      this.aiLoading = true;
      this.aiAnswer = '';
      this.streamController = new AbortController();

      try {
        const response = await fetch('https://api.coze.cn/v3/chat', {
          method: 'POST',
          headers: {
            'Authorization': '',
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            bot_id: '7444824264752119817',
            user_id: '123456789',
            stream: true,
            auto_save_history: true,
            additional_messages: [
              {
                role: 'user',
                content: this.question.question,
                content_type: 'text'
              }
            ]
          }),
          signal: this.streamController.signal
        });

        const reader = response.body.getReader();
        const decoder = new TextDecoder();

        let reading = true;
        while (reading) {
          const { value, done } = await reader.read();
          if (done) {
            reading = false;
            break;
          }

          const chunk = decoder.decode(value);
          const lines = chunk.split('\n');
          let currentEvent = null;

          for (const line of lines) {
            // 跳过空行
            if (!line.trim()) continue;

            // 处理事件行
            if (line.startsWith('event:')) {
              currentEvent = line.slice(6).trim();
              continue;
            }

            // 处理数据行
            if (line.startsWith('data:')) {
              try {
                const data = JSON.parse(line.slice(5));

                // 处理流式回答
                if (currentEvent === 'conversation.message.delta' &&
                    data.role === 'assistant' && 
                    data.type === 'answer' && 
                    data.content_type === 'text') {
                  this.aiAnswer += data.content;
                  continue;
                }

                // 处理结束事件
                if (data.type === 'verbose' && 
                    typeof data.content === 'string' && 
                    data.content.includes('generate_answer_finish')) {
                  reading = false;
                  break;
                }
              } catch (e) {
                // 忽略解析错误
                console.error('Parse error:', e);
              }
            }
          }
        }
      } catch (error) {
        if (error.name === 'AbortError') {
          console.log('请求被取消');
        } else {
          console.error('获取AI回答失败:', error);
          this.$message.error('获取AI回答失败，请稍后重试');
        }
      } finally {
        this.aiLoading = false;
        this.streamController = null;
      }
    },
  },
  created() {
    this.loadQuestionDetail();
  }
}
</script>

<style scoped>
.question-detail {
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

.question-content {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;
  line-height: 1.8;
  white-space: pre-wrap;
}

.ai-answer-section {
  margin-top: 30px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 20px;
}

.ai-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.ai-content {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  line-height: 1.6;
}

/* 确保 markdown-body 的样式不受 scoped 影响 */
:deep(.markdown-body) {
  background-color: transparent;
}

:deep(.markdown-body pre) {
  background-color: #f6f8fa;
  border-radius: 4px;
  padding: 16px;
}

:deep(.markdown-body code) {
  background-color: rgba(175,184,193,0.2);
  border-radius: 4px;
  padding: 0.2em 0.4em;
}
</style>
