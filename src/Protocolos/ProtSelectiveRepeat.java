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
		int erro = 0;
		int[] ret = null;
		Vector novoPacote = new Vector<int[]>();
		while(!pacote.isEmpty()){
			for(int i = erro; i < Constantes.TAMANHO_JANELA && i < pacote.size(); i++){
				novoPacote.add(pacote.get(i));
			}
			novoPacote.trimToSize();
			out.reset();
			out.writeObject(Constantes.TRANSMISSAO);
			out.reset();
			out.writeObject(novoPacote);
			
			System.out.print("Emi -> ");
			try{
				ret	 = (int[]) in.readObject();
				
				System.out.println();
				erro = 0;
				for(int i = ret.length - 1; i >= 0 ; i--){
					if(ret[i] == Constantes.NACK){
						erro++;
						System.out.println("NACK do pacote "+(i+1));

					}else if(ret[i] == Constantes.ACK){
						System.out.println("ACK do pacote "+(i+1));
						novoPacote.remove(i);
						pacote.remove(i);
					}
				}
			}catch(InterruptedByTimeoutException e){
				
			}		
		}
	}
}
