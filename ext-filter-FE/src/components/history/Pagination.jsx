import Button from '../common/Button';

function Pagination({
  currentPage,
  totalPages,
  pageSize,
  onPageChange,
  onPageSizeChange,
  loading
}) {
  const pageSizes = [10, 20, 50, 100];

  const handlePrevious = () => {
    if (currentPage > 0) {
      onPageChange(currentPage - 1);
    }
  };

  const handleNext = () => {
    if (currentPage < totalPages - 1) {
      onPageChange(currentPage + 1);
    }
  };

  const getPageNumbers = () => {
    const pages = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(0, currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxPagesToShow - 1);

    if (endPage - startPage + 1 < maxPagesToShow) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  };

  if (totalPages === 0) {
    return null;
  }

  return (
    <div className="bg-white rounded-lg shadow-sm p-4">
      <div className="flex flex-col sm:flex-row items-center justify-between space-y-4 sm:space-y-0">
        {/* 페이지 크기 선택 */}
        <div className="flex items-center space-x-2">
          <span className="text-sm text-gray-700">페이지당</span>
          <select
            value={pageSize}
            onChange={(e) => onPageSizeChange(Number(e.target.value))}
            className="px-3 py-1 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            disabled={loading}
          >
            {pageSizes.map((size) => (
              <option key={size} value={size}>
                {size}
              </option>
            ))}
          </select>
          <span className="text-sm text-gray-700">개씩 보기</span>
        </div>

        {/* 페이지 네비게이션 */}
        <div className="flex items-center space-x-2">
          <Button
            onClick={handlePrevious}
            disabled={currentPage === 0 || loading}
            variant="secondary"
            size="sm"
          >
            이전
          </Button>

          <div className="flex items-center space-x-1">
            {getPageNumbers().map((pageNum) => (
              <button
                key={pageNum}
                onClick={() => onPageChange(pageNum)}
                disabled={loading}
                className={`px-3 py-1 rounded-lg text-sm font-medium transition-colors ${
                  currentPage === pageNum
                    ? 'bg-blue-600 text-white'
                    : 'bg-white text-gray-700 hover:bg-gray-100 border border-gray-300'
                } disabled:opacity-50 disabled:cursor-not-allowed`}
              >
                {pageNum + 1}
              </button>
            ))}
          </div>

          <Button
            onClick={handleNext}
            disabled={currentPage === totalPages - 1 || loading}
            variant="secondary"
            size="sm"
          >
            다음
          </Button>
        </div>

        {/* 페이지 정보 */}
        <div className="text-sm text-gray-700">
          {currentPage + 1} / {totalPages} 페이지
        </div>
      </div>
    </div>
  );
}

export default Pagination;
