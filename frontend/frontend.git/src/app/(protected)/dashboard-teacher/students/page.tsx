"use client";

import { useState } from "react";
import { StudentsList } from "@/features/dashboard-teacher/components/StudentsList";
import { StudentDetails } from "@/features/dashboard-teacher/components/StudentDetails";
import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";

/**
 * Strona zarządzania studentami
 */
const StudentsPage = () => {
  const { user, isLoading } = useProtectedRoute({
    requiredAccountType: "TEACHER",
  });

  const [selectedStudentId, setSelectedStudentId] = useState<string | null>(
    null
  );

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">Ładowanie...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8">
        {selectedStudentId ? (
          <StudentDetails
            studentId={selectedStudentId}
            onBack={() => setSelectedStudentId(null)}
          />
        ) : (
          <>
            <div className="mb-6">
              <h1 className="text-3xl font-bold mb-2">Studenci</h1>
              <p className="text-muted-foreground">
                Zarządzaj swoimi studentami i śledź ich postępy
              </p>
            </div>
            <StudentsList onViewDetails={setSelectedStudentId} />
          </>
        )}
      </div>
    </div>
  );
};

export default StudentsPage;
