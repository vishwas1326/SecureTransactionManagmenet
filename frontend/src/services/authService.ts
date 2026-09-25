import api from './api';
import type {
  LoginRequest,
  LoginResponse
} from "../types/Auth";

export const login = async (request: LoginRequest): Promise<LoginResponse> => {
  const response = await api.post('/auth/login',request );
  return response.data;
};
