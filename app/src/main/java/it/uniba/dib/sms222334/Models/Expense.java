package it.uniba.dib.sms222334.Models;

public class Expense {
    private String name;
    private Double price;
    private int category; //categorie per le spese

    private Expense(String name, Double price, int category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public static class Builder {
        private String bName;
        private Double bprice;
        private int bcategory;

        private Builder(final Double price){
            this.bprice=price;
        }


        public Builder create(final Double price){
            return new Builder(price);
        }


        public Builder setCategory(final int category){
            this.bcategory=category;
            return this;
        }

        public Builder setName(final String name){
            this.bName=name;
            return this;
        }


        public Expense build(){
            return new Expense(bName,bprice,bcategory);
        }
    }
}

