package org.edx.mobile.user;

public class EnrolledCoursesCourseMode {
    private String slug;
    private String name;
    private Double min_price;
    private String suggested_prices;
    private String currency;
    private String expiration_datetime;
    private String description;
    private String sku;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMin_price() {
        return min_price;
    }

    public void setMin_price(Double min_price) {
        this.min_price = min_price;
    }

    public String getSuggested_prices() {
        return suggested_prices;
    }

    public void setSuggested_prices(String suggested_prices) {
        this.suggested_prices = suggested_prices;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExpiration_datetime() {
        return expiration_datetime;
    }

    public void setExpiration_datetime(String expiration_datetime) {
        this.expiration_datetime = expiration_datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
