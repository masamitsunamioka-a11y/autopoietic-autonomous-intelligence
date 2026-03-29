package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Episodic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Semantic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Engram;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.nio.file.Path;
import java.util.function.Function;

@ApplicationScoped
public class Producers {
    private static final Logger logger = LoggerFactory.getLogger(Producers.class);

    @Produces
    @ApplicationScoped
    public Adapter<Area> areaAdapter(Serializer serializer) {
        var configuration = new Configuration().resolve(Area.class);
        var extern = new LocalFileSystem(Path.of(configuration.get("path"), ""));
        var translator = new JsonTranslator<>(serializer, Area.class);
        return new AdapterImpl<>(extern, translator, new JsonNamer());
    }

    @Produces
    @ApplicationScoped
    public Repository<Area> areaRepository(Adapter<Area> adapter) {
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @ApplicationScoped
    public Adapter<Neuron> neuronAdapter(Serializer serializer) {
        var configuration = new Configuration().resolve(Neuron.class);
        var extern = new LocalFileSystem(Path.of(configuration.get("path"), ""));
        var translator = new JsonTranslator<>(serializer, Neuron.class);
        return new AdapterImpl<>(extern, translator, new JsonNamer());
    }

    @Produces
    @ApplicationScoped
    public Repository<Neuron> neuronRepository(Adapter<Neuron> adapter) {
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @ApplicationScoped
    public Adapter<Effector> effectorAdapter(Serializer serializer) {
        var configuration = new Configuration().resolve(Effector.class);
        var extern = new LocalFileSystem(Path.of(configuration.get("path"), ""));
        var translator = new JavaTranslator<>(serializer, configuration, Effector.class);
        return new AdapterImpl<>(extern, translator, new JavaNamer(configuration.get("package")));
    }

    @Produces
    @ApplicationScoped
    public Repository<Effector> effectorRepository(Adapter<Effector> adapter) {
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @Semantic
    @ApplicationScoped
    public Adapter<Trace> semanticAdapter(Serializer serializer) {
        var configuration = new Configuration().resolve(Knowledge.class);
        var extern = new LocalFileSystem(Path.of(configuration.get("path"), ""));
        var translator = new JsonTranslator<>(serializer, Trace.class);
        return new AdapterImpl<>(extern, translator, new JsonNamer());
    }

    @Produces
    @Semantic
    @ApplicationScoped
    public Repository<Trace> semanticRepository(@Semantic Adapter<Trace> adapter) {
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @Episodic
    @ApplicationScoped
    public Adapter<Trace> episodicAdapter(Serializer serializer) {
        var configuration = new Configuration().scope("hippocampal").scope("episode");
        var extern = new LocalFileSystem(Path.of(configuration.get("path"), ""));
        var threshold = Double.parseDouble(configuration.get("threshold"));
        var translator = new JsonTranslator<>(serializer, Engram.class);
        return new AdapterImpl<>(extern, translator, new JsonNamer(), x -> x.strength() >= threshold, Engram::trace);
    }

    @Produces
    @Episodic
    @ApplicationScoped
    public Repository<Trace> episodicRepository(@Episodic Adapter<Trace> adapter) {
        return new RepositoryImpl<>(adapter);
    }

    /// @formatter:off
    public static class JsonNamer implements Function<String, String> {
        @Override
        public String apply(String id) {
            return id.replaceAll("([A-Z])", "_$1").toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "") + ".json";
        }
    }
    public static class JavaNamer implements Function<String, String> {
        private final String package_;
        public JavaNamer(String package_) {
            this.package_ = package_;
        }
        @Override
        public String apply(String id) {
            return (this.package_ + "." + id).replace(".", "/") + ".java";
        }
    }
    /// @formatter:on
}
