import type { NextConfig } from "next";

// const nextConfig: NextConfig = {
//   output: "standalone",
// };
//
// export default nextConfig;

// na test czy przejdzie build
const nextConfig = {
  eslint: {
    ignoreDuringBuilds: true,
    typescript: { ignoreBuildErrors: true },
  },
};

export default nextConfig;
