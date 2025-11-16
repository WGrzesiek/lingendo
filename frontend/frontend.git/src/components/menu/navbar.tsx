"use client";

import Link from "next/link";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import ModeToggle from "@/components/ui/mode-toggle";
import {
  Sheet,
  SheetTrigger,
  SheetContent,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Menu, User, LogOut, Settings, BookOpen } from "lucide-react";
import { useCurrentUser } from "@/features/auth/hooks/useCurrentUser";
import { useAuth } from "@/features/auth/hooks/useAuth";

/**
 * Navbar - Nawigacja główna aplikacji
 * Automatycznie dostosowuje się do stanu zalogowania użytkownika
 */
export function Navbar() {
  const [open, setOpen] = useState(false);
  const { data: user, isLoading } = useCurrentUser();
  const { logout } = useAuth();

  // Mock zalogowanego użytkownika - odkomentuj useCurrentUser powyżej gdy backend będzie gotowy
  // const user = {
  //   userId: "student-456",
  //   username: "Piotr Wiśniewski",
  //   accountType: "BASIC" as "BASIC" | "PREMIUM" | "STUDENT" | "TEACHER",
  //   userType: "NORMAL" as const,
  //   isEnabled: true,
  // };
  // const isLoading = false;

  const publicMenuItems = [
    { label: "Funkcje", href: "#features" },
    { label: "Jak to działa", href: "#how" },
    { label: "Cennik", href: "#pricing" },
    { label: "FAQ", href: "#faq" },
  ];

  const privateMenuItems = [
    {
      label: "Dashboard",
      href:
        user?.accountType === "TEACHER" ? "/dashboard-teacher" : "/dashboard",
    },
    { label: "Społeczność", href: "/community" },
    { label: "Kursy", href: "/courses" },
  ];

  const handleLogout = async () => {
    await logout();
  };

  return (
    <nav className="fixed top-0 left-0 right-0 bg-background/70 backdrop-blur-md border-b z-50">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        <Link href="/" className="text-xl font-semibold">
          LearnWords
        </Link>

        <div className="hidden md:flex gap-6 items-center">
          {!user &&
            publicMenuItems.map((item) => (
              <Link
                key={item.href}
                href={item.href}
                className="text-sm font-medium hover:text-primary transition"
              >
                {item.label}
              </Link>
            ))}

          {user &&
            privateMenuItems.map((item) => (
              <Link
                key={item.href}
                href={item.href}
                className="text-sm font-medium hover:text-primary transition"
              >
                {item.label}
              </Link>
            ))}

          <ModeToggle />

          {isLoading ? (
            <div className="w-8 h-8 border-2 border-primary border-t-transparent rounded-full animate-spin" />
          ) : user ? (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="gap-2">
                  <User className="w-4 h-4" />
                  {user.username}
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-56">
                <DropdownMenuLabel>
                  <div className="flex flex-col gap-1">
                    <p className="font-medium">{user.username}</p>
                    <p className="text-xs text-muted-foreground">
                      {user.accountType}
                    </p>
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild>
                  <Link
                    href={privateMenuItems[0].href}
                    className="cursor-pointer"
                  >
                    <BookOpen className="w-4 h-4 mr-2" />
                    Dashboard
                  </Link>
                </DropdownMenuItem>
                <DropdownMenuItem asChild>
                  <Link href="/settings" className="cursor-pointer">
                    <Settings className="w-4 h-4 mr-2" />
                    Ustawienia
                  </Link>
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={handleLogout}
                  className="cursor-pointer text-red-600"
                >
                  <LogOut className="w-4 h-4 mr-2" />
                  Wyloguj się
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <>
              <Button variant="ghost" asChild>
                <Link href="/login">Zaloguj się</Link>
              </Button>

              <Button asChild>
                <Link href="/signup">Rozpocznij za darmo</Link>
              </Button>
            </>
          )}
        </div>

        {/* Mobile menu / hamburger */}
        <div className="md:hidden flex items-center gap-2">
          <ModeToggle />

          <Sheet open={open} onOpenChange={setOpen}>
            <SheetTrigger asChild>
              <Button size="icon" variant="ghost">
                <Menu className="w-6 h-6" />
              </Button>
            </SheetTrigger>

            <SheetContent side="right">
              <SheetHeader>
                <SheetTitle>Menu</SheetTitle>
              </SheetHeader>

              <div className="flex flex-col gap-4 mt-6">
                {!user &&
                  publicMenuItems.map((item) => (
                    <Link
                      key={item.href}
                      href={item.href}
                      onClick={() => setOpen(false)}
                      className="text-md font-medium"
                    >
                      {item.label}
                    </Link>
                  ))}

                {user && (
                  <>
                    <Link
                      href={privateMenuItems[0].href}
                      onClick={() => setOpen(false)}
                      className="text-md font-medium"
                    >
                      Dashboard
                    </Link>

                    <Link
                      href="/settings"
                      onClick={() => setOpen(false)}
                      className="text-md font-medium"
                    >
                      Ustawienia
                    </Link>

                    <div className="pt-4 border-t">
                      <p className="text-sm text-muted-foreground mb-2">
                        Zalogowany jako:
                      </p>
                      <p className="font-medium">{user.username}</p>
                      <p className="text-xs text-muted-foreground">
                        {user.accountType}
                      </p>
                    </div>

                    <Button
                      variant="destructive"
                      onClick={() => {
                        setOpen(false);
                        handleLogout();
                      }}
                    >
                      <LogOut className="w-4 h-4 mr-2" />
                      Wyloguj się
                    </Button>
                  </>
                )}

                {!user && (
                  <>
                    <Button asChild>
                      <Link href="/signup">Rozpocznij za darmo</Link>
                    </Button>

                    <Button variant="ghost" asChild>
                      <Link href="/login">Zaloguj się</Link>
                    </Button>
                  </>
                )}
              </div>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </nav>
  );
}
