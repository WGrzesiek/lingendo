"use client";

type Link = { label: string; href: string };
type Props = { title: string; links: Link[] };

export function FooterLinkGroup({ title, links }: Props) {
  return (
    <div>
      <h4 className="text-sm font-semibold text-foreground mb-3">{title}</h4>
      <ul className="space-y-2 text-sm text-muted-foreground">
        {links.map((l) => (
          <li key={l.href}>
            <a
              href={l.href}
              className="transition-colors hover:text-foreground/80"
            >
              {l.label}
            </a>
          </li>
        ))}
      </ul>
    </div>
  );
}
