package FontesTeste;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Maquina implements Runnable{
	Socket conn;
	int numPacotes;
	boolean ehEmissor;
	public static final int FIM_TRANSMISSAO = -1;
	public static final int CANAL_PRONTO    = 10;
	public static final int TRANSMISSAO     = 20;
	
	public Maquina(String ip, int porta, int qtdPacotes){
		try {
			this.numPacotes = qtdPacotes;
			this.conn = new Socket(ip, porta);
		} catch (IOException e) {
			System.out.println("Erro ao realizar conex�o da m�quina!");
			e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			System.out.println("M�quina "+conn.getInetAddress()+" conectada com o servidor na porta "+conn.getPort());
			ObjectOutputStream saidaCanal  = new ObjectOutputStream(conn.getOutputStream());
			ObjectInputStream entradaCanal = new ObjectInputStream(conn.getInputStream());
			 
			ehEmissor = (boolean)entradaCanal.readObject();
			
			saidaCanal.writeObject(ehEmissor?"Emissora": "Receptora");
			
			if((Integer)entradaCanal.readObject() == CANAL_PRONTO && ehEmissor){
				System.out.println("Emissor conectado e transferindo");
				funcionalidadeEmissor(entradaCanal, saidaCanal);
			}else{
				System.out.println("Receptor conectado e transferindo");
				funcionalidadeReceptor(entradaCanal, saidaCanal);
			}
			
		} catch (IOException e) {
			System.out.println("Erro na m�quina!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Classe n�o encontrada!");
			e.printStackTrace();
		}
		
	}
	
	private void funcionalidadeEmissor(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException{
		for(int i = 0; i < numPacotes; i++){
			out.writeObject(TRANSMISSAO);
			out.writeObject("1");
			System.out.print("Emi -> ");
			System.out.println(in.readObject());
		}
		out.writeObject(FIM_TRANSMISSAO);
	}
	
	private void funcionalidadeReceptor(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException{
		for(int i = 0; i < numPacotes; i++){
			System.out.println(" Rec: "+in.readObject());
			out.writeObject("ACK");
		}
	}
}
