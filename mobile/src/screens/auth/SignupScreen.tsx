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
import { mockSignup } from '../../mocks/auth';
import type { AccountType, User } from '../../types/auth';

interface SignupScreenProps {
  onSignupSuccess: (user: User) => void;
  onNavigateToLogin: () => void;
}

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
export const SignupScreen = ({ onSignupSuccess, onNavigateToLogin }: SignupScreenProps) => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [accountType, setAccountType] = useState<AccountType>('BASIC');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSignup = async () => {
    // Walidacja
    if (!firstName.trim() || !lastName.trim() || !username.trim() || !email.trim() || !password.trim()) {
      setError('Wypełnij wszystkie pola');
      return;
    }

    if (password.length < 8) {
      setError('Hasło musi mieć minimum 8 znaków');
      return;
    }

    if (password !== confirmPassword) {
      setError('Hasła nie są identyczne');
      return;
    }

    if (!email.includes('@')) {
      setError('Podaj prawidłowy adres email');
      return;
    }

    setError(null);
    setIsLoading(true);

    try {
      const user = await mockSignup({
        firstName,
        lastName,
        username,
        email,
        password,
        accountType,
      });
      onSignupSuccess(user);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Wystąpił błąd podczas rejestracji');
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
        <View className="flex-1 px-6 py-8">
          {/* Header */}
          <View className="items-center mb-6">
            <View className="w-16 h-16 bg-primary rounded-2xl items-center justify-center mb-3">
              <Text className="text-3xl text-white font-bold">L</Text>
            </View>
            <Text className="text-2xl font-bold text-foreground">Utwórz konto</Text>
            <Text className="text-muted-foreground mt-1">Dołącz do LearnWords</Text>
          </View>

          {/* Formularz */}
          <View className="bg-card rounded-2xl p-6 shadow-sm border border-border">
            {/* Imię i Nazwisko */}
            <View className="flex-row gap-3 mb-4">
              <View className="flex-1">
                <Text className="text-sm font-medium mb-2 text-foreground">Imię</Text>
                <TextInput
                  value={firstName}
                  onChangeText={setFirstName}
                  placeholder="Jan"
                  placeholderTextColor="#71717a"
                  autoCapitalize="words"
                  editable={!isLoading}
                  className="w-full px-4 py-3 bg-secondary border border-input rounded-lg text-foreground"
                />
              </View>
              <View className="flex-1">
                <Text className="text-sm font-medium mb-2 text-foreground">Nazwisko</Text>
                <TextInput
                  value={lastName}
                  onChangeText={setLastName}
                  placeholder="Kowalski"
                  placeholderTextColor="#71717a"
                  autoCapitalize="words"
                  editable={!isLoading}
                  className="w-full px-4 py-3 bg-secondary border border-input rounded-lg text-foreground"
                />
              </View>
            </View>

            {/* Username */}
            <View className="mb-4">
              <Text className="text-sm font-medium mb-2 text-foreground">
                Nazwa użytkownika
              </Text>
              <TextInput
                value={username}
                onChangeText={setUsername}
                placeholder="jan_kowalski"
                placeholderTextColor="#71717a"
                autoCapitalize="none"
                autoCorrect={false}
                editable={!isLoading}
                className="w-full px-4 py-3 bg-secondary border border-input rounded-lg text-foreground"
              />
            </View>

            {/* Email */}
            <View className="mb-4">
              <Text className="text-sm font-medium mb-2 text-foreground">Email</Text>
              <TextInput
                value={email}
                onChangeText={setEmail}
                placeholder="jan@example.com"
                placeholderTextColor="#71717a"
                keyboardType="email-address"
                autoCapitalize="none"
                autoCorrect={false}
                editable={!isLoading}
                className="w-full px-4 py-3 bg-secondary border border-input rounded-lg text-foreground"
              />
            </View>

            {/* Password */}
            <View className="mb-4">
              <Text className="text-sm font-medium mb-2 text-foreground">Hasło</Text>
              <TextInput
                value={password}
                onChangeText={setPassword}
                placeholder="Minimum 8 znaków"
                placeholderTextColor="#71717a"
                secureTextEntry
                editable={!isLoading}
                className="w-full px-4 py-3 bg-secondary border border-input rounded-lg text-foreground"
              />
            </View>

            {/* Confirm Password */}
            <View className="mb-4">
              <Text className="text-sm font-medium mb-2 text-foreground">
                Potwierdź hasło
              </Text>
              <TextInput
                value={confirmPassword}
                onChangeText={setConfirmPassword}
                placeholder="Powtórz hasło"
                placeholderTextColor="#71717a"
                secureTextEntry
                editable={!isLoading}
                className="w-full px-4 py-3 bg-secondary border border-input rounded-lg text-foreground"
              />
            </View>

            {/* Account Type */}
            <View className="mb-4">
              <Text className="text-sm font-medium mb-3 text-foreground">Typ konta</Text>
              <View className="flex-row flex-wrap gap-2">
                {ACCOUNT_TYPE_OPTIONS.map((option) => (
                  <TouchableOpacity
                    key={option.value}
                    onPress={() => setAccountType(option.value)}
                    disabled={isLoading}
                    className={`flex-1 min-w-[45%] p-3 rounded-lg border ${
                      accountType === option.value
                        ? 'border-primary bg-primary-light'
                        : 'border-input bg-secondary'
                    }`}
                  >
                    <Text
                      className={`font-medium text-sm ${
                        accountType === option.value ? 'text-primary-dark' : 'text-foreground'
                      }`}
                    >
                      {option.label}
                    </Text>
                    <Text className="text-xs text-muted-foreground mt-1">
                      {option.description}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>
            </View>

            {/* Error */}
            {error && (
              <View className="mb-4 p-3 bg-error-light rounded-lg border border-error/20">
                <Text className="text-destructive text-sm">{error}</Text>
              </View>
            )}

            {/* Submit Button */}
            <TouchableOpacity
              onPress={handleSignup}
              disabled={isLoading}
              className={`w-full py-4 rounded-lg items-center ${
                isLoading ? 'bg-primary/50' : 'bg-primary'
              }`}
            >
              {isLoading ? (
                <ActivityIndicator color="#ffffff" />
              ) : (
                <Text className="text-white font-semibold text-base">
                  Zarejestruj się
                </Text>
              )}
            </TouchableOpacity>
          </View>

          {/* Login link */}
          <View className="flex-row justify-center mt-6">
            <Text className="text-muted-foreground">Masz już konto? </Text>
            <TouchableOpacity onPress={onNavigateToLogin}>
              <Text className="text-primary font-semibold">Zaloguj się</Text>
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};
