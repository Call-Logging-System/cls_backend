package com.cdac.cls_services.phone_book.service;

import com.cdac.cls_services.call_logs.models.OfficeModel;
import com.cdac.cls_services.call_logs.repositories.OfficeRepository;
import com.cdac.cls_services.exception.RecordNotFoundException;
import com.cdac.cls_services.phone_book.dto.OfficeDto;
import com.cdac.cls_services.phone_book.dto.UpdateOfficeDto;
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
        if(office == null){throw new RecordNotFoundException("Office not found");}
        OfficeDto officeDto = new OfficeDto();
        officeDto.setContactNumber(office.getContactNumber());
        return officeDto;
    }

    @Override
    public void update(UpdateOfficeDto dto) {
        OfficeModel office = officeRepo.findById(dto.getId()).orElseThrow(()-> new RecordNotFoundException("Office not found"));

        office.setContactNumber(dto.getContactNumber());
        office.setAlternateContactNumber(dto.getAlternateContactNumber());
        office.setEmail(dto.getEmail());
        office.setAddress(dto.getAddress());
        office.setIsActive(dto.getIsActive());

        officeRepo.save(office);
    }
}
