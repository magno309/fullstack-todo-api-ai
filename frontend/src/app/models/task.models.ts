export interface Task {
  id: number;
  title: string;
  description: string | null;
  completed: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTaskRequest {
  title: string;
  description: string | null;
}

export interface UpdateTaskRequest {
  title: string;
  description: string | null;
  completed: boolean;
}
