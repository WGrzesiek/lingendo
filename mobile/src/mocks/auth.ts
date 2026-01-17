import type { User } from '../types/auth';

/**
 * Zamockowany użytkownik do celów testowych
 */
export const MOCK_USER: User = {
  id: 1,
  username: 'jan_kowalski',
  email: 'jan@example.com',
  firstName: 'Jan',
  lastName: 'Kowalski',
  accountType: 'STUDENT',
  userType: 'NORMAL',
  isEnabled: true,
  createdAt: '2025-01-01T12:00:00Z',
};

/**
 * Symulacja logowania - zwraca sukces po 1 sekundzie
 */
export const mockLogin = (username: string, password: string): Promise<User> => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (username === 'demo' && password === 'demo123') {
        resolve(MOCK_USER);
      } else if (username && password.length >= 6) {
        resolve({ ...MOCK_USER, username });
      } else {
        reject(new Error('Nieprawidłowa nazwa użytkownika lub hasło'));
      }
    }, 1000);
  });
};

/**
 * Symulacja rejestracji - zwraca sukces po 1.5 sekundy
 */
export const mockSignup = (data: {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  accountType: string;
}): Promise<User> => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        ...MOCK_USER,
        ...data,
        id: Math.floor(Math.random() * 1000),
        userType: 'NORMAL',
        isEnabled: true,
        createdAt: new Date().toISOString(),
      } as User);
    }, 1500);
  });
};
