package fr.aquazus.d1proxy.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import fr.aquazus.d1proxy.ProxyConfiguration;

public class DebugFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (Level.DEBUG.isGreaterOrEqual(event.getLevel()) && !ProxyConfiguration.proxyDebug) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}