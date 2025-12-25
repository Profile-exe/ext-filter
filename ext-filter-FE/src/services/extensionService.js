import api from "./api";

const extensionService = {
  // 고정 확장자 조회
  getFixedExtensions: async () => {
    const response = await api.get("/api/extensions/fixed");
    return response.data;
  },

  // 고정 확장자 토글
  toggleFixedExtension: async (name, isBlocked) => {
    const response = await api.put(`/api/extensions/fixed/${name}`, {
      isBlocked,
    });
    return response.data;
  },

  // 커스텀 확장자 조회
  getCustomExtensions: async () => {
    const response = await api.get("/api/extensions/custom");
    return response.data;
  },

  // 커스텀 확장자 추가
  addCustomExtension: async (extensionName) => {
    const response = await api.post("/api/extensions/custom", {
      extensionName,
    });
    return response.data;
  },

  // 커스텀 확장자 삭제
  deleteCustomExtension: async (id) => {
    await api.delete(`/api/extensions/custom/${id}`);
  },
};

export default extensionService;
