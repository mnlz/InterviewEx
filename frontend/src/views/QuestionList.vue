<template>
  <div class="question-list">
    <!-- 搜索区域 -->
    <div class="search-section">
      <!-- 类别选择 -->
      <el-select
        v-model="selectedCategory"
        clearable
        placeholder="选择类别"
        class="category-select"
        @change="handleSearch">
        <el-option
          v-for="category in categoryList"
          :key="category"
          :label="category"
          :value="category">
          <el-tag
            :type="getCategoryType(category)"
            size="mini">
            {{ category }}
          </el-tag>
        </el-option>
      </el-select>

      <!-- 问题搜索框 -->
      <el-input
        v-model="searchKeyword"
        placeholder="搜索问题内容"
        clearable
        class="search-input"
        @clear="handleSearch"
        @keyup.enter.native="handleSearch">
        <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
      </el-input>
    </div>

    <!-- 问题列表表格 -->
    <el-table 
      :data="questions" 
      style="width: 100%" 
      v-loading="loading"
      element-loading-text="加载中..."
      element-loading-spinner="el-icon-loading"
      element-loading-background="rgba(255, 255, 255, 0.8)"
      size="small"
      stripe
      border>
      <el-table-column 
        prop="company" 
        label="公司" 
        width="150"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column 
        prop="question" 
        label="问题"
        min-width="400"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column 
        prop="category" 
        label="类别" 
        width="200">
        <template slot-scope="scope">
          <div class="category-tags">
            <el-tag
              v-for="cat in scope.row.category.split(',')"
              :key="cat"
              :type="getCategoryType(cat.trim())"
              size="mini"
              class="category-tag">
              {{ cat.trim() }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column 
        prop="editTime" 
        label="时间" 
        width="120">
        <template slot-scope="scope">
          {{ formatDate(scope.row.editTime) }}
        </template>
      </el-table-column>
      <el-table-column 
        label="操作" 
        width="180"
        align="center">
        <template slot-scope="scope">
          <div class="operation-buttons">
            <el-button 
              size="mini" 
              type="primary" 
              icon="el-icon-view"
              @click="viewDetail(scope.row)">
              查看
            </el-button>
            <el-button 
              size="mini" 
              type="danger" 
              icon="el-icon-delete"
              @click="handleDelete(scope.row)">
              删除
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        :disabled="loading"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: 'QuestionList',
  data() {
    return {
      loading: false,
      questions: [],
      selectedCategory: '',
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      categoryList: [
        'java-面向对象',
        'java-集合',
        'java-多线程',
        'MySQL数据库',
        'Redis缓存',
        'SSM项目',
        '分布式微服务',
        '数据结构算法',
        '计算机基础',
        '消息队列'
      ],
      categoryColors: {
        'java-面向对象': '',
        'java-集合': 'success',
        'java-多线程': 'warning',
        'MySQL数据库': 'danger',
        'Redis缓存': 'info',
        'SSM项目': '',
        '分布式微服务': 'warning',
        '数据结构算法': 'success',
        '计算机基础': 'info',
        '消息队列': 'danger'
      }
    }
  },
  methods: {
    // 搜索问题
    async searchQuestions() {
      this.loading = true;
      try {
        const response = await this.$axios.get('/api/questions/search', {
          params: {
            category: this.selectedCategory || undefined,
            keyword: this.searchKeyword || undefined,
            page: this.currentPage - 1,
            size: this.pageSize
          }
        });
        
        this.questions = response.data.content;
        this.total = response.data.totalElements;
      } catch (error) {
        console.error('Failed to search questions:', error);
        this.$message.error('搜索问题失败');
      } finally {
        this.loading = false;
      }
    },

    // 查看详情
    viewDetail(row) {
      this.$router.push({
        name: 'QuestionDetail',
        params: { id: row.id },
        query: {
          page: this.currentPage,
          size: this.pageSize,
          category: this.selectedCategory,
          keyword: this.searchKeyword
        }
      });
    },

    // 删除问题
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除这个问题吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        await this.$axios.delete(`/api/questions/${row.id}`);
        this.$message.success('删除成功');
        this.searchQuestions();
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Failed to delete question:', error);
          this.$message.error('删除失败');
        }
      }
    },

    // 处理搜索
    handleSearch() {
      this.currentPage = 1;
      this.searchQuestions();
    },

    // 处理页码变化
    handleCurrentChange(val) {
      this.currentPage = val;
      this.searchQuestions();
    },

    // 处理每页条数变化
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.searchQuestions();
    },

    // 格式化日期
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    },

    // 获取类别对应的颜色类型
    getCategoryType(category) {
      return this.categoryColors[category] || '';
    }
  },
  created() {
    // 从 URL 查询参数恢复状态
    const query = this.$route.query;
    if (query) {
      this.currentPage = parseInt(query.page) || 1;
      this.pageSize = parseInt(query.size) || 10;
      this.selectedCategory = query.category || '';
      this.searchKeyword = query.keyword || '';
    }
    this.searchQuestions();
  }
}
</script>

<style scoped>
.question-list {
  padding: 20px;
}

.search-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.category-select {
  width: 160px;
}

.search-input {
  width: 300px;
}

.el-select-dropdown__item {
  padding: 0 12px;
}

.category-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.category-tag {
  margin-right: 4px;
}

.category-tag:last-child {
  margin-right: 0;
}

.operation-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>