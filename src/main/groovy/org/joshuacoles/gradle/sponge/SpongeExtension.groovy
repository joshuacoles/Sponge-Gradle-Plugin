package org.joshuacoles.gradle.sponge

class SpongeExtension {
    def platforms
    def version = '2.0'

    String[] getVersionStrings() {
        if (platforms instanceof SpongePlatform.Type) return [new SpongePlatform(platforms as SpongePlatform.Type).versionString]
        else if (platforms instanceof SpongePlatform) return [(platforms as SpongePlatform).versionString]
        else if (platforms instanceof List) {
            return (platforms.collect {
                if (it instanceof SpongePlatform.Type) return new SpongePlatform(it).versionString
                else if (it instanceof SpongePlatform) return it.versionString
                else return null
            }).findAll { it != null }
        } else {
            println 'DEAD'
            return [new SpongePlatform(SpongePlatform.Type.FORGE).versionString]
        }
    }
}
