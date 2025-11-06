"use client";

import { Github, Twitter, Linkedin, Mail } from "lucide-react";

const socials = [
  { icon: Github, href: "https://github.com/", label: "GitHub" },
  { icon: Twitter, href: "https://twitter.com/", label: "Twitter" },
  { icon: Linkedin, href: "https://linkedin.com/", label: "LinkedIn" },
  { icon: Mail, href: "mailto:contact@lingendo.app", label: "Mail" },
];

export function FooterSocials() {
  return (
    <div className="flex items-center gap-4">
      {socials.map((s) => (
        <a
          key={s.label}
          href={s.href}
          target="_blank"
          rel="noopener noreferrer"
          className="text-muted-foreground transition hover:text-foreground"
        >
          <s.icon className="h-5 w-5" aria-label={s.label} />
        </a>
      ))}
    </div>
  );
}
