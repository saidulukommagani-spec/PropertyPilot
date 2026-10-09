import { apiClient } from './client';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface CustomerRegistrationRequest {
  fullName: string;
  email: string;
  mobileNumber: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface CurrentUserResponse {
  userId: string;
  fullName: string;
  email: string;
  mobileNumber: string;
  status: string;
  /** Roles are returned by the backend and normalized by getUserRoles. */
  roles?: Array<string | { authority?: string; name?: string }>;
}

export const authApi = {
  async registerCustomer(request: CustomerRegistrationRequest): Promise<void> {
    await apiClient.post('/api/v1/users', request);
  },
  async login(request: LoginRequest): Promise<LoginResponse> {
    const { data } = await apiClient.post<LoginResponse>('/api/v1/auth/login', request);
    return data;
  },

  async currentUser(): Promise<CurrentUserResponse> {
    const { data } = await apiClient.get<CurrentUserResponse>('/api/v1/users/me');
    return data;
  },
};

export function getUserRoles(user: CurrentUserResponse | null): string[] {
  return (user?.roles ?? []).map((role) => {
    const value = typeof role === 'string' ? role : role.authority ?? role.name ?? '';
    return value.replace(/^ROLE_/, '').toUpperCase();
  });
}
