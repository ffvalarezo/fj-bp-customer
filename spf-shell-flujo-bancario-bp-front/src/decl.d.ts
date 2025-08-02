// Declaraciones de tipos para módulos remotos
declare module "mf_cliente_app/mf_cliente_app" {
  export const ClienteModule: any;
}
declare module "mf_movimiento_app/mf_movimiento_app" {
  export const MovimientoModule: any;
}
declare module "mf_transaccion_app/mf_transaccion_app" {
  export const TransaccionModule: any;
}

// Declaraciones para microfrontends
declare module "*/Module";
declare module "*/mf_cliente_app";
declare module "*/mf_movimiento_app";
declare module "*/mf_transaccion_app";
declare module "*/ClienteModule";
declare module "*/MovimientoModule";
declare module "*/TransaccionModule";
