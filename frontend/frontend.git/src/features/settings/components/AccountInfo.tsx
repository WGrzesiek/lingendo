"use client";

import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useProfile } from "../hooks/useSettings";
import {
  Shield,
  Calendar,
  Clock,
  Flame,
  Crown,
  GraduationCap,
  Loader2,
} from "lucide-react";
import {timee} from "@/lib/time";


/**
 * Karta z informacjami o koncie
 */
export const AccountInfo = () => {
  const { data: profile, isLoading } = useProfile();

  if (isLoading) {
    return (
      <Card>
        <CardContent className="p-6">
          <div className="flex items-center justify-center h-32">
            <Loader2 className="w-6 h-6 animate-spin text-muted-foreground" />
          </div>
        </CardContent>
      </Card>
    );
  }

  const getAccountTypeLabel = (type?: string) => {
    switch (type) {
      case "STUDENT":
        return {
          label: "Student",
          icon: GraduationCap,
          color: "bg-blue-500/10 text-blue-600",
        };
      case "TEACHER":
        return {
          label: "Nauczyciel",
          icon: Crown,
          color: "bg-purple-500/10 text-purple-600",
        };
      case "PREMIUM":
        return {
          label: "Premium",
          icon: Crown,
          color: "bg-yellow-500/10 text-yellow-600",
        };
      default:
        return {
          label: "Podstawowe",
          icon: Shield,
          color: "bg-gray-500/10 text-gray-600",
        };
    }
  };

  const accountType = getAccountTypeLabel(profile?.accountType);
  const AccountIcon = accountType.icon;

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Shield className="w-5 h-5" />
          Informacje o koncie
        </CardTitle>
        <CardDescription>Szczegóły Twojego konta</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* Typ konta */}
        <div className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
          <div className="flex items-center gap-3">
            <div className={`p-2 rounded-lg ${accountType.color}`}>
              <AccountIcon className="w-5 h-5" />
            </div>
            <div>
              <p className="font-medium">Typ konta</p>
              <p className="text-sm text-muted-foreground">
                {profile?.userType === "ADMIN" ? "Administrator" : "Użytkownik"}
              </p>
            </div>
          </div>
          <Badge className={accountType.color}>{accountType.label}</Badge>
        </div>

        {/* Data utworzenia */}
        <div className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-lg bg-green-500/10 text-green-600">
              <Calendar className="w-5 h-5" />
            </div>
            <div>
              <p className="font-medium">Data założenia konta</p>
              <p className="text-sm text-muted-foreground">
                {timee.formatDate(profile?.createdAt)}
              </p>
            </div>
          </div>
        </div>

        {/* Ostatnie logowanie */}
        <div className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-lg bg-blue-500/10 text-blue-600">
              <Clock className="w-5 h-5" />
            </div>
            <div>
              <p className="font-medium">Ostatnie logowanie</p>
              <p className="text-sm text-muted-foreground">
                {timee.formatDateTime(profile?.lastLogin)}
              </p>
            </div>
          </div>
        </div>

        {/* Seria dni */}
        <div className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-lg bg-orange-500/10 text-orange-600">
              <Flame className="w-5 h-5" />
            </div>
            <div>
              <p className="font-medium">Seria dni</p>
              <p className="text-sm text-muted-foreground">
                Aktywność pod rząd
              </p>
            </div>
          </div>
          <span className="text-2xl font-bold text-orange-500">
            {profile?.streak || 0}
          </span>
        </div>
      </CardContent>
    </Card>
  );
};
