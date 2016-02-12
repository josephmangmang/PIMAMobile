package com.pimamobile.pima.utils.interfaces;


import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.History;
import com.pimamobile.pima.models.Item;
import com.pimamobile.pima.models.Sale;

public interface OnFragmentInteractListener {
    void onLibraryItemClickListener(Item item);
    void onLibraryItemClickListener(Discount discount);
    void onCurrentSaleClicked(Sale sale);
    void onCurrentSaleAllDiscountsClick();
    void onFragmentStart(boolean showHomAsUp, boolean fromSaleActivity, String title);
    void onEditDiscountClicked(Discount discount);
    void onEditCurrentSalesItemDistroy();
    void onEditQuantityButtonClicked(int newQuantity);
    void onEditCurrentSalesItemDeleted();
    void updateCurrentItemNote(String note);
    void onCurrentDiscountRemove(Discount discount, int position);
    void onChargeButtonClicked();
    void onChargeConfirmClicked(String amountRecieved);
    void onNewSaleClicked();
    void onAddCustomItem(String note, String price);
    void onHistoryItemClicked(History history);
}
