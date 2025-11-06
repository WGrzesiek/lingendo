import Link from "next/link";
import { SignupForm } from "@/features/auth/components/SignupForm";

const SignupPage = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-background">
      <div className="w-full max-w-md p-8 bg-card rounded-lg shadow-md border">
        <h1 className="text-2xl font-bold text-center mb-6">Create Account</h1>

        <SignupForm />

        <p className="text-center text-sm text-muted-foreground mt-4">
          Already have an account?{" "}
          <Link href="/signin" className="text-primary hover:underline">
            Sign in
          </Link>
        </p>
      </div>
    </div>
  );
};

export default SignupPage;
