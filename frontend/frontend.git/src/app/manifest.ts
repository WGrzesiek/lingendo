import type { MetadataRoute } from "next";

export default function manifest(): MetadataRoute.Manifest {
  return {
    name: "Lingendo — nauka słownictwa wspierana przez AI",
    short_name: "Lingendo",
    description:
      "Demonstracyjna platforma do nauki słownictwa z wykorzystaniem AI, fiszek i powtórek przestrzennych.",
    start_url: "/",
    scope: "/",
    display: "standalone",
    background_color: "#111111",
    theme_color: "#22a447",
    lang: "pl",
    categories: ["education", "productivity"],
    icons: [
      {
        src: "/icon.svg",
        sizes: "any",
        type: "image/svg+xml",
        purpose: "any",
      },
    ],
  };
}
