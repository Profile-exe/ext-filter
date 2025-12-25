import { useState, useEffect, useCallback, useRef } from 'react';
import extensionService from '../services/extensionService';

function useExtensions(showSuccess, showError) {
  const [fixedExtensions, setFixedExtensions] = useState([]);
  const [customExtensions, setCustomExtensions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // showSuccess와 showError의 최신 참조를 유지
  const showSuccessRef = useRef(showSuccess);
  const showErrorRef = useRef(showError);

  useEffect(() => {
    showSuccessRef.current = showSuccess;
    showErrorRef.current = showError;
  }, [showSuccess, showError]);

  const loadFixedExtensions = useCallback(async () => {
    try {
      const data = await extensionService.getFixedExtensions();
      // 확장자 이름 기준 오름차순 정렬
      const sortedData = data.sort((a, b) =>
        a.extensionName.localeCompare(b.extensionName)
      );
      setFixedExtensions(sortedData);
    } catch (err) {
      console.error('고정 확장자 로드 실패:', err);
      setError(err);
      showErrorRef.current('고정 확장자 목록을 불러오는데 실패했습니다');
    }
  }, []);

  const loadCustomExtensions = useCallback(async () => {
    try {
      const data = await extensionService.getCustomExtensions();
      // 생성 날짜 기준 최신순 정렬 (또는 확장자 이름순)
      const sortedData = data.sort((a, b) =>
        new Date(b.createdAt) - new Date(a.createdAt)
      );
      setCustomExtensions(sortedData);
    } catch (err) {
      console.error('커스텀 확장자 로드 실패:', err);
      setError(err);
      showErrorRef.current('커스텀 확장자 목록을 불러오는데 실패했습니다');
    }
  }, []);

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      await Promise.all([loadFixedExtensions(), loadCustomExtensions()]);
      setLoading(false);
    };

    loadData();
  }, [loadFixedExtensions, loadCustomExtensions]);

  const handleToggleFixed = useCallback(async (name, isBlocked) => {
    try {
      const updatedExtension = await extensionService.toggleFixedExtension(name, isBlocked);

      setFixedExtensions(prev =>
        prev.map(ext =>
          ext.extensionName === name ? updatedExtension : ext
        )
      );

      showSuccessRef.current(
        isBlocked
          ? `${name} 확장자가 차단되었습니다`
          : `${name} 확장자 차단이 해제되었습니다`
      );
    } catch (err) {
      console.error('고정 확장자 토글 실패:', err);
      showErrorRef.current(err.response?.data?.message || '확장자 설정 변경에 실패했습니다');
      throw err;
    }
  }, []);

  const handleAddCustom = useCallback(async (extensionName) => {
    try {
      const newExtension = await extensionService.addCustomExtension(extensionName);
      // 추가 시에도 정렬 유지 (최신순)
      setCustomExtensions(prev => [newExtension, ...prev]);
      showSuccessRef.current(`${extensionName} 확장자가 추가되었습니다`);
    } catch (err) {
      console.error('커스텀 확장자 추가 실패:', err);
      showErrorRef.current(err.response?.data?.message || '확장자 추가에 실패했습니다');
      throw err;
    }
  }, []);

  const handleDeleteCustom = useCallback(async (id) => {
    try {
      await extensionService.deleteCustomExtension(id);
      setCustomExtensions(prev => prev.filter(ext => ext.id !== id));
      showSuccessRef.current('확장자가 삭제되었습니다');
    } catch (err) {
      console.error('커스텀 확장자 삭제 실패:', err);
      showErrorRef.current(err.response?.data?.message || '확장자 삭제에 실패했습니다');
      throw err;
    }
  }, []);

  return {
    fixedExtensions,
    customExtensions,
    loading,
    error,
    handleToggleFixed,
    handleAddCustom,
    handleDeleteCustom
  };
}

export default useExtensions;
