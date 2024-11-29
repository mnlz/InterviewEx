const { createApp, ref, onMounted } = Vue;

const app = createApp({
    setup() {
        // 数据定义
        const tableData = ref([]);
        const loading = ref(false);
        const searchCompany = ref('');
        const currentPage = ref(1);
        const pageSize = ref(20);
        const total = ref(0);
        const detailVisible = ref(false);
        const currentInterview = ref(null);

        // 获取面试题列表
        const fetchInterviews = async () => {
            loading.value = true;
            try {
                const params = {
                    page: currentPage.value - 1, // 后端页码从0开始
                    size: pageSize.value
                };
                if (searchCompany.value) {
                    params.company = searchCompany.value;
                }
                const response = await axios.get('/api/interviews/search', { params });
                tableData.value = response.data.content;
                total.value = response.data.totalElements;
            } catch (error) {
                console.error('获取数据失败:', error);
                ElMessage.error('获取数据失败，请稍后重试');
            } finally {
                loading.value = false;
            }
        };

        // 搜索处理
        const handleSearch = () => {
            currentPage.value = 1;
            fetchInterviews();
        };

        // 页码改变处理
        const handleCurrentChange = (val) => {
            currentPage.value = val;
            fetchInterviews();
        };

        // 每页条数改变处理
        const handleSizeChange = (val) => {
            pageSize.value = val;
            currentPage.value = 1;
            fetchInterviews();
        };

        // 表格行点击处理
        const handleRowClick = (row) => {
            currentInterview.value = row;
            detailVisible.value = true;
        };

        // 日期格式化（表格中使用）
        const formatDate = (row, column, cellValue) => {
            if (!cellValue) return '';
            return formatDetailDate(cellValue);
        };

        // 日期格式化（详情中使用）
        const formatDetailDate = (dateStr) => {
            if (!dateStr) return '';
            const date = new Date(dateStr);
            return date.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
        };

        // 页面加载时获取数据
        onMounted(() => {
            fetchInterviews();
        });

        return {
            tableData,
            loading,
            searchCompany,
            currentPage,
            pageSize,
            total,
            detailVisible,
            currentInterview,
            handleSearch,
            handleCurrentChange,
            handleSizeChange,
            handleRowClick,
            formatDate,
            formatDetailDate
        };
    }
});

// 使用Element Plus
app.use(ElementPlus);
app.mount('#app');
