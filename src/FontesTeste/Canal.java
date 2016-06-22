package FontesTeste;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Canal implements Runnable{
	ServerSocket canalEntrada;
	ServerSocket canalSaida;
	int          numPacotes;
	
	public Canal(int portaEntrada, int portaSaida, int qtdPacotes){
		try {
			this.canalEntrada = new ServerSocket(portaEntrada);
			this.canalSaida   = new ServerSocket(portaSaida);
			this.numPacotes   = qtdPacotes;
			System.out.println("Entrada do canal escutando na porta "+portaEntrada);
			System.out.println("Saida do canal escutando na porta "+portaSaida);
			
		} catch (IOException e) {
			System.out.println("ERRO ao instanciar o Canal");
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("Aguardando conexao dos clientes...");
		try {
			//Esperando a conexão das máquinas
			Socket maqEmissora = canalEntrada.accept();
			Socket maqReceptora = canalSaida.accept();
			
			//Realizando a conexão da máquina emissora
			ObjectOutputStream saidaMaqEmi  = new ObjectOutputStream(maqEmissora.getOutputStream());
			saidaMaqEmi.flush();
			ObjectInputStream entradaMaqEmi = new ObjectInputStream(maqEmissora.getInputStream());
			saidaMaqEmi.writeObject("1");
			System.out.println("Maquina Emissora conectada com o canal");
			
			//Realizando a conexão da máquina receptora
			ObjectOutputStream saidaMaqRec  = new ObjectOutputStream(maqReceptora.getOutputStream());
			saidaMaqRec.flush();
			ObjectInputStream entradaMaqRec = new ObjectInputStream(maqReceptora.getInputStream());
			saidaMaqRec.writeObject("0");
			System.out.println("Maquina Receptora conectada com o canal");
			
			for(int i = 0; i < numPacotes; i++){
				entradaMaqEmi.read();
				System.out.print(" Canal -> ");
				saidaMaqRec.write(1);
				entradaMaqRec.read();
				saidaMaqEmi.write(0);
			}
		} catch (IOException e) {
			System.out.println("Erro dentro do canal!");
			e.printStackTrace();
		}
		
	}
}
