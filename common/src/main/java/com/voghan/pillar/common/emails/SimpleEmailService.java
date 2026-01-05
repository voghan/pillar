package com.voghan.pillar.common.emails;

import java.util.Map;

public interface SimpleEmailService {

    void sendEmail(String mailTo, String templatePath, Map<String, String> parameters);
}
