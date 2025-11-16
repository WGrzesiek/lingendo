import { Card } from "@/components/ui/card";
import { Plus, BookOpen, Users, FileText, Settings } from "lucide-react";

interface QuickAction {
  title: string;
  description: string;
  icon: typeof BookOpen;
  color: string;
  action: string;
}

/**
 * Sekcja szybkich akcji dla nauczyciela
 * Umożliwia szybki dostęp do najczęstszych operacji
 */
export const QuickActions = () => {
  const actions: QuickAction[] = [
    {
      title: "Utwórz nowy kurs",
      description: "Dodaj nowy kurs dla swoich uczniów",
      icon: BookOpen,
      color: "bg-blue-500",
      action: "create-course",
    },
    {
      title: "Dodaj uczniów",
      description: "Zaproś nowych uczniów do kursu",
      icon: Users,
      color: "bg-green-500",
      action: "add-students",
    },
    {
      title: "Stwórz test",
      description: "Przygotuj test lub quiz dla uczniów",
      icon: FileText,
      color: "bg-purple-500",
      action: "create-test",
    },
    {
      title: "Ustawienia",
      description: "Zarządzaj swoim kontem i preferencjami",
      icon: Settings,
      color: "bg-orange-500",
      action: "settings",
    },
  ];

  return (
    <Card className="p-6">
      <h2 className="text-2xl font-bold mb-6">Szybkie akcje</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {actions.map((action) => {
          const Icon = action.icon;
          return (
            <button
              key={action.action}
              className="flex items-start gap-4 p-4 rounded-lg border hover:border-primary hover:bg-accent/50 transition-all text-left group"
            >
              <div
                className={`p-3 ${action.color} rounded-lg text-white group-hover:scale-110 transition-transform`}
              >
                <Icon className="w-6 h-6" />
              </div>

              <div className="flex-1">
                <h3 className="font-semibold mb-1">{action.title}</h3>
                <p className="text-sm text-muted-foreground">
                  {action.description}
                </p>
              </div>

              <Plus className="w-5 h-5 text-muted-foreground group-hover:text-primary transition-colors" />
            </button>
          );
        })}
      </div>
    </Card>
  );
};
