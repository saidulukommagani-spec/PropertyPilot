import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import axios from 'axios';
import { setTokenProvider, setUnauthorizedHandler } from '../../api/client';
import { authApi, type CurrentUserResponse, type LoginRequest, type LoginResponse } from '../../api/auth';

interface AuthContextValue {
  user: CurrentUserResponse | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isInitializing: boolean;
  isSigningIn: boolean;
  signIn(credentials: LoginRequest): Promise<void>;
  signOut(): void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);
const SESSION_KEY = 'propertypilot.session.v1';

interface PersistedSession { accessToken: string; expiresAt: number }

function readSession(): PersistedSession | null {
  try {
    const raw = window.sessionStorage.getItem(SESSION_KEY);
    if (!raw) return null;
    const parsed: unknown = JSON.parse(raw);
    if (typeof parsed !== 'object' || parsed === null || !('accessToken' in parsed) || !('expiresAt' in parsed)) return null;
    const session = parsed as PersistedSession;
    if (typeof session.accessToken !== 'string' || !session.accessToken || typeof session.expiresAt !== 'number' || session.expiresAt <= Date.now()) {
      window.sessionStorage.removeItem(SESSION_KEY);
      return null;
    }
    return session;
  } catch {
    window.sessionStorage.removeItem(SESSION_KEY);
    return null;
  }
}

function persistSession(accessToken: string, expiresIn: number) {
  const session = { accessToken, expiresAt: Date.now() + Math.max(0, expiresIn) * 1000 };
  window.sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
  return session;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<CurrentUserResponse | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(() => readSession()?.accessToken ?? null);
  const accessTokenRef = useRef<string | null>(accessToken);
  const [isInitializing, setIsInitializing] = useState(() => Boolean(accessToken));
  const [isSigningIn, setIsSigningIn] = useState(false);

  const signIn = useCallback(async (credentials: LoginRequest) => {
    setIsSigningIn(true);
    try {
      let session: LoginResponse;
      try {
        session = await authApi.login(credentials);
      } catch (error) {
        if (axios.isAxiosError(error) && error.response) {
          if (error.response.status === 401) {
            throw new Error('The email or password was not accepted.');
          }
          if (error.response.status === 403) {
            throw new Error('This account is not authorized to sign in. Contact an administrator.');
          }
          throw new Error('The sign-in service returned an error. Please try again.');
        }
        throw new Error('We could not connect to the sign-in service. Try again.');
      }

      accessTokenRef.current = session.accessToken;
      setAccessToken(session.accessToken);
      persistSession(session.accessToken, session.expiresIn);
      try {
        setUser(await authApi.currentUser());
      } catch {
        throw new Error('Sign in succeeded, but your profile could not be loaded. Please try again.');
      }
    } catch (error) {
      accessTokenRef.current = null;
      setAccessToken(null);
      setUser(null);
      window.sessionStorage.removeItem(SESSION_KEY);
      throw error instanceof Error ? error : new Error('We could not sign you in. Please try again.');
    } finally {
      setIsSigningIn(false);
    }
  }, []);

  const signOut = useCallback(() => {
    accessTokenRef.current = null;
    setAccessToken(null);
    setUser(null);
    window.sessionStorage.removeItem(SESSION_KEY);
  }, []);

  useEffect(() => {
    setTokenProvider(() => accessTokenRef.current);
    setUnauthorizedHandler(signOut);
    let active = true;
    if (accessTokenRef.current) {
      authApi.currentUser()
        .then((currentUser) => { if (active) setUser(currentUser); })
        .catch(() => {
          if (active) {
            accessTokenRef.current = null;
            setAccessToken(null);
            setUser(null);
            window.sessionStorage.removeItem(SESSION_KEY);
          }
        })
        .finally(() => { if (active) setIsInitializing(false); });
    } else {
      setIsInitializing(false);
    }
    return () => {
      active = false;
      setTokenProvider(() => null);
      setUnauthorizedHandler(undefined);
    };
  }, [signOut]);

  const value = useMemo(() => ({
    user,
    accessToken,
    isAuthenticated: Boolean(accessToken && user),
    isInitializing,
    isSigningIn,
    signIn,
    signOut,
  }), [user, accessToken, isInitializing, isSigningIn, signIn, signOut]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
}
