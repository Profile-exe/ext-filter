import * as XLSX from 'xlsx-js-style';

/**
 * 파일 크기를 읽기 쉬운 형식으로 변환
 * @param {number} bytes - 바이트 단위 파일 크기
 * @returns {string} 포맷된 파일 크기 (예: "1.5 MB")
 */
export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
};

/**
 * 날짜를 YYYY-MM-DD HH:MM:SS 형식으로 변환
 * @param {string} dateString - ISO 날짜 문자열
 * @returns {string} 포맷된 날짜 문자열
 */
export const formatDateTime = (dateString) => {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

/**
 * 업로드 이력 데이터를 엑셀 파일로 내보내기
 * @param {Array} data - 내보낼 업로드 이력 데이터 배열
 * @param {string} filename - 저장할 파일명 (기본값: 현재 날짜 포함)
 */
export const exportUploadHistoryToExcel = (data, filename) => {
  if (!data || data.length === 0) {
    throw new Error('내보낼 데이터가 없습니다.');
  }

  // 엑셀 데이터 포맷팅
  const excelData = data.map((item, index) => ({
    번호: index + 1,
    날짜: formatDateTime(item.createdAt),
    파일명: item.originalFilename,
    확장자: item.fileExtension,
    '파일 크기': formatFileSize(item.fileSize),
    상태: item.uploadStatus === 'SUCCESS' ? '성공' : '차단',
    저장파일명: item.storedFilename || '-',
  }));

  // 워크시트 생성
  const worksheet = XLSX.utils.json_to_sheet(excelData);

  // 컬럼 너비 설정
  worksheet['!cols'] = [
    { wch: 8 },  // 번호
    { wch: 20 }, // 날짜
    { wch: 40 }, // 파일명
    { wch: 10 }, // 확장자
    { wch: 12 }, // 파일 크기
    { wch: 10 }, // 상태
    { wch: 40 }, // 저장파일명
  ];

  // 헤더 스타일 적용 (연한 회색 배경)
  const headerStyle = {
    fill: {
      fgColor: { rgb: 'E5E7EB' }, // 연한 회색 (Tailwind gray-200)
    },
    font: {
      bold: true,
    },
  };

  // 헤더 행(첫 번째 행)에 스타일 적용
  const range = XLSX.utils.decode_range(worksheet['!ref']);
  for (let col = range.s.c; col <= range.e.c; col++) {
    const cellAddress = XLSX.utils.encode_cell({ r: 0, c: col });
    if (!worksheet[cellAddress]) continue;
    worksheet[cellAddress].s = headerStyle;
  }

  // 워크북 생성
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, '업로드 이력');

  // 파일명 생성 (제공되지 않은 경우 현재 날짜 포함)
  let finalFilename = filename;
  if (!finalFilename) {
    const now = new Date();
    const dateStr = `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`;
    finalFilename = `업로드이력_${dateStr}.xlsx`;
  }

  // 파일 다운로드 (스타일 포함)
  XLSX.writeFile(workbook, finalFilename, { cellStyles: true });
};
