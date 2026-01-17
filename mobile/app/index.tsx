import React, { useEffect } from 'react';
import { View, Text, ActivityIndicator } from 'react-native';
import { router } from 'expo-router';

import { useAuth } from '@/features/auth';

/**
 * Splash screen - sprawdza auth i przekierowuje
 */
export default function Index() {
  const { user, isUserLoading } = useAuth();

  useEffect(() => {
    if (!isUserLoading) {
      if (user) {
        router.replace('/(dashboard)/student');
      } else {
        router.replace('/(auth)/login');
      }
    }
  }, [user, isUserLoading]);

  return (
    <View className="flex-1 items-center justify-center bg-background">
      <View className="mb-4 h-20 w-20 items-center justify-center rounded-2xl bg-primary">
        <Text className="text-4xl font-bold text-white">L</Text>
      </View>
      <ActivityIndicator size="large" color="#22c55e" />
      <Text className="mt-4 text-muted-foreground">Ładowanie...</Text>
    </View>
  );
}
