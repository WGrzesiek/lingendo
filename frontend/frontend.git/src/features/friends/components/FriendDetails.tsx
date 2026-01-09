"use client";

import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import {
  ArrowLeft,
  Trophy,
  Target,
  Flame,
  Clock,
  Calendar,
  UserMinus,
  Ban,
  Medal,
  BookOpen,
} from "lucide-react";
import {
  useFriendDetail,
  useRemoveFriend,
  useBlockUser,
} from "../hooks/useFriends";
import { time } from "@/lib/time";

/**
 * Komponent karty statystyk
 */
const StatCard = ({
  icon: Icon,
  label,
  value,
  subValue,
  color = "primary",
}: {
  icon: React.ElementType;
  label: string;
  value: string | number;
  subValue?: string;
  color?: "primary" | "yellow" | "blue" | "orange" | "green";
}) => {
  const colorClasses = {
    primary: "bg-primary/10 text-primary",
    yellow: "bg-yellow-500/10 text-yellow-600",
    blue: "bg-blue-500/10 text-blue-600",
    orange: "bg-orange-500/10 text-orange-600",
    green: "bg-green-500/10 text-green-600",
  };

  return (
    <div className="flex items-center gap-3 p-4 bg-muted/50 rounded-lg">
      <div className={`p-2 rounded-lg ${colorClasses[color]}`}>
        <Icon className="w-5 h-5" />
      </div>
      <div>
        <p className="text-2xl font-bold">{value}</p>
        <p className="text-sm text-muted-foreground">{label}</p>
        {subValue && (
          <p className="text-xs text-muted-foreground">{subValue}</p>
        )}
      </div>
    </div>
  );
};

/**
 * Komponent szczegółów znajomego
 */
export const FriendDetails = ({
  userId,
  onBack,
}: {
  userId: string;
  onBack: () => void;
}) => {
  const { data: userStats, isLoading, error } = useFriendDetail(userId);
  const removeMutation = useRemoveFriend();
  const blockMutation = useBlockUser();

  const handleRemove = () => {
    if (window.confirm("Czy na pewno chcesz usunąć tego znajomego?")) {
      removeMutation.mutate(userId, {
        onSuccess: () => onBack(),
      });
    }
  };

  const handleBlock = () => {
    if (window.confirm("Czy na pewno chcesz zablokować tego użytkownika?")) {
      blockMutation.mutate(userId, {
        onSuccess: () => onBack(),
      });
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (error || !userStats) {
    return (
      <Card className="p-6">
        <p className="text-destructive">Nie znaleziono znajomego</p>
        <Button variant="outline" onClick={onBack} className="mt-4">
          <ArrowLeft className="w-4 h-4 mr-2" />
          Wróć
        </Button>
      </Card>
    );
  }

  const isRecentlyActive = userStats.lastActiveAt
    ? new Date().getTime() - new Date(userStats.lastActiveAt).getTime() <
      24 * 60 * 60 * 1000
    : false;

  return (
    <div className="space-y-6">
      {/* Nagłówek */}
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="icon" onClick={onBack}>
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center">
            <span className="text-2xl font-bold text-primary">
              {userStats.username.charAt(0).toUpperCase()}
            </span>
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-2xl font-bold">{userStats.username}</h1>
              {isRecentlyActive && (
                <Badge className="bg-green-500/10 text-green-600">
                  Aktywny
                </Badge>
              )}
            </div>
            <div className="flex items-center gap-2 text-muted-foreground mt-1">
              <Medal className="w-4 h-4" />
              <span>
                {userStats.globalRank > 0
                  ? `#${userStats.globalRank} w rankingu`
                  : "Brak pozycji w rankingu"}
              </span>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <Button variant="outline" onClick={handleRemove}>
            <UserMinus className="w-4 h-4 mr-2" />
            Usuń
          </Button>
          <Button variant="destructive" onClick={handleBlock}>
            <Ban className="w-4 h-4 mr-2" />
            Zablokuj
          </Button>
        </div>
      </div>

      {/* Statystyki */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <StatCard
          icon={Trophy}
          label="Punkty"
          value={userStats.totalPoints.toLocaleString()}
          color="yellow"
        />
        <StatCard
          icon={Target}
          label="Dokładność"
          value={`${userStats.accuracy.toFixed(1)}%`}
          subValue={`${userStats.totalCorrect}/${userStats.totalAnswers}`}
          color="blue"
        />
        <StatCard
          icon={Flame}
          label="Seria dni"
          value={userStats.streakDays}
          color="orange"
        />
        <StatCard
          icon={Clock}
          label="Ostatnia aktywność"
          value={time(userStats.lastActiveAt ?? "")}
          color="green"
        />
      </div>

      {/* Dodatkowe informacje */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Calendar className="w-5 h-5" />
            Aktywność w tym tygodniu
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-between mb-4">
            <span className="text-sm text-muted-foreground">
              Punkty zdobyte w tym tygodniu
            </span>
            <span className="font-bold text-primary">
              {userStats.weeklyPoints} pkt
            </span>
          </div>
          <Progress
            value={Math.min((userStats.weeklyPoints / 1000) * 100, 100)}
            className="h-2"
          />
          <div className="flex items-center gap-1 text-xs text-muted-foreground mt-2">
            <BookOpen className="w-3 h-3" />
            <span>{userStats.totalSessions} sesji łącznie</span>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};
