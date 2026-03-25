package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.AdapterPlugin;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s.JavaTranslator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s.JsonTranslator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Episodic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Semantic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.nio.file.Path;

@ApplicationScoped
public class Producers {
    private static final Logger logger = LoggerFactory.getLogger(Producers.class);

    @Produces
    @ApplicationScoped
    public Repository<Area> areaRepository(Serializer serializer) {
        var configuration = new Configuration().resolve(Area.class);
        var extern = new LocalFileSystem(Path.of(configuration.get("target"), ""));
        var translator = new JsonTranslator<>(serializer, configuration, Area.class);
        var plugin = new JsonNamingPlugin();
        var adapter = new AdapterImpl<>(extern, translator, plugin);
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @ApplicationScoped
    public Repository<Neuron> neuronRepository(Serializer serializer) {
        var configuration = new Configuration().resolve(Neuron.class);
        var extern = new LocalFileSystem(Path.of(configuration.get("target"), ""));
        var translator = new JsonTranslator<>(serializer, configuration, Neuron.class);
        var plugin = new JsonNamingPlugin();
        var adapter = new AdapterImpl<>(extern, translator, plugin);
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @ApplicationScoped
    public Repository<Effector> effectorRepository(Serializer serializer) {
        var configuration = new Configuration().resolve(Effector.class);
        var extern = new LocalFileSystem(Path.of(configuration.get("source"), ""));
        var translator = new JavaTranslator<>(serializer, configuration, Effector.class);
        var plugin = new JavaNamingPlugin(configuration.get("package"));
        var adapter = new AdapterImpl<>(extern, translator, plugin);
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @Semantic
    @ApplicationScoped
    public Repository<Trace> semanticRepository(Serializer serializer) {
        var configuration = new Configuration().resolve(Knowledge.class);
        var extern = new LocalFileSystem(Path.of(configuration.get("target"), ""));
        var translator = new JsonTranslator<>(serializer, configuration, Trace.class);
        var plugin = new JsonNamingPlugin();
        var adapter = new AdapterImpl<>(extern, translator, plugin);
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @Episodic
    @ApplicationScoped
    public Repository<Trace> episodicRepository(Serializer serializer) {
        var configuration = new Configuration().resolve(Episode.class);
        var extern = new LocalFileSystem(Path.of(configuration.get("target"), ""));
        var translator = new JsonTranslator<>(serializer, configuration, Trace.class);
        var plugin = new JsonNamingPlugin();
        var adapter = new AdapterImpl<>(extern, translator, plugin);
        return new RepositoryImpl<>(adapter);
    }

    /// @formatter:off
    private static class JsonNamingPlugin implements AdapterPlugin {
        @Override
        public String onNaming(String id) {
            return id.replaceAll("([A-Z])", "_$1").toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "") + ".json";
        }
    }
    private static class JavaNamingPlugin implements AdapterPlugin {
        private final String package_;
        public JavaNamingPlugin(String package_) {
            this.package_ = package_;
        }
        @Override
        public String onNaming(String id) {
            return (this.package_ + "." + id).replace(".", "/") + ".java";
        }
    }
    /// @formatter:on
}
