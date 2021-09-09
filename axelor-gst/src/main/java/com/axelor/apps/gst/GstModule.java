package com.axelor.apps.gst;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.report.IReport;
import com.axelor.apps.account.service.invoice.print.InvoicePrintServiceImpl;
import com.axelor.apps.account.web.InvoiceLineController;
import com.axelor.apps.base.web.ProductController;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.apps.gst.service.GstInvoicePrintServiceImpl;
import com.axelor.apps.gst.service.GstProductController;
import com.axelor.apps.gst.service.InvoiceLineGstController;
import com.axelor.apps.gst.service.InvoiceServiceGstImpl;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(InvoiceServiceManagementImpl.class).to(InvoiceServiceGstImpl.class);
    bind(InvoiceLineController.class).to(InvoiceLineGstController.class);
    bind(InvoicePrintServiceImpl.class).to(GstInvoicePrintServiceImpl.class);
    bind(ProductController.class).to(GstProductController.class);
  }
}
