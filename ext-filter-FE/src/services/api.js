import axios from "axios";

// Axios 인스턴스 생성
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request 인터셉터
api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response 인터셉터
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // 에러 처리
    if (error.response) {
      // 서버가 응답을 반환한 경우
      const { status, data } = error.response;

      switch (status) {
        case 400:
          console.error(
            "잘못된 요청:",
            data.message || "요청 데이터를 확인해주세요."
          );
          break;
        case 401:
          console.error("인증 오류:", data.message || "로그인이 필요합니다.");
          break;
        case 403:
          console.error("권한 오류:", data.message || "접근 권한이 없습니다.");
          break;
        case 404:
          console.error(
            "Not Found:",
            data.message || "요청한 리소스를 찾을 수 없습니다."
          );
          break;
        case 409:
          console.error("충돌 오류:", data.message || "중복된 데이터입니다.");
          break;
        case 413:
          console.error(
            "파일 크기 초과:",
            data.message || "파일 크기가 너무 큽니다."
          );
          break;
        case 422:
          console.error(
            "처리 불가:",
            data.message || "요청을 처리할 수 없습니다."
          );
          break;
        case 500:
          console.error(
            "서버 오류:",
            data.message || "서버에 문제가 발생했습니다."
          );
          break;
        default:
          console.error(
            "오류 발생:",
            data.message || "알 수 없는 오류가 발생했습니다."
          );
      }
    } else if (error.request) {
      // 요청은 보냈지만 응답을 받지 못한 경우
      console.error("네트워크 오류:", "서버에 연결할 수 없습니다.");
    } else {
      // 요청 설정 중 오류 발생
      console.error("요청 오류:", error.message);
    }

    return Promise.reject(error);
  }
);

export default api;
