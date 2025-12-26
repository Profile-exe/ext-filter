import { useState, useEffect } from "react";
import uploadService from "../services/uploadService";
import useToast from "../hooks/useToast";
import { exportUploadHistoryToExcel } from "../utils/excelUtils";
import HistoryFilters from "../components/history/HistoryFilters";
import HistoryTable from "../components/history/HistoryTable";
import Pagination from "../components/history/Pagination";
import Toast from "../components/common/Toast";

function UploadHistoryPage() {
  const { toasts, showError, removeToast } = useToast();
  const [historyData, setHistoryData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    status: undefined,
    extension: undefined,
  });
  const [pagination, setPagination] = useState({
    currentPage: 0,
    totalPages: 0,
    pageSize: 5,
    totalElements: 0,
  });

  const fetchHistory = async (page = 0, size = 5, filterParams = {}) => {
    try {
      setLoading(true);
      const params = {
        page,
        size,
        sort: "createdAt,desc",
        ...filterParams,
      };

      const response = await uploadService.getUploadHistory(params);
      setHistoryData(response.content);
      setPagination({
        currentPage: response.number,
        totalPages: response.totalPages,
        pageSize: response.size,
        totalElements: response.totalElements,
      });
    } catch (error) {
      console.error("업로드 이력 조회 실패:", error);
      showError("업로드 이력을 불러오는데 실패했습니다.");
      setHistoryData([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory(0, pagination.pageSize, filters);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleFilter = (newFilters) => {
    setFilters(newFilters);
    fetchHistory(0, pagination.pageSize, newFilters);
  };

  const handleReset = () => {
    const resetFilters = { status: undefined, extension: undefined };
    setFilters(resetFilters);
    fetchHistory(0, pagination.pageSize, resetFilters);
  };

  const handlePageChange = (page) => {
    fetchHistory(page, pagination.pageSize, filters);
  };

  const handlePageSizeChange = (size) => {
    fetchHistory(0, size, filters);
  };

  const handleExport = async () => {
    try {
      setLoading(true);
      // 전체 데이터 가져오기 (페이지네이션 없이)
      const params = {
        page: 0,
        size: 10000, // 충분히 큰 값으로 전체 데이터 조회
        sort: "createdAt,desc",
        ...filters,
      };

      const response = await uploadService.getUploadHistory(params);
      const allData = response.content;

      // 엑셀로 내보내기
      exportUploadHistoryToExcel(allData);
    } catch (error) {
      console.error("엑셀 내보내기 실패:", error);
      showError(error.message || "엑셀 파일 생성에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-6 max-w-7xl">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">업로드 이력</h1>
        <p className="text-gray-600">
          파일 업로드 기록을 조회하고 필터링할 수 있습니다.
        </p>
      </div>

      <div className="space-y-4">
        <HistoryFilters
          onFilter={handleFilter}
          onReset={handleReset}
          onExport={handleExport}
          loading={loading}
        />

        <HistoryTable data={historyData} loading={loading} />

        {pagination.totalPages > 0 && (
          <Pagination
            currentPage={pagination.currentPage}
            totalPages={pagination.totalPages}
            pageSize={pagination.pageSize}
            onPageChange={handlePageChange}
            onPageSizeChange={handlePageSizeChange}
            loading={loading}
          />
        )}
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

export default UploadHistoryPage;
