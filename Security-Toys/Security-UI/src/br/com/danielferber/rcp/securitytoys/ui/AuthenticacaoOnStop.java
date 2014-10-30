package br.com.danielferber.rcp.securitytoys.ui;

import br.com.danielferber.rcp.securitytoys.api.SegurancaService;
import java.util.concurrent.Callable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.OnStop;

@OnStop
public class AuthenticacaoOnStop implements Callable<Boolean> {

    @Override
    public Boolean call() throws Exception {
        if (SegurancaService.getDefault().getUsuario() != null) {
            NotifyDescriptor d = new NotifyDescriptor("Finalizar aplicação?", "Pergunta",
                    NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE,
                    null, null);
            return DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION;        
        }
        return true;
    }

}
