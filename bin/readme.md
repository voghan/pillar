
## Page Import
```
./bin/postPageImport.sh imports/page/import-page.json
./bin/postPageImport.sh imports/page/import-page-htl.json
./bin/postPageImport.sh imports/page/import-article-page.json
```

## Content Fragment Imports
```
./bin/postCfmImport.sh imports/cfm/import-cfm-card.json
./bin/postCfmImport.sh imports/cfm/import-cfm-simple-card.json
./bin/postCfmImport.sh imports/cfm/import-cfm-featured-card.json
./bin/postCfmImport.sh imports/cfm/import-cfm-hero-card.json
./bin/postCfmImport.sh imports/cfm/import-cfm-article-detail.json
```

## GrahpQL

Author

```
curl -u admin:admin "http://localhost:4502/graphql/execute.json/pillar/all-hero-cards"
```
```
curl -u admin:admin "http://localhost:4502/graphql/execute.json/pillar/get-hero-by-path"
```
```
curl -u admin:admin "http://localhost:4502/graphql/execute.json/pillar/find-available-links;text=Learnmore;url=/content/page/learn"
```

Publisher
```
curl "http://localhost:4503/graphql/execute.json/pillar/all-hero-cards"
```
```
curl "http://localhost:4503/graphql/execute.json/pillar/get-hero-by-path"
```
```
curl "http://localhost:4503/graphql/execute.json/pillar/find-available-links;text=Learnmore;url=/content/page/learn"
```