package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.Map;

import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.web.SaleOrderLineController;
import com.axelor.exception.AxelorException;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;

public class SaleOrderLineGstController extends SaleOrderLineController {
  
  @Override
  public void compute(ActionRequest request, ActionResponse response) {
	  Context context = request.getContext();

	    SaleOrderLine saleOrderLine = context.asType(SaleOrderLine.class);

	    SaleOrder saleOrder = Beans.get(SaleOrderLineService.class).getSaleOrder(context);

	    try {
	      compute(response, saleOrder, saleOrderLine);
	    } catch (Exception e) {
	      TraceBackService.trace(response, e);
	    }
  }
  
  
  private void compute(ActionResponse response, SaleOrder saleOrder, SaleOrderLine orderLine)
	      throws AxelorException {

	    Map<String, BigDecimal> map =
	        Beans.get(SaleOrderLineService.class).computeValues(saleOrder, orderLine);

	    map.put("price", orderLine.getPrice());
	    map.put("inTaxPrice", orderLine.getInTaxPrice());
	    map.put("companyCostPrice", orderLine.getCompanyCostPrice());
	    map.put("discountAmount", orderLine.getDiscountAmount());

	    response.setValues(map);
	    response.setAttr(
	        "priceDiscounted",
	        "hidden",
	        map.getOrDefault("priceDiscounted", BigDecimal.ZERO)
	                .compareTo(saleOrder.getInAti() ? orderLine.getInTaxPrice() : orderLine.getPrice())
	            == 0);
	    if(map.get("stateNotFound")!=null) {
	    	response.setFlash(
	            I18n.get(
	                "Both the saleOrder address & company address should contain STATE information without which it is not possible to calculate the GST"));
	      
	    }
	  }
}
