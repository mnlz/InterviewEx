<template>
  <div class="interview-list">
    <!-- 搜索和添加区域 -->
    <div class="controls">
      <div class="search-section">
        <el-select
          v-model="selectedCompany"
          filterable
          clearable
          placeholder="选择或搜索公司"
          class="company-select"
          @change="handleCompanyChange"
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
          <el-option-group label="互联网中厂">
            <el-option
              v-for="item in companies.mediumCompanies"
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
          <el-option-group label="互联网小厂">
            <el-option
              v-for="item in companies.smallCompanies"
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
        min-width="200"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column 
        prop="content" 
        label="内容" 
        show-overflow-tooltip>
        <template slot-scope="scope">
          <span class="content-preview">{{ scope.row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column 
        prop="editTime" 
        label="更新时间" 
        width="120">
        <template #default="scope">
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
      this.$router.push(`/interview/${row.id}`)
    },
    
    // 搜索面试列表
    async searchInterviews() {
      this.loading = true
      try {
        // 构建搜索条件
        let companyNames = []
        if (this.selectedCompany) {
          // 获取选中公司的全称和所有简称
          const selectedFullName = this.selectedCompany
          const selectedAliases = [...this.companies.bigCompanies, ...this.companies.mediumCompanies, ...this.companies.smallCompanies]
            .find(company => company.value === selectedFullName)?.aliases || []
          
          // 将全称和所有简称加入搜索条件
          companyNames = [selectedFullName, ...selectedAliases]
        }

        // 调用后端API，传入所有可能的公司名称
        const response = await this.$store.dispatch('interview/fetchInterviews', {
          page: this.currentPage,
          pageSize: this.pageSize,
          companyNames
        })

        this.interviews = response.data
        this.total = response.total
      } catch (error) {
        console.error('Failed to fetch interviews:', error)
        this.$message.error('获取面试列表失败')
      } finally {
        this.loading = false
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

    // 监听公司选择变化
    handleCompanyChange() {
      this.currentPage = 1
      this.searchInterviews()
    },

    // 格式化日期
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  },
  created() {
    this.searchInterviews()
  }
}
</script>

<style scoped>
.interview-list {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.controls {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 20px;
  gap: 20px;
}

.search-section {
  flex: 1;
  max-width: 400px;
}

.company-select {
  width: 100%;
}

.company-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.company-name {
  font-weight: bold;
}

.company-aliases {
  color: #909399;
  font-size: 0.9em;
}

/* 自定义下拉选项样式 */
:deep(.el-select-dropdown__item) {
  padding: 8px 12px;
}

:deep(.el-select-dropdown__item.selected) {
  font-weight: normal;
}

:deep(.el-select-dropdown__item.selected .company-name) {
  color: #409EFF;
  font-weight: bold;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.el-table {
  margin-top: 20px;
  font-size: 14px;
}

.content-preview {
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .controls {
    flex-direction: column;
  }
  
  .search-section {
    max-width: 100%;
  }

  .interview-list {
    padding: 10px;
  }
}
</style>
