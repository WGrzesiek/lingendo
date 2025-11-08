# 🔒 Przykłady użycia zabezpieczeń

## Hook `useProtectedRoute`

Uniwersalny hook do zabezpieczania widoków. Wykorzystuje endpoint `/me` do weryfikacji użytkownika.

### 1. Podstawowe zabezpieczenie - tylko zalogowani użytkownicy

```tsx
"use client";

import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";

export default function DashboardPage() {
  const { user, isLoading } = useProtectedRoute();

  if (isLoading) {
    return <div>Ładowanie...</div>;
  }

  return (
    <div>
      <h1>Witaj, {user?.username}!</h1>
    </div>
  );
}
```

### 2. Tylko dla nauczycieli

```tsx
"use client";

import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";

export default function DashboardTeacherPage() {
  const { user, isLoading } = useProtectedRoute({
    requiredAccountType: "TEACHER",
  });

  if (isLoading) {
    return <div>Ładowanie...</div>;
  }

  return (
    <div>
      <h1>Panel nauczyciela</h1>
      <p>Witaj, {user?.username}!</p>
    </div>
  );
}
```

### 3. Dla uczniów lub nauczycieli

```tsx
"use client";

import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";

export default function CoursePage() {
  const { user, isLoading } = useProtectedRoute({
    requiredAccountType: ["STUDENT", "TEACHER"],
  });

  if (isLoading) {
    return <div>Ładowanie...</div>;
  }

  return (
    <div>
      <h1>Strona kursu</h1>
      <p>Typ konta: {user?.accountType}</p>
    </div>
  );
}
```

### 4. Tylko dla administratorów

```tsx
"use client";

import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";

export default function AdminPanelPage() {
  const { user, isLoading } = useProtectedRoute({
    requiredUserType: "ADMIN",
  });

  if (isLoading) {
    return <div>Ładowanie...</div>;
  }

  return (
    <div>
      <h1>Panel administratora</h1>
      <p>Witaj, {user?.username}!</p>
    </div>
  );
}
```

### 5. Tylko dla użytkowników PREMIUM z aktywnym kontem

```tsx
"use client";

import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";

export default function PremiumFeaturesPage() {
  const { user, isLoading } = useProtectedRoute({
    requiredAccountType: "PREMIUM",
    requireEnabled: true,
  });

  if (isLoading) {
    return <div>Ładowanie...</div>;
  }

  return (
    <div>
      <h1>Funkcje Premium</h1>
      <p>Witaj, {user?.username}!</p>
    </div>
  );
}
```

### 6. Zaawansowane - niestandardowe przekierowania

```tsx
"use client";

import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";

export default function SpecialPage() {
  const { user, isLoading, hasAccess } = useProtectedRoute({
    requiredAccountType: "TEACHER",
    requireEnabled: true,
    redirectTo: "/upgrade", // gdzie przekierować gdy brak uprawnień
    loginRedirect: "/login?returnUrl=/special", // gdzie przekierować gdy niezalogowany
  });

  if (isLoading) {
    return <div>Ładowanie...</div>;
  }

  return (
    <div>
      <h1>Specjalna strona</h1>
      <p>Masz dostęp: {hasAccess ? "Tak" : "Nie"}</p>
    </div>
  );
}
```

### 7. Sprawdzenie konta bez wymuszania przekierowania

Jeśli chcesz tylko pobrać dane użytkownika bez automatycznego przekierowywania, użyj `useCurrentUser`:

```tsx
"use client";

import { useCurrentUser } from "@/features/auth/hooks/useCurrentUser";

export default function ProfilePage() {
  const { user, isLoading, error } = useCurrentUser();

  if (isLoading) {
    return <div>Ładowanie...</div>;
  }

  if (error || !user) {
    return <div>Musisz być zalogowany</div>;
  }

  return (
    <div>
      <h1>Profil</h1>
      <p>Username: {user.username}</p>
      <p>Typ konta: {user.accountType}</p>
      <p>Typ użytkownika: {user.userType}</p>
      <p>Aktywne: {user.isEnabled ? "Tak" : "Nie"}</p>
    </div>
  );
}
```

## Jak to działa?

1. **Backend sprawdza cookie** - Access token jest w httpOnly cookie, więc backend automatycznie go weryfikuje
2. **Endpoint `/me`** - Zwraca pełne dane użytkownika z backendu
3. **Automatyczne odświeżanie** - Gdy token wygaśnie (401), axios automatycznie wywołuje `/refresh`
4. **Bezpieczeństwo** - Token nie jest dostępny dla JavaScript (httpOnly), więc jest bezpieczniejszy

## Opcje `useProtectedRoute`

| Opcja                 | Typ                                                          | Domyślnie      | Opis                                  |
| --------------------- | ------------------------------------------------------------ | -------------- | ------------------------------------- |
| `requiredAccountType` | `"BASIC" \| "PREMIUM" \| "STUDENT" \| "TEACHER"` lub tablica | -              | Wymagany typ konta                    |
| `requiredUserType`    | `"NORMAL" \| "ADMIN"`                                        | -              | Wymagany typ użytkownika              |
| `requireEnabled`      | `boolean`                                                    | `true`         | Czy konto musi być aktywne            |
| `redirectTo`          | `string`                                                     | `"/dashboard"` | Gdzie przekierować gdy brak uprawnień |
| `loginRedirect`       | `string`                                                     | `"/login"`     | Gdzie przekierować gdy niezalogowany  |

## Zwracane wartości

| Wartość     | Typ              | Opis                                                  |
| ----------- | ---------------- | ----------------------------------------------------- |
| `user`      | `User \| null`   | Dane użytkownika z `/me`                              |
| `isLoading` | `boolean`        | Czy trwa ładowanie danych                             |
| `error`     | `string \| null` | Błąd jeśli wystąpił                                   |
| `hasAccess` | `boolean`        | Czy użytkownik ma dostęp (nie ładuje + user istnieje) |
