echo 'Usage example: ./postPageImport.sh json-file'

curl -X POST \
  -H "Content-Type: application/json" \
  --data-binary @"$1" \
  http://localhost:4502/content/pillar/language-head/en/_jcr_content.cfm.import.json \
  -u admin:admin
