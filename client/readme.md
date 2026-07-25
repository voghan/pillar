# OAuth Setup

## Steps tp create public and private keys

Generate pem file (default password is 'notasecret')
``` 
openssl pkcs12 -in local-store.p12 -out local-store.crt.pem -clcerts -nokeys
```

Extract private key as text
```
openssl pkcs12 -in local-store.p12 -passin pass:notasecret -nocerts -nodes -out local-store.private.key.txt
```

## Test Scopes
Default profile
```
http://localhost:4502/libs/granite/oauth/content/authorization.html?client_id=8quq772o21u4aqr5ja9ob4dsij-lqtpgrtd&scope=profile&redirect_uri=http:__localhost:8080/pillar
```

Pillar Site Read profile
```
http://localhost:4502/libs/granite/oauth/content/authorization.html?client_id=8quq772o21u4aqr5ja9ob4dsij-lqtpgrtd&scope=profile&redirect_uri=http:__localhost:8080/pillar
http://localhost:4502/libs/granite/oauth/content/authorization.html?client_id=8quq772o21u4aqr5ja9ob4dsij-lqtpgrtd&scope=pillar_site_read&redirect_uri=http:__localhost:8080/pillar
```