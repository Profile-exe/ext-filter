import api from "./api";

const uploadService = {
  // 파일 업로드
  uploadFile: async (file, onUploadProgress) => {
    const formData = new FormData();
    formData.append("file", file);

    const response = await api.post("/api/files/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
      onUploadProgress,
    });
    return response.data;
  },

  // 업로드 이력 조회
  getUploadHistory: async (params = {}) => {
    const {
      status,
      extension,
      page = 0,
      size = 20,
      sort = "uploadedAt,desc",
    } = params;

    const queryParams = new URLSearchParams({
      page,
      size,
      sort,
    });

    if (status) {
      queryParams.append("status", status);
    }

    if (extension) {
      queryParams.append("extension", extension);
    }

    const response = await api.get(
      `/api/uploads/history?${queryParams.toString()}`
    );
    return response.data;
  },
};

export default uploadService;
