import { ComponentFixture, TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { of } from "rxjs";
import { JwtAuthService } from "../../services/jwt-auth.service";

import { DashboardComponent } from "./dashboard.component";

describe("DashboardComponent", () => {
  let component: DashboardComponent;
  let compiled: HTMLElement;
  let fixture: ComponentFixture<DashboardComponent>;
  let mockJwtAuthService: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockJwtAuthService = {
      getUserInfo: jest.fn().mockReturnValue({
        name: "Test",
        lastName: "User",
        username: "testuser",
      }),
      logout: jest.fn(),
      isAuthenticated: jest.fn().mockReturnValue(true),
      isAuthenticated$: of(true),
    };

    mockRouter = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: JwtAuthService, useValue: mockJwtAuthService },
        { provide: Router, useValue: mockRouter },
      ],
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
    expect(component.userInfo).toBeTruthy();
    expect(component.userInfo.name).toBe("Test");
    expect(component.isAuthenticated).toBe(true);
  });

  it("should navigate to route", () => {
    component.navigateTo("/test-route");
    expect(mockRouter.navigate).toHaveBeenCalledWith(["/test-route"]);
  });

  it("should handle card action", () => {
    const mockMicrofrontend = { title: "Test", route: "/test" };
    const navigateSpy = jest.spyOn(component, "navigateTo");

    component.onCardAction(mockMicrofrontend, "Test Action");

    expect(navigateSpy).toHaveBeenCalledWith("/test");
  });

  it("should handle menu item click", () => {
    const mockEvent = {
      detail: { value: "/menu-route" },
    } as CustomEvent<any>;

    component.handleClickMenuItem(mockEvent);

    expect(mockRouter.navigate).toHaveBeenCalledWith(["/menu-route"]);
  });

  it("should have microfrontends defined", () => {
    expect(component.microfrontends).toBeDefined();
    expect(component.microfrontends.length).toBeGreaterThan(0);
  });

  it("should unsubscribe on destroy", () => {
    const unsubscribeSpy = jest.spyOn(component["subscription"], "unsubscribe");
    component.ngOnDestroy();
    expect(unsubscribeSpy).toHaveBeenCalled();
  });
});
