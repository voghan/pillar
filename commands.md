

export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH=$JAVA_HOME/bin:$PATH
export MAVEN_HOME=/Users/brianvaughn/apache/apache-maven-3.9.12
export PATH=$MAVEN_HOME/bin:$PATH

mvn -B org.apache.maven.plugins:maven-archetype-plugin:3.3.1:generate -D archetypeGroupId=com.adobe.aem -D archetypeArtifactId=aem-project-archetype -D archetypeVersion=56 -D aemVersion=6.5.8 -D appTitle="Pillar" -D appId="pillar" -D groupId="com.voghan.pillar"
