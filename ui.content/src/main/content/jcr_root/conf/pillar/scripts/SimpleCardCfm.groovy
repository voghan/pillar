import com.voghan.pillar.core.models.cfm.LinkCfm
import com.voghan.pillar.core.models.cfm.SimpleCardCfm

String cfmPath = "/content/dam/pillar/us/en/demo/simple-cards/option1"

SimpleCardCfm master = getContentFragmentModel(cfmPath,"master")
println "Master: $master.headline $master.shortDescription $master.image"
master.callToActions?.each { link ->
    println "Links: $link.linkPath $link.linkText"
}

SimpleCardCfm version = getContentFragmentModel(cfmPath,"es")
println "Version: $version.headline $version.shortDescription $master.image"
version.callToActions?.each { link ->
    println "Links: $link.linkPath $link.linkText"
}

def getContentFragmentModel(def cfmPath, def version) {

    Resource r = getResource(cfmPath + "/jcr:content/data/" + version);
    SimpleCardCfm link = r.adaptTo(SimpleCardCfm.class)
}