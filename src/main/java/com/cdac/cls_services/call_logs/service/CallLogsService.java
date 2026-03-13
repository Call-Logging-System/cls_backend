package com.cdac.cls_services.call_logs.service;

import com.cdac.cls_services.call_logs.dto.*;
import com.cdac.cls_services.call_logs.models.OfficeModel;

import java.util.List;

public interface CallLogsService {
    List<CallLogResponseDto> getCallLogsList();

    void saveOffice(AddOfficeReqDto dto);

    List<DropdownDto> getUsersDropdown();

    void saveCallLog(AddCallLogDto dto);

    void delete(DeleteCallLogDto dto);

    CallLogResponseDto get(GetCallLogDto dto);

    void update(AddCallLogDto dto);

    List<OfficeModel> getOffices();
}
