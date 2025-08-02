import { ComponentFixture, TestBed } from "@angular/core/testing";
import { AppComponent } from "./app.component";
import { RouterOutlet } from "@angular/router";
import { LoginService } from "./services/login.service";

describe("AppComponent", () => {
  let fixture: ComponentFixture<AppComponent>;
  let compiled: HTMLElement;
  let component: AppComponent;

  let loginServiceMock: jest.Mocked<LoginService>;

  beforeEach(async () => {
    loginServiceMock = {
      initialConfig: jest.fn(),
    } as unknown as jest.Mocked<LoginService>;

    await TestBed.configureTestingModule({
      imports: [AppComponent, RouterOutlet],
      providers: [{ provide: LoginService, useValue: loginServiceMock }],
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
    expect(loginServiceMock.initialConfig).toBeCalled()
  });
});
