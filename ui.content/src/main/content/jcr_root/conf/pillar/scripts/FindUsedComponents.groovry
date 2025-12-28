import com.day.cq.wcm.api.components.ComponentManager

componentManager = resourceResolver.adaptTo(ComponentManager)
validResourceTypes = componentManager.components*.resourceType
data = []

def footer = getPage("/content/experience-fragments/pillar/us/en/site/footer/master")
def header = getPage("/content/experience-fragments/pillar/us/en/site/header/master")

addPage(header)
addPage(footer)

getPage("/content/pillar").recurse { page ->
    def content = page.node

    content?.recurse { node ->
        def resourceType = node.get("sling:resourceType")

        if (resourceType && validResourceTypes.contains(resourceType)) {
            data.add([node.path, resourceType])
        }
    }
}

def addPage(def page) {

    def content = page.node

    content?.recurse { node ->
        def resourceType = node.get("sling:resourceType")

        if (resourceType && validResourceTypes.contains(resourceType)) {
            data.add([node.path, resourceType])
        }
    }
}

table {
    columns("Component Path", "Resource Type")
    rows(data)
}