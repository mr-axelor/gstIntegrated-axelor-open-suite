package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.Map;

import com.axelor.apps.businessproduction.service.SaleOrderLineBusinessProductionServiceImpl;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.exception.AxelorException;

public class SaleOrderLineServiceGstImpl extends SaleOrderLineBusinessProductionServiceImpl {

  @Override
  public Map<String, BigDecimal> computeValues(SaleOrder saleOrder, SaleOrderLine saleOrderLine)
      throws AxelorException {
    Map<String, BigDecimal> map = super.computeValues(saleOrder, saleOrderLine);
    
    BigDecimal cgst = BigDecimal.ZERO;
    BigDecimal igst = BigDecimal.ZERO;
    BigDecimal sgst = BigDecimal.ZERO;
    		
    if (saleOrderLine.getTaxLine() != null
        && saleOrderLine.getTaxLine().getTax().getCode().contains("GST")) {
      BigDecimal gstRate = saleOrderLine.getTaxLine().getValue();
      BigDecimal qty = saleOrderLine.getQty();
      BigDecimal unitPrice = saleOrderLine.getPrice();
      BigDecimal totalPriceWithoutTax = qty.multiply(unitPrice);
      BigDecimal totalGstTax = gstRate.multiply(totalPriceWithoutTax);
      if (saleOrder.getMainInvoicingAddress() != null
          && saleOrder.getCompany().getAddress() != null
          && saleOrder.getMainInvoicingAddress().getState() != null
          && saleOrder.getCompany().getAddress().getState() != null) {
        if (!(saleOrder.getMainInvoicingAddress().getState() != null
            && saleOrder
                .getMainInvoicingAddress()
                .getState()
                .equals(saleOrder.getCompany().getAddress().getState()))) {
          map.put("igst", totalGstTax);

        } else {
          map.put("cgst", totalGstTax.divide(BigDecimal.valueOf(2)));
          map.put("sgst", totalGstTax.divide(BigDecimal.valueOf(2)));
        }
      } else {
        map.put("taxLine", null);
        map.put("inTaxTotal", BigDecimal.ZERO);
        map.put("stateNotFound", BigDecimal.ONE);
      }
    }
    
    return map;
  }
}
