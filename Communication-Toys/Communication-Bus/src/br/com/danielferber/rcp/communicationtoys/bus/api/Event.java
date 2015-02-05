package br.com.danielferber.rcp.communicationtoys.bus.api;

/**
 * Event sent through the event bus. Events are delivered only to Listeners that
 * implement the interface passed to the constructor.
 *
 * @param <ListenerType> The type of Listeners called for this Event.
 * @author Daniel Felix Ferber
 */
public abstract class Event<ListenerType extends EventListener> {

    private final Class<ListenerType> listenerType;

    public Event(final Class<ListenerType> listenerType) {
        if (listenerType == null) {
            throw new IllegalArgumentException();
        }
        this.listenerType = listenerType;
    }

    final boolean isCompatible(final EventListener listener) {
        return listenerType.isAssignableFrom(listener.getClass());
    }

    final void executarCaller(final EventListener listener) {
        this.executar((ListenerType) listener);
    }

    protected abstract void executar(ListenerType listener);
}
