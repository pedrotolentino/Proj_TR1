package FontesTeste;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Canal implements Runnable{
	ServerSocket canalEntrada;
	ServerSocket canalSaida;
	public static final int FIM_TRASMISSAO = -1;
	public static final int CANAL_PRONTO   = 10;
	
	public Canal(int portaEntrada, int portaSaida, int qtdPacotes){
		try {
			this.canalEntrada = new ServerSocket(portaEntrada);
			this.canalSaida   = new ServerSocket(portaSaida);
			System.out.println("Entrada do canal escutando na porta "+canalEntrada.getLocalPort());
			System.out.println("Saida do canal escutando na porta "+canalSaida.getLocalPort());
			
		} catch (IOException e) {
			System.out.println("ERRO ao instanciar o Canal");
			e.printStackTrace();
		}
	}

	public void run() {
		int valor = CANAL_PRONTO;
		System.out.println("Aguardando conexao dos clientes...");
		try {
			//Esperando a conex�o das m�quinas
			Socket maqEmissora = canalEntrada.accept();
			Socket maqReceptora = canalSaida.accept();
			
			//Realizando a conex�o da m�quina emissora
			maqEmissora.sendUrgentData(1);
			ObjectInputStream entradaMaqEmi = new ObjectInputStream(maqEmissora.getInputStream());
			ObjectOutputStream saidaMaqEmi  = new ObjectOutputStream(maqEmissora.getOutputStream());
			saidaMaqEmi.writeObject(true);
			System.out.println("Maquina "+entradaMaqEmi.readObject()+" conectada com o canal ");
			
			//Realizando a conex�o da m�quina receptora
			ObjectInputStream entradaMaqRec = new ObjectInputStream(maqReceptora.getInputStream());
			ObjectOutputStream saidaMaqRec  = new ObjectOutputStream(maqReceptora.getOutputStream());
			saidaMaqRec.writeObject(false);
			System.out.println("Maquina "+entradaMaqRec.readObject()+" conectada com o canal");
			
			/*for(int i = 0; i < numPacotes; i++){
				entradaMaqEmi.read();
				System.out.print(" Canal -> ");
				saidaMaqRec.write(1);
				entradaMaqRec.read();
				saidaMaqEmi.write(0);
			}*/
			saidaMaqRec.writeObject(CANAL_PRONTO);
			saidaMaqEmi.writeObject(CANAL_PRONTO);
			
			while((Integer)entradaMaqEmi.readObject() != FIM_TRASMISSAO){
				System.out.print(" Canal -> "+entradaMaqEmi.readObject());
				saidaMaqRec.writeObject("1");
				saidaMaqEmi.writeObject(entradaMaqRec.readObject());
			}
		} catch (IOException e) {
			System.out.println("Erro dentro do canal!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
