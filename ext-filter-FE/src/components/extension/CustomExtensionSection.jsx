import CustomExtensionForm from './CustomExtensionForm';
import CustomExtensionList from './CustomExtensionList';

function CustomExtensionSection({ extensions, onAdd, onDelete, loading }) {
  const maxCount = 200;
  const currentCount = extensions.length;

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-semibold">커스텀 확장자</h2>
        <div className="flex items-center gap-2">
          <span className={`text-lg font-semibold ${currentCount >= maxCount ? 'text-red-600' : 'text-blue-600'}`}>
            {currentCount} / {maxCount}
          </span>
        </div>
      </div>

      <p className="text-gray-600 text-sm mb-4">
        차단할 확장자를 직접 추가할 수 있습니다. 최대 {maxCount}개까지 등록 가능합니다.
      </p>

      {currentCount >= maxCount && (
        <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
          <p className="text-yellow-800 text-sm">
            ⚠ 커스텀 확장자 등록 한도에 도달했습니다. 새 확장자를 추가하려면 기존 확장자를 삭제해주세요.
          </p>
        </div>
      )}

      <div className="mb-6">
        <CustomExtensionForm
          onAdd={onAdd}
          disabled={loading}
          currentCount={currentCount}
          maxCount={maxCount}
        />
      </div>

      <CustomExtensionList
        extensions={extensions}
        onDelete={onDelete}
        loading={loading}
      />
    </div>
  );
}

export default CustomExtensionSection;
