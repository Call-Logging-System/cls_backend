package com.cdac.cls_services.phone_book.service;

import com.cdac.cls_services.call_logs.models.OfficeModel;
import com.cdac.cls_services.call_logs.repositories.OfficeRepository;
import com.cdac.cls_services.phone_book.dto.OfficeDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PhoneBookServiceImpl implements PhoneBookService{

    private OfficeRepository officeRepo;

    @Override
    public List<OfficeModel> getOffices() {
        return officeRepo.findAll();
    }

    @Override
    public OfficeDto getOfficeByUserName(String userName) {
        OfficeModel office = officeRepo.findByOfficeUserName(userName);
        OfficeDto officeDto = new OfficeDto();
        officeDto.setContactNumber(office.getContactNumber());
        return officeDto;
    }
}
