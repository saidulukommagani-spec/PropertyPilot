import { serviceRequestApi } from '../../../api/serviceRequestApi';

export const ServiceRequestManagementService = {
  list: serviceRequestApi.list,
  details: serviceRequestApi.getDetails,
  customers: serviceRequestApi.listCustomers,
  properties: serviceRequestApi.listProperties,
  quotes: serviceRequestApi.listQuotes,
  create: serviceRequestApi.createRequest,
  update: serviceRequestApi.updateRequest,
};
