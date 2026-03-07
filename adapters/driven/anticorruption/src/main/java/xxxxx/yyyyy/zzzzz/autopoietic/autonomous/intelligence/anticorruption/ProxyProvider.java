package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

public interface ProxyProvider<I extends Entity> {
    I provide(Resource resource);
}
