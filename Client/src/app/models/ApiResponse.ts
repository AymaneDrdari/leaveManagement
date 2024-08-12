export interface ApiResponse<T> {
  data: T;
  message: string;
  code: number;
  error: any;
  timestamp: string;
  totalElements: number;
  totalPages: number;
  currentPage: number;
}
