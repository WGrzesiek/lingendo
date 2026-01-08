import Link from "next/link";
import { Card } from "@/components/ui/card";
import {
  BookOpen,
  Users,
  UsersRound,
  Link2,
  Settings,
  ChevronRight,
} from "lucide-react";

interface QuickAction {
  title: string;
  description: string;
  icon: typeof BookOpen;
  color: string;
  href: string;
}

/**
 * Sekcja szybkich akcji dla nauczyciela
 */
export const QuickActions = () => {
  const actions: QuickAction[] = [
    {
      title: "Zarządzaj grupami",
      description: "Twórz grupy i udostępniaj kursy",
      icon: UsersRound,
      color: "bg-indigo-500",
      href: "/groups",
    },
    {
      title: "Zarządzaj studentami",
      description: "Zobacz listę i śledź postępy uczniów",
      icon: Users,
      color: "bg-blue-500",
      href: "/dashboard-teacher/students",
    },
    {
      title: "Generuj zaproszenia",
      description: "Utwórz kody dla nowych studentów",
      icon: Link2,
      color: "bg-green-500",
      href: "/dashboard-teacher/invitations",
    },
    {
      title: "Zarządzaj kursami",
      description: "Twórz i udostępniaj kursy",
      icon: BookOpen,
      color: "bg-purple-500",
      href: "/my-courses",
    },
    {
      title: "Ustawienia",
      description: "Zarządzaj swoim kontem",
      icon: Settings,
      color: "bg-orange-500",
      href: "/settings",
    },
  ];

  return (
    <Card className="p-6">
      <h2 className="text-2xl font-bold mb-6">Szybkie akcje</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {actions.map((action) => {
          const Icon = action.icon;
          return (
            <Link
              key={action.href}
              href={action.href}
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

              <ChevronRight className="w-5 h-5 text-muted-foreground group-hover:text-primary transition-colors" />
            </Link>
          );
        })}
      </div>
    </Card>
  );
};
