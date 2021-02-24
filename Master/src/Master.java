import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;


public class Master extends UnicastRemoteObject implements MasterInterface{
    private boolean tarefa = false;
    StorageInterface storage;
    LinkedList<ProcessCombinationModel> statistics = new LinkedList<ProcessCombinationModel>();
    protected Master() throws RemoteException {

    }
    public LinkedList<ProcessCombinationModel> criacao() throws RemoteException, NotBoundException, MalformedURLException {
        tarefa = true;
        System.out.println("Tarefa Criada!");
        distribuicao();
        return statistics;
    }
    private void distribuicao() throws RemoteException, NotBoundException, MalformedURLException {
        System.out.println("A iniciar distribuição! ");
        storage = (StorageInterface) Naming.lookup("rmi://localhost:2020/storage");
        storage.SendMapper();
        while(tarefa){
            boolean recebe = storage.VerificarCombStatistic();
            if(recebe){
                tarefa = false;
                statistics = storage.GetCombStatistic();
            }
        }
    }

}
