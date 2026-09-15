package com.zenve.admin.dto;

import com.zenve.admin.model.Admin;

public record AdminDto(String id, String name, String email) {
    public static AdminDto from(Admin admin) {
        return new AdminDto(admin.getId(), admin.getName(), admin.getEmail());
    }
}
