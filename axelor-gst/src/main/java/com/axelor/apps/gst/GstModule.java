package com.axelor.apps.gst;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.web.InvoiceLineController;
import com.axelor.apps.businessproduction.service.SaleOrderLineBusinessProductionServiceImpl;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.apps.gst.service.InvoiceLineGstController;
import com.axelor.apps.gst.service.InvoiceServiceGstImpl;
import com.axelor.apps.gst.service.SaleOrderComputeServiceGstImpl;
import com.axelor.apps.gst.service.SaleOrderLineGstController;
import com.axelor.apps.gst.service.SaleOrderLineServiceGstImpl;
import com.axelor.apps.gst.service.SaleOrderServiceGstImpl;
import com.axelor.apps.sale.web.SaleOrderLineController;
import com.axelor.apps.supplychain.service.SaleOrderComputeServiceSupplychainImpl;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(InvoiceServiceManagementImpl.class).to(InvoiceServiceGstImpl.class);
    bind(InvoiceLineController.class).to(InvoiceLineGstController.class);
    bind(SaleOrderLineBusinessProductionServiceImpl.class).to(SaleOrderLineServiceGstImpl.class);
    bind(SaleOrderLineController.class).to(SaleOrderLineGstController.class);
    bind(SaleOrderComputeServiceSupplychainImpl.class).to(SaleOrderComputeServiceGstImpl.class);
    bind(SaleOrderInvoiceProjectServiceImpl.class).to(SaleOrderServiceGstImpl.class);
  }
}
