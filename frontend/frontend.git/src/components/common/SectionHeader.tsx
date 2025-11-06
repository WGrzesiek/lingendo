"use client";

type Props = {
  eyebrow?: string;
  title: string;
  subtitle?: string;
  align?: "left" | "center";
  className?: string;
};

export function SectionHeader({
  eyebrow,
  title,
  subtitle,
  align = "center",
  className = "",
}: Props) {
  const alignCls = align === "center" ? "text-center" : "text-left";
  return (
    <div className={`space-y-3 ${alignCls} ${className}`}>
      {eyebrow && (
        <div className="inline-flex items-center gap-2 rounded-full bg-foreground/5 px-3 py-1 text-xs text-foreground/70 ring-1 ring-foreground/10">
          <span className="h-2 w-2 rounded-full bg-lime-400" />
          {eyebrow}
        </div>
      )}
      <h2 className="text-2xl font-bold tracking-tight sm:text-3xl">{title}</h2>
      {subtitle && (
        <p className="text-muted-foreground max-w-2xl mx-auto">{subtitle}</p>
      )}
    </div>
  );
}
