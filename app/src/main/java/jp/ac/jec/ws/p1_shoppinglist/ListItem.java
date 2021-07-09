package jp.ac.jec.ws.p1_shoppinglist;


class ListItem {

    private String inputData;
    private String productName;
    private int itemID;


    public ListItem(String s) {
        this.productName = s;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public ListItem() {

    }

    public ListItem(String inputData, String productName, String itemID) {
        this.inputData = inputData;
        this.productName = productName;
        this.itemID = Integer.parseInt(itemID);
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
