@OpenAPIDefinition(
        tags = {
                @Tag(name = OpenApis.TAG_DB, description = "Database's operations."),
                @Tag(name = OpenApis.TAG_COLL, description = "Collection's operations."),
                @Tag(name = TAG_DV, description = "Dynamic view's operations."),
                @Tag(name = TAG_TR, description = "Registered map's operations."),
                @Tag(name = TAG_STATS, description = "Statistic's operations."),
                @Tag(name = TAG_SEARCH, description = "Search's operations."),
                @Tag(name = TAG_INDEX, description = "Index's operations."),
        },
        info = @Info(
                title = "Database API",
                version = "1.0.0",
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html")
        )
)
package fr.genin.christophe.thor.server.rest.database;

import fr.genin.christophe.thor.server.rest.OpenApis;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import static fr.genin.christophe.thor.server.rest.OpenApis.*;
import static fr.genin.christophe.thor.server.rest.OpenApis.TAG_TR;