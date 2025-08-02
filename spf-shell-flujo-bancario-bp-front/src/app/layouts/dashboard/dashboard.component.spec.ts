import { ComponentFixture, TestBed } from "@angular/core/testing";
import { JwtAuthService } from "../../services/jwt-auth.service";

import { DashboardComponent } from "./dashboard.component";

describe("DashboardComponent", () => {
  let component: DashboardComponent;
  let compiled: HTMLElement;
  let fixture: ComponentFixture<DashboardComponent>;
  let mockJwtAuthService: any;

  beforeEach(async () => {
    mockJwtAuthService = {
      getUserInfo: jest.fn().mockReturnValue({ name: 'Test', lastName: 'User' }),
      logout: jest.fn(),
      isAuthenticated: jest.fn().mockReturnValue(true)
    };

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: JwtAuthService, useValue: mockJwtAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    compiled = fixture.nativeElement as HTMLElement;
    component = fixture.componentInstance;

    fixture.detectChanges();
  });


  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should have user info when authenticated", () => {
    component.userInfo = { name: 'Test User' };
    expect(component.userInfo).toBeTruthy();
    expect(component.userInfo.name).toBe('Test User');
  });
});
