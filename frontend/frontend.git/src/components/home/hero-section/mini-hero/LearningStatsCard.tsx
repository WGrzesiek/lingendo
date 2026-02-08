export function LearningStatsCard() {
  const sessions = [
    { name: "Poranna sesja", status: "Aktywna", words: 25 },
    { name: "Wieczorna powtórka", status: "Nieaktywna", words: 12 },
  ];

  return (
    <div className="absolute -bottom-6 right-6 w-[70%] rounded-2xl p-3 shadow-2xl ring-1 ring-foreground/10 bg-card/75 hidden lg:block">
      <div className="mb-2 flex items-center justify-between text-xs">
        <p className="font-semibold">Twoje sesje</p>
        <span className="rounded-full bg-emerald-100 px-2 py-0.5 text-[10px] font-medium text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300">
          2 aktywne
        </span>
      </div>
      <ul className="space-y-2">
        {sessions.map((s, i) => (
          <li
            key={s.name}
            className="flex items-center justify-between rounded-xl border border-foreground/10 bg-foreground/5 px-3 py-2 text-xs"
          >
            <span>{s.name}</span>
            <span className={i === 0 ? "text-emerald-600" : "text-zinc-500"}>
              {s.status} ({s.words})
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}
