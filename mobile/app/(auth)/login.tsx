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
import { useAuth } from '@/features/auth/hooks/useAuth';
import { router } from 'expo-router';

/**
 * Ekran logowania
 */
export const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [localError, setLocalError] = useState<string | null>(null);

  const { loginAsync, isLoginLoading, loginError, resetLoginError } = useAuth();

  const handleLogin = async () => {
    if (!username.trim() || !password.trim()) {
      setLocalError('Wypełnij wszystkie pola');
      return;
    }

    setLocalError(null);
    resetLoginError();

    try {
      await loginAsync({ username, password });
    } catch {
      console.log('[Login] Błąd logowania');
    }
  };

  // Pobierz komunikat błędu
  const errorMessage =
    localError ||
    loginError?.response?.data?.message ||
    (loginError ? 'Nieprawidłowa nazwa użytkownika lub hasło' : null);

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      className="flex-1 bg-background">
      <ScrollView contentContainerStyle={{ flexGrow: 1 }} keyboardShouldPersistTaps="handled">
        <View className="flex-1 justify-center px-6 py-12">
          {/* Logo / Header */}
          <View className="mb-10 items-center">
            <View className="mb-4 h-20 w-20 items-center justify-center rounded-2xl bg-primary">
              <Text className="text-4xl font-bold text-white">L</Text>
            </View>
            <Text className="text-3xl font-bold text-foreground">LearnWords</Text>
            <Text className="mt-2 text-muted-foreground">Ucz się języków z przyjemnością</Text>
          </View>

          {/* Formularz */}
          <View className="rounded-2xl border border-border bg-card p-6 shadow-sm">
            <Text className="mb-6 text-center text-2xl font-bold text-foreground">Zaloguj się</Text>

            {/* Username */}
            <View className="mb-4">
              <Text className="mb-2 text-sm font-medium text-foreground">Nazwa użytkownika</Text>
              <TextInput
                value={username}
                onChangeText={setUsername}
                placeholder="Wpisz nazwę użytkownika"
                placeholderTextColor="#71717a"
                autoCapitalize="none"
                autoCorrect={false}
                editable={!isLoginLoading}
                className="w-full rounded-lg border border-input bg-secondary px-4 py-3 text-foreground"
              />
            </View>

            {/* Password */}
            <View className="mb-4">
              <Text className="mb-2 text-sm font-medium text-foreground">Hasło</Text>
              <TextInput
                value={password}
                onChangeText={setPassword}
                placeholder="Wpisz hasło"
                placeholderTextColor="#71717a"
                secureTextEntry
                editable={!isLoginLoading}
                className="w-full rounded-lg border border-input bg-secondary px-4 py-3 text-foreground"
              />
            </View>

            {/* Error */}
            {errorMessage && (
              <View className="mb-4 rounded-lg border border-error/20 bg-error-light p-3">
                <Text className="text-sm text-destructive">{errorMessage}</Text>
              </View>
            )}

            {/* Submit Button */}
            <TouchableOpacity
              onPress={handleLogin}
              disabled={isLoginLoading}
              className={`w-full items-center rounded-lg py-4 ${
                isLoginLoading ? 'bg-primary/50' : 'bg-primary'
              }`}>
              {isLoginLoading ? (
                <ActivityIndicator color="#ffffff" />
              ) : (
                <Text className="text-base font-semibold text-white">Zaloguj się</Text>
              )}
            </TouchableOpacity>
          </View>

          {/* Sign up link */}
          <View className="mt-6 flex-row justify-center">
            <Text className="text-muted-foreground">Nie masz konta? </Text>
            <TouchableOpacity
              onPress={() => {
                router.push('/(auth)/signup');
              }}
              disabled={isLoginLoading}>
              <Text className="font-semibold text-primary">Zarejestruj się</Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

export default Login;
