package domino;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipal extends JFrame {
	
	private final PartidaDAO dao = new PartidaDAO();
	
	private Partida partida;
	
	private final PainelTabuleiro painelTabuleiro = new PainelTabuleiro();
	
	private final DefaultListModel<Peca> modeloPecas = new DefaultListModel<>();
	
	private final JList<Peca> listaPecas = new JList<>(modeloPecas);
	
	private final JButton btnEsq = new JButton("< Jogar Esquerda");
	
	private final JButton btnDir = new JButton("Jogar Direita >");
	
	private final JButton btnBuy = new JButton("Comprar");
	
	private final JButton btnPass = new JButton("Passar");
	
	private final JButton btnRein = new JButton("Recomeçar");
	
	private final JLabel lblInfo = new JLabel(" ");
	
	private boolean resultadoMostrado = false;
	
	public TelaPrincipal(Partida partida) {
		
		this.partida = partida;
		
		setTitle("Dominó - " + partida.jogador1.nome + " (Você) vs I.A.");
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		montarInterface();
		
		atualizarInterface();
		
		processarIA();
		
	}
	
	private void montarInterface() {
		
		setLayout(new BorderLayout());
		
		add(painelTabuleiro, BorderLayout.CENTER);
		
		JPanel rodape = new JPanel(new BorderLayout());
		
		listaPecas.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		
		listaPecas.setVisibleRowCount(1);
		
		listaPecas.setFixedCellWidth(64);
		
		listaPecas.setFixedCellHeight(34);
		
		listaPecas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		listaPecas.setCellRenderer(new RenderizadorPeca());
		
		JScrollPane scroll = new JScrollPane(listaPecas,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		scroll.setPreferredSize(new Dimension(760, 70));
		
		rodape.add(scroll, BorderLayout.CENTER);
		
		JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
		
		botoes.add(btnEsq);
		botoes.add(btnDir);
		botoes.add(btnBuy);
		botoes.add(btnPass);
		botoes.add(btnRein);
		
		btnRein.setVisible(false);
		
		rodape.add(botoes, BorderLayout.SOUTH);
		
		lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
		
		rodape.add(lblInfo, BorderLayout.EAST);
		
		add(rodape, BorderLayout.SOUTH);
		
		ActionListener acaoJogar = e -> 
			jogar(e.getSource() == btnEsq);
		btnEsq.addActionListener(acaoJogar);
		btnDir.addActionListener(acaoJogar);
		
		btnBuy.addActionListener(e -> {
			if (partida.comprarPecaHumano()) {
				dao.salvarOuAtualizar(partida);
				atualizarInterface();
			}
		});
		
		btnPass.addActionListener(e -> {
			partida.passarVez(1);
			dao.salvarOuAtualizar(partida);
			atualizarInterface();
			processarIA();
		});
		
		btnRein.addActionListener(e -> reiniciarPartida());
		
	}
	
	private void jogar(boolean esquerda) {
		int idx = listaPecas.getSelectedIndex();
		if (idx == -1) {
			JOptionPane.showMessageDialog(this, "Selecione uma peça.");
			return;
		}
		
		if (!partida.jogar(1, idx, esquerda)) {
			JOptionPane.showMessageDialog(this, "A peça não encaixa na ponta");
			return;
		}
		
		dao.salvarOuAtualizar(partida);
		
		atualizarInterface();
		
		processarIA();
		
	}
	
	private void processarIA() {
		
		if (!partida.status.equals("em andamento") || partida.turnoAtual != 2) return;
		
		new javax.swing.Timer(500, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				((javax.swing.Timer) e.getSource()).stop(); // Para o timer para que não repita a ação em loop.
				partida.jogadaIA();
				dao.salvarOuAtualizar(partida);
				atualizarInterface();
				
			}
			
		}).start();
		
	}
	
	private void atualizarInterface() {
		painelTabuleiro.setPecas(partida.mesa);
		modeloPecas.clear();
		partida.jogador1.pecas.forEach(modeloPecas::addElement);
		
		String titulo = "Turno de " + (partida.turnoAtual == 1 ? partida.jogador1.nome : "I.A.");
		
		if (partida.status.equals("finalizada")) titulo += " - FIM (Vencedor: " + partida.vencedor + ")";
		
		setTitle(titulo);
		
		boolean humanoTemJogada = partida.jogador1.pecas.stream()
				.anyMatch(p -> p.combinaCom(partida.pontaEsquerda())
						|| p.combinaCom(partida.pontaDireita()));
		
		boolean vezHumano = partida.turnoAtual == 1 && partida.status.equals("em andamento");
		
		btnEsq.setEnabled(vezHumano && humanoTemJogada);
		btnDir.setEnabled(vezHumano && humanoTemJogada);
		
		btnBuy.setEnabled(vezHumano && !humanoTemJogada && !partida.estoque.isEmpty());
		
		btnPass.setEnabled(vezHumano && !humanoTemJogada && partida.estoque.isEmpty());
		
		if (partida.status.equals("finalizada")) {
			
			if (!resultadoMostrado) {
				String mensagem = partida.vencedor.equals("Empate") 
						? "O jogo travou. Resultado: EMPATE."
						: "Fim de Jogo!. Vencedor: " + partida.vencedor;
				
				JOptionPane.showMessageDialog(this, 
						mensagem,
						"Resultado",
						JOptionPane.INFORMATION_MESSAGE);
				
				resultadoMostrado = true;
			}
			
			lblInfo.setText(RankingDAO.topCinco());
			
			btnRein.setVisible(true);
			
			 btnEsq .setEnabled(false);
	         btnDir .setEnabled(false);
	         btnBuy .setEnabled(false);
	         btnPass.setEnabled(false);
			
		} else {
			
			btnRein.setVisible(false);
			
			lblInfo.setText(String.format(
					"<html>Minhas peças: <b>%d</b>&nbsp;|&nbsp;" +
					"Peças I.A.: <b>%d</b>&nbsp/|&nbsp;" + 
					"Estoque: <b>%d</b></html",
					partida.jogador1.pecas.size(),
					partida.jogador2.pecas.size(),
					partida.estoque.size()));
			
		}
	}
	
	private void reiniciarPartida() {
		
		this.partida = new Partida(partida.jogador1.nome, "I.A.");
		
		resultadoMostrado = false;
		
		dao.salvarOuAtualizar(partida);
		
		atualizarInterface();
		
		processarIA();
		
	}
	
	private static class RenderizadorPeca extends JPanel implements ListCellRenderer<Peca> {
		
		private static final int L = 60;
		private static final int A = 30;
		
		private Peca peca;
		
		private boolean selecionada;
		
		public RenderizadorPeca() {
			
			setPreferredSize(new Dimension(L + 6, A + 6));
			
			setOpaque(true);
			
		}
		
		@Override
		public Component getListCellRendererComponent(
				JList<? extends Peca> list,
				Peca value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) {
			
			this.peca = value;
			this.selecionada = isSelected;
			
			setBackground(isSelected ? new Color(255, 245, 220) : list.getBackground());
			
			setBorder(isSelected 
					? BorderFactory.createLineBorder(new Color(255, 140, 0), 3, true)
					: BorderFactory.createEmptyBorder(3, 3, 3, 3));
			
			return this;
			
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D) g;
			
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			int x0 = 3, y0 = 3;
			
			g2.setColor(Color.WHITE);
			
			g2.fillRoundRect(x0, y0, L, A, 6, 6);
			
			g2.setColor(new Color(160, 160, 160));
			
			g2.drawRoundRect(x0, y0, L, A, 6, 6);
			
			g2.drawLine(x0 + L / 2, y0, x0 + L / 2, y0 + A);
			
			desenharPips(g2, peca.ladoEsquerdo, x0, y0, L / 2, A);
			
			desenharPips(g2, peca.ladoDireito, x0 + L / 2, y0, L / 2, A);
			
		}
		
		private void desenharPips(Graphics2D g2, int v, int x, int y, int w, int h) {
			
			g2.setColor(v <= 3 
					? new Color(30, 90, 200)
					: new Color(200, 50, 50));
			
			int cx = x + w / 2;
			
			int cy = y + h / 2;
			
			int r = 5;
			
			int dx = w / 3;
			
			int dy = h / 3;
			
			switch(v) {
				case 0 -> {// Não pinta nenhum ponto
				}
				
				case 1 -> {
					// Pinta apenas 1 ponto na peça
					pip(g2, cx, cy, r);
				}
				
				case 2 -> {
					// Pinta 2 pontos
					pip(g2, cx - dx, cy - dy, r);
					pip(g2, cx + dx, cy + dy, r);
				}
				
				case 3 -> {
					// Pinta 3 pontos
					pip(g2, cx - dx, cy - dy, r);  
					pip(g2, cx, cy, r);       
	                pip(g2, cx + dx, cy + dy, r); 
				}
				
				case 4 -> {
					// Pinta 4 pontos
                    pip(g2, cx - dx, cy - dy, r); 
                    pip(g2, cx + dx, cy - dy, r);    
                    pip(g2, cx - dx, cy + dy, r); 
                    pip(g2, cx + dx, cy + dy, r); 
                }

                case 5 -> {
                	// Pinta 5 pontos
                    desenharPips(g2, 4, x, y, w, h); // Desenha os 4 pontos
                    pip(g2, cx, cy, r); // Dsenha o ultimo ponto
                    
                }
                
                case 6 -> {
                	// Pinta 6 pontos
                    desenharPips(g2, 4, x, y, w, h);
                    pip(g2, cx, cy - dy,  r); 
                    pip(g2, cx, cy + dy, r); 
                }
			
			}
			
		}
		
		private void pip(Graphics2D g2, int cx, int cy, int r) {
			
			int cantoX = cx - r;
			int cantoY = cy - r;
			int diametro = r * 2;
			
			g2.fillOval(cantoX, cantoY, diametro, diametro);
			
		}
		
	}

}
