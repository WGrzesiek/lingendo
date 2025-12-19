"use client";

import { useState } from "react";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Users } from "lucide-react";
import { FriendsList } from "@/features/friends/components/FriendsList";
import { AddFriendForm } from "@/features/friends/components/AddFriendForm";
import { IFriend } from "@/features/friends/types/friend.types";

// Mock data - znajomi użytkownika
const mockFriends: IFriend[] = [
  {
    userId: "user-1",
    username: "anna_kowalska",
    rankPosition: 1,
    totalPoints: 15420,
    friendsSince: "2024-01-15T10:00:00Z",
    isActive: true,
  },
  {
    userId: "user-2",
    username: "piotr_wisniewski",
    rankPosition: 2,
    totalPoints: 14890,
    friendsSince: "2024-02-20T14:30:00Z",
    isActive: true,
  },
  {
    userId: "user-3",
    username: "maria_nowak",
    rankPosition: 3,
    totalPoints: 13275,
    friendsSince: "2024-03-10T09:15:00Z",
    isActive: true,
  },
  {
    userId: "user-4",
    username: "jan_kaminski",
    rankPosition: 15,
    totalPoints: 8940,
    friendsSince: "2024-05-05T16:45:00Z",
    isActive: true,
  },
  {
    userId: "user-5",
    username: "ewa_lewandowska",
    rankPosition: 23,
    totalPoints: 7320,
    friendsSince: "2024-06-18T11:20:00Z",
    isActive: true,
  },
  {
    userId: "user-6",
    username: "tomasz_zielinski",
    rankPosition: 47,
    totalPoints: 4560,
    friendsSince: "2024-08-22T13:00:00Z",
    isActive: false,
  },
  {
    userId: "user-7",
    username: "kasia_wojcik",
    rankPosition: 89,
    totalPoints: 2180,
    friendsSince: "2024-10-30T08:30:00Z",
    isActive: true,
  },
];

/**
 * Strona "Znajomi" - lista znajomych z możliwością dodawania nowych
 */
const FriendsPage = () => {
  const [friends, setFriends] = useState<IFriend[]>(mockFriends);

  const handleAddFriend = async (username: string) => {
    // Symulacja API call
    await new Promise((resolve) => setTimeout(resolve, 1000));

    // Sprawdź czy użytkownik już istnieje
    const exists = friends.some(
      (f) => f.username.toLowerCase() === username.toLowerCase()
    );

    if (exists) {
      throw new Error("Ten użytkownik jest już na liście znajomych");
    }

    // Symulacja dodania nowego znajomego
    const newFriend: IFriend = {
      userId: `user-${Date.now()}`,
      username: username,
      rankPosition: Math.floor(Math.random() * 500) + 100,
      totalPoints: Math.floor(Math.random() * 3000) + 500,
      friendsSince: new Date().toISOString(),
      isActive: true,
    };

    setFriends([...friends, newFriend]);
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-8">
        {/* Header */}
        <div className="space-y-1">
          <h1 className="text-4xl font-bold">Znajomi</h1>
          <p className="text-muted-foreground text-lg">
            Zarządzaj swoją listą znajomych i śledź ich postępy w nauce
          </p>
        </div>

        {/* Formularz dodawania */}
        <AddFriendForm onAddFriend={handleAddFriend} />

        {/* Lista znajomych */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Users className="w-5 h-5" />
              Twoi znajomi
            </CardTitle>
          </CardHeader>
          <CardContent>
            <FriendsList friends={friends} />
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default FriendsPage;
