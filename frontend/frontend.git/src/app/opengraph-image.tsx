import { ImageResponse } from "next/og";

export const alt =
  "Lingendo — demonstracyjna platforma do nauki słownictwa z AI";
export const size = { width: 1200, height: 630 };
export const contentType = "image/png";

export default function OpenGraphImage() {
  return new ImageResponse(
    (
      <div
        style={{
          display: "flex",
          height: "100%",
          width: "100%",
          position: "relative",
          overflow: "hidden",
          background: "#0d110e",
          color: "#f7faf8",
          fontFamily: "sans-serif",
        }}
      >
        <div
          style={{
            position: "absolute",
            width: 620,
            height: 620,
            left: -180,
            top: -260,
            borderRadius: 999,
            background: "rgba(74, 222, 128, 0.26)",
            filter: "blur(80px)",
          }}
        />
        <div
          style={{
            position: "absolute",
            width: 520,
            height: 520,
            right: -180,
            bottom: -300,
            borderRadius: 999,
            background: "rgba(16, 185, 129, 0.2)",
            filter: "blur(70px)",
          }}
        />
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "space-between",
            padding: "64px 72px",
            width: "100%",
            zIndex: 1,
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: 18 }}>
            <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                width: 64,
                height: 64,
                borderRadius: 18,
                border: "2px solid rgba(74, 222, 128, 0.35)",
                background: "rgba(74, 222, 128, 0.12)",
                fontSize: 36,
              }}
            >
              L
            </div>
            <div style={{ fontSize: 38, fontWeight: 700 }}>Lingendo</div>
          </div>

          <div style={{ display: "flex", flexDirection: "column", gap: 22 }}>
            <div style={{ fontSize: 68, lineHeight: 1.06, fontWeight: 800 }}>
              Słownictwo, które zostaje na dłużej.
            </div>
            <div style={{ fontSize: 28, color: "#a7b2aa" }}>
              Fiszki · kontekst wspierany przez AI · powtórki przestrzenne
            </div>
          </div>

          <div style={{ display: "flex", alignItems: "center", gap: 14 }}>
            <div
              style={{
                padding: "10px 18px",
                borderRadius: 999,
                background: "#4ade80",
                color: "#09210f",
                fontSize: 20,
                fontWeight: 700,
              }}
            >
              Projekt demonstracyjny
            </div>
            <div style={{ fontSize: 20, color: "#8a968d" }}>
              lingendo.app · projekt portfolio
            </div>
          </div>
        </div>
      </div>
    ),
    size
  );
}
