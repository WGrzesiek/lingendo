import type { NextConfig } from "next";

// standalone = mały serwer node (dynamiczne trasy [id] wymagają runtime → static export odpada).
// rewrites = proxy /api -> api-gateway (odpowiednik proxy w Vite). Front woła relatywnie /api
// (axios baseURL), serwer Next przekierowuje na gateway. Działa dev (localhost) i prod (k8s
// Service DNS przez env API_PROXY_TARGET).
const API_PROXY_TARGET = (
  process.env.API_PROXY_TARGET ?? "http://localhost:8811"
).replace(/\/+$/, "");

const nextConfig: NextConfig = {
  output: "standalone",
  poweredByHeader: false,
  compiler: {
    removeConsole: process.env.NODE_ENV === "production",
  },
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${API_PROXY_TARGET}/api/:path*`,
      },
    ];
  },
  async headers() {
    return [
      {
        source: "/:path*",
        headers: [
          { key: "X-Content-Type-Options", value: "nosniff" },
          { key: "Referrer-Policy", value: "strict-origin-when-cross-origin" },
          { key: "X-Frame-Options", value: "DENY" },
          {
            key: "Permissions-Policy",
            value: "camera=(), microphone=(), geolocation=()",
          },
        ],
      },
    ];
  },
};

export default nextConfig;
