import { HttpClient } from "@angular/common/http";
import { TestBed } from "@angular/core/testing";
import { TranslateHttpLoader } from "@ngx-translate/http-loader";
import * as InternationalizationFile from "./internationalization.module";

describe("internationalization module", () => {
  let module: InternationalizationFile.InternationalizationModule;
  let http: HttpClient;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InternationalizationFile.InternationalizationModule],
    });
    module = TestBed.inject(
      InternationalizationFile.InternationalizationModule
    );
    http = TestBed.inject(HttpClient);
  });

  it("should create", () => {
    expect(module).toBeTruthy();
  });

  it("should call httpLoader", () => {
    const result = InternationalizationFile.HttpLoaderFactory(http);
    expect(result).toBeInstanceOf(TranslateHttpLoader);
  });
});
