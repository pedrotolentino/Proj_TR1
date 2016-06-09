package FontesTeste;
import java.io.*;
import java.net.*;

public class Emissor {
	Socket emissor;
	ObjectOutputStream saida;
	ObjectInputStream entrada;
	String pacote;
	String ack;
	String str;
	String msg;
	int n;
	int i = 0;
	int seq = 0;

	public void run() {
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Esperando por conexão...");
			emissor = new Socket("localhost", 1234);
			seq = 0;

			saida = new ObjectOutputStream(emissor.getOutputStream());
			saida.flush();
			entrada = new ObjectInputStream(emissor.getInputStream());
			str = (String) entrada.readObject();
			System.out.println("Receptor: " + str);
			System.out.println("Digite os dados a serem enviados....");
			pacote = buffer.readLine();
			n = pacote.length();
			do {
				try {
					if (i < n) {
						msg = String.valueOf(seq);
						msg = msg.concat(pacote.substring(i, i + 1));
					} else if (i == n) {
						msg = "end";
						saida.writeObject(msg);
						break;
					}
					saida.writeObject(msg);
					seq = (seq == 0) ? 1 : 0;
					saida.flush();
					System.out.println("Dados enviados ->" + msg);
					ack = (String) entrada.readObject();
					System.out.println("Esperando por ack...\n\n");
					if (ack.equals(String.valueOf(seq))) {
						i++;
						System.out.println("Receptor ->  " + " Pacote recebido\n\n");
					} else {
						System.out.println("Timeout alcançado... Reenviando dados\n\n");
						seq = (seq == 0) ? 1 : 0;
					}
				} catch (Exception e) {
				}
			} while (i < n + 1);
			System.out.println("Todos os dados enviados... fechando conexão.");
		} catch (Exception e) {
		} finally {
			try {
				entrada.close();
				saida.close();
				emissor.close();
			} catch (Exception e) {
			}
		}
	}

	public static void main(String args[]) {
		Emissor s = new Emissor();
		s.run();
	}
}
