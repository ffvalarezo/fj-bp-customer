export interface LinkItem {
  code: string;
  label: string;
  icon?: string;
  url: string;
  isActive: boolean;
  type: 'item' | 'link' | 'button';
}
