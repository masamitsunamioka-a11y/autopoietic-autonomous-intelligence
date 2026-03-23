package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s.JavaAdapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s.JavaTranslator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s.JsonAdapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s.JsonTranslator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s.TimestampAdapterPlugin;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Episodic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Semantic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.util.List;

@ApplicationScoped
public class Producers {
    private static final Logger logger = LoggerFactory.getLogger(Producers.class);

    @Produces
    @ApplicationScoped
    public Repository<Area> areaRepository(Serializer serializer) {
        var configuration = new Configuration().resolve(Area.class);
        var translator = new JsonTranslator<>(serializer, configuration, Area.class);
        var adapter = new JsonAdapter<>(translator, configuration, List.of());
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @ApplicationScoped
    public Repository<Neuron> neuronRepository(Serializer serializer) {
        var configuration = new Configuration().resolve(Neuron.class);
        var translator = new JsonTranslator<>(serializer, configuration, Neuron.class);
        var adapter = new JsonAdapter<>(translator, configuration, List.of());
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @ApplicationScoped
    public Repository<Effector> effectorRepository(Serializer serializer) {
        var configuration = new Configuration().resolve(Effector.class);
        var translator = new JavaTranslator<>(serializer, configuration, Effector.class);
        var adapter = new JavaAdapter<>(translator, configuration);
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @Semantic
    @ApplicationScoped
    public Repository<Trace> semanticRepository(Serializer serializer) {
        var configuration = new Configuration().resolve(Knowledge.class);
        var translator = new JsonTranslator<>(serializer, configuration, Trace.class);
        var adapter = new JsonAdapter<>(translator, configuration, List.of());
        return new RepositoryImpl<>(adapter);
    }

    @Produces
    @Episodic
    @ApplicationScoped
    public Repository<Trace> episodicRepository(Serializer serializer) {
        var configuration = new Configuration().resolve(Episode.class);
        var translator = new JsonTranslator<>(serializer, configuration, Trace.class);
        var adapter = new JsonAdapter<>(translator, configuration, List.of(new TimestampAdapterPlugin()));
        return new RepositoryImpl<>(adapter);
    }
}
