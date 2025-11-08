// Główny punkt wejścia dla feature'a autoryzacji
export { useAuth } from "./hooks/useAuth";
export { useCurrentUser } from "./hooks/useCurrentUser";
export { useProtectedRoute } from "./hooks/useProtectedRoute";
export { useRequireAuth } from "./hooks/useRequireAuth";
export { useRequireRole } from "./hooks/useRequireRole";
export { useRedirectIfAuthenticated } from "./hooks/useRedirectIfAuthenticated";

export { login, signup, logout, getCurrentUser } from "./services/auth";

export type { LoginRequest, SignupRequest, User, AuthError } from "./types";
