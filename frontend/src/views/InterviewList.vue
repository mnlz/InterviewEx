<template>
  <div class="interview-list">
    <h2>Interview Questions</h2>
    
    <!-- Search and Add Section -->
    <div class="controls">
      <el-input
        v-model="searchCompany"
        placeholder="Search by company name"
        class="search-input"
        @keyup.enter.native="handleSearch"
      >
        <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
      </el-input>
      
      <el-button type="primary" @click="showAddDialog">Add New Question</el-button>
    </div>

    <!-- Interview Questions Table -->
    <el-table :data="interviews" style="width: 100%" v-loading="loading">
      <el-table-column prop="company" label="Company" width="180"></el-table-column>
      <el-table-column prop="title" label="Title"></el-table-column>
      <el-table-column prop="content" label="Content" show-overflow-tooltip></el-table-column>
      <el-table-column prop="editTime" label="Edit Time" width="180">
        <template slot-scope="scope">
          {{ formatDate(scope.row.editTime) }}
        </template>
      </el-table-column>
      <el-table-column label="Operations" width="200">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">Edit</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div class="pagination">
      <el-pagination
        @current-change="handlePageChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalItems"
        layout="total, prev, pager, next"
      ></el-pagination>
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="Company" prop="company">
          <el-input v-model="form.company"></el-input>
        </el-form-item>
        <el-form-item label="Title" prop="title">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="Content" prop="content">
          <el-input type="textarea" v-model="form.content" rows="4"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit">Submit</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'

export default {
  name: 'InterviewList',
  data() {
    return {
      searchCompany: '',
      loading: false,
      dialogVisible: false,
      dialogTitle: 'Add Interview Question',
      form: {
        id: null,
        company: '',
        title: '',
        content: '',
        editTime: null
      },
      rules: {
        company: [{ required: true, message: 'Please input company name', trigger: 'blur' }],
        title: [{ required: true, message: 'Please input title', trigger: 'blur' }],
        content: [{ required: true, message: 'Please input content', trigger: 'blur' }]
      }
    }
  },
  computed: {
    ...mapState(['interviews', 'currentPage', 'pageSize', 'totalItems'])
  },
  methods: {
    ...mapActions(['fetchInterviews', 'createInterview', 'updateInterview', 'deleteInterview']),
    
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString()
    },
    
    async handleSearch() {
      this.loading = true
      await this.fetchInterviews({ company: this.searchCompany })
      this.loading = false
    },
    
    async handlePageChange(page) {
      this.$store.commit('SET_CURRENT_PAGE', page)
      await this.fetchInterviews({ company: this.searchCompany })
    },
    
    showAddDialog() {
      this.form = {
        id: null,
        company: '',
        title: '',
        content: '',
        editTime: null
      }
      this.dialogTitle = 'Add Interview Question'
      this.dialogVisible = true
    },
    
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = 'Edit Interview Question'
      this.dialogVisible = true
    },
    
    async handleDelete(row) {
      try {
        await this.$confirm('Are you sure you want to delete this question?', 'Warning', {
          type: 'warning'
        })
        await this.deleteInterview(row.id)
        this.$message.success('Deleted successfully')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('Failed to delete')
        }
      }
    },
    
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            if (this.form.id) {
              await this.updateInterview({ id: this.form.id, interview: this.form })
            } else {
              await this.createInterview(this.form)
            }
            this.dialogVisible = false
            this.$message.success('Saved successfully')
            this.fetchInterviews({ company: this.searchCompany })
          } catch (error) {
            this.$message.error('Failed to save')
          }
        }
      })
    }
  },
  async created() {
    this.loading = true
    await this.fetchInterviews()
    this.loading = false
  }
}
</script>

<style scoped>
.interview-list {
  padding: 20px;
}

.controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
