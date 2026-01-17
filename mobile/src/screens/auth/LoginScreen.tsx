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
import { mockLogin } from '../../mocks/auth';
import type { User } from '../../types/auth';

interface LoginScreenProps {
  onLoginSuccess: (user: User) => void;
  onNavigateToSignup: () => void;
}

/**
 * Ekran logowania
 */
export const LoginScreen = ({ onLoginSuccess, onNavigateToSignup }: LoginScreenProps) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async () => {
    if (!username.trim() || !password.trim()) {
      setError('Wypełnij wszystkie pola');
      return;
    }

    setError(null);
    setIsLoading(true);

    try {
      const user = await mockLogin(username, password);
      onLoginSuccess(user);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Wystąpił błąd podczas logowania');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      className="flex-1 bg-background"
    >
      <ScrollView
        contentContainerStyle={{ flexGrow: 1 }}
        keyboardShouldPersistTaps="handled"
      >
        <View className="flex-1 justify-center px-6 py-12">
          {/* Logo / Header */}
          <View className="items-center mb-10">
            <View className="w-20 h-20 bg-primary rounded-2xl items-center justify-center mb-4">
              <Text className="text-4xl text-white font-bold">L</Text>
            </View>
            <Text className="text-3xl font-bold text-foreground">LearnWords</Text>
            <Text className="text-muted-foreground mt-2">Ucz się języków z przyjemnością</Text>
          </View>

          {/* Formularz */}
          <View className="bg-card rounded-2xl p-6 shadow-sm border border-border">
            <Text className="text-2xl font-bold text-center mb-6 text-foreground">
              Zaloguj się
            </Text>

            {/* Username */}
            <View className="mb-4">
              <Text className="text-sm font-medium mb-2 text-foreground">
                Nazwa użytkownika
              </Text>
              <TextInput
                value={username}
                onChangeText={setUsername}
                placeholder="Wpisz nazwę użytkownika"
                placeholderTextColor="#71717a"
                autoCapitalize="none"
                autoCorrect={false}
                editable={!isLoading}
                className="w-full px-4 py-3 bg-secondary border border-input rounded-lg text-foreground"
              />
            </View>

            {/* Password */}
            <View className="mb-4">
              <Text className="text-sm font-medium mb-2 text-foreground">
                Hasło
              </Text>
              <TextInput
                value={password}
                onChangeText={setPassword}
                placeholder="Wpisz hasło"
                placeholderTextColor="#71717a"
                secureTextEntry
                editable={!isLoading}
                className="w-full px-4 py-3 bg-secondary border border-input rounded-lg text-foreground"
              />
            </View>

            {/* Error */}
            {error && (
              <View className="mb-4 p-3 bg-error-light rounded-lg border border-error/20">
                <Text className="text-destructive text-sm">{error}</Text>
              </View>
            )}

            {/* Submit Button */}
            <TouchableOpacity
              onPress={handleLogin}
              disabled={isLoading}
              className={`w-full py-4 rounded-lg items-center ${
                isLoading ? 'bg-primary/50' : 'bg-primary'
              }`}
            >
              {isLoading ? (
                <ActivityIndicator color="#ffffff" />
              ) : (
                <Text className="text-white font-semibold text-base">
                  Zaloguj się
                </Text>
              )}
            </TouchableOpacity>
          </View>

          {/* Sign up link */}
          <View className="flex-row justify-center mt-6">
            <Text className="text-muted-foreground">Nie masz konta? </Text>
            <TouchableOpacity onPress={onNavigateToSignup}>
              <Text className="text-primary font-semibold">Zarejestruj się</Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};
