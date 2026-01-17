import React, { useEffect, useState } from 'react';
import { View, Text, ActivityIndicator } from 'react-native';
import { router } from 'expo-router';

import { AuthService } from '@/features/auth';

export default function Index() {
  const [isChecking, setIsChecking] = useState(true);

  useEffect(() => {
    const run = async () => {
      try {
        const user = await AuthService.getCurrentUser();

        if (user) {
          router.replace('/(dashboard)/student');
          setIsChecking(false);
        } else {
          router.replace('/(auth)/login');
          setIsChecking(false);
        }
      } catch (e) {
        router.replace('/(auth)/login');
        setIsChecking(false);
      }
    };

    run();
  }, []);

  if (isChecking) {
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

  return null;
}
