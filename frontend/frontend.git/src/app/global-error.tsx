"use client";

import { useEffect } from "react";

interface GlobalErrorProps {
  error: Error & { digest?: string };
  reset: () => void;
}

/**
 * Globalna strona błędu wyświetlana gdy wystąpi krytyczny błąd,
 * który uniemożliwia wyrenderowanie nawet głównego layoutu.
 *
 */
export default function GlobalError({ error, reset }: GlobalErrorProps) {
  useEffect(() => {
    console.error("Krytyczny błąd aplikacji:", error);
  }, [error]);

  return (
    <html lang="pl">
      <body className="antialiased">
        <div
          style={{
            minHeight: "100vh",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: "1rem",
            fontFamily:
              'Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
            backgroundColor: "#fafafa",
          }}
        >
          <div
            style={{
              maxWidth: "28rem",
              width: "100%",
              textAlign: "center",
            }}
          >
            {/* Ikona błędu */}
            <div
              style={{
                width: "5rem",
                height: "5rem",
                borderRadius: "50%",
                backgroundColor: "#fee2e2",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                margin: "0 auto 1.5rem",
              }}
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="40"
                height="40"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#dc2626"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3" />
                <path d="M12 9v4" />
                <path d="M12 17h.01" />
              </svg>
            </div>

            {/* Nagłówek */}
            <h1
              style={{
                fontSize: "1.5rem",
                fontWeight: "700",
                color: "#171717",
                marginBottom: "0.5rem",
              }}
            >
              Wystąpił krytyczny błąd
            </h1>
            <p
              style={{
                color: "#737373",
                marginBottom: "1.5rem",
                lineHeight: "1.5",
              }}
            >
              Przepraszamy, aplikacja napotkała poważny problem. Spróbuj
              odświeżyć stronę lub wróć później.
            </p>

            {/* Kod błędu */}
            {error.digest && (
              <div
                style={{
                  backgroundColor: "#f5f5f5",
                  borderRadius: "0.5rem",
                  padding: "0.5rem 1rem",
                  display: "inline-block",
                  marginBottom: "1.5rem",
                }}
              >
                <p style={{ fontSize: "0.75rem", color: "#737373", margin: 0 }}>
                  Kod błędu:{" "}
                  <code
                    style={{
                      fontFamily: "monospace",
                      color: "#171717",
                    }}
                  >
                    {error.digest}
                  </code>
                </p>
              </div>
            )}

            {/* Przyciski */}
            <div
              style={{
                display: "flex",
                gap: "0.75rem",
                justifyContent: "center",
                flexWrap: "wrap",
              }}
            >
              <button
                onClick={reset}
                style={{
                  display: "inline-flex",
                  alignItems: "center",
                  gap: "0.5rem",
                  padding: "0.625rem 1rem",
                  backgroundColor: "#2563eb",
                  color: "white",
                  border: "none",
                  borderRadius: "0.375rem",
                  fontSize: "0.875rem",
                  fontWeight: "500",
                  cursor: "pointer",
                  transition: "background-color 0.2s",
                }}
                onMouseOver={(e) =>
                  (e.currentTarget.style.backgroundColor = "#1d4ed8")
                }
                onMouseOut={(e) =>
                  (e.currentTarget.style.backgroundColor = "#2563eb")
                }
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8" />
                  <path d="M3 3v5h5" />
                  <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16" />
                  <path d="M16 16h5v5" />
                </svg>
                Spróbuj ponownie
              </button>
              <button
                onClick={() => (window.location.href = "/")}
                style={{
                  display: "inline-flex",
                  alignItems: "center",
                  gap: "0.5rem",
                  padding: "0.625rem 1rem",
                  backgroundColor: "white",
                  color: "#171717",
                  border: "1px solid #e5e5e5",
                  borderRadius: "0.375rem",
                  fontSize: "0.875rem",
                  fontWeight: "500",
                  cursor: "pointer",
                  transition: "background-color 0.2s",
                }}
                onMouseOver={(e) =>
                  (e.currentTarget.style.backgroundColor = "#f5f5f5")
                }
                onMouseOut={(e) =>
                  (e.currentTarget.style.backgroundColor = "white")
                }
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                  <polyline points="9 22 9 12 15 12 15 22" />
                </svg>
                Strona główna
              </button>
            </div>

            {/* Wskazówka */}
            <p
              style={{
                fontSize: "0.875rem",
                color: "#a3a3a3",
                marginTop: "2rem",
              }}
            >
              Jeśli problem się powtarza, skontaktuj się z nami.
            </p>
          </div>
        </div>
      </body>
    </html>
  );
}
