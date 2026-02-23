package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Conversation;

/// @ApplicationScoped
public class ConversationImpl implements Conversation {
    private static final Logger logger = LoggerFactory.getLogger(ConversationImpl.class);
    private final Context context;

    /// @Inject
    public ConversationImpl(Context context) {
        this.context = context;
    }

    @Override
    public void begin() {
        /// this.context.begin();
    }

    @Override
    public void begin(String id) {
        /// this.context.begin(id);
    }

    @Override
    public void end() {
        /// this.context.end();
    }

    @Override
    public boolean isTransient() {
        /// return this.context.isTransient();
        return false;
    }
}
