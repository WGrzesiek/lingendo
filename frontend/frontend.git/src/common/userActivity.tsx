import { Trophy, BookCheck, BookOpen, Flame } from "lucide-react";
/**
 * Wpis aktywności użytkownika (z user_activity)
 */

export interface IUserActivityItem {
  eventTime: string;
  type: "LESSON_COMPLETED" | "SESSION_STARTED" | "SESSION_COMPLETED" | "LOGIN";
  title: string;
  subtitle: string;
  points: number;
}

export const userActivity = {
  getActivityIcon: (type: IUserActivityItem["type"]) => {
    switch (type) {
      case "LESSON_COMPLETED":
        return <BookCheck className="w-4 h-4 text-green-500" />;
      case "SESSION_STARTED":
        return <BookOpen className="w-4 h-4 text-blue-500" />;
      case "SESSION_COMPLETED":
        return <Trophy className="w-4 h-4 text-yellow-500" />;
      case "LOGIN":
        return <Flame className="w-4 h-4 text-orange-500" />;
    }
  },

  getActivityColor: (type: IUserActivityItem["type"]) => {
    switch (type) {
      case "LESSON_COMPLETED":
        return "bg-green-500/10 border-green-500/20";
      case "SESSION_STARTED":
        return "bg-blue-500/10 border-blue-500/20";
      case "SESSION_COMPLETED":
        return "bg-yellow-500/10 border-yellow-500/20";
      case "LOGIN":
        return "bg-orange-500/10 border-orange-500/20";
    }
  },
};
