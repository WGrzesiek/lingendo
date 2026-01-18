import { Tabs } from 'expo-router';
import { Home, Globe, BookOpen } from 'lucide-react-native';

/**
 * Layout nawigacji tab bar dla dashboard
 */
export default function DashboardLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarActiveTintColor: '#22c55e',
        tabBarInactiveTintColor: '#9ca3af',
        tabBarStyle: {
          backgroundColor: '#ffffff',
          borderTopColor: '#e5e7eb',
          paddingBottom: 8,
          paddingTop: 8,
          height: 60,
        },
        tabBarLabelStyle: {
          fontSize: 12,
          fontWeight: '500',
        },
      }}>
      <Tabs.Screen
        name="student"
        options={{
          title: 'Dashboard',
          tabBarIcon: ({ color, size }) => <Home size={size} color={color} />,
        }}
      />
      <Tabs.Screen
        name="community"
        options={{
          title: 'Społeczność',
          tabBarIcon: ({ color, size }) => <Globe size={size} color={color} />,
        }}
      />
      <Tabs.Screen
        name="courses"
        options={{
          title: 'Moje kursy',
          tabBarIcon: ({ color, size }) => <BookOpen size={size} color={color} />,
        }}
      />

      {/* Ukryte ekrany - */}
      <Tabs.Screen
        name="course/[id]"
        options={{
          href: null,
        }}
      />
      <Tabs.Screen
        name="learn/[enrollmentId]/[sessionId]"
        options={{
          href: null,
        }}
      />
      <Tabs.Screen
        name="review/[enrollmentId]/index"
        options={{
          href: null,
        }}
      />
      <Tabs.Screen
        name="review/[enrollmentId]/session"
        options={{
          href: null,
        }}
      />
    </Tabs>
  );
}
