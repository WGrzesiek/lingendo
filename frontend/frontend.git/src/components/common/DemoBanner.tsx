import Link from "next/link";
import { FlaskConical } from "lucide-react";

export function DemoBanner() {
  return (
    <div className="fixed inset-x-0 top-0 z-[60] flex h-8 items-center justify-center border-b border-emerald-900/10 bg-emerald-500 px-3 text-emerald-950 shadow-sm">
      <div className="flex items-center gap-2 text-center text-xs font-medium sm:text-sm">
        <FlaskConical className="size-3.5 shrink-0" aria-hidden="true" />
        <span className="sm:hidden">Demo · projekt portfolio na k3s</span>
        <span className="hidden sm:inline">
          Środowisko demonstracyjne · projekt portfolio na własnym klastrze k3s
          · dane mogą być okresowo resetowane
        </span>
        <Link
          href="https://gwawrzen.pl/projekty/lingendo/"
          target="_blank"
          rel="noopener noreferrer"
          className="shrink-0 underline decoration-emerald-900/30 underline-offset-2 hover:decoration-emerald-950"
        >
          O projekcie
        </Link>
      </div>
    </div>
  );
}
