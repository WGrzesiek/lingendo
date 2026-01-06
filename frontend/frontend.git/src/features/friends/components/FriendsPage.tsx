"use client";

import { useState } from "react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";
import { FriendsList } from "./FriendsList";
import { FriendRequestsManager } from "./FriendRequestsManager";
import { UserSearchBox } from "./UserSearchBox";
import { BlockedUsersManager } from "./BlockedUsersManager";
import { useReceivedRequests, useBlockedUsers } from "../hooks/useFriends";
import { Users, Search, Ban, Bell } from "lucide-react";

type FriendsTab = "friends" | "requests" | "search" | "blocked";

interface FriendsPageProps {
  initialTab?: FriendsTab;
}

/**
 * Główna strona znajomych z nawigacją zakładkową
 */
export const FriendsPage = ({ initialTab = "friends" }: FriendsPageProps) => {
  const [activeTab, setActiveTab] = useState<FriendsTab>(initialTab);


  const { data: receivedRequests } = useReceivedRequests();
  const { data: blockedUsers } = useBlockedUsers();

  const pendingRequestsCount =
    receivedRequests?.filter((r) => r.status === "PENDING").length || 0;

  return (
    <div className="container mx-auto py-6 space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-2">
        <h1 className="text-3xl font-bold flex items-center gap-3">
          <Users className="w-8 h-8" />
          Znajomi
        </h1>
        <p className="text-muted-foreground">
          Zarządzaj znajomymi, wysyłaj zaproszenia i porównuj postępy w nauce
        </p>
      </div>

      {/* Tabs */}
      <Tabs
        value={activeTab}
        onValueChange={(v) => setActiveTab(v as FriendsTab)}
        className="w-full"
      >
        <TabsList className="grid w-full grid-cols-4 lg:w-auto lg:inline-flex">
          <TabsTrigger value="friends" className="flex items-center gap-2">
            <Users className="w-4 h-4" />
            <span className="hidden sm:inline">Znajomi</span>
          </TabsTrigger>
          <TabsTrigger value="requests" className="flex items-center gap-2">
            <Bell className="w-4 h-4" />
            <span className="hidden sm:inline">Zaproszenia</span>
            {pendingRequestsCount > 0 && (
              <Badge variant="destructive" className="text-xs px-1.5 min-w-5">
                {pendingRequestsCount}
              </Badge>
            )}
          </TabsTrigger>
          <TabsTrigger value="search" className="flex items-center gap-2">
            <Search className="w-4 h-4" />
            <span className="hidden sm:inline">Szukaj</span>
          </TabsTrigger>
          <TabsTrigger value="blocked" className="flex items-center gap-2">
            <Ban className="w-4 h-4" />
            <span className="hidden sm:inline">Zablokow.</span>
            {blockedUsers && blockedUsers.length > 0 && (
              <Badge variant="secondary" className="text-xs px-1.5 min-w-5">
                {blockedUsers.length}
              </Badge>
            )}
          </TabsTrigger>
        </TabsList>

        <div className="mt-6">
          <TabsContent value="friends" className="m-0">
            <FriendsList />
          </TabsContent>

          <TabsContent value="requests" className="m-0">
            <FriendRequestsManager />
          </TabsContent>

          <TabsContent value="search" className="m-0">
            <UserSearchBox />
          </TabsContent>

          <TabsContent value="blocked" className="m-0">
            <BlockedUsersManager />
          </TabsContent>
        </div>
      </Tabs>
    </div>
  );
};
