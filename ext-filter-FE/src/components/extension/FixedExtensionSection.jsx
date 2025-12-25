import { useState, useEffect } from 'react';

function FixedExtensionSection({ extensions, onToggle, loading }) {
  const [optimisticExtensions, setOptimisticExtensions] = useState(extensions);

  // extensions props가 변경될 때 optimisticExtensions 동기화
  useEffect(() => {
    setOptimisticExtensions(extensions);
  }, [extensions]);

  const handleToggle = async (extension) => {
    const previousExtensions = [...optimisticExtensions];

    setOptimisticExtensions(prev =>
      prev.map(ext =>
        ext.extensionName === extension.extensionName
          ? { ...ext, isBlocked: !ext.isBlocked }
          : ext
      )
    );

    try {
      await onToggle(extension.extensionName, !extension.isBlocked);
    } catch (error) {
      console.error('확장자 토글 실패:', error);
      setOptimisticExtensions(previousExtensions);
    }
  };

  const displayExtensions = loading ? extensions : optimisticExtensions;

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h2 className="text-xl font-semibold mb-4">고정 확장자</h2>
      <p className="text-gray-600 text-sm mb-4">
        기본으로 제공되는 확장자입니다. 체크박스를 선택하여 차단 여부를 설정하세요.
      </p>

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {displayExtensions.map((extension) => (
          <label
            key={extension.id}
            className={`flex items-center gap-3 p-3 rounded-lg border-2 cursor-pointer transition-all duration-200 ${
              extension.isBlocked
                ? 'border-red-500 bg-red-50'
                : 'border-gray-200 bg-white hover:border-blue-300'
            }`}
          >
            <input
              type="checkbox"
              checked={extension.isBlocked}
              onChange={() => handleToggle(extension)}
              disabled={loading}
              className="w-5 h-5 text-red-600 rounded focus:ring-2 focus:ring-red-500 cursor-pointer disabled:cursor-not-allowed"
            />
            <span className={`font-medium ${extension.isBlocked ? 'text-red-700' : 'text-gray-700'}`}>
              .{extension.extensionName}
            </span>
          </label>
        ))}
      </div>
    </div>
  );
}

export default FixedExtensionSection;
