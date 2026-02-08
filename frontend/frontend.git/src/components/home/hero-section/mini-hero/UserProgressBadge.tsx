import Image from "next/image";

export function UserProgressBadge() {
  return (
    <div
      className="
        mt-4 w-full rounded-2xl bg-card/75 p-3 shadow-xl ring-1 ring-border
        md:absolute md:-left-6 md:-top-6 md:mt-0 md:w-56
        pointer-events-auto
        hidden lg:block
      "
    >
      <div className="flex items-center gap-3">
        <div className="h-10 w-10 overflow-hidden rounded-full ring-1 ring-border">
          <Image src="/avatar.png" alt="Grzesiek" width={40} height={40} />
        </div>
        <div>
          <p className="text-sm font-semibold">Grzesiek</p>
          <p className="text-xs text-muted-foreground">Poziom: B1</p>
        </div>
      </div>
      <div className="mt-3 space-y-1 text-[11px] text-muted-foreground">
        <p>
          <span className="font-medium text-foreground">Słówka dziś:</span> 15 /
          20
        </p>
        <p>
          <span className="font-medium text-foreground">Seria dni:</span> 🔥 7
          dni
        </p>
      </div>
    </div>
  );
}
