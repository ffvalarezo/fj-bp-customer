import { ComponentFixture, TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { of } from "rxjs";
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
      url: "/home",
    };

    await TestBed.configureTestingModule({
      imports: [HeaderComponent],
      providers: [
        { provide: JwtAuthService, useValue: mockJwtAuthService },
        { provide: Router, useValue: mockRouter },
      ],
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
    const spyLogout = jest.spyOn(component, "handleCloseSession");

    component.handleCloseSession();

    expect(spyLogout).toHaveBeenCalled();
    expect(mockJwtAuthService.logout).toHaveBeenCalled();
  });

  it("should set current user on init", () => {
    expect(component.currentUser).toBeTruthy();
    expect(component.currentUser?.name).toBe("testuser");
  });

  it("should navigate to microfrontend", () => {
    component.goToMicrofrontend("cliente");
    expect(mockRouter.navigate).toHaveBeenCalledWith(["/cliente"]);
  });

  it("should navigate to item and emit event", () => {
    const mockItem = { label: "Inicio" };
    const emitSpy = jest.spyOn(component.onItemSelected, "emit");

    component.navigateToItem(mockItem);

    expect(mockRouter.navigate).toHaveBeenCalledWith(["/home"]);
    expect(emitSpy).toHaveBeenCalled();
  });

  it("should have navigation items defined", () => {
    expect(component.navigationItems).toBeDefined();
    expect(component.navigationItems.length).toBeGreaterThan(0);
  });

  it("should get display user", () => {
    const displayUser = component.displayUser;
    expect(displayUser).toBeTruthy();
    expect(displayUser?.name).toBe("testuser");
  });

  it("should unsubscribe on destroy", () => {
    const unsubscribeSpy = jest.spyOn(component["subscription"], "unsubscribe");
    component.ngOnDestroy();
    expect(unsubscribeSpy).toHaveBeenCalled();
  });
});
