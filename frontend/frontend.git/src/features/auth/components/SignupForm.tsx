"use client";

import { useState } from "react";
import { useAuth } from "../hooks/useAuth";
import type { AccountType } from "../types";

const ACCOUNT_TYPE_OPTIONS: {
  value: AccountType;
  label: string;
  description: string;
}[] = [
  {
    value: "BASIC",
    label: "Basic",
    description: "Darmowy plan z podstawowymi funkcjami",
  },
  {
    value: "PREMIUM",
    label: "Premium",
    description: "Pełny dostęp i kursy społeczności",
  },
  {
    value: "STUDENT",
    label: "Student",
    description: "Dostęp do talii nauczyciela",
  },
  {
    value: "TEACHER",
    label: "Teacher",
    description: "Panel nauczyciela i zarządzanie uczniami",
  },
];

export const SignupForm = () => {
  const { signupAsync, isLoading, signupError } = useAuth();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [accountType, setAccountType] = useState<AccountType>("BASIC");
  const [passwordError, setPasswordError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordError("");

    if (password !== confirmPassword) {
      setPasswordError("Hasła nie są identyczne");
      return;
    }

    await signupAsync({
      firstName,
      lastName,
      username,
      email,
      password,
      userType: "NORMAL",
      accountType,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 w-full max-w-md">
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label htmlFor="firstName" className="block text-sm font-medium mb-1">
            Imię
          </label>
          <input
            id="firstName"
            type="text"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            required
            className="w-full px-3 py-2 bg-background border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
            disabled={isLoading}
          />
        </div>
        <div>
          <label htmlFor="lastName" className="block text-sm font-medium mb-1">
            Nazwisko
          </label>
          <input
            id="lastName"
            type="text"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            required
            className="w-full px-3 py-2 bg-background border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
            disabled={isLoading}
          />
        </div>
      </div>

      <div>
        <label htmlFor="username" className="block text-sm font-medium mb-1">
          Nazwa użytkownika
        </label>
        <input
          id="username"
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          className="w-full px-3 py-2 bg-background border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
          disabled={isLoading}
        />
      </div>

      <div>
        <label htmlFor="email" className="block text-sm font-medium mb-1">
          Email
        </label>
        <input
          id="email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className="w-full px-3 py-2 bg-background border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
          disabled={isLoading}
        />
      </div>

      <div>
        <label htmlFor="password" className="block text-sm font-medium mb-1">
          Hasło
        </label>
        <input
          id="password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          minLength={8}
          className="w-full px-3 py-2 bg-background border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
          disabled={isLoading}
        />
        <p className="text-xs text-muted-foreground mt-1">Minimum 8 znaków</p>
      </div>

      <div>
        <label
          htmlFor="confirmPassword"
          className="block text-sm font-medium mb-1"
        >
          Potwierdź hasło
        </label>
        <input
          id="confirmPassword"
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
          minLength={8}
          className="w-full px-3 py-2 bg-background border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
          disabled={isLoading}
        />
      </div>

      <div>
        <label className="block text-sm font-medium mb-2">Typ konta</label>
        <div className="grid grid-cols-2 gap-2">
          {ACCOUNT_TYPE_OPTIONS.map((option) => (
            <button
              key={option.value}
              type="button"
              onClick={() => setAccountType(option.value)}
              disabled={isLoading}
              className={`p-3 rounded-lg border text-left transition-all ${
                accountType === option.value
                  ? "border-primary bg-primary/10 ring-1 ring-primary"
                  : "border-input hover:border-primary/50"
              } disabled:opacity-50 disabled:cursor-not-allowed`}
            >
              <span className="font-medium text-sm">{option.label}</span>
              <p className="text-xs text-muted-foreground mt-0.5">
                {option.description}
              </p>
            </button>
          ))}
        </div>
      </div>

      {passwordError && (
        <div className="text-destructive text-sm bg-destructive/10 p-3 rounded border border-destructive/20">
          {passwordError}
        </div>
      )}

      {signupError && (
        <div className="text-destructive text-sm bg-destructive/10 p-3 rounded border border-destructive/20">
          {signupError.response?.data.message ||
            "Wystąpił błąd podczas rejestracji."}
        </div>
      )}

      <button
        type="submit"
        disabled={isLoading}
        className="w-full bg-primary text-primary-foreground py-2 rounded-lg hover:bg-primary/90 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
      >
        {isLoading ? "Tworzenie konta..." : "Zarejestruj się"}
      </button>
    </form>
  );
};
