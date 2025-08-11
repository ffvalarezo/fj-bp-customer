import { IAzureConfig, InteractionType } from '@pichincha/angular-sdk/auth';
import { environment } from 'src/environments/environment';

export const azureConfig: IAzureConfig = {
    client_id: environment.authProvider.clientId,
    tenant_id: environment.authProvider.tenantId,
    redirect_url:  environment.authProvider.redirectUrl,
    interactionType: InteractionType.Redirect,
    secretKey: environment.storage.key ,
  };
