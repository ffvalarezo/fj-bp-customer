export interface SidebarItem {
  code: string;
  type: "item" | "collapse" | "group";
  classes?: string;
  label: string;
  url: string;
  icon?: string;
  isActive: boolean;
  hidden?: boolean;
  breadcrumbs?: boolean;
  children?: SidebarItem[];
}
