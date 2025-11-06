import type { Metadata } from "next";
import "../styles/globals.css";
import { ThemeProvider } from "@/components/theme-provider";
import { Navbar } from "@/components/menu/navbar";
import { Footer } from "@/components/footer/Footer";

export const metadata: Metadata = {
  title: "Lingendo",
  description: "Unlock Your Words",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        <ThemeProvider
          attribute="class"
          defaultTheme="system"
          enableSystem
          disableTransitionOnChange
        >
          <Navbar />
          <main className="pt-16">{children}</main>
          <Footer />
        </ThemeProvider>
      </body>
    </html>
  );
}
