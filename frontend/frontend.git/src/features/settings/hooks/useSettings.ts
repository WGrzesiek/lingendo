import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { settingsApiService } from "../services/settings.service";
import type {
  UpdateProfileRequest,
  ChangePasswordRequest,
} from "../types/settings.types";
import { toast } from "sonner";
import {qk} from "@/lib/queryKeys";


export const useProfile = () => {
  return useQuery({
    queryKey: qk.settings.profile(),
    queryFn: () => settingsApiService.getProfile(),
  });
};


export const useUpdateProfile = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: UpdateProfileRequest) =>
      settingsApiService.updateProfile(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.settings.profile() });
      queryClient.invalidateQueries({ queryKey: qk.auth.me() });
      toast.success("Profil został zaktualizowany");
    },
    onError: (error: Error) => {
      if (error.message.includes("email")) {
        toast.error("Ten adres email jest już zajęty");
      } else {
        toast.error("Nie udało się zaktualizować profilu");
      }
    },
  });
};

export const useChangePassword = () => {
  return useMutation({
    mutationFn: (request: ChangePasswordRequest) =>
      settingsApiService.changePassword(request),
    onSuccess: () => {
      toast.success("Hasło zostało zmienione");
    },
    onError: () => {
      toast.error("Nieprawidłowe aktualne hasło");
    },
  });
};
