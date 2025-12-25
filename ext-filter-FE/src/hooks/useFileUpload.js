import { useState } from 'react';
import uploadService from '../services/uploadService';

function useFileUpload(showSuccess, showError) {
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [uploadResult, setUploadResult] = useState(null);
  const [uploadProgress, setUploadProgress] = useState(0);

  const handleFileSelect = (file) => {
    // 파일 크기 검증 (10MB)
    const maxSize = 10 * 1024 * 1024; // 10MB in bytes
    if (file.size > maxSize) {
      showError('파일 크기는 10MB를 초과할 수 없습니다.');
      return;
    }

    setSelectedFile(file);
    setUploadResult(null);
    setUploadProgress(0);
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      showError('업로드할 파일을 선택해주세요.');
      return;
    }

    setUploading(true);
    setUploadProgress(0);

    try {
      const onUploadProgress = (progressEvent) => {
        const percentCompleted = Math.round(
          (progressEvent.loaded * 100) / progressEvent.total
        );
        setUploadProgress(percentCompleted);
      };

      const response = await uploadService.uploadFile(
        selectedFile,
        onUploadProgress
      );

      setUploadResult({
        success: true,
        data: response,
      });
      showSuccess('파일이 성공적으로 업로드되었습니다.');
    } catch (error) {
      const errorMessage =
        error.response?.data?.message || '파일 업로드에 실패했습니다.';

      setUploadResult({
        success: false,
        error: errorMessage,
        blockedExtension: error.response?.data?.blockedExtension,
      });
      showError(errorMessage);
    } finally {
      setUploading(false);
    }
  };

  const handleReset = () => {
    setSelectedFile(null);
    setUploadResult(null);
    setUploadProgress(0);
  };

  return {
    selectedFile,
    uploading,
    uploadResult,
    uploadProgress,
    handleFileSelect,
    handleUpload,
    handleReset,
  };
}

export default useFileUpload;
