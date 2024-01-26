package bg.sofia.uni.fmi.mjt.compass.dish;

public enum DishType {
    ALCOHOL_COCKTAIL("Alcohol Cocktail"),
    BISCUITS_AND_COOKIES("Biscuits and Cookies"),
    BREAD("Bread"),
    CEREALS("Cereals"),
    CONDIMENTS_AND_SAUCES("Condiments and Sauces"),
    DESSERTS("Desserts"),
    DRINKS("Drinks"),
    EGG("Egg"),
    ICE_CREAM_AND_CUSTARD("Ice Cream and Custard"),
    MAIN_COURSE("Main Course"),
    PANCAKE("Pancake"),
    PASTA("Pasta"),
    PASTRY("Pastry"),
    PIES_AND_TARTS("Pies and Tarts"),
    PIZZA("Pizza"),
    PREPS("Preps"),
    PRESERVE("Preserve"),
    SALAD("Salad"),
    SANDWICHES("Sandwiches"),
    SEAFOOD("Seafood"),
    SIDE_DISH("Side Dish"),
    SOUP("Soup"),
    SPECIAL_OCCASIONS("Special Occasions"),
    STARTER("Starter"),
    SWEETS("Sweets");

    private final String value;

    DishType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
