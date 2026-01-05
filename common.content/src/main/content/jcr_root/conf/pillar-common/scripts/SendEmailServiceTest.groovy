import com.voghan.pillar.common.emails.SimpleEmailService

SimpleEmailService emailService = getService(SimpleEmailService.class)

String template ="/conf/pillar-common/notifications/email/demo-email.html"
String mailTo = "bfvaughn@gmail.com"

Map<String, String> params = new HashMap<>();
params.put("givenName", "Wally")
params.put("authorLink", "http://localhost:4502/aem/start.html")
params.put("initiator","Brian")

emailService.sendEmail(mailTo, template, params)

