import Button from '../common/Button';

function UploadResult({ result, onReset }) {
  if (!result) return null;

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
  };

  if (result.success) {
    const { originalFileName, storedFileName, fileExtension, fileSize, uploadedAt } =
      result.data;

    return (
      <div className="bg-green-50 border border-green-200 rounded-lg p-6">
        <div className="flex items-start">
          <div className="flex-shrink-0">
            <svg
              className="w-6 h-6 text-green-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
          </div>

          <div className="ml-3 flex-1">
            <h3 className="text-lg font-medium text-green-800 mb-3">
              파일 업로드 성공
            </h3>

            <div className="space-y-2 text-sm">
              <div className="flex">
                <span className="font-medium text-green-700 w-32">파일명:</span>
                <span className="text-green-900">{originalFileName}</span>
              </div>
              <div className="flex">
                <span className="font-medium text-green-700 w-32">저장된 파일명:</span>
                <span className="text-green-900 break-all">{storedFileName}</span>
              </div>
              <div className="flex">
                <span className="font-medium text-green-700 w-32">확장자:</span>
                <span className="text-green-900">.{fileExtension}</span>
              </div>
              <div className="flex">
                <span className="font-medium text-green-700 w-32">파일 크기:</span>
                <span className="text-green-900">{formatFileSize(fileSize)}</span>
              </div>
              <div className="flex">
                <span className="font-medium text-green-700 w-32">업로드 시간:</span>
                <span className="text-green-900">{formatDate(uploadedAt)}</span>
              </div>
            </div>

            <div className="mt-4">
              <Button onClick={onReset} variant="secondary" size="sm">
                새 파일 업로드
              </Button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // 실패한 경우
  return (
    <div className="bg-red-50 border border-red-200 rounded-lg p-6">
      <div className="flex items-start">
        <div className="flex-shrink-0">
          <svg
            className="w-6 h-6 text-red-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
        </div>

        <div className="ml-3 flex-1">
          <h3 className="text-lg font-medium text-red-800 mb-2">
            파일 업로드 실패
          </h3>

          <p className="text-sm text-red-700 mb-3">{result.error}</p>

          {result.blockedExtension && (
            <div className="bg-red-100 rounded p-3 mb-4">
              <p className="text-sm font-medium text-red-800">
                차단된 확장자: .{result.blockedExtension}
              </p>
              <p className="text-xs text-red-600 mt-1">
                이 확장자는 보안상의 이유로 업로드가 차단되었습니다.
              </p>
            </div>
          )}

          <div className="mt-4">
            <Button onClick={onReset} variant="secondary" size="sm">
              다시 시도
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default UploadResult;
