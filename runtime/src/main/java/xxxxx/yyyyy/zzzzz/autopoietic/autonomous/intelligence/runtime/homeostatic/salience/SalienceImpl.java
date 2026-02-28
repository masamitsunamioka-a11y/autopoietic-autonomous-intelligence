package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic.salience;

import jakarta.enterprise.context.ApplicationScoped;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Salience;

import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class SalienceImpl implements Salience {
    private final AtomicBoolean oriented = new AtomicBoolean(false);

    @Override
    public void orient() {
        this.oriented.set(true);
    }

    @Override
    public void release() {
        this.oriented.set(false);
    }

    @Override
    public boolean isOriented() {
        return this.oriented.get();
    }
}
