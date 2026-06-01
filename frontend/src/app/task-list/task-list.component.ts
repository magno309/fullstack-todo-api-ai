import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { CreateTaskRequest, Task, UpdateTaskRequest } from '../models/task.models';
import { TaskApiService } from '../services/task-api.service';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css'
})
export class TaskListComponent implements OnInit {
  private readonly taskApiService = inject(TaskApiService);
  private readonly formBuilder = inject(FormBuilder);

  readonly taskForm = this.formBuilder.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(120)]],
    description: ['', [Validators.maxLength(500)]]
  });

  tasks: Task[] = [];
  loading = false;
  submitting = false;
  errorMessage: string | null = null;
  private readonly updatingTaskIds = new Set<number>();

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    this.errorMessage = null;

    this.taskApiService.getTasks()
      .pipe(finalize(() => {
        this.loading = false;
      }))
      .subscribe({
        next: (tasks) => {
          this.tasks = tasks;
        },
        error: (error: Error) => {
          this.errorMessage = error.message;
        }
      });
  }

  createTask(): void {
    if (this.taskForm.invalid) {
      this.taskForm.markAllAsTouched();
      return;
    }

    const value = this.taskForm.getRawValue();
    const payload: CreateTaskRequest = {
      title: value.title.trim(),
      description: value.description.trim() ? value.description.trim() : null
    };

    this.submitting = true;
    this.errorMessage = null;

    this.taskApiService.createTask(payload)
      .pipe(finalize(() => {
        this.submitting = false;
      }))
      .subscribe({
        next: (created) => {
          this.tasks = [created, ...this.tasks];
          this.taskForm.reset({ title: '', description: '' });
        },
        error: (error: Error) => {
          this.errorMessage = error.message;
        }
      });
  }

  toggleComplete(task: Task): void {
    if (this.updatingTaskIds.has(task.id)) {
      return;
    }

    const originalTask = { ...task };
    const toggledTask: Task = {
      ...task,
      completed: !task.completed
    };

    this.tasks = this.tasks.map((current) => current.id === task.id ? toggledTask : current);

    const payload: UpdateTaskRequest = {
      title: toggledTask.title,
      description: toggledTask.description,
      completed: toggledTask.completed
    };

    this.errorMessage = null;
    this.updatingTaskIds.add(task.id);
    this.taskApiService.updateTask(task.id, payload)
      .pipe(finalize(() => {
        this.updatingTaskIds.delete(task.id);
      }))
      .subscribe({
        next: (updated) => {
          this.tasks = this.tasks.map((current) => current.id === updated.id ? updated : current);
        },
        error: (error: Error) => {
          this.tasks = this.tasks.map((current) => current.id === originalTask.id ? originalTask : current);
          this.errorMessage = error.message;
        }
      });
  }

  deleteTask(taskId: number): void {
    if (this.updatingTaskIds.has(taskId)) {
      return;
    }

    this.errorMessage = null;
    this.taskApiService.deleteTask(taskId).subscribe({
      next: () => {
        this.tasks = this.tasks.filter((task) => task.id !== taskId);
      },
      error: (error: Error) => {
        this.errorMessage = error.message;
      }
    });
  }

  trackByTaskId(_: number, task: Task): number {
    return task.id;
  }

  isTaskUpdating(taskId: number): boolean {
    return this.updatingTaskIds.has(taskId);
  }

  get titleControl() {
    return this.taskForm.controls.title;
  }

  get descriptionControl() {
    return this.taskForm.controls.description;
  }
}
