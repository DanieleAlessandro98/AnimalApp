package it.uniba.dib.sms222334.Models;

public class Food extends Document{
    private String name;

    private Food(String name) {
        this.name = name;
    }

    public static class Builder {
        private String bName;

        private Builder( final String name){
            this.bName=name;
        }

        public static Builder create(final String name){
            return new Builder(name);
        }

        public Food build(){
            return new Food(bName);
        }
    }
}
