package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

public interface AdapterPlugin<I extends Entity> extends Plugin {
    I onFetch(I entity);

    I onPublish(I entity);
}
