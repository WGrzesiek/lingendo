import { Card } from "@/components/ui/card";
import { Plus, BookOpen, Users, Trophy, Calendar } from "lucide-react";

interface QuickAction {
  title: string;
  description: string;
  icon: typeof BookOpen;
  color: string;
  action: string;
}

/**
 * Sekcja szybkich akcji dla ucznia
 * Umożliwia szybki dostęp do najczęstszych operacji
 */
export const StudentQuickActions = () => {
  const actions: QuickAction[] = [
    {
      title: "Utwórz własny kurs",
      description: "Stwórz kurs dopasowany do swoich potrzeb",
      icon: BookOpen,
      color: "bg-info",
      action: "create-course",
    },
    {
      title: "Przeglądaj społeczność",
      description: "Zobacz kursy utworzone przez innych",
      icon: Users,
      color: "bg-success",
      action: "browse-community",
    },
    {
      title: "Zobacz ranking",
      description: "Sprawdź swoją pozycję w rankingu",
      icon: Trophy,
      color: "bg-premium",
      action: "view-leaderboard",
    },
    {
      title: "Dzienna praktyka",
      description: "Powtórz słówka z dzisiaj",
      icon: Calendar,
      color: "bg-streak",
      action: "daily-practice",
    },
  ];

  return (
    <Card className="p-6">
      <h2 className="text-2xl font-bold mb-6">Szybkie akcje</h2>

      <div className="grid grid-cols-1 gap-3">
        {actions.map((action) => {
          const Icon = action.icon;
          return (
            <button
              key={action.action}
              className="flex items-start gap-4 p-4 rounded-lg border hover:border-primary hover:bg-accent/50 transition-all text-left group"
            >
              <div
                className={`p-3 ${action.color} rounded-lg text-white group-hover:scale-110 transition-transform flex-shrink-0`}
              >
                <Icon className="w-5 h-5" />
              </div>

              <div className="flex-1 min-w-0">
                <h3 className="font-semibold mb-1">{action.title}</h3>
                <p className="text-sm text-muted-foreground">
                  {action.description}
                </p>
              </div>

              <Plus className="w-5 h-5 text-muted-foreground group-hover:text-primary transition-colors flex-shrink-0" />
            </button>
          );
        })}
      </div>
    </Card>
  );
};
