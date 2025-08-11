import { ComponentFixture, TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { of } from "rxjs";
import { JwtAuthService } from "../../services/jwt-auth.service";
import { FullpageWithHeaderComponent } from "./fullpage-with-header.component";

describe("FullpageWithHeaderComponent", () => {
  let component: FullpageWithHeaderComponent;
  let compiled: HTMLElement;
  let fixture: ComponentFixture<FullpageWithHeaderComponent>;
  let mockJwtAuthService: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockJwtAuthService = {
      getUserInfo: jest.fn().mockReturnValue({
        username: "testuser",
        name: "Test",
        lastName: "User",
      }),
      logout: jest.fn(),
      isAuthenticated: jest.fn().mockReturnValue(true),
      isAuthenticated$: of(true),
    };

    mockRouter = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [FullpageWithHeaderComponent],
      providers: [
        { provide: JwtAuthService, useValue: mockJwtAuthService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FullpageWithHeaderComponent);
    compiled = fixture.nativeElement as HTMLElement;
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should set user info when authenticated", () => {
    expect(component.userInfo).toBeTruthy();
    expect(component.userInfo?.name).toBe("testuser");
  });

  it("should handle close session", () => {
    component.onCloseSession();
    expect(mockJwtAuthService.logout).toHaveBeenCalled();
  });

  it("should handle menu item click", () => {
    const mockEvent = {
      detail: { value: "/test-route" },
    } as CustomEvent<any>;

    component.handleClickMenuItem(mockEvent);

    expect(mockRouter.navigate).toHaveBeenCalledWith(["/test-route"]);
  });

  it("should have menu items defined", () => {
    expect(component.menuItems).toBeDefined();
    expect(component.menuItems.length).toBeGreaterThan(0);
  });

  it("should unsubscribe on destroy", () => {
    const unsubscribeSpy = jest.spyOn(component["subscription"], "unsubscribe");
    component.ngOnDestroy();
    expect(unsubscribeSpy).toHaveBeenCalled();
  });
});
