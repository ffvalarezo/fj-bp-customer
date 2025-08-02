import { authGuard } from "../guards/auth.guard";
import { DashboardComponent } from "../layouts/dashboard/dashboard.component";
import { FullpageWithHeaderComponent } from "../layouts/fullpage-with-header/fullpage-with-header.component";

export const ListLayout: any = {
    dashboard: DashboardComponent,
    fullPageHeader: FullpageWithHeaderComponent,
    empty: "",
};

export const Guards: any = {
    logger: authGuard,
    auth: [authGuard],
    default: [authGuard],
};
