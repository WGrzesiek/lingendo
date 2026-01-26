"use client";

import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Share2, Heart, Loader2, CheckCircle, AlertCircle } from "lucide-react";
import { useShareDeckWithAllFriends } from "../hooks/useDeckShare";

interface ShareDeckDialogProps {
  deckId: string;
  deckName: string;
  trigger?: React.ReactNode;
}

/**
 * Dialog do udostępniania talii znajomym
 * Dla zwykłych użytkowników - udostępnia wszystkim znajomym
 */
export function ShareDeckDialog({
  deckId,
  deckName,
  trigger,
}: ShareDeckDialogProps) {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState("");
  const [showSuccess, setShowSuccess] = useState(false);

  const shareMutation = useShareDeckWithAllFriends();

  const handleShare = () => {
    shareMutation.mutate(
      {
        deckId,
        message: message.trim() || undefined,
      },
      {
        onSuccess: () => {
          setShowSuccess(true);
          setTimeout(() => {
            setOpen(false);
            setShowSuccess(false);
            setMessage("");
          }, 1500);
        },
      }
    );
  };

  const handleOpenChange = (newOpen: boolean) => {
    if (!shareMutation.isPending) {
      setOpen(newOpen);
      if (!newOpen) {
        setMessage("");
        setShowSuccess(false);
        shareMutation.reset();
      }
    }
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>
        {trigger || (
          <Button variant="outline" size="lg" className="gap-2">
            <Share2 className="w-5 h-5" />
            Udostępnij
          </Button>
        )}
      </DialogTrigger>

      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Share2 className="w-5 h-5" />
            Udostępnij kurs
          </DialogTitle>
          <DialogDescription>
            Udostępnij kurs &quot;{deckName}&quot; swoim znajomym
          </DialogDescription>
        </DialogHeader>

        {showSuccess ? (
          <div className="flex flex-col items-center justify-center py-8 text-center">
            <div className="w-16 h-16 rounded-full bg-green-100 dark:bg-green-900/30 flex items-center justify-center mb-4">
              <CheckCircle className="w-8 h-8 text-green-600" />
            </div>
            <h3 className="text-lg font-semibold mb-2">Udostępniono!</h3>
            <p className="text-sm text-muted-foreground">
              Kurs został udostępniony wszystkim Twoim znajomym
            </p>
          </div>
        ) : (
          <>
            <div className="space-y-4 py-4">
              {/* Info o udostępnieniu */}
              <div className="flex items-start gap-3 p-3 bg-muted/50 rounded-lg">
                <Heart className="w-5 h-5 text-pink-500 mt-0.5 shrink-0" />
                <div className="text-sm">
                  <p className="font-medium mb-1">Udostępnij znajomym</p>
                  <p className="text-muted-foreground">
                    Kurs będzie widoczny dla wszystkich osób na Twojej liście
                    znajomych. Będą mogli się do niego zapisać i uczyć razem z
                    Tobą.
                  </p>
                </div>
              </div>

              {/* Opcjonalna wiadomość */}
              <div className="space-y-2">
                <Label htmlFor="message">Wiadomość (opcjonalnie)</Label>
                <Textarea
                  id="message"
                  placeholder="Dodaj wiadomość dla znajomych..."
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  rows={3}
                  className="resize-none"
                  maxLength={500}
                />
                <p className="text-xs text-muted-foreground text-right">
                  {message.length}/500
                </p>
              </div>

              {/* Error message */}
              {shareMutation.isError && (
                <div className="flex items-center gap-2 p-3 bg-destructive/10 text-destructive rounded-lg text-sm">
                  <AlertCircle className="w-4 h-4 shrink-0" />
                  <span>
                    {shareMutation.error?.message ||
                      "Wystąpił błąd podczas udostępniania"}
                  </span>
                </div>
              )}
            </div>

            <DialogFooter className="gap-2 sm:gap-0">
              <Button
                variant="ghost"
                onClick={() => setOpen(false)}
                disabled={shareMutation.isPending}
              >
                Anuluj
              </Button>
              <Button
                onClick={handleShare}
                disabled={shareMutation.isPending}
                className="gap-2"
              >
                {shareMutation.isPending ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" />
                    Udostępnianie...
                  </>
                ) : (
                  <>
                    <Share2 className="w-4 h-4" />
                    Udostępnij znajomym
                  </>
                )}
              </Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
