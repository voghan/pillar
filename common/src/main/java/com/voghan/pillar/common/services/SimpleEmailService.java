package com.voghan.pillar.common.services;

import java.util.Map;

public interface SimpleEmailService {

    void sendEmail(String mailTo, String templatePath, Map<String, String> parameters);
}
