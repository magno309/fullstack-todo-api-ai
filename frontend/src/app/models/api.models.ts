export interface ApiResponse<T> {
  message: string;
  status: number;
  timestamp: string;
  data: T;
}

export interface ApiErrorResponse {
  message: string;
  status: number;
  timestamp: string;
  validationDetails: Record<string, string>;
}
