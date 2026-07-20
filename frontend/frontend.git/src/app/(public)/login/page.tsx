"use client";

import Link from "next/link";
import { LoginForm } from "@/features/auth/components/LoginForm";

const SigninPage = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-background">
      <div className="w-full max-w-md p-8 bg-card rounded-lg shadow-md border">
        <h1 className="text-2xl font-bold text-center mb-6">Zaloguj się</h1>
        <LoginForm />
        <p className="text-center text-sm text-muted-foreground mt-4">
          Nie masz jeszcze konta?{" "}
          <Link href="/signup" className="text-primary hover:underline">
            Załóż konto
          </Link>
        </p>
      </div>
    </div>
  );
};
export default SigninPage;
