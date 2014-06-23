package br.com.danielferber.rcp.communicationtoys.bus.api;

/**
 * Mensagem enviada pelo barramento. Os listeners do barramento são chamados na
 * mesma ordem que foram adicionados ao barramento. Cabe a mensagem decidir se o
 * listener é compatível com a mensagem, e se for, chamar o método específico do
 * listener conforme a semântica da mensagem.
 * <p>
 * Para isto, o barramento executará o método
 * <code>antes</code> sobre todos os listeners do barramento. Em seguida, repete
 * o mesmo processo para os métodos
 * <code>executar</code> e
 * <code>depois</code>. Desta forma, a mensagem funciona como um 'visitor' sobre
 * os listeners, ou seja, a própria mensagem contém a lógica de como disparar o
 * listener. O tipo do listener é indicado pela parâmetro T.
 *
 * @author X7WS
 */
public abstract class Event<ListenerType extends EventListener> {

    private final Class<ListenerType> classe;

    public Event(final Class<ListenerType> classe) {
        if (classe == null) {
            throw new IllegalArgumentException();
        }
        this.classe = classe;
    }

    final boolean isCompativel(final EventListener listener) {
        return classe.isAssignableFrom(listener.getClass());
    }

    final void executarCaller(final EventListener listener) {
        this.executar((ListenerType) listener);
    }

    protected abstract void executar(ListenerType listener);
}
