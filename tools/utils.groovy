def gaeversion = new String(project.version).replace('.','-')
if (gaeversion.endsWith("SNAPSHOT")) {
gaeversion = gaeversion.substring(0, gaeversion.length() - 9)
}
project.properties['gae.application.version'] = gaeversion