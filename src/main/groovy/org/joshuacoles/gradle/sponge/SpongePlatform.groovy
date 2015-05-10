package org.joshuacoles.gradle.sponge

import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

/**
 * Created by joshuacoles on 07/05/2015.
 */
@TupleConstructor
class SpongePlatform {
    Type type
    String version = 'LATEST'

    enum Type {
        FORGE('org.spongepowered:sponge:')

        private Type(String groupAndArtifact) {
            this.groupAndArtifact = groupAndArtifact
        }

        @PackageScope
        String groupAndArtifact
    }

    @PackageScope
    String getVersionString() {
        "$type.groupAndArtifact:$version"
    }
}
