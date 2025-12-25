function Loading({ fullScreen = false, size = 'md', text = '로딩 중...' }) {
  const sizeStyles = {
    sm: 'w-6 h-6 border-2',
    md: 'w-10 h-10 border-3',
    lg: 'w-16 h-16 border-4'
  };

  const spinnerElement = (
    <div className="flex flex-col items-center justify-center gap-3">
      <div className={`${sizeStyles[size]} border-blue-600 border-t-transparent rounded-full animate-spin`}></div>
      {text && <p className="text-gray-600">{text}</p>}
    </div>
  );

  if (fullScreen) {
    return (
      <div className="fixed inset-0 bg-white bg-opacity-80 flex items-center justify-center z-50">
        {spinnerElement}
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center p-8">
      {spinnerElement}
    </div>
  );
}

export default Loading;
