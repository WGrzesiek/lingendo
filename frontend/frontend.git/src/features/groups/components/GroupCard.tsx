"use client";

import { Users, Calendar, MoreVertical, Trash2, Edit } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardAction,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { cn } from "@/lib/utils";
import type { Group, GroupStatus } from "../types/group.types";
import { timee } from "@/lib/time";

interface GroupCardProps {
  group: Group;
  onEdit?: (group: Group) => void;
  onDelete?: (group: Group) => void;
  className?: string;
}

const statusConfig: Record<
  GroupStatus,
  {
    label: string;
    variant: "default" | "secondary" | "destructive" | "outline";
  }
> = {
  ACTIVE: { label: "Aktywna", variant: "default" },
  INACTIVE: { label: "Nieaktywna", variant: "secondary" },
  ARCHIVED: { label: "Zarchiwizowana", variant: "outline" },
};

/**
 * Karta pojedynczej grupy do wyświetlania na liście
 */
export function GroupCard({
  group,
  onEdit,
  onDelete,
  className,
}: GroupCardProps) {
  const router = useRouter();
  const { label, variant } = statusConfig[group.status];

  const handleCardClick = (e: React.MouseEvent) => {
    const target = e.target as HTMLElement;
    if (
      target.closest("[data-radix-dropdown-menu-trigger]") ||
      target.closest("button")
    ) {
      return;
    }
    router.push(`/groups/${group.id}`);
  };

  return (
    <Card
      className={cn(
        "hover:shadow-md transition-shadow cursor-pointer",
        className
      )}
      onClick={handleCardClick}
    >
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="space-y-1">
            <Link href={`/groups/${group.id}`} className="hover:underline">
              <CardTitle className="text-lg">{group.name}</CardTitle>
            </Link>
            {group.description && (
              <p className="text-sm text-muted-foreground line-clamp-2">
                {group.description}
              </p>
            )}
          </div>
          <Badge variant={variant}>{label}</Badge>
        </div>

        <CardAction>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon-sm">
                <MoreVertical className="size-4" />
                <span className="sr-only">Akcje</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => onEdit?.(group)}>
                <Edit className="size-4 mr-2" />
                Edytuj
              </DropdownMenuItem>
              <DropdownMenuItem
                onClick={() => onDelete?.(group)}
                className="text-destructive focus:text-destructive"
              >
                <Trash2 className="size-4 mr-2" />
                Usuń
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </CardAction>
      </CardHeader>

      <CardContent>
        <div className="flex items-center gap-6 text-sm text-muted-foreground">
          <div className="flex items-center gap-1.5">
            <Users className="size-4" />
            <span>{group.memberCount} uczniów</span>
          </div>
          {/*<div className="flex items-center gap-1.5">*/}
          {/*  <BookOpen className="size-4" />*/}
          {/*  <span>{group.sharedDecksCount} kursów</span>*/}
          {/*</div>*/}
          <div className="flex items-center gap-1.5">
            <Calendar className="size-4" />
            <span>{timee.formatDate(group.createdAt)}</span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
