export interface ApiRestError {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  [key: string]: any;
}
