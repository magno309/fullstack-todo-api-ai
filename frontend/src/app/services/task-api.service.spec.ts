import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TaskApiService } from './task-api.service';

describe('TaskApiService', () => {
  let service: TaskApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TaskApiService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(TaskApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should map tasks from API response', () => {
    const task = {
      id: 1,
      title: 'Task A',
      description: 'Description',
      completed: false,
      createdAt: '2026-05-26T12:00:00Z',
      updatedAt: '2026-05-26T12:00:00Z'
    };

    service.getTasks().subscribe((tasks) => {
      expect(tasks.length).toBe(1);
      expect(tasks[0].title).toBe('Task A');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/tasks');
    expect(req.request.method).toBe('GET');
    req.flush({
      message: 'Tasks retrieved',
      status: 200,
      timestamp: '2026-05-26T12:00:00Z',
      data: [task]
    });
  });

  it('should expose validation messages from API', () => {
    service.createTask({ title: '', description: null }).subscribe({
      next: () => {
        throw new Error('Expected request to fail');
      },
      error: (error: Error) => {
        expect(error.message).toContain('title is required');
      }
    });

    const req = httpMock.expectOne('http://localhost:8080/api/tasks');
    req.flush(
      {
        message: 'Validation failed',
        status: 400,
        timestamp: '2026-05-26T12:00:00Z',
        validationDetails: {
          title: 'title is required'
        }
      },
      { status: 400, statusText: 'Bad Request' }
    );
  });
});
