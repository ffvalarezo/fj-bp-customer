import { TestBed } from '@angular/core/testing';

import { CrudService } from './crud.service';
import { HttpService } from '@pichincha/angular-sdk/http';
import { CreateDtoProducto } from '../types/product.type';

describe('CrudService', () => {
  let service: CrudService;
  let apiUrl: string = 'products';

  let mockHttpService = {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CrudService,
        { provide: HttpService, useValue: mockHttpService },
      ],
    });
    service = TestBed.inject(CrudService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('Get', (done) => {
    mockHttpService.get.mockResolvedValue([]);

    service.getAll().then(() => {
      done();
      expect(mockHttpService.get).toHaveBeenCalledWith(apiUrl);
    });
  });

  it('getById', (done) => {
    mockHttpService.get.mockReturnValue(Promise.resolve({}));

    service.getById(1).then(() => {
      done();
      expect(mockHttpService.get).toHaveBeenCalledWith(`${apiUrl}/1`);
    });
  });

  it('create', (done) => {
    mockHttpService.post.mockResolvedValue({});

    service.create({} as CreateDtoProducto).then(() => {
      done();
      expect(mockHttpService.post).toHaveBeenCalledWith(`${apiUrl}/add`, {});
    });
  });

  it('update', (done) => {
    mockHttpService.put.mockResolvedValue({});

    service.update(1, {} as CreateDtoProducto).then(() => {
      done();
      expect(mockHttpService.put).toHaveBeenCalledWith(`${apiUrl}/1`, {});
    });
  });

  it('delete', (done) => {
    mockHttpService.delete.mockResolvedValue({});

    service.delete(1).then(() => {
      done();
      expect(mockHttpService.delete).toHaveBeenCalledWith(`${apiUrl}/1`);
    });
  });
});
