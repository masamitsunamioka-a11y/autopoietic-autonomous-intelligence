package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Episodic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Semantic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.DecoderImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Decodic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.EncoderImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Encodic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class Producers {
    private static final Logger logger = LoggerFactory.getLogger(Producers.class);

    /// @formatter:off
    @Produces @Encodic @ApplicationScoped
    public Service<EncoderImpl.Input, String> encodicService(
        Serializer serializer,
        Repository<Area> areaRepository,
        Repository<Neuron> neuronRepository,
        Repository<Effector> effectorRepository) {
        var s = new Configuration().synaptic().shared();
        var f = new Configuration().synaptic().function();
        return new EncodingService(serializer,
            new LocalFileSystem(Path.of(s.get("source"), "")),
            new LocalFileSystem(Path.of(f.get("source"), "")),
            areaRepository, neuronRepository, effectorRepository);
    }
    @Produces @Decodic @ApplicationScoped
    public Service<DecoderImpl.Input, Object> decodicService(
        Serializer serializer) {
        return new DecodingService(serializer);}
    /// @formatter:on
    /// @formatter:off
    @Produces @Semantic @ApplicationScoped
    public Repository<Trace> semanticRepository(@Semantic Adapter<Trace> adapter) {
        return new RepositoryImpl<>(adapter);}
    @Produces @Episodic @ApplicationScoped
    public Repository<Trace> episodicRepository(@Episodic Adapter<Trace> adapter) {
        return new RepositoryImpl<>(adapter);}
    @Produces @ApplicationScoped
    public Repository<Area> areaRepository(Adapter<Area> adapter) {
        return new RepositoryImpl<>(adapter);}
    @Produces @ApplicationScoped
    public Repository<Neuron> neuronRepository(Adapter<Neuron> adapter) {
        return new RepositoryImpl<>(adapter);}
    @Produces @ApplicationScoped
    public Repository<Effector> effectorRepository(Adapter<Effector> adapter) {
        return new RepositoryImpl<>(adapter);}
    /// @formatter:on
    /// @formatter:off
    @Produces @Semantic @ApplicationScoped
    public Adapter<Trace> semanticAdapter(
        @Semantic Translator<List<Trace>, Resource> translator) {
        return new JsonArrayAdapter<>(translator, Knowledge.class);}
    @Produces @Episodic @ApplicationScoped
    public Adapter<Trace> episodicAdapter(
        @Episodic Translator<List<Trace>, Resource> translator) {
        return new JsonArrayAdapter<>(translator, Episode.class);}
    @Produces @ApplicationScoped
    public Adapter<Area> areaAdapter(
        Translator<Area, Resource> translator) {
        return new JsonObjectAdapter<>(translator, Area.class);}
    @Produces @ApplicationScoped
    public Adapter<Neuron> neuronAdapter(
        Translator<Neuron, Resource> translator) {
        return new JsonObjectAdapter<>(translator, Neuron.class);}
    @Produces @ApplicationScoped
    public Adapter<Effector> effectorAdapter(
        Translator<Effector, Resource> translator) {
        return new JavaAdapter<>(translator, Effector.class);}
    /// @formatter:on
    /// @formatter:off
    @Produces @Semantic @ApplicationScoped
    public Translator<List<Trace>, Resource> semanticTranslator(
        Serializer serializer) {
        return new JsonArrayTranslator<>(serializer, Trace.class);}
    @Produces @Episodic @ApplicationScoped
    public Translator<List<Trace>, Resource> episodicTranslator(
        Serializer serializer) {
        return new JsonArrayTranslator<>(serializer, Trace.class);}
    @Produces @ApplicationScoped
    public Translator<Area, Resource> areaTranslator(
        Serializer serializer, ProxyProvider<Area> proxyProvider) {
        return new JsonObjectTranslator<>(serializer, proxyProvider);}
    @Produces @ApplicationScoped
    public Translator<Neuron, Resource> neuronTranslator(
        Serializer serializer, ProxyProvider<Neuron> proxyProvider) {
        return new JsonObjectTranslator<>(serializer, proxyProvider);}
    @Produces @ApplicationScoped
    public Translator<Effector, Resource> effectorTranslator(
        ProxyProvider<Effector> proxyProvider) {
        return new JavaTranslator<>(proxyProvider, Effector.class);}
    /// @formatter:on
    /// @formatter:off
    @Produces @ApplicationScoped
    public ProxyProvider<Area> areaProxyProvider(Serializer serializer) {
        return new JsonObjectProxyProvider<>(serializer, Area.class);}
    @Produces @ApplicationScoped
    public ProxyProvider<Neuron> neuronProxyProvider(Serializer serializer) {
        return new JsonObjectProxyProvider<>(serializer, Neuron.class);}
    @Produces @ApplicationScoped
    public ProxyProvider<Effector> effectorProxyProvider(Serializer serializer) {
        return new JavaProxyProvider(serializer, Effector.class);}
    /// @formatter:on
}
