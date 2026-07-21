import type { MetadataRoute } from "next";

export default function robots(): MetadataRoute.Robots {
  return {
    rules: {
      userAgent: "*",
      allow: "/",
      disallow: [
        "/api/",
        "/login",
        "/signup",
        "/account-disabled",
        "/community",
        "/course",
        "/dashboard",
        "/dashboard-teacher",
        "/decks",
        "/friends",
        "/groups",
        "/join",
        "/leaderboard",
        "/learn",
        "/my-courses",
        "/my-teachers",
        "/review",
        "/settings",
        "/shared-courses",
        "/statistics",
      ],
    },
    sitemap: "https://www.lingendo.app/sitemap.xml",
    host: "https://www.lingendo.app",
  };
}
