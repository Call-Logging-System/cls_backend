package com.cdac.cls_services.phone_book.dto;

import lombok.Data;

@Data
public class UpdateOfficeDto {

    private Integer id;
    private String contactNumber;
    private String alternateContactNumber;
    private String email;
    private String address;
    private Boolean isActive;

}