package fr.genin.christophe.thor.server;

import fr.genin.christophe.thor.core.options.ThorOptions;
import fr.genin.christophe.thor.server.config.ThorOptionsProperties;
import io.vavr.control.Option;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class ThorConfiguration {

    @Inject
    ThorOptionsProperties thorOptionsProperties;

    @Produces
    ThorOptions thorOptions() {
        final ThorOptions thorOptions = new ThorOptions();

        Option.ofOptional(thorOptionsProperties.options.defaultName).peek(thorOptions::setDefaultName);
        Option.ofOptional(thorOptionsProperties.options.directory).peek(thorOptions::setDirectory);
        Option.ofOptional(thorOptionsProperties.options.extensionFile).peek(thorOptions::setExtensionFile);
        Option.ofOptional(thorOptionsProperties.options.adapterType).peek(thorOptions::setAdapterType);
        Option.ofOptional(thorOptionsProperties.options.verbose).peek(thorOptions::setVerbose);
        Option.ofOptional(thorOptionsProperties.options.autosave).peek(thorOptions::setAutosave);
        Option.ofOptional(thorOptionsProperties.options.autoload).peek(thorOptions::setAutoload);
        Option.ofOptional(thorOptionsProperties.options.throttledSaves).peek(thorOptions::setThrottledSaves);
        Option.ofOptional(thorOptionsProperties.options.partitioned).peek(thorOptions::setPartitioned);
        Option.ofOptional(thorOptionsProperties.options.delimited).peek(thorOptions::setDelimited);
        Option.ofOptional(thorOptionsProperties.options.delimiter).peek(thorOptions::setDelimiter);
        Option.ofOptional(thorOptionsProperties.options.autosaveInterval).peek(thorOptions::setAutosaveInterval);
        Option.ofOptional(thorOptionsProperties.options.serializationMethod).peek(thorOptions::setSerializationMethod);
        Option.ofOptional(thorOptionsProperties.options.destructureDelimiter).peek(thorOptions::setDestructureDelimiter);
        return thorOptions;
    }
}
