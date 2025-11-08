export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupRequest {
  email: string;
  password: string;
  name?: string;
}

export interface User {
  userId: string;
  username: string;
  accountType: "BASIC" | "PREMIUM" | "STUDENT" | "TEACHER";
  userType: "NORMAL" | "ADMIN";
  isEnabled: boolean;
}

export interface AuthError {
  message: string;
  code?: string;
}
