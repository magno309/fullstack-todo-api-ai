import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NEVER, of, throwError } from 'rxjs';
import { TaskListComponent } from './task-list.component';
import { TaskApiService } from '../services/task-api.service';

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;

  const taskApiServiceMock = {
    getTasks: () => of([]),
    createTask: () => of({
      id: 1,
      title: 'New task',
      description: null,
      completed: false,
      createdAt: '2026-05-26T12:00:00Z',
      updatedAt: '2026-05-26T12:00:00Z'
    }),
    updateTask: (_id: number, payload: { title: string; description: string | null; completed: boolean }) =>
      of({
        id: 1,
        title: payload.title,
        description: payload.description,
        completed: payload.completed,
        createdAt: '2026-05-26T12:00:00Z',
        updatedAt: '2026-05-26T12:05:00Z'
      }),
    deleteTask: (_id: number) => of(void 0)
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskListComponent],
      providers: [
        { provide: TaskApiService, useValue: taskApiServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load tasks on init', () => {
    expect(component.tasks).toEqual([]);
    expect(component.loading).toBe(false);
  });

  it('should add created task to list', () => {
    component.taskForm.setValue({
      title: 'New task',
      description: ''
    });

    component.createTask();

    expect(component.tasks.length).toBe(1);
    expect(component.tasks[0].title).toBe('New task');
  });

  it('should handle loading error', () => {
    const failingService = {
      ...taskApiServiceMock,
      getTasks: () => throwError(() => new Error('Network error'))
    };

    TestBed.resetTestingModule();

    TestBed.configureTestingModule({
      imports: [TaskListComponent],
      providers: [
        { provide: TaskApiService, useValue: failingService }
      ]
    });

    const failingFixture = TestBed.createComponent(TaskListComponent);
    const failingComponent = failingFixture.componentInstance;
    failingFixture.detectChanges();

    expect(failingComponent.errorMessage).toBe('Network error');
  });

  it('should block delete while the same task is updating', () => {
    const task = {
      id: 1,
      title: 'In progress task',
      description: null,
      completed: false,
      createdAt: '2026-05-26T12:00:00Z',
      updatedAt: '2026-05-26T12:00:00Z'
    };

    let deleteCalls = 0;
    const guardedService = {
      ...taskApiServiceMock,
      getTasks: () => of([task]),
      updateTask: () => NEVER,
      deleteTask: (_id: number) => {
        deleteCalls += 1;
        return of(void 0);
      }
    };

    TestBed.resetTestingModule();

    TestBed.configureTestingModule({
      imports: [TaskListComponent],
      providers: [
        { provide: TaskApiService, useValue: guardedService }
      ]
    });

    const guardedFixture = TestBed.createComponent(TaskListComponent);
    const guardedComponent = guardedFixture.componentInstance;
    guardedFixture.detectChanges();

    guardedComponent.toggleComplete(task);
    expect(guardedComponent.isTaskUpdating(1)).toBe(true);

    guardedComponent.deleteTask(1);
    expect(deleteCalls).toBe(0);
  });
});
