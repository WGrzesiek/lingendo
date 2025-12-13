export function timeAgo(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();

  const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  const intervals: [number, Intl.RelativeTimeFormatUnit][] = [
    [60, "second"],
    [60 * 60, "minute"],
    [60 * 60 * 24, "hour"],
    [60 * 60 * 24 * 30, "day"],
    [60 * 60 * 24 * 365, "month"],
  ];

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
