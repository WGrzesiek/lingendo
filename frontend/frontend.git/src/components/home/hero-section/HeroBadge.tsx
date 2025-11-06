export function HeroBadge({ text }: { text: string }) {
  return (
    <div className="inline-flex items-center gap-2 rounded-full bg-foreground/5 px-3 py-1 text-xs text-foreground/70 ring-1 ring-foreground/10">
      <span className="h-2 w-2 rounded-full bg-lime-400" />
      {text}
    </div>
  );
}
