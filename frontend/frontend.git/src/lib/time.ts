

export function time(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();

  const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  let unit: Intl.RelativeTimeFormatUnit = "second";
  let value = -seconds;

  if (seconds < 60) {
    unit = "second";
    value = -seconds;
  } else if (seconds < 3600) {
    unit = "minute";
    value = -Math.floor(seconds / 60);
  } else if (seconds < 86400) {
    unit = "hour";
    value = -Math.floor(seconds / 3600);
  } else if (seconds < 86400 * 30) {
    unit = "day";
    value = -Math.floor(seconds / 86400);
  } else if (seconds < 86400 * 365) {
    unit = "month";
    value = -Math.floor(seconds / (86400 * 30));
  } else {
    unit = "year";
    value = -Math.floor(seconds / (86400 * 365));
  }

  return new Intl.RelativeTimeFormat("pl", { numeric: "auto" }).format(
    value,
    unit
  );
}

export const timee = {
  /**
   * Formatuje datę
   */
  formatDate: (dateString?: string): string => {
    if (!dateString) return "Brak danych";
    return new Date(dateString).toLocaleDateString("pl-PL", {
      day: "numeric",
      month: "long",
      year: "numeric",
    });
  },

  /**
   * Formatuje datę z czasem
   */
  formatDateTime: (dateString?: string): string => {
    if (!dateString) return "Nigdy";
    return new Date(dateString).toLocaleString("pl-PL", {
      day: "numeric",
      month: "long",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  }
}