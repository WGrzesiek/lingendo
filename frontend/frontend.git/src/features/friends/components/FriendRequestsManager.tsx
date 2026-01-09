"use client";

import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Clock,
  Check,
  X,
  MessageSquare,
  RefreshCw,
  Inbox,
  Send,
} from "lucide-react";
import {
  useReceivedRequests,
  useSentRequests,
  useAcceptRequest,
  useRejectRequest,
  useCancelRequest,
} from "../hooks/useFriends";
import type { FriendRequest } from "../types/friend.types";
import { time } from "@/lib/time";

/**
 * Komponent pojedynczego otrzymanego zaproszenia
 */
const ReceivedRequestCard = ({
  request,
  onAccept,
  onReject,
  isAccepting,
  isRejecting,
}: {
  request: FriendRequest;
  onAccept: () => void;
  onReject: () => void;
  isAccepting: boolean;
  isRejecting: boolean;
}) => {
  return (
    <div className="flex items-start gap-4 p-4 border rounded-lg bg-card hover:shadow-sm transition-shadow">
      <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
        <span className="text-lg font-semibold text-primary">
          {request.senderUsername.charAt(0).toUpperCase()}
        </span>
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2 mb-1">
          <span className="font-semibold">{request.senderUsername}</span>
          <span className="text-xs text-muted-foreground flex items-center gap-1">
            <Clock className="w-3 h-3" />
            {time(request.createdAt)}
          </span>
        </div>

        {request.message && (
          <p className="text-sm text-muted-foreground mb-2 flex items-start gap-1">
            <MessageSquare className="w-4 h-4 flex-shrink-0 mt-0.5" />
            <span className="italic">&quot;{request.message}&quot;</span>
          </p>
        )}

        <div className="flex items-center gap-2">
          <Button
            size="sm"
            onClick={onAccept}
            disabled={isAccepting || isRejecting}
          >
            {isAccepting ? (
              <RefreshCw className="w-4 h-4 animate-spin" />
            ) : (
              <>
                <Check className="w-4 h-4 mr-1" />
                Akceptuj
              </>
            )}
          </Button>
          <Button
            size="sm"
            variant="outline"
            onClick={onReject}
            disabled={isAccepting || isRejecting}
          >
            {isRejecting ? (
              <RefreshCw className="w-4 h-4 animate-spin" />
            ) : (
              <>
                <X className="w-4 h-4 mr-1" />
                Odrzuć
              </>
            )}
          </Button>
        </div>
      </div>
    </div>
  );
};

/**
 * Komponent pojedynczego wysłanego zaproszenia
 */
const SentRequestCard = ({
  request,
  onCancel,
  isCancelling,
}: {
  request: FriendRequest;
  onCancel: () => void;
  isCancelling: boolean;
}) => {
  return (
    <div className="flex items-center gap-4 p-4 border rounded-lg bg-card">
      <div className="w-10 h-10 rounded-full bg-muted flex items-center justify-center flex-shrink-0">
        <span className="text-sm font-semibold text-muted-foreground">
          {request.receiverUsername.charAt(0).toUpperCase()}
        </span>
      </div>

      <div className="flex-1 min-w-0">
        <span className="font-medium">{request.receiverUsername}</span>
        <div className="flex items-center gap-2 text-xs text-muted-foreground">
          <Clock className="w-3 h-3" />
          {time(request.createdAt)}
          <Badge variant="secondary" className="text-xs">
            Oczekuje
          </Badge>
        </div>
      </div>

      <Button
        size="sm"
        variant="ghost"
        onClick={onCancel}
        disabled={isCancelling}
        className="text-destructive hover:text-destructive"
      >
        {isCancelling ? (
          <RefreshCw className="w-4 h-4 animate-spin" />
        ) : (
          <>
            <X className="w-4 h-4 mr-1" />
            Anuluj
          </>
        )}
      </Button>
    </div>
  );
};

/**
 * Komponent zarządzania zaproszeniami do znajomych
 */
export const FriendRequestsManager = () => {
  const { data: receivedRequests, isLoading: isLoadingReceived } =
    useReceivedRequests();
  const { data: sentRequests, isLoading: isLoadingSent } = useSentRequests();

  const acceptMutation = useAcceptRequest();
  const rejectMutation = useRejectRequest();
  const cancelMutation = useCancelRequest();

  const pendingReceivedCount = receivedRequests?.length || 0;
  const pendingSentCount = sentRequests?.length || 0;

  return (
    <div className="space-y-6">
      {/* Otrzymane zaproszenia */}
      <Card>
        <CardHeader className="border-b">
          <CardTitle className="flex items-center gap-2">
            <Inbox className="w-5 h-5" />
            Otrzymane zaproszenia
            {pendingReceivedCount > 0 && (
              <Badge variant="default">{pendingReceivedCount}</Badge>
            )}
          </CardTitle>
        </CardHeader>
        <CardContent className="pt-4">
          {isLoadingReceived ? (
            <div className="flex items-center justify-center py-8">
              <RefreshCw className="w-6 h-6 animate-spin text-muted-foreground" />
            </div>
          ) : !receivedRequests || receivedRequests.length === 0 ? (
            <div className="text-center py-8">
              <Inbox className="w-12 h-12 mx-auto mb-4 text-muted-foreground opacity-50" />
              <p className="text-muted-foreground">
                Brak oczekujących zaproszeń
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {receivedRequests.map((request) => (
                <ReceivedRequestCard
                  key={request.id}
                  request={request}
                  onAccept={() => acceptMutation.mutate(request.id)}
                  onReject={() => rejectMutation.mutate(request.id)}
                  isAccepting={
                    acceptMutation.isPending &&
                    acceptMutation.variables === request.id
                  }
                  isRejecting={
                    rejectMutation.isPending &&
                    rejectMutation.variables === request.id
                  }
                />
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Wysłane zaproszenia */}
      <Card>
        <CardHeader className="border-b">
          <CardTitle className="flex items-center gap-2">
            <Send className="w-5 h-5" />
            Wysłane zaproszenia
            {pendingSentCount > 0 && (
              <Badge variant="secondary">{pendingSentCount}</Badge>
            )}
          </CardTitle>
        </CardHeader>
        <CardContent className="pt-4">
          {isLoadingSent ? (
            <div className="flex items-center justify-center py-8">
              <RefreshCw className="w-6 h-6 animate-spin text-muted-foreground" />
            </div>
          ) : !sentRequests || sentRequests.length === 0 ? (
            <div className="text-center py-8">
              <Send className="w-12 h-12 mx-auto mb-4 text-muted-foreground opacity-50" />
              <p className="text-muted-foreground">
                Nie masz oczekujących zaproszeń
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {sentRequests.map((request) => (
                <SentRequestCard
                  key={request.id}
                  request={request}
                  onCancel={() => cancelMutation.mutate(request.id)}
                  isCancelling={
                    cancelMutation.isPending &&
                    cancelMutation.variables === request.id
                  }
                />
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};
