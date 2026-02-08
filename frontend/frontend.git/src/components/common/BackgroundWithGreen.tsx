export function BackgroundWithGreen() {
  return (
    <>
      <div className="pointer-events-none absolute -top-32 -left-32 h-[36rem] w-[36rem] rounded-full bg-lime-300/20 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-32 -right-32 h-[36rem] w-[36rem] rounded-full bg-emerald-300/20 blur-3xl" />
    </>
  );
}
