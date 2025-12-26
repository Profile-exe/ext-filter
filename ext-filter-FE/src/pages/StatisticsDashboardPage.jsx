import { useState, useEffect } from 'react';
import statisticsService from '../services/statisticsService';
import useToast from '../hooks/useToast';
import StatCard from '../components/statistics/StatCard';
import TopBlockedChart from '../components/statistics/TopBlockedChart';
import UploadTrendChart from '../components/statistics/UploadTrendChart';
import Toast from '../components/common/Toast';

function StatisticsDashboardPage() {
  const { toasts, showError, removeToast } = useToast();
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStatistics();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetchStatistics = async () => {
    try {
      setLoading(true);
      const data = await statisticsService.getStatisticsOverview();
      setStatistics(data);
    } catch (error) {
      console.error('통계 데이터 조회 실패:', error);
      showError('통계 데이터를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const formatPercentage = (value) => {
    return value.toFixed(1);
  };

  return (
    <div className="container mx-auto p-6 max-w-7xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">통계 대시보드</h1>
        <p className="text-gray-600">
          파일 업로드 및 차단 통계를 한눈에 확인할 수 있습니다.
        </p>
      </div>

      <div className="space-y-6">
        {/* 통계 카드 그리드 */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <StatCard
            title="전체 업로드"
            value={statistics?.totalUploads || 0}
            suffix="건"
            color="blue"
            loading={loading}
            icon={
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
            }
          />
          <StatCard
            title="성공"
            value={statistics?.successCount || 0}
            suffix="건"
            color="green"
            loading={loading}
            icon={
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            }
          />
          <StatCard
            title="차단"
            value={statistics?.blockedCount || 0}
            suffix="건"
            color="red"
            loading={loading}
            icon={
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
              </svg>
            }
          />
          <StatCard
            title="차단율"
            value={statistics ? formatPercentage(statistics.blockingRate) : 0}
            suffix="%"
            color="purple"
            loading={loading}
            icon={
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
            }
          />
        </div>

        {/* 차트 섹션 */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <TopBlockedChart
            data={statistics?.topBlockedExtensions || []}
            loading={loading}
          />
          <UploadTrendChart
            data={statistics?.uploadTrend || []}
            loading={loading}
          />
        </div>
      </div>

      {toasts.map((toast) => (
        <Toast
          key={toast.id}
          type={toast.type}
          message={toast.message}
          onClose={() => removeToast(toast.id)}
        />
      ))}
    </div>
  );
}

export default StatisticsDashboardPage;
