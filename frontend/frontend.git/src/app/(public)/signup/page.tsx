"use client";

import Link from "next/link";
import { SignupForm } from "@/features/auth/components/SignupForm";
import { BackgroundWithGreen } from "@/components/common/BackgroundWithGreen";
// import { useRedirectIfAuthenticated } from "@/features/auth/hooks/useRedirectIfAuthenticated";

const SignupPage = () => {
  // useRedirectIfAuthenticated();

  return (
    <main className="relative isolate flex min-h-screen items-center justify-center overflow-hidden px-4 py-12">
      <BackgroundWithGreen />
      <div className="relative z-10 w-full max-w-lg rounded-2xl border border-primary/10 bg-card/90 p-8 shadow-xl shadow-primary/5 backdrop-blur-sm">
        <h1 className="text-2xl font-bold text-center mb-6">Utwórz konto</h1>

        <SignupForm />

        <p className="text-center text-sm text-muted-foreground mt-4">
          Masz już konto?{" "}
          <Link href="/login" className="text-primary hover:underline">
            Zaloguj się
          </Link>
        </p>
      </div>
    </main>
  );
};

export default SignupPage;
