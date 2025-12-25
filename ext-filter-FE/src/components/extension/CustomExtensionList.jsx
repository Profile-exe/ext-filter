import { useState } from 'react';
import Button from '../common/Button';

function CustomExtensionList({ extensions, onDelete, loading }) {
  const [deletingId, setDeletingId] = useState(null);

  const handleDelete = async (id) => {
    if (!window.confirm('정말 삭제하시겠습니까?')) {
      return;
    }

    setDeletingId(id);
    try {
      await onDelete(id);
    } catch (error) {
      console.error('삭제 실패:', error);
    } finally {
      setDeletingId(null);
    }
  };

  if (extensions.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        등록된 커스텀 확장자가 없습니다
      </div>
    );
  }

  return (
    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
      {extensions.map((extension) => (
        <div
          key={extension.id}
          className="flex items-center justify-between p-3 bg-gray-50 rounded-lg border border-gray-200"
        >
          <span className="font-medium text-gray-700">.{extension.extensionName}</span>
          <Button
            variant="danger"
            size="sm"
            onClick={() => handleDelete(extension.id)}
            disabled={loading || deletingId === extension.id}
            loading={deletingId === extension.id}
          >
            삭제
          </Button>
        </div>
      ))}
    </div>
  );
}

export default CustomExtensionList;
