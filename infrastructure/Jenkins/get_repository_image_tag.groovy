import groovy.json.JsonSlurper
def out = new URL('http://localhost:5000/v2/learnwords/vocabulary-read-service/tags/list').text
def json = new JsonSlurper().parseText(out)
return (json.tags ?: [])*.toString()
