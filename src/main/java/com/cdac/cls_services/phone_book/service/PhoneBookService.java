package com.cdac.cls_services.phone_book.service;

import com.cdac.cls_services.call_logs.models.OfficeModel;
import com.cdac.cls_services.phone_book.dto.DeleteOfficeDto;

import java.util.List;

public interface PhoneBookService {
    List<OfficeModel> getOffices();
}
