let accessToken: string | null = null;
const listeners = new Set<(t: string | null) => void>();

export const TokenStore = {
  get: () => accessToken,
  set: (t: string | null) => {
    accessToken = t;
    listeners.forEach((l) => l(t));
  },
  subscribe: (l: (t: string | null) => void) => {
    listeners.add(l);
    return () => listeners.delete(l);
  },
};

export const authFetch = async (url: string, options: RequestInit = {}) => {
  const token = TokenStore.get();

  return fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      ...(token && { Authorization: `Bearer ${token}` }),
    },
  });
};
