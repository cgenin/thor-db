package fr.genin.christophe.thor.server.rest;

import fr.genin.christophe.thor.server.infrastructure.DatabasePort;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/health")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthResource {


    @Inject
    DatabasePort databasePort;


    @GET
    public Health get() {

        return new Health(true, databasePort.list().size());
    }


    public static class Health {
        public boolean up;
        public Integer nbDatabases;

        public Health() {
        }

        public Health(boolean up, Integer nbDatabases) {
            this.up = up;
            this.nbDatabases = nbDatabases;
        }
    }
}
