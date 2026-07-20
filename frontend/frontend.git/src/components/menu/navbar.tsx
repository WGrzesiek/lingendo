"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { usePathname } from "next/navigation";
import { BookOpen, LogOut, Menu, Settings, User } from "lucide-react";
import { Button } from "@/components/ui/button";
import ModeToggle from "@/components/ui/mode-toggle";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useCurrentUser } from "@/features/auth/hooks/useCurrentUser";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { cn } from "@/lib/utils";

const publicMenuItems = [
  { label: "Funkcje", href: "/#features" },
  { label: "Jak to działa", href: "/how-it-works" },
  { label: "O twórcy", href: "/#creator" },
  { label: "FAQ", href: "/faq" },
];

export function Navbar() {
  const pathname = usePathname();
  const [open, setOpen] = useState(false);
  const { data: user, isLoading } = useCurrentUser();
  const { logout } = useAuth();

  useEffect(() => setOpen(false), [pathname]);

  const privateMenuItems = [
    {
      label: "Dashboard",
      href:
        user?.accountType === "TEACHER" ? "/dashboard-teacher" : "/dashboard",
    },
    { label: "Moje talie", href: "/my-courses" },
    { label: "Statystyki", href: "/statistics" },
    { label: "Znajomi", href: "/friends" },
    ...(user?.accountType === "STUDENT"
      ? [{ label: "Moi nauczyciele", href: "/my-teachers" }]
      : []),
    { label: "Społeczność", href: "/community" },
  ];

  const menuItems = user ? privateMenuItems : publicMenuItems;
  const isActive = (href: string) => {
    const route = href.split("#")[0] || "/";
    return route !== "/" && (pathname === route || pathname.startsWith(`${route}/`));
  };

  const closeMenu = () => setOpen(false);

  return (
    <nav className="fixed inset-x-0 top-8 z-50 border-b bg-background/85 backdrop-blur-xl">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        <Link
          href="/"
          className="flex items-center gap-2 text-xl font-bold tracking-tight"
          onClick={closeMenu}
        >
          <span className="flex size-9 items-center justify-center rounded-xl bg-primary/10">
            <BookOpen className="size-5 text-primary" aria-hidden="true" />
          </span>
          Lingendo
        </Link>

        <div className="hidden items-center gap-1 md:flex">
          {menuItems.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                "rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-accent hover:text-foreground",
                isActive(item.href) && "bg-primary/10 text-primary"
              )}
            >
              {item.label}
            </Link>
          ))}

          <div className="ml-2 flex items-center gap-2 border-l pl-3">
            <ModeToggle />

            {isLoading ? (
              <div
                className="size-8 animate-spin rounded-full border-2 border-primary border-t-transparent"
                aria-label="Sprawdzanie sesji"
              />
            ) : user ? (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="gap-2">
                    <User className="size-4" />
                    <span className="max-w-28 truncate">{user.username}</span>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56">
                  <DropdownMenuLabel>
                    <p className="truncate font-medium">{user.username}</p>
                    <p className="text-xs font-normal text-muted-foreground">
                      Konto {user.accountType.toLowerCase()}
                    </p>
                  </DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem asChild>
                    <Link href={privateMenuItems[0].href}>
                      <BookOpen className="mr-2 size-4" />
                      Dashboard
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link href="/settings">
                      <Settings className="mr-2 size-4" />
                      Ustawienia
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem
                    onClick={() => logout()}
                    className="text-destructive focus:text-destructive"
                  >
                    <LogOut className="mr-2 size-4" />
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
                  <Link href="/signup">Wypróbuj demo</Link>
                </Button>
              </>
            )}
          </div>
        </div>

        <div className="flex items-center gap-1 md:hidden">
          <ModeToggle />
          <Sheet open={open} onOpenChange={setOpen}>
            <SheetTrigger asChild>
              <Button size="icon" variant="ghost" aria-label="Otwórz menu">
                <Menu className="size-6" />
              </Button>
            </SheetTrigger>
            <SheetContent
              side="right"
              className="top-8 bottom-0 h-auto w-full max-w-none border-l border-primary/10 bg-background/95 px-6 backdrop-blur-xl sm:max-w-sm"
            >
              <SheetHeader className="items-center border-b px-0 pb-5 pt-8 text-center">
                <SheetTitle className="text-xl">Menu Lingendo</SheetTitle>
                <SheetDescription>
                  {user ? `Zalogowano jako ${user.username}` : "Wybierz stronę"}
                </SheetDescription>
              </SheetHeader>

              <div className="flex flex-1 flex-col gap-2 pt-4">
                {menuItems.map((item) => (
                  <Link
                    key={item.href}
                    href={item.href}
                    onClick={closeMenu}
                    className={cn(
                      "rounded-xl px-4 py-3 text-center text-base font-medium text-muted-foreground transition-colors hover:bg-accent hover:text-foreground",
                      isActive(item.href) && "bg-primary/10 text-primary"
                    )}
                  >
                    {item.label}
                  </Link>
                ))}

                {user ? (
                  <>
                    <Link
                      href="/settings"
                      onClick={closeMenu}
                      className="rounded-xl px-4 py-3 text-center font-medium text-muted-foreground hover:bg-accent hover:text-foreground"
                    >
                      Ustawienia
                    </Link>
                    <Button
                      variant="destructive"
                      className="mt-auto mb-6"
                      onClick={() => {
                        closeMenu();
                        logout();
                      }}
                    >
                      <LogOut className="mr-2 size-4" />
                      Wyloguj się
                    </Button>
                  </>
                ) : (
                  <div className="mt-auto mb-6 grid gap-3 border-t pt-6">
                    <Button asChild>
                      <Link href="/signup" onClick={closeMenu}>
                        Wypróbuj demo
                      </Link>
                    </Button>
                    <Button variant="outline" asChild>
                      <Link href="/login" onClick={closeMenu}>
                        Zaloguj się
                      </Link>
                    </Button>
                  </div>
                )}
              </div>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </nav>
  );
}
