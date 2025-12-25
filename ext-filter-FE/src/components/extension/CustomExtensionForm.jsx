import { useState } from 'react';
import Input from '../common/Input';
import Button from '../common/Button';

function CustomExtensionForm({ onAdd, disabled, currentCount, maxCount = 200 }) {
  const [extensionName, setExtensionName] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validateExtension = (value) => {
    if (!value) {
      return '';
    }

    if (!/^[a-zA-Z0-9]+$/.test(value)) {
      return '영문자와 숫자만 입력 가능합니다';
    }

    if (value.length > 20) {
      return '최대 20자까지 입력 가능합니다';
    }

    return '';
  };

  const handleChange = (e) => {
    const value = e.target.value;
    setExtensionName(value);
    setError(validateExtension(value));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!extensionName.trim()) {
      setError('확장자를 입력해주세요');
      return;
    }

    const validationError = validateExtension(extensionName);
    if (validationError) {
      setError(validationError);
      return;
    }

    if (currentCount >= maxCount) {
      setError(`커스텀 확장자는 최대 ${maxCount}개까지 등록 가능합니다`);
      return;
    }

    setIsSubmitting(true);
    try {
      await onAdd(extensionName);
      setExtensionName('');
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || '확장자 추가에 실패했습니다');
    } finally {
      setIsSubmitting(false);
    }
  };

  const isDisabled = disabled || currentCount >= maxCount || isSubmitting;

  return (
    <form onSubmit={handleSubmit} className="flex gap-2">
      <div className="flex-1">
        <Input
          type="text"
          value={extensionName}
          onChange={handleChange}
          placeholder="확장자 입력 (예: pdf, docx)"
          error={error}
          disabled={isDisabled}
          maxLength={20}
        />
      </div>
      <Button
        type="submit"
        variant="primary"
        disabled={isDisabled || !extensionName.trim() || !!error}
        loading={isSubmitting}
      >
        추가
      </Button>
    </form>
  );
}

export default CustomExtensionForm;
