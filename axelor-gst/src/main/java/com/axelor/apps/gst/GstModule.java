package com.axelor.apps.gst;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.service.invoice.generator.InvoiceGenerator;
import com.axelor.apps.account.web.InvoiceLineController;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.apps.gst.service.InvoiceLineGstController;
import com.axelor.apps.gst.service.InvoiceServiceGstImpl;

public class GstModule extends AxelorModule {
	
	  @Override
	  protected void configure() {
		  bind(InvoiceServiceManagementImpl.class).to(InvoiceServiceGstImpl.class);
		  bind(InvoiceLineController.class).to(InvoiceLineGstController.class);
	  }
}
