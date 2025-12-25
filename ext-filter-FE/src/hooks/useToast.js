import { useState, useCallback } from 'react';

function useToast() {
  const [toasts, setToasts] = useState([]);

  const showToast = useCallback((type, message) => {
    const id = Date.now();
    setToasts(prev => [...prev, { id, type, message }]);
  }, []);

  const showSuccess = useCallback((message) => {
    showToast('success', message);
  }, [showToast]);

  const showError = useCallback((message) => {
    showToast('error', message);
  }, [showToast]);

  const showWarning = useCallback((message) => {
    showToast('warning', message);
  }, [showToast]);

  const removeToast = useCallback((id) => {
    setToasts(prev => prev.filter(toast => toast.id !== id));
  }, []);

  return {
    toasts,
    showSuccess,
    showError,
    showWarning,
    removeToast
  };
}

export default useToast;
