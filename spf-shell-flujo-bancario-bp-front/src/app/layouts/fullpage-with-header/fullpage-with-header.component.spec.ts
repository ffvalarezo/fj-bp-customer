import { ComponentFixture, TestBed } from "@angular/core/testing";
import { JwtAuthService } from "../../services/jwt-auth.service";
import { FullpageWithHeaderComponent } from "./fullpage-with-header.component";

describe("FullpageWithHeaderComponent", () => {
  let component: FullpageWithHeaderComponent;
  let compiled: HTMLElement;
  let fixture: ComponentFixture<FullpageWithHeaderComponent>;
  let mockJwtAuthService: any;

  beforeEach(async () => {
    mockJwtAuthService = {
      getUserInfo: jest.fn().mockReturnValue({ name: 'Test', lastName: 'User' }),
      logout: jest.fn(),
      isAuthenticated: jest.fn().mockReturnValue(true)
    };

    await TestBed.configureTestingModule({
      imports: [FullpageWithHeaderComponent],
      providers: [
        { provide: JwtAuthService, useValue: mockJwtAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FullpageWithHeaderComponent);
    compiled = fixture.nativeElement as HTMLElement;
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
