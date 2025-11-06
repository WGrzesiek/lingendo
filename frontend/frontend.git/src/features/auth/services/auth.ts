// import apiClient from "@/lib/api/axios";
// import { TokenStore } from "@/lib/tokenStore";

// interface LoginResponse {
//   accessToken: string;
// }

// export const login = async (
//   email: string,
//   password: string
// ): Promise<string> => {
//   const response = await apiClient.post<LoginResponse>("/login", {
//     email,
//     password,
//   });

//   const { accessToken } = response.data;

//   TokenStore.set(accessToken);

//   return accessToken;
// };

// export const logout = async () => {
//   try {
//     await apiClient.post("/logout");
//   } catch (error) {
//     console.error("Logout error:", error);
//   } finally {
//     TokenStore.set(null);
//   }
// };
import apiClient from "@/lib/api/axios";
import { TokenStore } from "@/lib/tokenStore";
import type { LoginRequest, LoginResponse, SignupRequest } from "../types";

export const login = async (data: LoginRequest): Promise<string> => {
  const response = await apiClient.post<LoginResponse>("/login", data);
  const { accessToken } = response.data;
  TokenStore.set(accessToken);
  return accessToken;
};

export const signup = async (data: SignupRequest): Promise<void> => {
  await apiClient.post("/signup", data);
};

export const logout = async () => {
  try {
    await apiClient.post("/logout");
  } catch (error) {
    console.error("Logout error:", error);
  } finally {
    TokenStore.set(null);
  }
};

export const getCurrentUser = async () => {
  const response = await apiClient.get("/me");
  return response.data;
};
