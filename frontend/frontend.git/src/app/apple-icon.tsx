import { ImageResponse } from "next/og";

export const size = { width: 180, height: 180 };
export const contentType = "image/png";

export default function AppleIcon() {
  return new ImageResponse(
    (
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          width: "100%",
          height: "100%",
          borderRadius: 36,
          background: "#112118",
          color: "#4ade80",
          fontSize: 96,
          fontWeight: 800,
        }}
      >
        L
      </div>
    ),
    size
  );
}
