/*
 * Este arquivo pertence à Petrobras e não pode ser utilizado fora
 * desta empresa sem prévia autorização.
 */
package org.usefultoys.netbeansrcp.platform.bus;

/**
 *
 * @author x7ws
 */
public abstract class TypeCaller<ListenerType extends Bus.Listener> implements Bus.Caller {

    private final Class<ListenerType> listenerType;
    private final String name;

    protected TypeCaller(Class<ListenerType> listenerType) {
        if (listenerType == null) {
            throw new IllegalArgumentException();
        }
        this.listenerType = listenerType;
        this.name = null;
    }

    protected TypeCaller(String name, Class<ListenerType> listenerType) {
        if (listenerType == null) {
            throw new IllegalArgumentException();
        }
        this.listenerType = listenerType;
        this.name = name;
    }

    @Override
    public boolean isCompatible(Bus.Listener listener) {
        return listenerType.isAssignableFrom(listener.getClass());
    }

    @Override
    public void callListener(Bus.Listener listener) {
        callListener((ListenerType) listener);
    }

    protected abstract void doCallListener(ListenerType listener);

    @Override
    public String toString() {
        return "TypeCaller{" + "listenerType=" + listenerType + ", name=" + name + '}';
    }
}
