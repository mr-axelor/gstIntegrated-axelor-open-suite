package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineTaxService;
import com.axelor.apps.supplychain.service.SaleOrderComputeServiceSupplychainImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;

public class SaleOrderComputeServiceGstImpl extends SaleOrderComputeServiceSupplychainImpl{
    
	@Inject
	public SaleOrderComputeServiceGstImpl(SaleOrderLineService saleOrderLineService,
			SaleOrderLineTaxService saleOrderLineTaxService) {
		super(saleOrderLineService, saleOrderLineTaxService);
		// TODO Auto-generated constructor stub
	}
	
	  @Override
	  public SaleOrder computeSaleOrder(SaleOrder saleOrder) throws AxelorException {
	    
	    saleOrder = super.computeSaleOrder(saleOrder);
	    List<SaleOrderLine> saleOrderLineList = saleOrder.getSaleOrderLineList();
	    BigDecimal igst =
	        saleOrderLineList.stream().map(i -> i.getIgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
	    BigDecimal cgst =
	    		saleOrderLineList.stream().map(i -> i.getCgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
	    BigDecimal sgst =
	    		saleOrderLineList.stream().map(i -> i.getSgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
	    saleOrder.setNetCgst(cgst);
	    saleOrder.setNetSgst(sgst);
	    saleOrder.setNetIgst(igst);
	    return saleOrder;
	  }
}
