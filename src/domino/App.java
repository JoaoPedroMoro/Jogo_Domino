package domino;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import org.bson.types.ObjectId;

public class App {
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
			
			try (PartidaDAO dao = new PartidaDAO()) {
				
				Partida partida;
				
				// 1º Cenário - Abrir uma partida já existente via argumento.
				if (args.length == 1) {
	
					String idHex = args[0];
					
					if (!idHex.matches("^[0-9a-fA-F]{24}$")) {
						JOptionPane.showMessageDialog(null, "ID da partida inválida!\nNecessário 24 caracteres hexadecimais.");
						return;
					}
					
					partida = dao.buscar(new ObjectId(idHex));
					
					if (partida == null) {
						JOptionPane.showMessageDialog(null, "Partida não encontrada no banco de dados.");
						return;
					}		
				
				} else { // 2º Cenário - criando uma nova partida.
					
					String nome = JOptionPane.showInputDialog("Digite seu nome (deixe em branco para \"Jogador\":");
					
					if (nome == null) return;
					
					if (nome.isBlank()) nome = "Jogador";
					
					partida = new Partida(nome, "I.A."); // Fixando o nome do segundo jogador como Inteligência Artificial
					
					dao.salvarOuAtualizar(partida);
					
					JOptionPane.showMessageDialog(null, "Partida criada!\nID para reabrir futuramente:\n" + partida.id);
					
				}
				
				new TelaPrincipal(partida).setVisible(true);
			
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Erro ao iniciar a aplicação:\n" + ex.getMessage());
				ex.printStackTrace();
			}
		
		});
		
	}

}
