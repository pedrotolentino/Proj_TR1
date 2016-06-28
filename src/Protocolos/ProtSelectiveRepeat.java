package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Vector;

import Simulacao.Protocolo;

public class ProtSelectiveRepeat implements Protocolo{	

	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException {
		int flagRetorno = -1;
		int[] ret = null;
		int[] reenvio = null;
		int k = 0;
		Vector novoPacote = new Vector();
		while(flagRetorno != ACK){
			out.writeObject(TRANSMISSAO);
			if(ret == null){
				out.writeObject(pacote);
			}else{
				k = 0;
				for(int j = 0; j < pacote.size(); j++){
					for(int l = 0; l < reenvio.length; l++){
						if(reenvio[l] == j){
							novoPacote.add(pacote.get(j));
						}
					}
				}
				out.writeObject(novoPacote);
			}
			System.out.print("Emi -> ");
			try{
				ret	 = (int[]) in.readObject();
				
			}catch(InterruptedByTimeoutException e){}
			
			for(int i = 0; i < ret.length; i++){
				if(ret[i] == NACK){
					reenvio = new int[ret.length];
					System.out.println("NACK do pacote "+i+1+"");
					flagRetorno = NACK;
					reenvio[k] = i;
					k++;
				}else{
					System.out.println("ACK do pacote "+i+1);
				}
			}
		}
	}
}
