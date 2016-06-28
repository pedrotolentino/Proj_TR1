package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Vector;

import Simulacao.Constantes;
import Simulacao.Protocolo;

public class ProtSelectiveRepeat implements Protocolo{	

	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException {
		int flagRetorno = -1;
		int[] ret = null;
		Vector novoPacote = null;
		while(flagRetorno != Constantes.ACK){
			flagRetorno = Constantes.ACK;
			int erro = 0;
			out.reset();
			out.writeObject(Constantes.TRANSMISSAO);
			out.writeObject(pacote);

			System.out.print("Emi -> ");
			try{
				ret	 = (int[]) in.readObject();
				
			}catch(InterruptedByTimeoutException e){}
						
			System.out.println();
			for(int i = 0; i < ret.length; i++){
				if(ret[i] == Constantes.NACK){
					flagRetorno = Constantes.NACK;
					erro++;
					System.out.println("NACK do pacote "+(i+1)+"");

				}else if(ret[i] == Constantes.ACK){
					System.out.println("ACK do pacote "+(i+1));
				}
			}
			if(flagRetorno == Constantes.NACK){
				novoPacote = new Vector<>(erro);
				for(int i = 0; i < ret.length; i++){
					if(ret[i] == Constantes.NACK){
						novoPacote.add(pacote.get(i));
					}
				}
				pacote = novoPacote;
			}
		}
	}
}
