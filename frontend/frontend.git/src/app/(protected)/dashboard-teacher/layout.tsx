"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { LayoutDashboard, Users, Link2, Settings } from "lucide-react";
import { cn } from "@/lib/utils";

interface NavItem {
  href: string;
  label: string;
  icon: React.ElementType;
}

const navItems: NavItem[] = [
  {
    href: "/dashboard-teacher",
    label: "Dashboard",
    icon: LayoutDashboard,
  },
  {
    href: "/dashboard-teacher/students",
    label: "Studenci",
    icon: Users,
  },
  {
    href: "/dashboard-teacher/invitations",
    label: "Zaproszenia",
    icon: Link2,
  },
];

/**
 * Layout dla dashboardu nauczyciela z nawigacją boczną
 */
export default function TeacherDashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const pathname = usePathname();

  return (
    <div className="min-h-screen bg-background">
      {/* Nawigacja górna na mobile */}
      <nav className="lg:hidden border-b bg-card sticky top-0 z-40">
        <div className="container mx-auto px-4">
          <div className="flex items-center gap-1 overflow-x-auto py-2 scrollbar-hide">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive =
                pathname === item.href ||
                (item.href !== "/dashboard-teacher" &&
                  pathname.startsWith(item.href));

              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={cn(
                    "flex items-center gap-2 px-3 py-2 rounded-lg whitespace-nowrap text-sm transition-colors",
                    isActive
                      ? "bg-primary text-primary-foreground"
                      : "text-muted-foreground hover:bg-muted"
                  )}
                >
                  <Icon className="w-4 h-4" />
                  {item.label}
                </Link>
              );
            })}
          </div>
        </div>
      </nav>

      <div className="flex">
        {/* Sidebar na desktop */}
        <aside className="hidden lg:flex flex-col w-64 border-r bg-card min-h-screen sticky top-0">
          <div className="p-6 border-b">
            <h2 className="text-xl font-bold">Panel Nauczyciela</h2>
          </div>

          <nav className="flex-1 p-4">
            <ul className="space-y-1">
              {navItems.map((item) => {
                const Icon = item.icon;
                const isActive =
                  pathname === item.href ||
                  (item.href !== "/dashboard-teacher" &&
                    pathname.startsWith(item.href));

                return (
                  <li key={item.href}>
                    <Link
                      href={item.href}
                      className={cn(
                        "flex items-center gap-3 px-4 py-3 rounded-lg transition-colors",
                        isActive
                          ? "bg-primary text-primary-foreground"
                          : "text-muted-foreground hover:bg-muted hover:text-foreground"
                      )}
                    >
                      <Icon className="w-5 h-5" />
                      {item.label}
                    </Link>
                  </li>
                );
              })}
            </ul>
          </nav>

          <div className="p-4 border-t">
            <Link
              href="/settings"
              className="flex items-center gap-3 px-4 py-3 rounded-lg text-muted-foreground hover:bg-muted hover:text-foreground transition-colors"
            >
              <Settings className="w-5 h-5" />
              Ustawienia
            </Link>
          </div>
        </aside>

        {/* Główna zawartość */}
        <main className="flex-1">{children}</main>
      </div>
    </div>
  );
}
