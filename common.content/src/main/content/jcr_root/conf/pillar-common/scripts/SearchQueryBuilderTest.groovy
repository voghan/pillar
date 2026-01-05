import com.voghan.pillar.common.queries.impl.SimpleQueryBuilderImpl
import com.voghan.pillar.core.models.Card;
import com.voghan.pillar.core.models.cfm.CardCfm;

SimpleQueryBuilderImpl simpleQueryBuilder = getService(SimpleQueryBuilderImpl.class)

List<Card> cards = new ArrayList<>()
Map<String, String> map = new HashMap<String, String>();
map.put("path", "/content/dam/pillar/cfm/basic-cards/demo");
map.put("type", "dam:AssetContent");
int i = 0;

map.put(i +"_tagid", "properties:orientation/portrait");
map.put(i +"_tagid.property", "metadata/cq:tags");
map.put("p.limit","25");

List<Resource> resources = simpleQueryBuilder.search(slingRequest, map);

//hydrate
for (Resource resource : resources) {
    Card card = resource.getChild("data/master").adaptTo(CardCfm.class);
    if (card != null) {
        cards.add(card);
        println card.headline
    }
}