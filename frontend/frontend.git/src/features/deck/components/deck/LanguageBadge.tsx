import { languageConfig, Language } from "@/types/common";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import { ArrowRight } from "lucide-react";

interface LanguageBadgeProps {
  languageFrom: Language;
  languageTo: Language;
}

/**
 * Badge pokazujący parę językową kursu (np. EN → PL)
 */
export const LanguageBadge = ({
  languageFrom,
  languageTo,
}: LanguageBadgeProps) => {
  const from = languageConfig[languageFrom];
  const to = languageConfig[languageTo];

  if (!from || !to) return null;

  return (
    <Badge
      variant="outline"
      className="gap-1.5 pr-3 bg-gradient-to-r from-blue-50 to-emerald-50 border-blue-200/50"
    >
      <span className="font-medium">{from.label}</span>
      <ArrowRight className="h-3 w-3 text-muted-foreground" />
      <span className="font-medium">{to.label}</span>
    </Badge>
  );
};

interface SingleLanguageBadgeProps {
  language: Language;
  label?: string;
}

/**
 * Badge pokazujący pojedynczy język
 */
export const SingleLanguageBadge = ({
  language,
  label,
}: SingleLanguageBadgeProps) => {
  const config = languageConfig[language];

  if (!config) return null;

  const Icon = config.icon;

  return (
    <Badge variant="outline" className={cn("gap-1.5 pr-3", config.className)}>
      <Icon className="h-3.5 w-3.5" />
      <span>{label ?? config.label}</span>
    </Badge>
  );
};
