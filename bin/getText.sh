echo 'Usage example: ./getText.sh /content/pillar/language-head/en/demo'

curl -X GET -u admin:admin http://localhost:4502/$1/_jcr_content.txt
