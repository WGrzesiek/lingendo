"use client";

import { useState } from "react";
import { toast } from "sonner";
import { BookOpen, Share2, Search, Check, Loader2 } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Checkbox } from "@/components/ui/checkbox";
import { useInfiniteDecksCreatedByMe } from "@/features/deck/hooks/useInfiniteDecksCreatedByMe";
import { useShareDeckWithGroup } from "@/features/deck-share/hooks/useDeckShare";
import type { ICreatedDeckListItem } from "@/features/deck/types/created-deck.types";

interface ShareDeckToGroupDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  groupId: string;
  groupName: string;
  sharedDeckIds?: string[];
  onSuccess?: () => void;
}

/**
 * Dialog do udostępniania talii grupie
 */
export function ShareDeckToGroupDialog({
  open,
  onOpenChange,
  groupId,
  groupName,
  sharedDeckIds = [],
  onSuccess,
}: ShareDeckToGroupDialogProps) {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedDecks, setSelectedDecks] = useState<string[]>([]);
  const [message, setMessage] = useState("");

  const { data: decksData, isLoading: decksLoading } =
    useInfiniteDecksCreatedByMe();
  const decks = decksData?.pages.flatMap((page) => page.content) ?? [];

  const shareMutation = useShareDeckWithGroup();

  // Filtruj talie po wyszukiwaniu
  const filteredDecks = decks.filter(
    (deck) =>
      deck.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      deck.deckDescription?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // Sprawdź czy talia jest już udostępniona
  const isDeckShared = (deckId: string) => sharedDeckIds.includes(deckId);

  const toggleDeck = (deckId: string) => {
    if (isDeckShared(deckId)) return; // Nie można odznaczyć już udostępnionych

    setSelectedDecks((prev) =>
      prev.includes(deckId)
        ? prev.filter((id) => id !== deckId)
        : [...prev, deckId]
    );
  };

  const handleShare = async () => {
    if (selectedDecks.length === 0) {
      return;
    }

    let successCount = 0;
    let failedCount = 0;

    for (const deckId of selectedDecks) {
      try {
        await shareMutation.mutateAsync({
          deckId,
          groupId,
          message: message || undefined,
        });
        successCount++;
      } catch {
        failedCount++;
      }
    }

    if (successCount > 0) {
      toast.success(
        `Pomyślnie udostępniono ${successCount} ${
          successCount === 1 ? "talię" : "talii"
        } grupie "${groupName}"`
      );
    }

    if (failedCount > 0) {
      toast.error(
        `Nie udało się udostępnić ${failedCount} ${
          failedCount === 1 ? "talii" : "talii"
        }`
      );
    }

    if (successCount > 0) {
      setSelectedDecks([]);
      setMessage("");
      onOpenChange(false);
      onSuccess?.();
    }
  };

  const handleClose = () => {
    setSelectedDecks([]);
    setMessage("");
    setSearchQuery("");
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="max-w-2xl max-h-[90vh]">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Share2 className="size-5" />
            Udostępnij talię grupie
          </DialogTitle>
          <DialogDescription>
            Wybierz talie, które chcesz udostępnić grupie {groupName}. Uczniowie
            będą mogli zapisać się na wybrane kursy.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* Wyszukiwanie */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
            <Input
              placeholder="Szukaj talii..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>

          {/* Lista talii */}
          <ScrollArea className="h-[300px] border rounded-lg">
            {decksLoading ? (
              <div className="flex items-center justify-center h-full">
                <Loader2 className="size-6 animate-spin text-muted-foreground" />
              </div>
            ) : filteredDecks.length === 0 ? (
              <div className="flex flex-col items-center justify-center h-full text-muted-foreground p-4">
                <BookOpen className="size-8 mb-2 opacity-50" />
                <p>Brak talii do wyświetlenia</p>
                {searchQuery && (
                  <p className="text-sm">Spróbuj zmienić wyszukiwanie</p>
                )}
              </div>
            ) : (
              <div className="p-2 space-y-1">
                {filteredDecks.map((deck) => (
                  <DeckSelectItem
                    key={deck.id}
                    deck={deck}
                    isSelected={selectedDecks.includes(deck.id)}
                    isShared={isDeckShared(deck.id)}
                    onToggle={() => toggleDeck(deck.id)}
                  />
                ))}
              </div>
            )}
          </ScrollArea>

          {/* Licznik wybranych */}
          {selectedDecks.length > 0 && (
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Check className="size-4 text-green-500" />
              Wybrano: {selectedDecks.length}{" "}
              {selectedDecks.length === 1 ? "talię" : "talii"}
            </div>
          )}

          {/* Opcjonalna wiadomość */}
          <div className="space-y-2">
            <Label htmlFor="message">Wiadomość (opcjonalna)</Label>
            <Textarea
              id="message"
              placeholder="Dodaj wiadomość dla uczniów..."
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              rows={2}
            />
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={handleClose}>
            Anuluj
          </Button>
          <Button
            onClick={handleShare}
            disabled={selectedDecks.length === 0 || shareMutation.isPending}
          >
            {shareMutation.isPending ? (
              <>
                <Loader2 className="size-4 mr-2 animate-spin" />
                Udostępnianie...
              </>
            ) : (
              <>
                <Share2 className="size-4 mr-2" />
                Udostępnij ({selectedDecks.length})
              </>
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

/**
 * Element listy talii do wyboru
 */
function DeckSelectItem({
  deck,
  isSelected,
  isShared,
  onToggle,
}: {
  deck: ICreatedDeckListItem;
  isSelected: boolean;
  isShared: boolean;
  onToggle: () => void;
}) {
  return (
    <div
      className={`
        flex items-center gap-3 p-3 rounded-lg cursor-pointer transition-colors
        ${isShared ? "opacity-60 cursor-not-allowed bg-muted/30" : ""}
        ${
          isSelected && !isShared
            ? "bg-primary/10 border border-primary/30"
            : "hover:bg-muted/50"
        }
      `}
      onClick={onToggle}
    >
      <Checkbox
        checked={isSelected || isShared}
        disabled={isShared}
        className="pointer-events-none"
      />
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2">
          <span className="font-medium truncate">{deck.name}</span>
          {isShared && (
            <Badge variant="secondary" className="shrink-0">
              Już udostępniona
            </Badge>
          )}
        </div>
        <p className="text-sm text-muted-foreground truncate">
          {deck.wordCount} słów • {deck.deckCategory}
        </p>
      </div>
      <Badge
        variant="outline"
        className={
          deck.visibility === "PUBLIC"
            ? "bg-green-100 text-green-700 border-green-200"
            : "bg-blue-100 text-blue-700 border-blue-200"
        }
      >
        {deck.visibility === "PUBLIC" ? "Publiczna" : "Prywatna"}
      </Badge>
    </div>
  );
}
