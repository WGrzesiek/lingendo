"use client";

import { useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import {
  ArrowLeft,
  Edit,
  Trash2,
  Users,
  BookOpen,
  Calendar,
  Share2,
} from "lucide-react";
import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  GroupFormDialog,
  DeleteGroupDialog,
  GroupMembersList,
  AddMembersDialog,
  GroupStatsGrid,
  GroupLeaderboard,
  GroupCoursesList,
  GroupActivityFeed,
  ShareDeckToGroupDialog,
} from "@/features/groups/components";
import {
  useGroupDetail,
  useGroupDashboard,
  useGroupLeaderboard,
  useGroupCourses,
  useGroupActivity,
} from "@/features/groups/hooks/useGroupsData";
import { useQueryClient } from "@tanstack/react-query";
import {timee} from "@/lib/time";

/**
 * Strona szczegółów grupy
 */
const GroupDetailPage = () => {
  const params = useParams();
  const router = useRouter();
  const queryClient = useQueryClient();
  const groupId = params.groupId as string;

  const { user, isLoading: authLoading } = useProtectedRoute({
    requiredAccountType: "TEACHER",
  });

  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [isAddMembersOpen, setIsAddMembersOpen] = useState(false);
  const [isShareDeckOpen, setIsShareDeckOpen] = useState(false);

  const { data: group, isLoading: groupLoading } = useGroupDetail(groupId);
  const { data: dashboard, isLoading: dashboardLoading } =
    useGroupDashboard(groupId);
  const { data: leaderboard, isLoading: leaderboardLoading } =
    useGroupLeaderboard(groupId);
  const { data: courses, isLoading: coursesLoading } = useGroupCourses(groupId);
  const { data: activity, isLoading: activityLoading } =
    useGroupActivity(groupId);

  const isLoading = authLoading || groupLoading;

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background">
        <div className="container mx-auto p-6 lg:p-8 space-y-6">
          <div className="flex items-center gap-4">
            <Skeleton className="h-10 w-10" />
            <div>
              <Skeleton className="h-8 w-64 mb-2" />
              <Skeleton className="h-5 w-96" />
            </div>
          </div>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            {[...Array(8)].map((_, i) => (
              <Skeleton key={i} className="h-28" />
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (!user || !group) {
    return null;
  }

  // const formattedDate = new Date(group.createdAt).toLocaleDateString("pl-PL", {
  //   day: "numeric",
  //   month: "long",
  //   year: "numeric",
  // });

  const handleDeleteSuccess = () => {
    router.push("/groups");
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-6">
        {/* Nagłówek */}
        <div className="flex items-start justify-between">
          <div className="flex items-start gap-4">
            <Button variant="ghost" size="icon" asChild>
              <Link href="/groups">
                <ArrowLeft className="size-5" />
              </Link>
            </Button>
            <div>
              <h1 className="text-3xl font-bold mb-2">{group.name}</h1>
              {group.description && (
                <p className="text-muted-foreground mb-3">
                  {group.description}
                </p>
              )}
              <div className="flex items-center gap-4 text-sm text-muted-foreground">
                <div className="flex items-center gap-1.5">
                  <Users className="size-4" />
                  <span>{group.memberCount} członków</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <BookOpen className="size-4" />
                  <span>{group.sharedDecksCount} kursów</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <Calendar className="size-4" />
                  <span>Utworzono {timee.formatDate(group.createdAt)}</span>
                </div>
              </div>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setIsShareDeckOpen(true)}
            >
              <Share2 className="size-4 mr-2" />
              Udostępnij kurs
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setIsEditOpen(true)}
            >
              <Edit className="size-4 mr-2" />
              Edytuj
            </Button>
            <Button
              variant="outline"
              size="sm"
              className="text-destructive hover:text-destructive"
              onClick={() => setIsDeleteOpen(true)}
            >
              <Trash2 className="size-4 mr-2" />
              Usuń
            </Button>
          </div>
        </div>

        {/* Statystyki */}
        <GroupStatsGrid stats={dashboard?.stats} isLoading={dashboardLoading} />

        {/* Zakładki */}
        <Tabs defaultValue="members" className="space-y-6">
          <TabsList>
            <TabsTrigger value="members">
              <Users className="size-4 mr-2" />
              Członkowie
            </TabsTrigger>
            <TabsTrigger value="courses">
              <BookOpen className="size-4 mr-2" />
              Kursy
            </TabsTrigger>
            <TabsTrigger value="activity">
              <Calendar className="size-4 mr-2" />
              Aktywność
            </TabsTrigger>
          </TabsList>

          <TabsContent value="members" className="space-y-6">
            <div className="grid gap-6 lg:grid-cols-3">
              <div className="lg:col-span-2">
                <GroupMembersList
                  groupId={groupId}
                  onAddMembers={() => setIsAddMembersOpen(true)}
                />
              </div>
              <div>
                <GroupLeaderboard
                  entries={leaderboard}
                  isLoading={leaderboardLoading}
                  title="Top uczniowie"
                />
              </div>
            </div>
          </TabsContent>

          <TabsContent value="courses" className="space-y-6">
            <GroupCoursesList courses={courses} isLoading={coursesLoading} />
          </TabsContent>

          <TabsContent value="activity" className="space-y-6">
            <GroupActivityFeed
              activities={activity}
              isLoading={activityLoading}
            />
          </TabsContent>
        </Tabs>

        {/* Dialogi */}
        <GroupFormDialog
          open={isEditOpen}
          onOpenChange={setIsEditOpen}
          group={group}
        />

        <DeleteGroupDialog
          open={isDeleteOpen}
          onOpenChange={setIsDeleteOpen}
          group={group}
          onSuccess={handleDeleteSuccess}
        />

        <AddMembersDialog
          open={isAddMembersOpen}
          onOpenChange={setIsAddMembersOpen}
          groupId={groupId}
        />

        <ShareDeckToGroupDialog
          open={isShareDeckOpen}
          onOpenChange={setIsShareDeckOpen}
          groupId={groupId}
          groupName={group.name}
          sharedDeckIds={courses?.map((course) => course.deckId) ?? []}
          onSuccess={() => {
            queryClient.invalidateQueries({
              queryKey: ["group-courses", groupId],
            });
            queryClient.invalidateQueries({
              queryKey: ["group-dashboard", groupId],
            });
            queryClient.invalidateQueries({
              queryKey: ["group-detail", groupId],
            });
          }}
        />
      </div>
    </div>
  );
};

export default GroupDetailPage;
