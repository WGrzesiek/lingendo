"use client";

import Link from "next/link";
import { UsersRound, Plus, ArrowRight, Users } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { useTeacherGroups } from "../hooks";

/**
 * Widget podsumowania grup na dashboardzie nauczyciela
 */
export const GroupsSummary = () => {
  const { data: groupsPage, isLoading } = useTeacherGroups(false, 0, 3);

  const recentGroups = groupsPage?.content || [];
  const totalGroups = groupsPage?.totalElements || 0;

  if (isLoading) {
    return (
      <Card className="p-6">
        <div className="flex items-center justify-between mb-6">
          <Skeleton className="h-7 w-32" />
          <Skeleton className="h-9 w-28" />
        </div>
        <div className="space-y-3">
          {[1, 2, 3].map((i) => (
            <Skeleton key={i} className="h-16 w-full" />
          ))}
        </div>
      </Card>
    );
  }

  return (
    <Card className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-2">
          <UsersRound className="size-5 text-indigo-500" />
          <h2 className="text-xl font-bold">Moje grupy</h2>
        </div>
        <Button variant="outline" size="sm" asChild>
          <Link href="/groups/create">
            <Plus className="size-4 mr-1" />
            Nowa grupa
          </Link>
        </Button>
      </div>

      {recentGroups.length > 0 ? (
        <>
          <div className="space-y-3">
            {recentGroups.map((group) => (
              <Link
                key={group.id}
                href={`/groups/${group.id}`}
                className="flex items-center gap-4 p-3 rounded-lg border hover:border-primary hover:bg-accent/50 transition-all group"
              >
                <div className="flex size-10 items-center justify-center rounded-full bg-indigo-100 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400">
                  <UsersRound className="size-5" />
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="font-medium truncate">{group.name}</h3>
                  <div className="flex items-center gap-3 text-sm text-muted-foreground">
                    <span className="flex items-center gap-1">
                      <Users className="size-3" />
                      {group.memberCount} uczniów
                    </span>
                    {group.description && (
                      <span className="truncate text-xs">
                        {group.description}
                      </span>
                    )}
                  </div>
                </div>
                <Badge
                  variant={!group.isArchived ? "default" : "secondary"}
                  className="shrink-0"
                >
                  {!group.isArchived ? "Aktywna" : "Zarchiwizowana"}
                </Badge>
              </Link>
            ))}
          </div>

          {totalGroups > 3 && (
            <Link
              href="/groups"
              className="flex items-center justify-center gap-2 mt-4 py-2 text-sm text-muted-foreground hover:text-primary transition-colors"
            >
              Zobacz wszystkie grupy ({totalGroups})
              <ArrowRight className="size-4" />
            </Link>
          )}
        </>
      ) : (
        <div className="text-center py-8">
          <UsersRound className="size-12 mx-auto text-muted-foreground/50 mb-3" />
          <h3 className="font-medium mb-1">Brak grup</h3>
          <p className="text-sm text-muted-foreground mb-4">
            Utwórz pierwszą grupę, aby zarządzać uczniami
          </p>
          <Button asChild>
            <Link href="/groups/create">
              <Plus className="size-4 mr-2" />
              Utwórz grupę
            </Link>
          </Button>
        </div>
      )}

      {recentGroups.length > 0 && (
        <div className="mt-4 pt-4 border-t">
          <Link
            href="/groups"
            className="flex items-center justify-between text-sm font-medium text-primary hover:underline"
          >
            Zarządzaj wszystkimi grupami
            <ArrowRight className="size-4" />
          </Link>
        </div>
      )}
    </Card>
  );
};
