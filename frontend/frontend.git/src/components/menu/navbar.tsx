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
import { Menu } from "lucide-react";

export function Navbar() {
  const [open, setOpen] = useState(false);

  const menuItems = [
    { label: "Funkcje", href: "#features" },
    { label: "Jak to działa", href: "#how" },
    { label: "Cennik", href: "#pricing" },
    { label: "FAQ", href: "#faq" },
  ];

  return (
    <nav className="fixed top-0 left-0 right-0 bg-background/70 backdrop-blur-md border-b z-50">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        {/* Logo */}
        <Link href="/" className="text-xl font-semibold">
          LearnWords
        </Link>

        {/* Desktop menu */}
        <div className="hidden md:flex gap-6 items-center">
          {menuItems.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className="text-sm font-medium hover:text-primary transition"
            >
              {item.label}
            </Link>
          ))}

          <ModeToggle />

          <Button variant="ghost" asChild>
            <Link href="/login">Zaloguj się</Link>
          </Button>

          <Button asChild>
            <Link href="/signup">Rozpocznij za darmo</Link>
          </Button>
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
                {menuItems.map((item) => (
                  <Link
                    key={item.href}
                    href={item.href}
                    onClick={() => setOpen(false)}
                    className="text-md font-medium"
                  >
                    {item.label}
                  </Link>
                ))}

                <Button asChild>
                  <Link href="/signup">Rozpocznij za darmo</Link>
                </Button>

                <Button variant="ghost" asChild>
                  <Link href="/login">Zaloguj się</Link>
                </Button>
              </div>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </nav>
  );
}
