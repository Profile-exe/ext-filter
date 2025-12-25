import api from "./api";

const statisticsService = {
  // 통계 개요 조회
  getStatisticsOverview: async () => {
    const response = await api.get("/api/statistics/overview");
    return response.data;
  },
};

export default statisticsService;
