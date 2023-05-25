package it.uniba.dib.sms222334.Models;

public class Expense extends Document{

    public enum expenseType{ACCESSORY,FOOD,HEALTH}
    private String note;
    private Double price;
    private expenseType category; //categorie per le spese

    private Expense(String id, String note, Double price, expenseType category) {
        super(id);

        this.note = note;
        this.price = price;
        this.category = category;
    }

    public String getnote() {
        return note;
    }

    public void setnote(String note) {
        this.note = note;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public expenseType getCategory() {
        return category;
    }

    public void setCategory(expenseType category) {
        this.category = category;
    }

    public static class Builder {
        private String bID;
        private String bnote;
        private Double bprice;
        private expenseType bcategory;

        private Builder(final String id, final Double price){
            this.bID = id;
            this.bprice=price;
        }


        public static Builder create(final String id, final Double price){
            return new Builder(id, price);
        }


        public Builder setCategory(final expenseType category){
            this.bcategory=category;
            return this;
        }

        public Builder setnote(final String note){
            this.bnote=note;
            return this;
        }


        public Expense build(){
            return new Expense(bID, bnote,bprice,bcategory);
        }
    }
}

