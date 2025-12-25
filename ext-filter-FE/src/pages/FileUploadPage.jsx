import useToast from '../hooks/useToast';
import useFileUpload from '../hooks/useFileUpload';
import FileDropZone from '../components/upload/FileDropZone';
import FilePreview from '../components/upload/FilePreview';
import UploadResult from '../components/upload/UploadResult';
import Button from '../components/common/Button';
import Toast from '../components/common/Toast';

function FileUploadPage() {
  const { toasts, showSuccess, showError, removeToast } = useToast();
  const {
    selectedFile,
    uploading,
    uploadResult,
    uploadProgress,
    handleFileSelect,
    handleUpload,
    handleReset,
  } = useFileUpload(showSuccess, showError);

  return (
    <div className="container mx-auto p-6 max-w-4xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">파일 업로드</h1>
        <p className="text-gray-600">
          파일을 선택하여 업로드하세요. 차단된 확장자는 업로드할 수 없습니다.
        </p>
      </div>

      <div className="space-y-6">
        {/* 업로드 결과가 없을 때만 드롭존 표시 */}
        {!uploadResult && (
          <div className="bg-white rounded-lg shadow-sm p-6">
            <FileDropZone
              onFileSelect={handleFileSelect}
              disabled={uploading}
            />
          </div>
        )}

        {/* 선택된 파일 미리보기 */}
        {selectedFile && !uploadResult && (
          <div className="space-y-4">
            <h2 className="text-lg font-semibold text-gray-800">선택된 파일</h2>
            <FilePreview file={selectedFile} onRemove={handleReset} />

            {/* 업로드 버튼 */}
            <div className="flex items-center space-x-4">
              <Button
                onClick={handleUpload}
                disabled={!selectedFile || uploading}
                loading={uploading}
                variant="primary"
                size="lg"
              >
                업로드
              </Button>
              {!uploading && (
                <Button
                  onClick={handleReset}
                  variant="secondary"
                  size="lg"
                >
                  취소
                </Button>
              )}
            </div>

            {/* 업로드 진행률 */}
            {uploading && (
              <div className="bg-white border border-gray-200 rounded-lg p-4">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-medium text-gray-700">
                    업로드 중...
                  </span>
                  <span className="text-sm font-medium text-blue-600">
                    {uploadProgress}%
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2.5">
                  <div
                    className="bg-blue-600 h-2.5 rounded-full transition-all duration-300"
                    style={{ width: `${uploadProgress}%` }}
                  ></div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* 업로드 결과 */}
        {uploadResult && (
          <UploadResult result={uploadResult} onReset={handleReset} />
        )}
      </div>

      {/* Toast 메시지 */}
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

export default FileUploadPage;
