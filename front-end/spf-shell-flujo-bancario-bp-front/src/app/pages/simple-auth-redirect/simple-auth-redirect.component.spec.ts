import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SimpleAuthRedirectComponent } from "./simple-auth-redirect.component";
import { LoginService } from "../../services/login.service";

describe("SimpleAuthRedirectComponent", () => {
  let component: SimpleAuthRedirectComponent;
  let fixture: ComponentFixture<SimpleAuthRedirectComponent>;

  let loginServiceMock: jest.Mocked<LoginService>;

  beforeEach(async () => {
    loginServiceMock = {
      getLoadingStorage: () => true,
      detectActivateAccount: jest.fn(),
      activateAccount: jest.fn()
    } as unknown as jest.Mocked<LoginService>;


    await TestBed.configureTestingModule({
      imports: [SimpleAuthRedirectComponent],
      providers: [{ provide: LoginService, useValue: loginServiceMock }]
    }).compileComponents();

    fixture = TestBed.createComponent(SimpleAuthRedirectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
    expect(loginServiceMock.detectActivateAccount).toBeCalled()
  });

  it("should execute handlerLogin and axecute activate account", () => {
    component.handlerLogin()
    expect(loginServiceMock.activateAccount).toBeCalled()
  });
});
