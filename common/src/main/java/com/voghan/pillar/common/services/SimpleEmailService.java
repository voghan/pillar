package com.voghan.pillar.common.services;

import java.util.Map;

public interface SimpleEmailService {

    void sendEmail(String toAddress, String subject, Map<String, String> parameters);
}
