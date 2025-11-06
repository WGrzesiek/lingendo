export default function HeroSocialProof({
  firstText,
  secondText,
}: {
  firstText: string;
  secondText: string;
}) {
  return (
    <div className="flex flex-wrap items-center gap-x-6 gap-y-3 pt-4 text-sm text-muted-foreground">
      <span>{firstText}</span>
      <div className="h-4 w-px bg-foreground/10" />
      <span>{secondText}</span>
    </div>
  );
}
