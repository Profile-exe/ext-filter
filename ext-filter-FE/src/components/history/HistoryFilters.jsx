import { useState } from 'react';
import Button from '../common/Button';
import Input from '../common/Input';

function HistoryFilters({ onFilter, onReset, loading }) {
  const [status, setStatus] = useState('');
  const [extension, setExtension] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onFilter({ status: status || undefined, extension: extension || undefined });
  };

  const handleReset = () => {
    setStatus('');
    setExtension('');
    onReset();
  };

  return (
    <div className="bg-white rounded-lg shadow-sm p-6">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">검색 필터</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              상태
            </label>
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              disabled={loading}
            >
              <option value="">전체</option>
              <option value="SUCCESS">성공</option>
              <option value="BLOCKED">차단</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              확장자
            </label>
            <Input
              type="text"
              value={extension}
              onChange={(e) => setExtension(e.target.value)}
              placeholder="예: jpg, pdf, exe"
              disabled={loading}
            />
          </div>
        </div>

        <div className="flex space-x-3">
          <Button
            type="submit"
            variant="primary"
            disabled={loading}
          >
            검색
          </Button>
          <Button
            type="button"
            variant="secondary"
            onClick={handleReset}
            disabled={loading}
          >
            초기화
          </Button>
        </div>
      </form>
    </div>
  );
}

export default HistoryFilters;
