export default function HeroH1({
  text,
  colorText,
}: {
  text: string;
  colorText: string;
}) {
  return (
    <h1 className="text-balance text-4xl font-extrabold leading-tight tracking-tight sm:text-5xl md:text-6xl">
      {text}{" "}
      <span className="relative whitespace-nowrap">
        <span className="absolute -inset-1 -skew-x-3 rounded-md bg-lime-300/40" />
        <span className="relative text-lime-500">{colorText}</span>
      </span>
    </h1>
  );
}
