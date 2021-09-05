package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.account.service.move.MoveToolService;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;

public class InvoiceServiceGstImpl extends InvoiceServiceManagementImpl{

	  @Inject
	  public InvoiceServiceGstImpl(
	      ValidateFactory validateFactory,
	      VentilateFactory ventilateFactory,
	      CancelFactory cancelFactory,
	      AlarmEngineService<Invoice> alarmEngineService,
	      InvoiceRepository invoiceRepo,
	      AppAccountService appAccountService,
	      PartnerService partnerService,
	      InvoiceLineService invoiceLineService,
	      AccountConfigService accountConfigService,
	      MoveToolService moveToolService) {
	    super(
	        validateFactory,
	        ventilateFactory,
	        cancelFactory,
	        alarmEngineService,
	        invoiceRepo,
	        appAccountService,
	        partnerService,
	        invoiceLineService,
	        accountConfigService,
	        moveToolService);
	  }
	  
	  @Override
	  public Invoice compute(final Invoice invoice) throws AxelorException {
		  Invoice computedInvoice = super.compute(invoice);
		  List<InvoiceLine> invoiceLineList = computedInvoice.getInvoiceLineList();
		  BigDecimal igst = invoiceLineList.stream().map(i -> i.getIgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
		  BigDecimal cgst = invoiceLineList.stream().map(i -> i.getCgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
		  BigDecimal sgst = invoiceLineList.stream().map(i -> i.getSgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
		  computedInvoice.setNetCgst(cgst);
		  computedInvoice.setNetSgst(sgst);
		  computedInvoice.setNetIgst(igst);
		  return computedInvoice;
	  }

}
