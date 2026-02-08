"use client";

import { motion } from "framer-motion";
import { type ReactNode } from "react";

type Variant = "fade" | "slide" | "zoom";

interface AnimatedDivProps {
  children: ReactNode;
  className?: string;
  variant?: Variant;
}

export function AnimatedContainer({
  children,
  className = "",
  variant = "fade",
}: AnimatedDivProps) {
  const variants = {
    fade: { initial: { opacity: 0 }, animate: { opacity: 1 } },
    slide: { initial: { opacity: 0, y: 20 }, animate: { opacity: 1, y: 0 } },
    zoom: {
      initial: { opacity: 0, scale: 0.98 },
      animate: { opacity: 1, scale: 1 },
    },
  }[variant];

  return (
    <motion.div
      {...variants}
      transition={{ duration: 0.6 }}
      className={className}
    >
      {children}
    </motion.div>
  );
}
