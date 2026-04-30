package com.localmate.api.admin.report.domain;

public enum SanctionType {
    WARNING("원활한 서비스 이용을 위해 주의해주시길 바랍니다."),
    SANCTION_1("서비스 이용이 1일 정지되었습니다."),
    SANCTION_2("서비스 이용이 3일 정지되었습니다."),
    SANCTION_3("서비스 이용이 5일 정지되었습니다."),
    SANCTION_4("서비스 이용이 7일 정지되었습니다."),
    SANCTION_5("서비스 이용이 10일 정지되었습니다.");

    private final String message;

    SanctionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return "신고가 접수되었습니다. " + message;
    }
}
