"use client";

import Link from "next/link";
import { SignupForm } from "@/features/auth/components/SignupForm";
// import { useRedirectIfAuthenticated } from "@/features/auth/hooks/useRedirectIfAuthenticated";

const SignupPage = () => {
  // useRedirectIfAuthenticated();

  return (
    <div className="min-h-screen flex items-center justify-center bg-background py-8">
      <div className="w-full max-w-lg p-8 bg-card rounded-lg shadow-md border">
        <h1 className="text-2xl font-bold text-center mb-6">Utwórz konto</h1>

        <SignupForm />

        <p className="text-center text-sm text-muted-foreground mt-4">
          Masz już konto?{" "}
          <Link href="/login" className="text-primary hover:underline">
            Zaloguj się
          </Link>
        </p>
      </div>
    </div>
  );
};

export default SignupPage;
