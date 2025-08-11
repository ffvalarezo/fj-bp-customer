import { ComponentFixture, TestBed } from "@angular/core/testing";
import { AppComponent } from "./app.component";
import { Router } from "@angular/router";
import { LoginService } from "./services/login.service";

describe("AppComponent", () => {
  let fixture: ComponentFixture<AppComponent>;
  let compiled: HTMLElement;
  let component: AppComponent;

  let loginServiceMock: jest.Mocked<LoginService>;
  let routerMock: any;

  beforeEach(async () => {
    loginServiceMock = {
      initialConfig: jest.fn(),
    } as unknown as jest.Mocked<LoginService>;

    routerMock = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      providers: [
        { provide: LoginService, useValue: loginServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    compiled = fixture.nativeElement as HTMLElement;
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it("should create the app", () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
    expect(loginServiceMock.initialConfig).toHaveBeenCalled();
  });

  it("should have as title 'angular-container'", () => {
    expect(component.title).toEqual("angular-container");
  });

  it("should inject services correctly", () => {
    expect(component.router).toBeDefined();
    expect(component.loginService).toBeDefined();
  });
});
