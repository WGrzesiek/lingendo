import { Github, Globe2 } from "lucide-react";

const socials = [
  {
    icon: Globe2,
    href: "https://gwawrzen.pl",
    label: "Portfolio Grzegorza Wawrzenia",
  },
  {
    icon: Github,
    href: "https://github.com/WGrzesiek/lingendo",
    label: "Kod Lingendo na GitHubie",
  },
];

export function FooterSocials() {
  return (
    <div className="flex items-center gap-3">
      {socials.map((social) => (
        <a
          key={social.href}
          href={social.href}
          target="_blank"
          rel="noopener noreferrer"
          className="rounded-lg bg-muted/60 p-2 text-muted-foreground transition-colors hover:bg-primary/10 hover:text-primary"
          aria-label={social.label}
        >
          <social.icon className="size-4" />
        </a>
      ))}
    </div>
  );
}
