import com.voghan.pillar.core.models.cfm.LinkCfm
import com.voghan.pillar.core.models.cfm.HeroCardCfm

String cfmPath = "/content/dam/pillar/us/en/demo/hero/demo-hero"

HeroCardCfm master = getContentFragmentModel(cfmPath,"master")
println "Master: $master.headline $master.shortDescription $master.backgroundImage"
master.callToActions?.each { link ->
    println "Links: $link.linkPath $link.linkText"
}

master.breadcrumbs?.each { link ->
    println "breadcrumb: $link.linkPath $link.linkText"
}


def getContentFragmentModel(def cfmPath, def version) {

    Resource r = getResource(cfmPath + "/jcr:content/data/" + version);
    HeroCardCfm link = r.adaptTo(HeroCardCfm.class)
}