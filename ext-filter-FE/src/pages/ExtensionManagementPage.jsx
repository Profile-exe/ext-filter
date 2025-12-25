import useToast from '../hooks/useToast';
import useExtensions from '../hooks/useExtensions';
import FixedExtensionSection from '../components/extension/FixedExtensionSection';
import CustomExtensionSection from '../components/extension/CustomExtensionSection';
import Loading from '../components/common/Loading';
import Toast from '../components/common/Toast';

function ExtensionManagementPage() {
  const { toasts, showSuccess, showError, removeToast } = useToast();
  const {
    fixedExtensions,
    customExtensions,
    loading,
    handleToggleFixed,
    handleAddCustom,
    handleDeleteCustom
  } = useExtensions(showSuccess, showError);

  if (loading) {
    return <Loading fullScreen text="확장자 목록을 불러오는 중..." />;
  }

  return (
    <div className="container mx-auto p-6 max-w-7xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">확장자 차단 설정 관리</h1>
        <p className="text-gray-600">
          파일 업로드 시 차단할 확장자를 관리할 수 있습니다.
        </p>
      </div>

      <div className="space-y-6">
        <FixedExtensionSection
          extensions={fixedExtensions}
          onToggle={handleToggleFixed}
          loading={loading}
        />

        <CustomExtensionSection
          extensions={customExtensions}
          onAdd={handleAddCustom}
          onDelete={handleDeleteCustom}
          loading={loading}
        />
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

export default ExtensionManagementPage;
