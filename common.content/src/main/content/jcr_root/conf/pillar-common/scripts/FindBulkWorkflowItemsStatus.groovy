// Update path
def path = "/etc/acs-commons/bulk-workflow-manager/demo/publish-pages0/jcr:content/workspace"

def predicates = [
        "path": path,
        "type": "oak:Unstructured"
]

def query = createQuery(predicates)

query.hitsPerPage = 1000

def result = query.result

println "${result.totalMatches} hits, execution time = ${result.executionTime}s\n--"

def total = 0
def complete = 0
def running = 0

result.hits.each { hit ->

    def props =  hit.getProperties()
    def status = props.get("status", String.class)

    if (status == null) {
        return;
    }

    total++
    if ("COMPLETED".equals(status)) {
        complete++
    } else if ("RUNNING".equals(status)) {
        running++
    } else {
        println "$status"
    }

}

println "Complete: $complete"
println "Running: $running"
println "Total: $total"
