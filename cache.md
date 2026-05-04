### Delete cached files
````
POST /dispatcher/invalidate.cache HTTP/1.1
CQ-Action: Activate
CQ-Handle: path-pattern
Content-Length: 0
````

### Resume cachs
````
POST /dispatcher/invalidate.cache HTTP/1.1
CQ-Action: Activate
Content-Type: text/plain
CQ-Handle: path-pattern
Content-Length: numchars in bodypage_path0

page_path1
...
page_pathn
````
````
POST /dispatcher/invalidate.cache HTTP/1.1
CQ-Action: Activate
Content-Type: text/plain
CQ-Handle: /content/geometrixx-outdoors/en/men.html
Content-Length: 36

/content/geometrixx-outdoors/en.html
````