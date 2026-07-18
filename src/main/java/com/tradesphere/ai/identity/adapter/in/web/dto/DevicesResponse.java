package com.tradesphere.ai.identity.adapter.in.web.dto;

import com.tradesphere.ai.identity.domain.model.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Response DTO for device list
 */
@Data
@AllArgsConstructor
public class DevicesResponse {
    private List<DeviceInfo> devices;
}

