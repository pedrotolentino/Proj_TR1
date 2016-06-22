package FontesTeste;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Maquina implements Runnable{
	Socket conn;
	int numPacotes;
	boolean ehEmissor;
	
	public Maquina(String ip, int porta, int qtdPacotes){
		try {
			this.numPacotes = qtdPacotes;
			this.conn = new Socket(ip, porta);
			System.out.println("Máquina "+ip+" conectada com o servidor na porta "+porta);
		} catch (IOException e) {
			System.out.println("Erro ao realizar conexão da máquina!");
			e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			ObjectOutputStream saidaCanal  = new ObjectOutputStream(conn.getOutputStream());
			saidaCanal.flush();
			ObjectInputStream entradaCanal = new ObjectInputStream(conn.getInputStream());
			 
			ehEmissor = (boolean)entradaCanal.readObject().equals("1")? true : false;
			
			if(ehEmissor){
				System.out.println("Emissor conectado e transferindo");
				funcionalidadeEmissor(entradaCanal, saidaCanal);
			}else{
				System.out.println("Receptor conectado e transferindo");
				funcionalidadeReceptor(entradaCanal, saidaCanal);
			}
			
		} catch (IOException e) {
			System.out.println("Erro na máquina!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Classe não encontrada!");
			e.printStackTrace();
		}
		
	}
	
	private void funcionalidadeEmissor(ObjectInputStream in, ObjectOutputStream out) throws IOException{
		for(int i = 0; i < numPacotes; i++){
			out.write(1);
			System.out.print("Emi -> ");
			in.read();
		}
	}
	
	private void funcionalidadeReceptor(ObjectInputStream in, ObjectOutputStream out) throws IOException{
		for(int i = 0; i < numPacotes; i++){
			System.out.println("Rec: "+in.read());
			out.write(0);
		}
	}
}
