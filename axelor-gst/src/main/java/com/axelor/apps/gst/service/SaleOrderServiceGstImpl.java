package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.axelor.apps.account.db.Account;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.InvoiceLineTax;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.invoice.InvoiceService;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.businessproject.service.app.AppBusinessProjectService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.SaleOrderLineTax;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.service.saleorder.SaleOrderWorkflowServiceImpl;
import com.axelor.apps.stock.db.repo.StockMoveRepository;
import com.axelor.apps.supplychain.exception.IExceptionMessage;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class SaleOrderServiceGstImpl extends SaleOrderInvoiceProjectServiceImpl {
	
	@Inject
	  public SaleOrderServiceGstImpl(AppBaseService appBaseService, AppSupplychainService appSupplychainService,
			SaleOrderRepository saleOrderRepo, InvoiceRepository invoiceRepo, InvoiceService invoiceService,
			AppBusinessProjectService appBusinessProjectService, StockMoveRepository stockMoveRepository,
			SaleOrderLineService saleOrderLineService, SaleOrderWorkflowServiceImpl saleOrderWorkflowServiceImpl) {
		super(appBaseService, appSupplychainService, saleOrderRepo, invoiceRepo, invoiceService, appBusinessProjectService,
				stockMoveRepository, saleOrderLineService, saleOrderWorkflowServiceImpl);
		// TODO Auto-generated constructor stub
	}

	@Override
	  public Invoice createInvoice(
	      SaleOrder saleOrder,
	      List<SaleOrderLine> saleOrderLineList,
	      Map<Long, BigDecimal> qtyToInvoiceMap)
	      throws AxelorException {
			Invoice invoice =  super.createInvoice(saleOrder, saleOrderLineList, qtyToInvoiceMap);
			List<InvoiceLine> invoiceLines = invoice.getInvoiceLineList();
			BigDecimal netIgst = BigDecimal.ZERO;
			BigDecimal netCgst = BigDecimal.ZERO;
			BigDecimal netSgst = BigDecimal.ZERO;
			for(InvoiceLine invoiceLine: invoiceLines) {
				netIgst = netIgst.add(invoiceLine.getIgst());
				netCgst = netCgst.add(invoiceLine.getCgst());
				netSgst = netSgst.add(invoiceLine.getSgst());
			}
			invoice.setNetCgst(netCgst);
			invoice.setNetIgst(netIgst);
			invoice.setNetSgst(netSgst);
			return invoice;
	  }
	
	@Override
	  public List<InvoiceLine> createInvoiceLine(
	      Invoice invoice, SaleOrderLine saleOrderLine, BigDecimal qtyToInvoice)
	      throws AxelorException {
	    List<InvoiceLine> invoiceLines = super.createInvoiceLine(invoice, saleOrderLine, qtyToInvoice);

	    //if (!Beans.get(AppBusinessProjectService.class).isApp("business-project")) {
	    //  return invoiceLines;
	    //}

	    //for (InvoiceLine invoiceLine : invoiceLines) {
	    //  if (saleOrderLine != null) {
	    //    invoiceLine.setProject(saleOrderLine.getProject());
	    //  }
	   // }
	  for (InvoiceLine invoiceLine : invoiceLines) {
	      if (saleOrderLine != null) {
	    	  if(saleOrderLine.getIgst().compareTo(BigDecimal.ZERO) > 0 ) {
	        invoiceLine.setIgst(invoiceLine.getTaxLine().getValue().multiply(invoiceLine.getExTaxTotal()));
	        }else if(saleOrderLine.getCgst().compareTo(BigDecimal.ZERO) > 0 ){
		        invoiceLine.setCgst(invoiceLine.getTaxLine().getValue().multiply(invoiceLine.getExTaxTotal()).divide(BigDecimal.valueOf(2)));
		        invoiceLine.setSgst(invoiceLine.getTaxLine().getValue().multiply(invoiceLine.getExTaxTotal()).divide(BigDecimal.valueOf(2)));
	        }
	      }
	    }
	    return invoiceLines;
	  }
	
	
	
	@Override
	  @Transactional(rollbackOn = {Exception.class})
	  public Invoice generateAdvancePayment(
	      SaleOrder saleOrder, BigDecimal amountToInvoice, boolean isPercent) throws AxelorException {
	    Invoice invoice = super.generateAdvancePayment(saleOrder, amountToInvoice, isPercent);
	    List<InvoiceLine> invoiceLines = invoice.getInvoiceLineList();
	    BigDecimal netIgst = BigDecimal.ZERO;
		BigDecimal netCgst = BigDecimal.ZERO;
		BigDecimal netSgst = BigDecimal.ZERO;
	    List<InvoiceLineTax> invoiceLineTaxList =  invoice.getInvoiceLineTaxList();
	    BigDecimal gstTax = BigDecimal.ZERO;
	    System.err.println("problemmmmmmmmmmmm not found");
	    for(InvoiceLineTax invoiceLineTax: invoiceLineTaxList) {
		    System.err.println(invoiceLineTax.toString());

	    	if(invoiceLineTax.getTaxLine().getTax().getCode().contains("GST")) {
	    		gstTax = gstTax.add(invoiceLineTax.getTaxTotal());
	    		System.err.println(gstTax);
	    	}
	    }
	    if(invoice.getCompany().getAddress().getState() == invoice.getAddress().getState()) {
	    	netCgst = gstTax.divide(BigDecimal.valueOf(2));
	    	netSgst = netCgst;
	    }else {
	    	netIgst = gstTax;
	    }
	    invoice.setNetCgst(netCgst);
	    invoice.setNetIgst(netIgst);
	    invoice.setNetSgst(netSgst);
	    return invoiceRepo.save(invoice);
	  }
}
