import apiClient from "@/lib/api/axios";

export const login = async (
  email: string,
  password: string
): Promise<string> => {
  const response = await apiClient.post("/login", { email, password });
  return response.data;
};
