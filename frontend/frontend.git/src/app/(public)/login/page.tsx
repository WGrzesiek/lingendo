"use client";

import Link from "next/link";
import { LoginForm } from "@/features/auth/components/LoginForm";
import { BackgroundWithGreen } from "@/components/common/BackgroundWithGreen";

const SigninPage = () => {
  return (
    <main className="relative isolate flex min-h-screen items-center justify-center overflow-hidden px-4 py-12">
      <BackgroundWithGreen />
      <div className="relative z-10 w-full max-w-md rounded-2xl border border-primary/10 bg-card/90 p-8 shadow-xl shadow-primary/5 backdrop-blur-sm">
        <h1 className="text-2xl font-bold text-center mb-6">Zaloguj się</h1>
        <LoginForm />
        <p className="text-center text-sm text-muted-foreground mt-4">
          Nie masz jeszcze konta?{" "}
          <Link href="/signup" className="text-primary hover:underline">
            Załóż konto
          </Link>
        </p>
      </div>
    </main>
  );
};
export default SigninPage;
