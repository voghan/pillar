import com.voghan.pillar.core.models.cfm.LinkCfm

String cfmPath = "/content/dam/pillar/us/en/demo/links/link1"

com.voghan.pillar.core.models.cfm.LinkCfm master = getLink(cfmPath,"master")
com.voghan.pillar.core.models.cfm.LinkCfm version = getLink(cfmPath,"es")

println "Master: $master.linkPath $master.linkText"
println "Version: $version.linkPath $version.linkText"

def getLink(def cfmPath, def version) {

    Resource r = getResource(cfmPath + "/jcr:content/data/" + version);

    com.voghan.pillar.core.models.cfm.LinkCfm link = r.adaptTo(com.voghan.pillar.core.models.cfm.LinkCfm.class)
}
