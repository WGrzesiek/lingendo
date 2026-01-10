"use client";

import Link from "next/link";

type FooterLink = { label: string; href: string };
type Props = { title: string; links: FooterLink[] };

export function FooterLinkGroup({ title, links }: Props) {
  return (
    <div>
      <h4 className="text-sm font-semibold text-foreground mb-4">{title}</h4>
      <ul className="space-y-3">
        {links.map((l) => (
          <li key={l.href}>
            <Link
              href={l.href}
              className="text-sm text-muted-foreground transition-colors hover:text-primary"
            >
              {l.label}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
