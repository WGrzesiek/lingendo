import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  ActivityIndicator,
} from 'react-native';

import type { AccountType } from '@/features/auth/types';
import { router } from 'expo-router';
import { useAuth } from '@/features/auth';


/**
 * Opcje typu konta
 */
const ACCOUNT_TYPE_OPTIONS: {
  value: AccountType;
  label: string;
  description: string;
}[] = [
  {
    value: 'BASIC',
    label: 'Basic',
    description: 'Darmowy plan z podstawowymi funkcjami',
  },
  {
    value: 'PREMIUM',
    label: 'Premium',
    description: 'Pełny dostęp i kursy społeczności',
  },
  {
    value: 'STUDENT',
    label: 'Uczeń',
    description: 'Dostęp do talii nauczyciela',
  },
  {
    value: 'TEACHER',
    label: 'Nauczyciel',
    description: 'Panel nauczyciela i zarządzanie uczniami',
  },
];

/**
 * Ekran rejestracji
 */
export const Signup = () => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [accountType, setAccountType] = useState<AccountType>('BASIC');
  const [localError, setLocalError] = useState<string | null>(null);

  const { signupAsync, isSignupLoading, signupError, resetSignupError } = useAuth();

  const handleSignup = async () => {
    // Walidacja
    if (
      !firstName.trim() ||
      !lastName.trim() ||
      !username.trim() ||
      !email.trim() ||
      !password.trim()
    ) {
      setLocalError('Wypełnij wszystkie pola');
      return;
    }

    if (password.length < 8) {
      setLocalError('Hasło musi mieć minimum 8 znaków');
      return;
    }

    if (password !== confirmPassword) {
      setLocalError('Hasła nie są identyczne');
      return;
    }

    if (!email.includes('@')) {
      setLocalError('Podaj prawidłowy adres email');
      return;
    }

    setLocalError(null);
    resetSignupError();

    try {
      await signupAsync({
        firstName,
        lastName,
        username,
        email,
        password,
        accountType,
        userType: 'NORMAL',
      });
    } catch (err) {
      // Błąd jest obsługiwany przez useAuth hook
      console.log('[Signup] Błąd rejestracji');
    }
  };

  const errorMessage =
    localError ||
    signupError?.response?.data?.message ||
    (signupError ? 'Wystąpił błąd podczas rejestracji' : null);

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      className="flex-1 bg-background">
      <ScrollView contentContainerStyle={{ flexGrow: 1 }} keyboardShouldPersistTaps="handled">
        <View className="flex-1 px-6 py-8">
          {/* Header */}
          <View className="mb-6 items-center">
            <View className="mb-3 h-16 w-16 items-center justify-center rounded-2xl bg-primary">
              <Text className="text-3xl font-bold text-white">L</Text>
            </View>
            <Text className="text-2xl font-bold text-foreground">Utwórz konto</Text>
            <Text className="mt-1 text-muted-foreground">Dołącz do LearnWords</Text>
          </View>

          {/* Formularz */}
          <View className="rounded-2xl border border-border bg-card p-6 shadow-sm">
            {/* Imię i Nazwisko */}
            <View className="mb-4 flex-row gap-3">
              <View className="flex-1">
                <Text className="mb-2 text-sm font-medium text-foreground">Imię</Text>
                <TextInput
                  value={firstName}
                  onChangeText={setFirstName}
                  placeholder="Jan"
                  placeholderTextColor="#71717a"
                  autoCapitalize="words"
                  editable={!isSignupLoading}
                  className="w-full rounded-lg border border-input bg-secondary px-4 py-3 text-foreground"
                />
              </View>
              <View className="flex-1">
                <Text className="mb-2 text-sm font-medium text-foreground">Nazwisko</Text>
                <TextInput
                  value={lastName}
                  onChangeText={setLastName}
                  placeholder="Kowalski"
                  placeholderTextColor="#71717a"
                  autoCapitalize="words"
                  editable={!isSignupLoading}
                  className="w-full rounded-lg border border-input bg-secondary px-4 py-3 text-foreground"
                />
              </View>
            </View>

            {/* Username */}
            <View className="mb-4">
              <Text className="mb-2 text-sm font-medium text-foreground">Nazwa użytkownika</Text>
              <TextInput
                value={username}
                onChangeText={setUsername}
                placeholder="jan_kowalski"
                placeholderTextColor="#71717a"
                autoCapitalize="none"
                autoCorrect={false}
                editable={!isSignupLoading}
                className="w-full rounded-lg border border-input bg-secondary px-4 py-3 text-foreground"
              />
            </View>

            {/* Email */}
            <View className="mb-4">
              <Text className="mb-2 text-sm font-medium text-foreground">Email</Text>
              <TextInput
                value={email}
                onChangeText={setEmail}
                placeholder="jan@example.com"
                placeholderTextColor="#71717a"
                keyboardType="email-address"
                autoCapitalize="none"
                autoCorrect={false}
                editable={!isSignupLoading}
                className="w-full rounded-lg border border-input bg-secondary px-4 py-3 text-foreground"
              />
            </View>

            {/* Password */}
            <View className="mb-4">
              <Text className="mb-2 text-sm font-medium text-foreground">Hasło</Text>
              <TextInput
                value={password}
                onChangeText={setPassword}
                placeholder="Minimum 8 znaków"
                placeholderTextColor="#71717a"
                secureTextEntry
                editable={!isSignupLoading}
                className="w-full rounded-lg border border-input bg-secondary px-4 py-3 text-foreground"
              />
            </View>

            {/* Confirm Password */}
            <View className="mb-4">
              <Text className="mb-2 text-sm font-medium text-foreground">Potwierdź hasło</Text>
              <TextInput
                value={confirmPassword}
                onChangeText={setConfirmPassword}
                placeholder="Powtórz hasło"
                placeholderTextColor="#71717a"
                secureTextEntry
                editable={!isSignupLoading}
                className="w-full rounded-lg border border-input bg-secondary px-4 py-3 text-foreground"
              />
            </View>

            {/* Account Type */}
            <View className="mb-4">
              <Text className="mb-3 text-sm font-medium text-foreground">Typ konta</Text>
              <View className="flex-row flex-wrap gap-2">
                {ACCOUNT_TYPE_OPTIONS.map((option) => (
                  <TouchableOpacity
                    key={option.value}
                    onPress={() => setAccountType(option.value)}
                    disabled={isSignupLoading}
                    className={`min-w-[45%] flex-1 rounded-lg border p-3 ${
                      accountType === option.value
                        ? 'border-primary bg-primary-light'
                        : 'border-input bg-secondary'
                    }`}>
                    <Text
                      className={`text-sm font-medium ${
                        accountType === option.value ? 'text-primary-dark' : 'text-foreground'
                      }`}>
                      {option.label}
                    </Text>
                    <Text className="mt-1 text-xs text-muted-foreground">{option.description}</Text>
                  </TouchableOpacity>
                ))}
              </View>
            </View>

            {/* Error */}
            {errorMessage && (
              <View className="mb-4 rounded-lg border border-error/20 bg-error-light p-3">
                <Text className="text-sm text-destructive">{errorMessage}</Text>
              </View>
            )}

            {/* Submit Button */}
            <TouchableOpacity
              onPress={handleSignup}
              disabled={isSignupLoading}
              className={`w-full items-center rounded-lg py-4 ${
                isSignupLoading ? 'bg-primary/50' : 'bg-primary'
              }`}>
              {isSignupLoading ? (
                <ActivityIndicator color="#ffffff" />
              ) : (
                <Text className="text-base font-semibold text-white">Zarejestruj się</Text>
              )}
            </TouchableOpacity>
          </View>

          {/* Login link */}
          <View className="mt-6 flex-row justify-center">
            <Text className="text-muted-foreground">Masz już konto? </Text>
            <TouchableOpacity
              onPress={() => router.push('/(auth)/login')}
              disabled={isSignupLoading}>
              <Text className="font-semibold text-primary">Zaloguj się</Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

export default Signup;
