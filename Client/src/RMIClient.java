import java.io.File;
import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.LinkedList;

public class RMIClient {
    public static void main(String[] args) {
        File filepath = new File(".\\Client\\files");
        String contents[] = filepath.list();

        StorageInterface storage;
        MasterInterface master;

        try{
            storage = (StorageInterface) Naming.lookup("rmi://localhost:2020/storage");
            master = (MasterInterface) Naming.lookup("rmi://localhost:2024/master");
            for (int i=0; i < contents.length; i++){
                File file = new File(".\\Client\\files\\" + contents[i]);
                        FileInputStream in = new FileInputStream(file);
                byte [] array = new byte[1024*1024];
                int size = in.read(array);
                storage.Send(contents[i], array, size, i);
                System.out.println(contents[i]);
            }
            LinkedList<ProcessCombinationModel> statistics = master.criacao();
            System.out.println(statistics);

        } catch(RemoteException e) {
            System.out.println(e.getMessage());
        }catch(Exception e) {e.printStackTrace();}
    }
}
