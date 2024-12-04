<template>
  <div class="interview-list">
    <!-- 搜索和添加区域 -->
    <div class="controls">
      <div class="search-section">
        <!-- 标题搜索框 -->
        <el-input
          v-model="searchTitle"
          placeholder="搜索标题"
          clearable
          class="search-input"
          @clear="handleSearch"
          @keyup.enter.native="handleSearch">
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>

        <!-- 公司搜索框 -->
        <el-input
          v-model="searchCompany"
          placeholder="搜索公司"
          clearable
          class="search-input"
          @clear="handleSearch"
          @keyup.enter.native="handleSearch">
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>

        <!-- 公司选择下拉框 -->
        <el-select
          v-model="selectedCompany"
          filterable
          clearable
          placeholder="选择预设公司"
          class="company-select"
          @change="handleCompanySelect"
          :loading="loading"
        >
          <el-option-group label="互联网大厂">
            <el-option
              v-for="item in companies.bigCompanies"
              :key="item.value"
              :label="item.label"
              :value="item.value">
              <span class="company-option">
                <span class="company-name">{{ item.label }}</span>
                <span class="company-aliases" v-if="item.aliases && item.aliases.length">
                  ({{ item.aliases.join(' / ') }})
                </span>
              </span>
            </el-option>
          </el-option-group>
          <!-- 其他公司分组保持不变 -->
        </el-select>
      </div>
    </div>

    <!-- 面试题表格 -->
    <el-table 
      :data="interviews" 
      style="width: 100%" 
      v-loading="loading"
      element-loading-text="加载中..."
      element-loading-spinner="el-icon-loading"
      element-loading-background="rgba(255, 255, 255, 0.8)"
      size="small"
      stripe
      border
      highlight-current-row
    >
      <el-table-column 
        prop="company" 
        label="公司" 
        width="150"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column 
        prop="title" 
        label="标题"
        width="200"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column 
        prop="content" 
        label="内容" 
        min-width="400"
        show-overflow-tooltip>
        <template slot-scope="scope">
          <span class="content-preview">{{ scope.row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column 
        prop="editTime" 
        label="更新时间" 
        width="120">
        <template slot-scope="scope">
          {{ formatDate(scope.row.editTime) }}
        </template>
      </el-table-column>
      <el-table-column 
        label="操作" 
        width="100"
        align="center">
        <template slot-scope="scope">
          <el-button 
            size="mini" 
            type="primary" 
            icon="el-icon-view"
            @click="viewDetail(scope.row)">
            查看
          </el-button>
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
        @size-change="handleSizeChange"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import companies from '@/assets/companies.json'

export default {
  name: 'InterviewList',
  data() {
    return {
      loading: false,
      selectedCompany: '',
      searchTitle: '', // 新增：标题搜索关键字
      searchCompany: '', // 新增：公司搜索关键字
      currentPage: 1,
      pageSize: 10,
      total: 0,
      interviews: [],
      companies
    }
  },
  methods: {
    // 查看详情
    viewDetail(row) {
      this.$router.replace({
        name: 'InterviewDetail',
        params: { id: row.id },
        query: {
          from: 'list',
          page: this.currentPage,
          size: this.pageSize,
          title: this.searchTitle || undefined,
          company: this.searchCompany || undefined
        }
      })
    },

    // 高级搜索面试列表
    async searchInterviews() {
      this.loading = true;
      try {
        const response = await this.$axios.get('/api/interviews/advanced-search', {
          params: {
            title: this.searchTitle || undefined,
            company: this.searchCompany || undefined,
            page: this.currentPage - 1,
            size: this.pageSize
          }
        });
        
        // 更新数据
        this.interviews = response.data.content;
        this.total = response.data.totalElements;
      } catch (error) {
        console.error('Failed to fetch interviews:', error);
        this.$message.error('获取面试列表失败');
      } finally {
        this.loading = false;
      }
    },

    // 处理搜索按钮点击
    handleSearch() {
      this.selectedCompany = ''; // 清空预设公司选择
      this.currentPage = 1; // 重置页码
      this.searchInterviews();
    },

    // 处理预设公司选择
    async handleCompanySelect(value) {
      this.loading = true;
      // 清空高级搜索的输入
      this.searchTitle = '';
      this.searchCompany = '';
      
      try {
        // 获取选中公司的所有搜索关键字（公司名称和别称）
        let searchKeywords = [];
        if (value) {
          // 查找选中的公司信息
          const selectedCompany = [
            ...this.companies.bigCompanies,
            ...this.companies.mediumCompanies,
            ...this.companies.smallCompanies
          ].find(company => company.value === value);

          if (selectedCompany) {
            // 添加公司全称
            searchKeywords.push(selectedCompany.value);
            // 添加所有别称
            if (selectedCompany.aliases && selectedCompany.aliases.length > 0) {
              searchKeywords = searchKeywords.concat(selectedCompany.aliases);
            }
          }
        }

        // 发送搜索请求
        const response = await this.$axios.get('/api/interviews/search', {
          params: {
            companyNames: searchKeywords.length > 0 ? searchKeywords.join(',') : undefined,
            page: this.currentPage - 1,
            size: this.pageSize
          }
        });
        
        // 更新数据
        this.interviews = response.data.content;
        this.total = response.data.totalElements;
      } catch (error) {
        console.error('Failed to fetch interviews by company:', error);
        this.$message.error('获取面试列表失败');
      } finally {
        this.loading = false;
      }
    },

    // 处理页码变化
    handleCurrentChange(val) {
      this.currentPage = val
      this.searchInterviews()
    },

    // 处理每页条数变化
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.searchInterviews()
    },

    // 格式化日期
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    }
  },
  
  created() {
    // 从路由参数中恢复状态
    const query = this.$route.query;
    if (query) {
      this.currentPage = parseInt(query.page) || 1;
      this.pageSize = parseInt(query.size) || 10;
      this.searchTitle = query.title || '';
      this.searchCompany = query.company || '';
    }
    this.searchInterviews();
  }
}
</script>

<style scoped>
.interview-list {
  padding: 20px;
}

.controls {
  margin-bottom: 20px;
}

.search-section {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-input {
  width: 250px;
}

.company-select {
  width: 200px;
}

.company-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.company-name {
  font-weight: bold;
}

.company-aliases {
  color: #909399;
  font-size: 12px;
}

.content-preview {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>