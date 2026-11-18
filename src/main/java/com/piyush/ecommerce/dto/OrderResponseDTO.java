package com.piyush.ecommerce.dto;

import java.util.List;

public class OrderResponseDTO
{
    private Long id;
    private String userEmail;
    private Double totalPrice;
    private String status;
    private List<String> productNames;

    public OrderResponseDTO(){}

    public OrderResponseDTO(Long id, String userEmail, Double totalPrice, String status, List<String> productNames) {
        this.id = id;
        this.userEmail = userEmail;
        this.totalPrice = totalPrice;
        this.status = status;
        this.productNames = productNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }
}
