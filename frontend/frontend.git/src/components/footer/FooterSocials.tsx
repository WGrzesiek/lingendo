"use client";

import { Github, Twitter, Linkedin, Mail } from "lucide-react";

const socials = [
  { icon: Github, href: "https://github.com/lingendo", label: "GitHub" },
  { icon: Twitter, href: "https://twitter.com/lingendo", label: "Twitter" },
  {
    icon: Linkedin,
    href: "https://linkedin.com/company/lingendo",
    label: "LinkedIn",
  },
  { icon: Mail, href: "mailto:contact@lingendo.app", label: "Mail" },
];

export function FooterSocials() {
  return (
    <div className="flex items-center gap-3">
      {socials.map((s) => (
        <a
          key={s.label}
          href={s.href}
          target="_blank"
          rel="noopener noreferrer"
          className="p-2 rounded-lg text-muted-foreground bg-muted/50 hover:bg-primary/10 hover:text-primary transition-colors"
          aria-label={s.label}
        >
          <s.icon className="h-4 w-4" />
        </a>
      ))}
    </div>
  );
}
