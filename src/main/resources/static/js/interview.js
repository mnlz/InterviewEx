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

        // 获取面试题列表
        const fetchInterviews = async () => {
            loading.value = true;
            try {
                const params = {
                    page: currentPage.value,
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

        // 日期格式化
        const formatDate = (row, column, cellValue) => {
            if (!cellValue) return '';
            const date = new Date(cellValue);
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
            handleSearch,
            handleCurrentChange,
            handleSizeChange,
            formatDate
        };
    }
});

// 使用Element Plus
app.use(ElementPlus);
app.mount('#app');
