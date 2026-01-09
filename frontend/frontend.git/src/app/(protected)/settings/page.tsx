"use client";

import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";
import {
  ProfileForm,
  PasswordForm,
  AccountInfo,
} from "@/features/settings/components";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Settings, User, Lock, Info, Bell } from "lucide-react";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";

/**
 * Strona ustawień użytkownika
 */
export default function SettingsPage() {
  const { isLoading } = useProtectedRoute();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="container max-w-4xl mx-auto py-8 px-4">
      {/* Nagłówek */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold flex items-center gap-3">
          <Settings className="w-8 h-8" />
          Ustawienia
        </h1>
        <p className="text-muted-foreground mt-2">
          Zarządzaj swoim kontem i preferencjami
        </p>
      </div>

      {/* Zakładki */}
      <Tabs defaultValue="profile" className="space-y-6">
        <TabsList className="grid w-full grid-cols-4 lg:w-auto lg:inline-flex">
          <TabsTrigger value="profile" className="flex items-center gap-2">
            <User className="w-4 h-4" />
            <span className="hidden sm:inline">Profil</span>
          </TabsTrigger>
          <TabsTrigger value="security" className="flex items-center gap-2">
            <Lock className="w-4 h-4" />
            <span className="hidden sm:inline">Bezpieczeństwo</span>
          </TabsTrigger>
          <TabsTrigger
            value="notifications"
            className="flex items-center gap-2"
          >
            <Bell className="w-4 h-4" />
            <span className="hidden sm:inline">Powiadomienia</span>
          </TabsTrigger>
        </TabsList>

        {/* Zakładka Profil */}
        <TabsContent value="profile" className="space-y-6">
          <ProfileForm />
          <AccountInfo />
        </TabsContent>

        {/* Zakładka Bezpieczeństwo */}
        <TabsContent value="security" className="space-y-6">
          <PasswordForm />

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Info className="w-5 h-5" />
                Sesje i urządzenia
              </CardTitle>
              <CardDescription>Zarządzaj aktywnymi sesjami</CardDescription>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-muted-foreground">
                Aktualnie jesteś zalogowany tylko na tym urządzeniu.
              </p>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Zakładka Powiadomienia */}
        <TabsContent value="notifications" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Bell className="w-5 h-5" />
                Powiadomienia email
              </CardTitle>
              <CardDescription>
                Wybierz jakie powiadomienia chcesz otrzymywać
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>Przypomnienia o nauce</Label>
                  <p className="text-sm text-muted-foreground">
                    Otrzymuj codzienne przypomnienia o nauce
                  </p>
                </div>
                <Switch defaultChecked />
              </div>

              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>Nowi znajomi</Label>
                  <p className="text-sm text-muted-foreground">
                    Powiadomienia o zaproszeniach do znajomych
                  </p>
                </div>
                <Switch defaultChecked />
              </div>

              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>Aktualności</Label>
                  <p className="text-sm text-muted-foreground">
                    Newsletter z nowościami i wskazówkami
                  </p>
                </div>
                <Switch />
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
