package com.zenve.admin.dto;

public record DatabaseInfoResponse(
        boolean connected,
        String driver,
        String name,
        String type,
        String host,
        Long sizeBytes,
        String lastModified,
        String error,
        DbCounts counts
) {
    public record DbCounts(long doctors, long admins, long notifications) {
    }
}
