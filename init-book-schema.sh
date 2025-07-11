#!/bin/bash
echo "Waiting for Solr to be ready..."
sleep 5

EXISTING_FIELDS=$(curl -s "http://solr:8983/solr/books/schema/fields" | grep -c '"name":"title"')

if [ "$EXISTING_FIELDS" -eq 0 ]; then
    echo "Adding schema fields..."

    curl -X POST -H 'Content-type:application/json' --data-binary '{
      "add-field": [
        {
          "name": "_entity",
          "type": "string",
          "indexed": true,
          "stored": true
        },
        {
          "name": "_id",
          "type": "string",
          "indexed": true,
          "stored": true
        },
        {
          "name": "title",
          "type": "text_general",
          "indexed": true,
          "stored": true
        },
        {
          "name": "author",
          "type": "text_general",
          "indexed": true,
          "stored": true
        },
        {
          "name": "isbn",
          "type": "string",
          "indexed": true,
          "stored": true
        },
        {
          "name": "publishYear",
          "type": "pint",
          "indexed": true,
          "stored": true
        },
        {
          "name": "description",
          "type": "text_general",
          "indexed": true,
          "stored": true
        }
      ]
    }' http://solr:8983/solr/books/schema

    echo "Schema fields added successfully"
else
    echo "Schema fields already exist, skipping..."
fi
