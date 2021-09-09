package com.axelor.apps.gst.service;

import com.axelor.apps.ReportFactory;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.AppGstRepository;
import com.axelor.apps.base.exceptions.IExceptionMessage;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.user.UserService;
import com.axelor.apps.base.web.ProductController;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.auth.db.User;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.base.Joiner;
import java.util.List;

public class GstProductController extends ProductController {

  @Override
  public void printProductCatalog(ActionRequest request, ActionResponse response)
      throws AxelorException {
    if (Beans.get(AppGstRepository.class).all().filter("self.code =? AND self.active =?", "gst",true).fetchOne() != null) {

      User user = Beans.get(UserService.class).getUser();

      int currentYear = Beans.get(AppBaseService.class).getTodayDateTime().getYear();
      String productIds = "";

      List<Integer> lstSelectedProduct = (List<Integer>) request.getContext().get("_ids");

      if (lstSelectedProduct != null) {
        productIds = Joiner.on(",").join(lstSelectedProduct);
      }

      String name = I18n.get("Product Catalog");

      String fileLink =
          ReportFactory.createReport("GstProductCatalog.rptdesign", name + "-${date}")
              .addParam("UserId", user.getId())
              .addParam("CurrYear", Integer.toString(currentYear))
              .addParam("ProductIds", productIds)
              .addParam("Locale", ReportSettings.getPrintingLocale(null))
              .addParam(
                  "Timezone",
                  user.getActiveCompany() != null ? user.getActiveCompany().getTimezone() : null)
              .generate()
              .getFileLink();

      // logger.debug("Printing " + name);

      response.setView(ActionView.define(name).add("html", fileLink).map());
    } else {
      super.printProductCatalog(request, response);
    }
  }

  @Override
  public void printProductSheet(ActionRequest request, ActionResponse response)
      throws AxelorException {
    if (Beans.get(AppGstRepository.class).all().filter("self.code =? AND self.active =?", "gst",true).fetchOne() != null) {
      try {
        Product product = request.getContext().asType(Product.class);
        User user = Beans.get(UserService.class).getUser();

        String name = I18n.get("Product") + " " + product.getCode();

        if (user.getActiveCompany() == null) {
          throw new AxelorException(
              TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
              I18n.get(IExceptionMessage.PRODUCT_NO_ACTIVE_COMPANY));
        }

        String fileLink =
            ReportFactory.createReport("GstProductSheet.rptdesign", name + "-${date}")
                .addParam("ProductId", product.getId())
                .addParam("CompanyId", user.getActiveCompany().getId())
                .addParam("Locale", ReportSettings.getPrintingLocale(null))
                .addParam(
                    "Timezone",
                    user.getActiveCompany() != null ? user.getActiveCompany().getTimezone() : null)
                .generate()
                .getFileLink();

        // logger.debug("Printing " + name);

        response.setView(ActionView.define(name).add("html", fileLink).map());
      } catch (Exception e) {
        TraceBackService.trace(response, e);
      }
    } else {
      super.printProductSheet(request, response);
    }
  }
}
