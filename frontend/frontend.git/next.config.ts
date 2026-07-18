import type { NextConfig } from "next";

// standalone = mały serwer node (dynamiczne trasy [id] wymagają runtime → static export odpada).
// rewrites = proxy /api -> api-gateway (odpowiednik proxy w Vite). Front woła relatywnie /api
// (axios baseURL), serwer Next przekierowuje na gateway. Działa dev (localhost) i prod (k8s
// Service DNS przez env API_PROXY_TARGET).
const API_PROXY_TARGET = process.env.API_PROXY_TARGET ?? "http://localhost:8811";

const nextConfig: NextConfig = {
  output: "standalone",
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
};

export default nextConfig;
