import apiClient from "@/lib/api/axios";
import type {
  UserProfile,
  UpdateProfileRequest,
  ChangePasswordRequest,
  ChangePasswordResponse,
} from "../types/settings.types";

const BASE_URL = "/v1/users";

export const settingsApiService = {

  async getProfile(): Promise<UserProfile> {
    const response = await apiClient.get<UserProfile>(`${BASE_URL}/profile`);
    return response.data;
  },

  async updateProfile(request: UpdateProfileRequest): Promise<UserProfile> {
    const response = await apiClient.put<UserProfile>(
      `${BASE_URL}/profile`,
      request
    );
    return response.data;
  },

  async changePassword(
    request: ChangePasswordRequest
  ): Promise<ChangePasswordResponse> {
    const response = await apiClient.post<ChangePasswordResponse>(
      `${BASE_URL}/change-password`,
      request
    );
    return response.data;
  },
};
