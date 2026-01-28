package domino;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PainelTabuleiro extends JPanel {
	
	private static final int ALTURA = 30;
	private static final int COMPRIMENTO = ALTURA * 2;
	private static final int ESPACO = 2;
	private static final Color COR_FUNDO = new Color(0, 128, 140);
	private static final Color COR_PECA = Color.WHITE;
	private static final Color COR_BORDA = new Color(160, 160, 160);
	private static final Color COR_AZUL = new Color(30, 90, 200);
	private static final Color COR_VERM = new Color(200, 50, 50);
	
	private List<Peca> sequencia;
	
	public void setPecas(List<Peca> pecas) {
		
		this.sequencia = pecas;
		repaint();
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		setBackground(COR_FUNDO);
		
		if (sequencia == null || sequencia.isEmpty()) return;
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int cx = 10;
		
		int cy = getHeight() / 2;
		
		int dx = 1, dy = 0;
		
		/* percorre todas as peças na ordem em que foram colocadas   */
        // Percorre cada peça na sequência, na ordem 
        //  		exata em que foram jogadas.
		for (Peca p : sequencia) {
			
			// Laço infinito controlado por 'break': usamos este loop 
            //  		para tentar desenhar a peça na direção atual; 
            //  		se ela "caber" dentro dos limites do painel, 
            //  		saímos do loop; caso ultrapasse a borda, 
            //  		giramos a direção em 90° (no sentido horário) e 
            //  		tentamos de novo.
			while (true) {
				
				/* ORIENTAÇÃO DA PEÇA
                 *    Definimos se a peça será desenhada verticalmente (em pé) 
                 *            ou horizontalmente:
                 *    • Se dx==0, estamos movendo verticalmente (para 
                 *    		  cima ou para baixo), então a peça 
                 *    		  fica "em pé" (vertical=true).
                 *    • Se p.duplo()==true, significa que os dois lados 
                 *    		  têm o mesmo valor (por exemplo, [3|3]),
                 *      	  e peças duplas sempre são desenhadas
                 *      	  verticalmente para caberem melhor no espaço.
                 *    Caso contrário, a peça é desenhada na 
                 *    		  orientação horizontal normal.
                 */
				boolean vertical = (dx == 0) || p.duplo();
				
				/* CÁLCULO DAS DIMENSÕES NA TELA
                 *    Dependendo da orientação, a largura (w) e altura (h) 
                 *    		reais que a peça ocupará mudam:
                 *    • Se vertical==true:
                 *        largura  = ALTURA (lado curto fica horizontal)
                 *        altura   = COMPRIMENTO (lado longo fica vertical)
                 *    • Se vertical==false:
                 *        largura  = COMPRIMENTO (lado longo fica horizontal)
                 *        altura   = ALTURA (lado curto fica vertical)
                 */
				int w = vertical ? ALTURA : COMPRIMENTO; // largura do retângulo a desenhar
				int h = vertical ? COMPRIMENTO : ALTURA; // altura do retângulo a desenhar
				
				// Declara duas variáveis inteiras para armazenar 
                //  		as coordenadas X e Y do canto superior-esquerdo 
                //  		do retângulo que representará a peça.
				int tlX, tlY;
				
				// Verifica se a direção atual de desenho é para a direita (dx == +1).
                // Nesse caso, o ponto-âncora (cx, cy) corresponde exatamente ao canto esquerdo da peça, e 
                //  		queremos centralizar verticalmente.
				if (dx == 1) {
					
					// Atribui à coordenada X do canto esquerdo da peça o valor de cx, pois desenhamos 
                    //  		para a direita a partir do ponto-âncora.
					tlX = cx;
					
					// Atribui à coordenada Y o valor de cy menos metade da altura da peça (h/2),
                    // 			para que o meio vertical da peça fique alinhado com cy.
					tlY = cy - h / 2;
					
				} else if (dx == -1) {
					// Se a direção for para a esquerda (dx == -1),precisamos deslocar o retângulo de desenho 
	                //  		para a esquerda inteira da peça.
					
					// Atribui à coordenada X o valor de cx menos a largura total da peça (w),
                    // 			posicionando o canto direito da peça no ponto-âncora.
					tlX = cx - w;
					
					// Assim como no caso anterior, subtrai metade da 
                    //  		altura para centralizar verticalmente.
					tlY = cy - h / 2;
					
				} else if (dy == 1) {
					// Se a direção for para baixo (dy == +1), o ponto-âncora corresponde ao topo central da peça.
					
					// Atribui à coordenada X o valor de cx menos metade da largura da peça (w/2), para centralizar
                    //  		horizontalmente a peça em relação a cx.
					tlX = cx - w / 2;
					
					// Atribui à coordenada Y o valor de cy, pois desenhamos para baixo a partir do ponto-âncora, alinhando o topo da peça em cy.
					tlY = cy;
					
				} else {
					// Caso contrário (dy == -1), significa que desenhamos para cima.
					
					// Atribui à coordenada X o valor de cx menos metade da largura da peça (w/2), centralizando 
                    //  		horizontalmente como no caso de movimento para baixo.
					tlX = cx - w / 2;
					
					// Atribui à coordenada Y o valor de cy menos a 
                    //  		altura total da peça (h),
                    // 			posicionando o canto inferior da peça no ponto-âncora.
					tlY = cy - h;
					
				}
				
//				// TESTE DE BORDA
//                // Verifica se o retângulo definido por (tlX, tlY, w, h) 
//                //  		ultrapassa qualquer limite do painel:
//                // • tlX < 0: ultrapassa a margem esquerda
//                // • tlY < 0: ultrapassa o topo
//                // • tlX + w > getWidth(): ultrapassa a margem direita
//                // • tlY + h > getHeight(): ultrapassa a margem inferior
//				if (tlX < 0 || tlY < 0 || tlX + w > getWidth() || tlY + h > getHeight()) {
//										
//					// Se a peça não caber na direção atual, calculamos uma nova direção, girando 90° no sentido horário (CW):
//                    // • novoDX = -dy
//                    // • novoDY =  dx
//                    // Isso efetivamente rotaciona o vetor (dx, dy) em 90° CW.
//					int novoDX = -dy;
//					int novoDY = dx;
//					
//					// Atualiza os componentes de direção para a nova orientação.
//					dx = novoDX;
//					dy = novoDY;
//					
//					continue;
//					
//				}
				
				boolean estouraDireita = (dx == 1 && tlX + w > getWidth());
				boolean estouraEsquerda = (dx == -1 && tlX < 0);
				boolean estouraBaixo = (dy == 1 && tlY + h > getHeight());
				boolean estouraCima = (dy == -1 && tlY < 0);

				if (estouraDireita) {
				    // terminou a linha → desce
				    dx = 0;
				    dy = 1;

				    // volta para a margem esquerda
				    cx = 10;

				    // desce uma "linha" de peças
				    cy += COMPRIMENTO + ESPACO;

				    continue;
				}

				if (estouraEsquerda || estouraBaixo || estouraCima) {
				    // fallback: gira 90° se quiser manter segurança
				    int novoDX = -dy;
				    int novoDY = dx;
				    dx = novoDX;
				    dy = novoDY;
				    continue;
				}

				
				// VALORES DOS DOIS LADOS (ladoA / ladoB)
                // Dependendo da direção, precisamos inverter qual lado da peça ficará em contato com a ponta livre:
                // • Se desenharmos para a esquerda (dx == -1) ou para cima (dy == -1), a peça deve ser invertida para manter o encaixe correto.
				boolean inverter = (dx == -1) || (dy == -1);
				
				// Define ladoA e ladoB com base na inversão:
                // • Se inverter == true: ladoA recebe p.ladoDireito e ladoB recebe p.ladoEsquerdo.
                // • Caso contrário: ladoA recebe p.ladoEsquerdo e ladoB recebe p.ladoDireito.
				int ladoA = inverter ? p.ladoDireito : p.ladoEsquerdo;
				int ladoB = inverter ? p.ladoEsquerdo : p.ladoDireito;
				
				// DESENHO DA PEÇA
                // Chama o método que desenha completamente a peça na tela,
                // 			incluindo corpo, bordas, divisória e pips (pontinhos).
                // Parâmetros:
                // • g2       : contexto gráfico (Graphics2D) para desenho avançado.
                // • tlX, tlY : coordenadas do canto superior-esquerdo onde começar a desenhar.
                // • w, h     : largura e altura do retângulo que representa a peça.
                // • vertical : indica se a peça deve ser desenhada verticalmente.
                // • ladoA    : valor do primeiro lado a ser pintado (pips).
                // • ladoB    : valor do segundo lado a ser pintado (pips).
				desenharPeca(g2, tlX, tlY, w, h, vertical, ladoA, ladoB);
				
				// DESLOCAMENTO DO PONTO-ÂNCORA (cx, cy)
                // Após desenhar, atualizamos o ponto-âncora para a 
                //  		"ponta livre" da peça recém-colocada,
                // 			para que a próxima peça seja encaixada corretamente.
                // • Se dx != 0, estamos movendo horizontalmente:
                //     		passo = (vertical ? ALTURA : COMPRIMENTO) + ESPACO
                // • Se dx == 0, ou seja, dy != 0, movemos verticalmente:
                //     		passo = (vertical ? COMPRIMENTO : ALTURA) + ESPACO
				if (dx != 0) { // deslocamento horizontal
					
					// Calcula quantos pixels avançar no eixo X:
                    // • Se a peça está vertical, usa ALTURA (o “lado curto”) como deslocamento;
                    // • Se horizontal, usa COMPRIMENTO (o “lado longo”);
                    // • Soma ESPACO para manter o espaçamento entre peças.
					int passo = (vertical ? ALTURA : COMPRIMENTO) + ESPACO;
					
					// Atualiza cx multiplicando a direção (±1) pelo passo para mover à esquerda ou direita.
					cx += dx * passo;
					
				} else { // deslocamento vertical
					
					// Calcula quantos pixels avançar no eixo Y:
                    // • Se a peça está vertical, o deslocamento é COMPRIMENTO;
                    // • Se horizontal, é ALTURA;
                    // • Soma ESPACO para separação entre linhas de peças.
                    int passo = (vertical ? COMPRIMENTO : ALTURA) + ESPACO;
                    
                    // Atualiza cy multiplicando a direção (±1) pelo passo para mover para cima ou para baixo.
                    cy += dy * passo;
					
				}
				
				break;
				
			}
			
		}
		
	}
	
    private void desenharPeca(Graphics2D g2,
            int x, int y, int w, int h,
            boolean vertical,
            int ladoA, int ladoB) {

		// Define a cor de preenchimento das peças (COR_PECA, normalmente branca)
		g2.setColor(COR_PECA);

		// Desenha o corpo da peça como um retângulo com cantos arredondados:
		// • x, y   definem a posição do canto superior-esquerdo
		// • w, h   definem as dimensões do retângulo
		// • 6, 6   definem o raio de arredondamento dos cantos em pixels
		g2.fillRoundRect(x, y, w, h, 6, 6);

		// Define a cor da borda das peças (COR_BORDA, tom de cinza)
		g2.setColor(COR_BORDA);
		
		// Desenha o contorno do retângulo que representa a peça, usando cantos arredondados.
		// • g2.drawRoundRect(...) utiliza o mesmo x, y, w e h que foram usados para preencher o retângulo,
		//   		garantindo que a borda se alinhe exatamente com as bordas do preenchimento.
		// • Os últimos dois parâmetros (6, 6) definem o raio de curvatura em pixels para as bordas,
		//   		produzindo um efeito suave em cada canto.
		// • Desenhar o contorno após o preenchimento é importante para garantir que a linha da 
		//  		borda fique visível e não seja obscurecida pela cor de fundo.
		g2.drawRoundRect(x, y, w, h, 6, 6);

		// Verifica se a peça está na orientação vertical (em pé).
		// • vertical == true quando dx == 0 (movimento vertical) ou a peça é duplo.
		if (vertical) {

			// Desenha a linha divisória que separa a parte superior da inferior.
			// • O ponto inicial é (x, y + h/2):
			//   - x: canto esquerdo da peça
			//   - y + h/2: posição vertical exatamente no meio da altura da peça
			// • O ponto final é (x + w, y + h/2):
			//   - x + w: canto direito da peça
			//   - y + h/2: mesma posição vertical, mantendo a linha horizontal
			// • g2.drawLine desenha um traço fino (largura de 1 pixel) ligando esses dois pontos, criando visualmente 
			//  		as duas metades da peça de dominó.
			g2.drawLine(x, y + h / 2, x + w, y + h / 2);


			// Desenha os pips (pontinhos) do lado superior da peça:
			// • g2        : contexto de desenho
			// • ladoA     : valor (0 a 6) que determina quantos pips desenhar
			// • x, y      : canto superior do retângulo onde começa este lado
			// • w         : largura total do retângulo
			// • h / 2     : altura do retângulo correspondente ao lado superior
			desenharPips(g2, ladoA, x, y, w, h / 2);

			// Desenha os pips (pontinhos) no lado inferior da peça:
			// • g2           : contexto gráfico onde serão desenhados os pips
			// • ladoB        : valor inteiro (0–6) que define quantos pips serão pintados
			// • x            : coordenada X do canto esquerdo do retângulo da peça
			// • y + h/2      : coordenada Y que inicia a metade inferior da peça
			// • w            : largura total da peça (todos os pips distribuem-se nesse espaço)
			// • h / 2        : altura da metade inferior (espaço vertical disponível para pips)
			desenharPips(g2, ladoB, x, y + h / 2, w, h / 2);

		} else {

			// Se vertical == false, a peça está deitada (orientação horizontal).
			// Precisamos desenhar a divisória vertical que separa ladoA e ladoB.
			// • x + w/2      : posição X no meio da peça
			// • y            : canto superior da peça
			// • y + h        : canto inferior da peça
			g2.drawLine(x + w / 2, y, x + w / 2, y + h);

			// Desenha os pips no lado esquerdo da peça deitada:
			// • ladoA        : valor do lado esquerdo (0–6)
			// • x, y         : canto superior-esquerdo do retângulo esquerdo
			// • w / 2        : largura da metade esquerda da peça
			// • h            : altura total da peça
			desenharPips(g2, ladoA, x, y, w / 2, h);

			// Desenha os pips no lado direito da peça deitada:
			// • x + w/2      : desloca para a metade direita (canto 
			//  					superior-esquerdo do retângulo direito)
			// • ladoB        : valor do lado direito (0–6)
			// • w / 2        : largura disponível na metade direita
			// • h            : altura total da peça
			desenharPips(g2, ladoB, x + w / 2, y, w / 2, h);

		}

    }

	
	// Método auxiliar para desenhar os "pips" (pontinhos) de uma face de dominó, levando em conta a área 
	//  		disponível e o valor (0 a 6) que define o padrão de pontos.
    private void desenharPips(Graphics2D g2,
            int valor,   // valor numérico da face da peça (quantos pips serão desenhados)
            int x,       // coordenada X do canto superior-esquerdo da área reservada para os pips
            int y,       // coordenada Y do canto superior-esquerdo da área reservada para os pips
            int w,       // largura total da área para desenhar os pips
            int h) {     // altura total da área para desenhar os pips

    	// Define a cor de pintura para os pips:
    	// Se o valor for menor ou igual a 3, usa COR_AZUL; caso contrário, usa COR_VERM.
    	// Isso diferencia visualmente valores baixos (azul) de valores altos (vermelho).
    	g2.setColor(valor <= 3 ? COR_AZUL : COR_VERM);

    	// Calcula o ponto X central (cx) da área de pips:
    	// • x é a borda esquerda da área.
    	// • w/2 desloca esse ponto até o meio da largura.
    	// O centro serve como referência para posicionar pips de forma simétrica.
    	int cx = x + w / 2;

    	// Calcula o ponto Y central (cy) da área de pips:
    	// • y é a borda superior da área.
    	// • h/2 desloca esse ponto até o meio da altura.
    	// Juntos, (cx, cy) formam o ponto central onde pips centrais ou referenciais são desenhados.
    	int cy = y + h / 2;

    	// Define o raio (r) padrão de cada pip (círculo):
    	// • Um valor de 6 pixels cria pontinhos visíveis, proporcionais à área disponível.
    	int r = 6;

    	// Calcula o deslocamento horizontal (dx) entre colunas de pips:
    	// • Divide a largura w em três partes iguais.
    	// • Esse dx será usado para posicionar pips à esquerda e à direita do centro.
    	int dx = w / 3;

    	// Calcula o deslocamento vertical (dy) entre linhas de pips:
    	// • Divide a altura h em três partes iguais.
    	// • Esse dy será usado para posicionar pips acima e abaixo do centro.
    	int dy = h / 3;

    	// Seleciona o padrão de desenho dos pips com base no valor inteiro (0 a 6) da face da peça.
    	// Usamos a instrução switch para tratar cada caso de forma clara e legível.
    	switch (valor) {

    		// Caso 0: não há pips na face, então não desenhamos nada.
    		case 0 -> {

    			// Nenhuma chamada ao método pip(), mantendo a área limpa.

    		}

    		// Caso 1: desenha um único pip, centralizado na área reservada.
    		case 1 -> 

    			// Chama pip() com coordenadas (cx, cy) e raio r, desenhando um círculo no centro exato da área.
    			// g2: Graphics2D
    			pip(g2, cx, cy, r);

    		// Caso 2: desenha dois pips em posições opostas em diagonal, simulando o padrão típico de uma face de dominó com valor 2.
    		case 2 -> {

    			// Primeiro pip: canto superior-esquerdo.
    			// • cx - dx: move do centro (cx) um terço da largura para a esquerda.
    			// • cy - dy: move do centro (cy) um terço da altura para cima.
    			pip(g2, // g2: Graphics2D
    					cx - dx,  // X: deslocamento para a esquerda do centro
    					cy - dy,  // Y: deslocamento para cima do centro
    					r);       // r: raio do círculo que representa o pip

    			// Segundo pip: canto inferior-direito.
    			// • cx + dx: move do centro um terço da largura para a direita.
    			// • cy + dy: move do centro um terço da altura para baixo.
    			pip(g2, // g2: Graphics2D
    					cx + dx,  // X: deslocamento para a direita do centro
    					cy + dy,  // Y: deslocamento para baixo do centro
    					r);       // r: raio do círculo que representa o pip

    		}

    		// Caso 3: combina o padrão de 2 pips (diagonal) com um pip central,
    		// criando o padrão de valor 3.
    		case 3 -> {

    			// Pip no canto superior-esquerdo (mesma posição do primeiro pip de case 2).
    			pip(g2, // g2: Graphics2D
    					cx - dx,  // X: um terço à esquerda do centro
    					cy - dy,  // Y: um terço acima do centro
    					r);       // raio do pip

    			// Pip central: exatamente no ponto (cx, cy),
    			// 			que é o centro da área disponível de pips.
    			pip(g2, // g2: Graphics2D
    					cx,       // X: centro
    					cy,       // Y: centro
    					r);       // raio do pip

    			// Pip no canto inferior-direito (mesma posição 
    			//  		do segundo pip de case 2).
    			pip(g2, // g2: Graphics2D
    					cx + dx,  // um terço à direita do centro
    					cy + dy,  // um terço abaixo do centro
    					r);       // raio do pip

    		}

    		// Caso 4: valor igual a 4, que no dominó corresponde a quatro pontos, posicionados nos quatro cantos da face da peça.
    		case 4 -> {

    			// Desenha o pip no canto superior-esquerdo:
    			// • cx - dx: desloca o ponto X para a esquerda partindo do centro (cx), em uma distância 
    			//  		igual a um terço da largura da área (dx).
    			// • cy - dy: desloca o ponto Y para cima partindo do centro (cy), em uma distância igual a um terço da altura da área (dy).
    			// • r: raio do círculo que representa cada pip.
    			pip(g2, cx - dx, cy - dy, r);

    			// Desenha o pip no canto superior-direito:
    			// • cx + dx: desloca o ponto X para a direita do centro.
    			// • cy - dy: mantém o mesmo deslocamento vertical para cima do centro.
    			pip(g2, cx + dx, cy - dy, r);

    			// Desenha o pip no canto inferior-esquerdo:
    			// • cx - dx: desloca para a esquerda do centro (mesma 
    			//  		coluna do pip superior-esquerdo).
    			// • cy + dy: desloca para baixo do centro, em 
    			//  		um terço da altura da área.
    			pip(g2, cx - dx, cy + dy, r);

    			// Desenha o pip no canto inferior-direito:
    			// • cx + dx: desloca para a direita do centro (mesma 
    			//  		coluna do pip superior-direito).
    			// • cy + dy: desloca para baixo do centro (mesma 
    			//  		linha do pip inferior-esquerdo).
    			pip(g2, cx + dx, cy + dy, r);

    		}

    		// Caso 5: desenha cinco pips — os quatro cantos do 
    		//  		padrão de valor 4, mais um no centro.
    		case 5 -> {

    			// Chamamos recursivamente o nosso próprio método desenharPips com valor 4 para reutilizar a lógica de 
    			//  		desenhar os quatro pips nos cantos.
    			// • g2  : contexto gráfico atual onde tudo está sendo desenhado.
    			// • 4   : o valor “4” indica que queremos o padrão de quatro cantos.
    			// • x,y : coordenadas do canto superior-esquerdo da área de pips original, garantindo que o recorte seja o mesmo.
    			// • w,h : largura e altura totais da área, para que a subdivisão de colunas/linhas
    			//         (dx, dy) seja recalculada corretamente dentro das mesmas dimensões.
    			desenharPips(g2, 4, x, y, w, h);

				// Agora adicionamos o quinto pip, centralizado na face da peça:
				// • cx : posição horizontal exata do centro da área,
				//         calculada como x + w/2.
				// • cy : posição vertical exata do centro da área,
				//         calculada como y + h/2.
				// • r  : raio do círculo, que mantém o tamanho uniforme
				//         em relação aos outros pips desenhados.
    			pip(g2, cx, cy, r);

    		}


    		// Caso 6: valor igual a 6, que no dominó corresponde a seis pontos organizados em três pips em cada coluna.
    		case 6 -> {

    			// 1) Primeiro, reutilizamos o padrão de quatro pips nos cantos da face, chamando desenharPips(g2, 4, x, y, w, h) para 
    			//  		redesenhar automaticamente os pips dos cantos superior-esquerdo, superior-direito, inferior-esquerdo e
    			//  		inferior-direito.
    			//    • Passamos g2 para manter o mesmo contexto de desenho.
    			//    • O valor 4 indica que estamos solicitando o padrão de quatro cantos.
    			//    • x, y definem a origem (canto superior-esquerdo) da área de desenho da face.
    			//    • w, h determinam a largura e altura totais dessa área, assegurando que dx e dy sejam recalculados 
    			//  		corretamente para ocupar exatamente as mesmas colunas e linhas.
    			desenharPips(g2, 4, x, y, w, h);

    			// 2) Agora adicionamos o quinto pip, que fica centralizado horizontalmente e um terço abaixo do centro vertical da área:
    			//    • cx é a coordenada X do centro exato da área (x + w/2), garantindo alinhamento horizontal perfeito.
    			//    • cy - dy desloca a coordenada Y para cima em um terço da altura (dy), posicionando o pip acima do centro, formando a 
    			//  		coluna esquerda de três pips.
    			//    • r é o raio do pip, mantendo o mesmo tamanho dos demais.
    			pip(g2, cx, cy - dy, r);

    			// 3) Por fim, desenhamos o sexto pip, que fica centralizado horizontalmente e um terço abaixo do centro vertical da área:
    			//    • cx continua sendo o centro horizontal (mesma coluna dos outros dois).
    			//    • cy + dy desloca o ponto Y para baixo em um terço da altura, completando a coluna direita de três pips.
    			//    • r permanece constante, para que todos os pips tenham diâmetro uniforme.
    			pip(g2, cx, cy + dy, r);

    		}

    	}

    }

    private void pip(Graphics2D g2, int cx, int cy, int r) {

    	// Calcula a coordenada X do canto superior-esquerdo do círculo:
    	// • cx - r: desloca o centro para a esquerda em r pixels, posicionando o início do desenho do oval exatamente no canto.
    	int cantoX = cx - r;

    	// Calcula a coordenada Y do canto superior-esquerdo do círculo:
    	// • cy - r: desloca o centro para cima em r pixels, posicionando o início do desenho do oval exatamente no canto.
    	int cantoY = cy - r;

    	// Calcula a largura e altura do oval:
    	// • 2 * r: diâmetro do círculo (raio * 2) tanto horizontalmente quanto verticalmente, garantindo que a forma seja perfeitamente circular.
    	int diametro = 2 * r;

    	// Desenha um oval preenchido (círculo) com:
    	// • cantoX, cantoY: canto superior-esquerdo do retângulo limitador do oval
    	// • diametro, diametro: largura e altura do retângulo, iguais para formar um círculo
    	g2.fillOval(cantoX, cantoY, diametro, diametro);

    }	
    
    @Override
    public Dimension getPreferredSize() { return new Dimension(1000, 700);}

}
