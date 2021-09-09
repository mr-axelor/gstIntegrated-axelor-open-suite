package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.web.InvoiceLineController;
import com.axelor.exception.AxelorException;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.math.BigDecimal;

public class InvoiceLineGstController extends InvoiceLineController {
  @Override
  public void compute(ActionRequest request, ActionResponse response) throws AxelorException {
    super.compute(request, response);
    Invoice invoice = request.getContext().getParent().asType(Invoice.class);
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    response.setValue("cgst", 0);
    response.setValue("sgst", 0);
    response.setValue("igst", 0);
    if (invoiceLine.getTaxLine() != null
        && invoiceLine.getTaxLine().getTax().getCode().contains("GST")) {
      BigDecimal gstRate = invoiceLine.getTaxLine().getValue();
      BigDecimal qty = invoiceLine.getQty();
      BigDecimal unitPrice = invoiceLine.getPrice();
      BigDecimal totalPriceWithoutTax = qty.multiply(unitPrice);
      BigDecimal totalGstTax = gstRate.multiply(totalPriceWithoutTax);
      if (invoice.getAddress() != null
          && invoice.getCompany().getAddress() != null
          && invoice.getAddress().getState() != null
          && invoice.getCompany().getAddress().getState() != null) {
        if (!(invoice.getAddress().getState() != null
            && invoice
                .getAddress()
                .getState()
                .equals(invoice.getCompany().getAddress().getState()))) {
          response.setValue("igst", totalGstTax);

        } else {
          response.setValue("cgst", totalGstTax.divide(BigDecimal.valueOf(2)));
          response.setValue("sgst", totalGstTax.divide(BigDecimal.valueOf(2)));
        }
      } else {
        response.setValue("taxLine", null);
        response.setValue("inTaxTotal", 0);
        // response.setError(I18n.get("cant"));
        response.setFlash(
            I18n.get(
                "Both the invoice address & company address should contain STATE information without which it is not possible to calculate the GST"));
      }
    }
  }
}
