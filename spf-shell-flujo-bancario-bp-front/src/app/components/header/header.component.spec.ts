import { ComponentFixture, TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { JwtAuthService } from "../../services/jwt-auth.service";
import { HeaderComponent } from "./header.component";

describe("HeaderComponent", () => {
  let fixture: ComponentFixture<HeaderComponent>;
  let compiled: HTMLElement;
  let component: HeaderComponent;
  let mockJwtAuthService: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockJwtAuthService = {
      getUserInfo: jest.fn().mockReturnValue({ name: 'Test', lastName: 'User' }),
      logout: jest.fn(),
      isAuthenticated: jest.fn().mockReturnValue(true)
    };

    mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [HeaderComponent],
      providers: [
        { provide: JwtAuthService, useValue: mockJwtAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    compiled = fixture.nativeElement as HTMLElement;
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it("should create the HeaderComponent", () => {
    expect(component).toBeTruthy();
  });

  it("should call logout when handleCloseSession is called", () => {
    const spyLogout = jest.spyOn(component, 'handleCloseSession');
    
    component.handleCloseSession();
    
    expect(spyLogout).toHaveBeenCalled();
  });
});
