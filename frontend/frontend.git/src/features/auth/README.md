# 🔐 Auth Feature

System autoryzacji i autentykacji dla aplikacji LearnWords.

## Architektura

System wykorzystuje **httpOnly cookies** do przechowywania access tokenu. Backend automatycznie weryfikuje token z cookie przy każdym requestzie.

### Przepływ autoryzacji

1. **Login**: Użytkownik loguje się → Backend ustawia access token w httpOnly cookie
2. **Request**: Frontend wysyła request → Axios automatycznie dołącza cookie
3. **Weryfikacja**: Backend sprawdza token z cookie → Zwraca dane lub 401
4. **Refresh**: Przy 401 axios automatycznie wywołuje `/refresh` → Nowy token w cookie
5. **Logout**: Backend usuwa cookie z tokenem

## Struktura

```
auth/
├── components/          # Komponenty UI (formularze)
│   ├── LoginForm.tsx
│   └── SignupForm.tsx
├── hooks/              # React hooks
│   ├── useAuth.ts              # Login, signup, logout
│   ├── useCurrentUser.ts       # Pobieranie danych z /me
│   ├── useProtectedRoute.ts    # ⭐ Główny hook do zabezpieczeń
│   ├── useRequireAuth.ts       # Stary hook (deprecated)
│   ├── useRequireRole.ts       # Stary hook (deprecated)
│   └── useRedirectIfAuthenticated.ts
├── services/           # API calls
│   └── auth.ts
├── types/             # TypeScript types
│   └── index.ts
└── index.ts           # Exports
```

## Główne hooki

### ⭐ `useProtectedRoute` (ZALECANY)

Uniwersalny hook do zabezpieczania widoków. Pobiera dane z `/me` i weryfikuje uprawnienia.

```tsx
const { user, isLoading } = useProtectedRoute({
  requiredAccountType: "TEACHER",
  requireEnabled: true,
});
```

**Opcje:**

- `requiredAccountType` - Wymagany typ konta (BASIC/PREMIUM/STUDENT/TEACHER)
- `requiredUserType` - Wymagany typ użytkownika (NORMAL/ADMIN)
- `requireEnabled` - Czy konto musi być aktywne (domyślnie: true)
- `redirectTo` - Gdzie przekierować przy braku uprawnień (domyślnie: /dashboard)
- `loginRedirect` - Gdzie przekierować niezalogowanego (domyślnie: /login)

### `useCurrentUser`

Pobiera dane użytkownika z `/me` bez wymuszania przekierowań.

```tsx
const { user, isLoading, error } = useCurrentUser();
```

### `useAuth`

Obsługa logowania, rejestracji i wylogowania.

```tsx
const { login, signup, logout, isLoading, error } = useAuth();
```

## Przykłady użycia

Zobacz plik `SECURITY_EXAMPLES.md` w głównym katalogu projektu.

### Podstawowe zabezpieczenie

```tsx
"use client";
import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";

export default function DashboardPage() {
  const { user, isLoading } = useProtectedRoute();

  if (isLoading) return <div>Ładowanie...</div>;

  return <div>Witaj, {user?.username}!</div>;
}
```

### Tylko dla nauczycieli

```tsx
const { user, isLoading } = useProtectedRoute({
  requiredAccountType: "TEACHER",
});
```

### Dla uczniów lub nauczycieli

```tsx
const { user, isLoading } = useProtectedRoute({
  requiredAccountType: ["STUDENT", "TEACHER"],
});
```

### Tylko dla administratorów

```tsx
const { user, isLoading } = useProtectedRoute({
  requiredUserType: "ADMIN",
});
```

## API Services

### `login(data: LoginRequest): Promise<void>`

Loguje użytkownika. Backend ustawia token w cookie.

### `signup(data: SignupRequest): Promise<void>`

Rejestruje nowego użytkownika.

### `logout(): Promise<void>`

Wylogowuje użytkownika. Backend usuwa cookie.

### `getCurrentUser(): Promise<User>`

Pobiera dane aktualnego użytkownika z `/me`.

## Typy

### `User`

```typescript
interface User {
  userId: string;
  username: string;
  accountType: "BASIC" | "PREMIUM" | "STUDENT" | "TEACHER";
  userType: "NORMAL" | "ADMIN";
  isEnabled: boolean;
}
```

### `LoginRequest`

```typescript
interface LoginRequest {
  username: string;
  password: string;
}
```

### `SignupRequest`

```typescript
interface SignupRequest {
  email: string;
  password: string;
  name?: string;
}
```

## Bezpieczeństwo

✅ **Token w httpOnly cookie** - JavaScript nie ma dostępu
✅ **Automatyczny refresh** - Axios interceptor obsługuje 401
✅ **Backend weryfikuje** - Frontend nie musi się martwić o token
✅ **Weryfikacja uprawnień** - Hook sprawdza accountType, userType, isEnabled
✅ **CSRF protection** - withCredentials w axios

## Migracja ze starych hooków

| Stary hook                            | Nowy hook                                               |
| ------------------------------------- | ------------------------------------------------------- |
| `useRequireAuth()`                    | `useProtectedRoute()`                                   |
| `useRequireRole("TEACHER", "NORMAL")` | `useProtectedRoute({ requiredAccountType: "TEACHER" })` |

Stare hooki nadal działają, ale zalecamy używanie `useProtectedRoute`.
